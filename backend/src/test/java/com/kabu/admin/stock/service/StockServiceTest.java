package com.kabu.admin.stock.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kabu.admin.stock.dto.StockCreateRequest;
import com.kabu.admin.stock.dto.StockFavoriteCreateRequest;
import com.kabu.admin.stock.dto.StockImportRequest;
import com.kabu.admin.stock.dto.StockListResponse;
import com.kabu.admin.stock.dto.StockQueryRequest;
import com.kabu.admin.stock.exception.StockConflictException;
import com.kabu.admin.stock.model.Stock;
import com.kabu.admin.stock.model.StockFavorite;
import com.kabu.admin.stock.model.StockRealtimeChange;
import com.kabu.admin.stock.repository.StockRepository;
import com.kabu.admin.stock.service.impl.StockServiceImpl;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void createStockShouldPersistAndReturnCreatedStock() {
        Stock stored = buildStock(1L, "7203", "Toyota", "0");

        when(stockRepository.findByStockCode("7203")).thenReturn(Optional.empty());
        when(stockRepository.insert(any(Stock.class))).thenAnswer(invocation -> {
            Stock stock = invocation.getArgument(0);
            stock.setId(1L);
            return 1;
        });
        when(stockRepository.findById(1L)).thenReturn(Optional.of(stored));

        var response = stockService.createStock(createRequest("7203", "Toyota"));

        assertEquals(1L, response.id());
        assertEquals("7203", response.stockCode());
        assertEquals("Toyota", response.stockName());
        assertEquals("0", response.delFlg());
    }

    @Test
    void createStockShouldRejectDuplicateStockCode() {
        when(stockRepository.findByStockCode("7203"))
            .thenReturn(Optional.of(buildStock(2L, "7203", "Duplicate", "0")));

        assertThrows(StockConflictException.class, () -> stockService.createStock(createRequest("7203", "Toyota")));
    }

    @Test
    void listStocksShouldUseDefaultQueryAndPaging() {
        when(stockRepository.findByCriteria(null, null, null, null, null, null, null, null, "STOCK_CODE", "ASC", 20, 0))
            .thenReturn(List.of(buildStock(1L, "7203", "Toyota", "0")));
        when(stockRepository.countByCriteria(null, null, null, null, null, null, null, null)).thenReturn(1L);

        StockListResponse response = stockService.listStocks(
            new StockQueryRequest(null, null, null, null, null, null, null, null, null, null, null)
        );

        assertEquals(1, response.items().size());
        assertEquals(1, response.total());
        assertEquals(1, response.page());
        assertEquals(20, response.size());
    }

    @Test
    void importStocksShouldReturnExpectedStatistics() {
        Stock existing = buildStock(2L, "6758", "Sony", "0");
        StockImportRequest request = new StockImportRequest(
            "FULL",
            List.of(
                createRequest("7203", "Toyota"),
                createRequest("6758", "Sony Updated"),
                createRequest("***", "Invalid")
            )
        );

        when(stockRepository.findByStockCode("7203")).thenReturn(Optional.empty());
        when(stockRepository.findByStockCode("6758")).thenReturn(Optional.of(existing));
        when(stockRepository.insert(any(Stock.class))).thenReturn(1);
        when(stockRepository.update(any(Stock.class))).thenReturn(1);

        var response = stockService.importStocks(request);

        assertEquals(3, response.total());
        assertEquals(2, response.success());
        assertEquals(1, response.created());
        assertEquals(1, response.updated());
        assertEquals(1, response.failed());

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        verify(stockRepository).markDeletedExceptCodes(captor.capture());
        assertEquals(List.of("7203", "6758"), captor.getValue());
    }

    @Test
    void getRealtimeChangeShouldCalculateAmountAndPercent() {
        StockRealtimeChange realtime = new StockRealtimeChange();
        realtime.setStockCode("7203");
        realtime.setStockName("Toyota");
        realtime.setCurrentPriceText("1,100円");
        realtime.setReferenceDate(LocalDate.parse("2026-02-20"));
        realtime.setReferenceClosePrice(new BigDecimal("1000.00"));

        when(stockRepository.findRealtimeChangeByStockCode("7203")).thenReturn(Optional.of(realtime));

        var response = stockService.getRealtimeChange("7203");

        assertNotNull(response.currentPrice());
        assertEquals(0, response.currentPrice().compareTo(new BigDecimal("1100.00")));
        assertNotNull(response.changeAmount());
        assertEquals(0, response.changeAmount().compareTo(new BigDecimal("100.00")));
        assertNotNull(response.changePercent());
        assertEquals(0, response.changePercent().compareTo(new BigDecimal("10.00")));
    }

    @Test
    void addFavoriteShouldInsertAndReturnFavorite() {
        Stock stock = buildStock(1L, "7203", "Toyota", "0");
        StockFavorite favorite = buildFavorite(10L, "7203");

        when(stockRepository.findByStockCode("7203")).thenReturn(Optional.of(stock));
        when(stockRepository.findFavoriteByStockCode("7203")).thenReturn(Optional.empty(), Optional.of(favorite));
        when(stockRepository.insertFavorite("7203")).thenReturn(1);

        var response = stockService.addFavorite(new StockFavoriteCreateRequest("7203"));

        assertEquals(10L, response.id());
        assertEquals("7203", response.stockCode());
        verify(stockRepository).insertFavorite("7203");
    }

    @Test
    void removeFavoriteShouldDeleteExistingFavorite() {
        StockFavorite favorite = buildFavorite(10L, "7203");

        when(stockRepository.findFavoriteById(10L)).thenReturn(Optional.of(favorite));
        when(stockRepository.deleteFavoriteById(10L)).thenReturn(1);

        stockService.removeFavorite(10L);

        verify(stockRepository).deleteFavoriteById(10L);
    }

    private StockCreateRequest createRequest(String stockCode, String stockName) {
        return new StockCreateRequest(
            stockCode,
            stockName,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );
    }

    private Stock buildStock(Long id, String stockCode, String stockName, String delFlg) {
        Stock stock = new Stock();
        stock.setId(id);
        stock.setStockCode(stockCode);
        stock.setStockName(stockName);
        stock.setDelFlg(delFlg);
        return stock;
    }

    private StockFavorite buildFavorite(Long id, String stockCode) {
        StockFavorite favorite = new StockFavorite();
        favorite.setId(id);
        favorite.setStockCode(stockCode);
        favorite.setStockName("Toyota");
        favorite.setTypeName("AUTO");
        favorite.setMarket("TSE");
        favorite.setStockPrice("1100");
        favorite.setCreatedAt(LocalDateTime.parse("2026-02-22T10:00:00"));
        return favorite;
    }
}

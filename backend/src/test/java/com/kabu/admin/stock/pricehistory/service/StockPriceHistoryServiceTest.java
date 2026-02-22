package com.kabu.admin.stock.pricehistory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.kabu.admin.stock.model.Stock;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryCreateRequest;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryImportRequest;
import com.kabu.admin.stock.pricehistory.exception.StockPriceHistoryConflictException;
import com.kabu.admin.stock.pricehistory.model.StockPriceHistory;
import com.kabu.admin.stock.pricehistory.repository.StockPriceHistoryRepository;
import com.kabu.admin.stock.pricehistory.service.impl.StockPriceHistoryServiceImpl;
import com.kabu.admin.stock.repository.StockRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StockPriceHistoryServiceTest {

    @Mock
    private StockPriceHistoryRepository stockPriceHistoryRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockPriceHistoryServiceImpl stockPriceHistoryService;

    @Test
    void createShouldPersistAndReturnCreatedRecord() {
        StockPriceHistory stored = buildHistory(1L, "7203", LocalDate.parse("2026-02-20"));
        Stock stock = buildStock("7203");

        when(stockRepository.findByStockCode("7203")).thenReturn(Optional.of(stock));
        when(stockPriceHistoryRepository.findByStockCodeAndTransDate("7203", LocalDate.parse("2026-02-20")))
            .thenReturn(Optional.empty());
        when(stockPriceHistoryRepository.insert(any(StockPriceHistory.class))).thenAnswer(invocation -> {
            StockPriceHistory item = invocation.getArgument(0);
            item.setId(1L);
            return 1;
        });
        when(stockPriceHistoryRepository.findById(1L)).thenReturn(Optional.of(stored));

        var response = stockPriceHistoryService.create(createRequest("7203", "2026-02-20"));

        assertEquals(1L, response.id());
        assertEquals("7203", response.stockCode());
        assertEquals(LocalDate.parse("2026-02-20"), response.transDate());
    }

    @Test
    void createShouldFailWhenDuplicateExists() {
        Stock stock = buildStock("7203");
        when(stockRepository.findByStockCode("7203")).thenReturn(Optional.of(stock));
        when(stockPriceHistoryRepository.findByStockCodeAndTransDate("7203", LocalDate.parse("2026-02-20")))
            .thenReturn(Optional.of(buildHistory(1L, "7203", LocalDate.parse("2026-02-20"))));

        assertThrows(
            StockPriceHistoryConflictException.class,
            () -> stockPriceHistoryService.create(createRequest("7203", "2026-02-20"))
        );
    }

    @Test
    void createShouldFailWhenPriceRangeInvalid() {
        StockPriceHistoryCreateRequest request = new StockPriceHistoryCreateRequest(
            "7203",
            "2026-02-20",
            3500,
            3600,
            3550,
            3400,
            3520,
            3520,
            20,
            new BigDecimal("0.57"),
            1200000,
            null
        );

        assertThrows(IllegalArgumentException.class, () -> stockPriceHistoryService.create(request));
    }

    @Test
    void importShouldHandleSkipDuplicateMode() {
        Stock stock7203 = buildStock("7203");
        Stock stock6758 = buildStock("6758");
        when(stockRepository.findByStockCode("7203")).thenReturn(Optional.of(stock7203));
        when(stockRepository.findByStockCode("6758")).thenReturn(Optional.of(stock6758));
        when(stockRepository.findByStockCode("0000")).thenReturn(Optional.empty());

        when(stockPriceHistoryRepository.findByStockCodeAndTransDate("7203", LocalDate.parse("2026-02-20")))
            .thenReturn(Optional.of(buildHistory(2L, "7203", LocalDate.parse("2026-02-20"))));
        when(stockPriceHistoryRepository.findByStockCodeAndTransDate("6758", LocalDate.parse("2026-02-20")))
            .thenReturn(Optional.empty());
        when(stockPriceHistoryRepository.insert(any(StockPriceHistory.class))).thenReturn(1);

        StockPriceHistoryImportRequest request = new StockPriceHistoryImportRequest(
            "SKIP_DUPLICATE",
            List.of(
                createRequest("7203", "2026-02-20"),
                createRequest("6758", "2026-02-20"),
                createRequest("0000", "2026-02-20")
            )
        );

        var response = stockPriceHistoryService.importData(request);

        assertEquals(3, response.total());
        assertEquals(2, response.success());
        assertEquals(1, response.created());
        assertEquals(0, response.updated());
        assertEquals(1, response.skipped());
        assertEquals(1, response.failed());
    }

    private StockPriceHistoryCreateRequest createRequest(String stockCode, String transDate) {
        return new StockPriceHistoryCreateRequest(
            stockCode,
            transDate,
            3500,
            3510,
            3550,
            3490,
            3540,
            3540,
            40,
            new BigDecimal("1.14"),
            1000000,
            null
        );
    }

    private StockPriceHistory buildHistory(Long id, String stockCode, LocalDate transDate) {
        StockPriceHistory item = new StockPriceHistory();
        item.setId(id);
        item.setStockCode(stockCode);
        item.setTransDate(transDate);
        item.setBeforeDayPrice(3500);
        item.setOpenPrice(3510);
        item.setHighPrice(3550);
        item.setLowPrice(3490);
        item.setClosePrice(3540);
        item.setAdjustedClosePrice(3540);
        item.setBeforeDayDiff(40);
        item.setBeforeDayDiffPercent(new BigDecimal("1.14"));
        item.setVolume(1000000);
        return item;
    }

    private Stock buildStock(String stockCode) {
        Stock stock = new Stock();
        stock.setId(1L);
        stock.setStockCode(stockCode);
        stock.setStockName("Test");
        return stock;
    }
}

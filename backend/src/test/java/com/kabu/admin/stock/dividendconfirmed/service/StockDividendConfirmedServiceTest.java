package com.kabu.admin.stock.dividendconfirmed.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedCreateRequest;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedImportRequest;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedUpdateRequest;
import com.kabu.admin.stock.dividendconfirmed.exception.StockDividendConfirmedConflictException;
import com.kabu.admin.stock.dividendconfirmed.model.StockDividendConfirmed;
import com.kabu.admin.stock.dividendconfirmed.model.StockDividendConfirmedRightsLastDayStats;
import com.kabu.admin.stock.dividendconfirmed.repository.StockDividendConfirmedRepository;
import com.kabu.admin.stock.dividendconfirmed.service.impl.StockDividendConfirmedServiceImpl;
import com.kabu.admin.stock.model.Stock;
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
class StockDividendConfirmedServiceTest {

    @Mock
    private StockDividendConfirmedRepository stockDividendConfirmedRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockDividendConfirmedServiceImpl stockDividendConfirmedService;

    @Test
    void createShouldPersistAndReturnCreatedRecord() {
        Stock stock = buildStock("7203");
        StockDividendConfirmed stored = buildItem(1L, "7203", LocalDate.parse("2026-03-31"), "0");

        when(stockRepository.findByStockCode("7203")).thenReturn(Optional.of(stock));
        when(stockDividendConfirmedRepository.findByStockCodeAndRecordDate("7203", LocalDate.parse("2026-03-31")))
            .thenReturn(Optional.empty());
        when(stockDividendConfirmedRepository.insert(any(StockDividendConfirmed.class))).thenAnswer(invocation -> {
            StockDividendConfirmed item = invocation.getArgument(0);
            item.setId(1L);
            return 1;
        });
        when(stockDividendConfirmedRepository.findById(1L)).thenReturn(Optional.of(stored));

        var response = stockDividendConfirmedService.create(createRequest("7203", "2026-03-31"));

        assertEquals(1L, response.id());
        assertEquals("7203", response.stockCode());
        assertEquals(LocalDate.parse("2026-03-31"), response.recordDate());
        assertEquals("0", response.confirmedFlg());
    }

    @Test
    void createShouldFailWhenDuplicateExists() {
        Stock stock = buildStock("7203");
        when(stockRepository.findByStockCode("7203")).thenReturn(Optional.of(stock));
        when(stockDividendConfirmedRepository.findByStockCodeAndRecordDate("7203", LocalDate.parse("2026-03-31")))
            .thenReturn(Optional.of(buildItem(2L, "7203", LocalDate.parse("2026-03-31"), "0")));

        assertThrows(
            StockDividendConfirmedConflictException.class,
            () -> stockDividendConfirmedService.create(createRequest("7203", "2026-03-31"))
        );
    }

    @Test
    void updateShouldFailWhenRecordAlreadyConfirmed() {
        when(stockDividendConfirmedRepository.findById(1L))
            .thenReturn(Optional.of(buildItem(1L, "7203", LocalDate.parse("2026-03-31"), "1")));

        assertThrows(
            IllegalArgumentException.class,
            () -> stockDividendConfirmedService.update(1L, updateRequest("7203", "2026-03-31"))
        );
    }

    @Test
    void confirmShouldUpdateStatusToConfirmed() {
        StockDividendConfirmed before = buildItem(1L, "7203", LocalDate.parse("2026-03-31"), "0");
        StockDividendConfirmed after = buildItem(1L, "7203", LocalDate.parse("2026-03-31"), "1");

        when(stockDividendConfirmedRepository.findById(1L)).thenReturn(Optional.of(before), Optional.of(after));
        when(stockDividendConfirmedRepository.updateConfirmedFlag(1L, "1")).thenReturn(1);

        var response = stockDividendConfirmedService.confirm(1L);

        assertEquals("1", response.confirmedFlg());
    }

    @Test
    void importShouldHandleSkipDuplicateMode() {
        Stock stock7203 = buildStock("7203");
        Stock stock6758 = buildStock("6758");

        when(stockRepository.findByStockCode("7203")).thenReturn(Optional.of(stock7203));
        when(stockRepository.findByStockCode("6758")).thenReturn(Optional.of(stock6758));

        when(stockDividendConfirmedRepository.findByStockCodeAndRecordDate("7203", LocalDate.parse("2026-03-31")))
            .thenReturn(Optional.of(buildItem(10L, "7203", LocalDate.parse("2026-03-31"), "0")));
        when(stockDividendConfirmedRepository.findByStockCodeAndRecordDate("6758", LocalDate.parse("2026-03-31")))
            .thenReturn(Optional.empty());
        when(stockDividendConfirmedRepository.insert(any(StockDividendConfirmed.class))).thenReturn(1);

        StockDividendConfirmedImportRequest request = new StockDividendConfirmedImportRequest(
            "SKIP_DUPLICATE",
            List.of(
                createRequest("7203", "2026-03-31"),
                createRequest("6758", "2026-03-31")
            )
        );

        var response = stockDividendConfirmedService.importData(request);

        assertEquals(2, response.total());
        assertEquals(2, response.success());
        assertEquals(1, response.created());
        assertEquals(0, response.updated());
        assertEquals(1, response.skipped());
        assertEquals(0, response.failed());
    }

    @Test
    void rightsLastDayStatsShouldAggregateByRightsLastDay() {
        StockDividendConfirmedRightsLastDayStats stats = new StockDividendConfirmedRightsLastDayStats();
        stats.setRightsLastDay(LocalDate.parse("2026-03-27"));
        stats.setTotalCount(3L);
        stats.setConfirmedCount(1L);
        stats.setAvgDividendAmount(new BigDecimal("76.50"));
        stats.setAvgDividendYield(new BigDecimal("2.12"));

        when(stockDividendConfirmedRepository.aggregateByRightsLastDay()).thenReturn(List.of(stats));

        var response = stockDividendConfirmedService.rightsLastDayStats();

        assertEquals(1, response.totalDays());
        assertEquals(1, response.items().size());
        assertEquals(LocalDate.parse("2026-03-27"), response.items().get(0).rightsLastDay());
        assertEquals(3L, response.items().get(0).totalCount());
        assertEquals(1L, response.items().get(0).confirmedCount());
        verify(stockDividendConfirmedRepository).aggregateByRightsLastDay();
    }

    private StockDividendConfirmedCreateRequest createRequest(String stockCode, String recordDate) {
        return new StockDividendConfirmedCreateRequest(
            stockCode,
            new BigDecimal("90.00"),
            new BigDecimal("2.56"),
            "2026-03-27",
            "2026-03-30",
            recordDate,
            "0"
        );
    }

    private StockDividendConfirmedUpdateRequest updateRequest(String stockCode, String recordDate) {
        return new StockDividendConfirmedUpdateRequest(
            stockCode,
            new BigDecimal("90.00"),
            new BigDecimal("2.56"),
            "2026-03-27",
            "2026-03-30",
            recordDate,
            "0"
        );
    }

    private StockDividendConfirmed buildItem(Long id, String stockCode, LocalDate recordDate, String confirmedFlg) {
        StockDividendConfirmed item = new StockDividendConfirmed();
        item.setId(id);
        item.setStockCode(stockCode);
        item.setDividendAmount(new BigDecimal("90.00"));
        item.setDividendYield(new BigDecimal("2.56"));
        item.setRightsLastDay(LocalDate.parse("2026-03-27"));
        item.setExDividendDate(LocalDate.parse("2026-03-30"));
        item.setRecordDate(recordDate);
        item.setConfirmedFlg(confirmedFlg);
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

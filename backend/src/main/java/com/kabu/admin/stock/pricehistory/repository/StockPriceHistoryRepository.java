package com.kabu.admin.stock.pricehistory.repository;

import com.kabu.admin.stock.pricehistory.model.StockPriceHistory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockPriceHistoryRepository {

    List<StockPriceHistory> findByCriteria(
        String stockCode,
        String typeName,
        LocalDate dateFrom,
        LocalDate dateTo,
        String sortBy,
        String sortDirection,
        int limit,
        int offset
    );

    long countByCriteria(String stockCode, String typeName, LocalDate dateFrom, LocalDate dateTo);

    Optional<StockPriceHistory> findById(Long id);

    Optional<StockPriceHistory> findByStockCodeAndTransDate(String stockCode, LocalDate transDate);

    int insert(StockPriceHistory stockPriceHistory);

    int update(StockPriceHistory stockPriceHistory);

    int deleteById(Long id);
}

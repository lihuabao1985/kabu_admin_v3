package com.kabu.admin.stock.dividendconfirmed.repository;

import com.kabu.admin.stock.dividendconfirmed.model.StockDividendConfirmed;
import com.kabu.admin.stock.dividendconfirmed.model.StockDividendConfirmedRightsLastDayStats;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockDividendConfirmedRepository {

    List<StockDividendConfirmed> findByCriteria(
        String stockCode,
        String industryCode,
        LocalDate rightsLastDay,
        String sortBy,
        String sortDirection,
        int limit,
        int offset
    );

    long countByCriteria(
        String stockCode,
        String industryCode,
        LocalDate rightsLastDay
    );

    Optional<StockDividendConfirmed> findById(Long id);

    Optional<StockDividendConfirmed> findByStockCodeAndRecordDate(String stockCode, LocalDate recordDate);

    int insert(StockDividendConfirmed stockDividendConfirmed);

    int update(StockDividendConfirmed stockDividendConfirmed);

    int updateConfirmedFlag(Long id, String confirmedFlg);

    int deleteById(Long id);

    List<StockDividendConfirmedRightsLastDayStats> aggregateByRightsLastDay();
}

package com.kabu.admin.stock.dividendconfirmed.mapper;

import com.kabu.admin.stock.dividendconfirmed.model.StockDividendConfirmed;
import com.kabu.admin.stock.dividendconfirmed.model.StockDividendConfirmedRightsLastDayStats;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StockDividendConfirmedMapper {

    List<StockDividendConfirmed> findByCriteria(
        @Param("stockCode") String stockCode,
        @Param("industryCode") String industryCode,
        @Param("rightsLastDay") LocalDate rightsLastDay,
        @Param("sortBy") String sortBy,
        @Param("sortDirection") String sortDirection,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    long countByCriteria(
        @Param("stockCode") String stockCode,
        @Param("industryCode") String industryCode,
        @Param("rightsLastDay") LocalDate rightsLastDay
    );

    StockDividendConfirmed findById(@Param("id") Long id);

    StockDividendConfirmed findByStockCodeAndRecordDate(
        @Param("stockCode") String stockCode,
        @Param("recordDate") LocalDate recordDate
    );

    int insert(StockDividendConfirmed stockDividendConfirmed);

    int update(StockDividendConfirmed stockDividendConfirmed);

    int updateConfirmedFlag(@Param("id") Long id, @Param("confirmedFlg") String confirmedFlg);

    int deleteById(@Param("id") Long id);

    List<StockDividendConfirmedRightsLastDayStats> aggregateByRightsLastDay();
}

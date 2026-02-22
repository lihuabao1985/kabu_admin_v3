package com.kabu.admin.stock.pricehistory.mapper;

import com.kabu.admin.stock.pricehistory.model.StockPriceHistory;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StockPriceHistoryMapper {

    List<StockPriceHistory> findByCriteria(
        @Param("stockCode") String stockCode,
        @Param("typeName") String typeName,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo,
        @Param("sortBy") String sortBy,
        @Param("sortDirection") String sortDirection,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    long countByCriteria(
        @Param("stockCode") String stockCode,
        @Param("typeName") String typeName,
        @Param("dateFrom") LocalDate dateFrom,
        @Param("dateTo") LocalDate dateTo
    );

    StockPriceHistory findById(@Param("id") Long id);

    StockPriceHistory findByStockCodeAndTransDate(
        @Param("stockCode") String stockCode,
        @Param("transDate") LocalDate transDate
    );

    int insert(StockPriceHistory stockPriceHistory);

    int update(StockPriceHistory stockPriceHistory);

    int deleteById(@Param("id") Long id);
}

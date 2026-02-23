package com.kabu.admin.stock.mapper;

import com.kabu.admin.stock.model.IndustryCodeOption;
import com.kabu.admin.stock.model.Stock;
import com.kabu.admin.stock.model.StockFavorite;
import com.kabu.admin.stock.model.StockOption;
import com.kabu.admin.stock.model.StockPriceChangeRanking;
import com.kabu.admin.stock.model.StockRealtimeChange;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StockMapper {

    List<Stock> findByCriteria(
        @Param("stockCode") String stockCode,
        @Param("stockName") String stockName,
        @Param("typeName") String typeName,
        @Param("market") String market,
        @Param("stockPriceFrom") String stockPriceFrom,
        @Param("stockPriceTo") String stockPriceTo,
        @Param("freeWord") String freeWord,
        @Param("sortBy") String sortBy,
        @Param("sortDirection") String sortDirection,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    long countByCriteria(
        @Param("stockCode") String stockCode,
        @Param("stockName") String stockName,
        @Param("typeName") String typeName,
        @Param("market") String market,
        @Param("stockPriceFrom") String stockPriceFrom,
        @Param("stockPriceTo") String stockPriceTo,
        @Param("freeWord") String freeWord
    );

    Stock findById(@Param("id") Long id);

    Stock findByStockCode(@Param("stockCode") String stockCode);

    List<IndustryCodeOption> findIndustryCodeOptions();

    List<StockOption> findOptions(
        @Param("keyword") String keyword,
        @Param("market") String market,
        @Param("limit") int limit
    );

    List<StockPriceChangeRanking> findPriceChangeRanking(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("changeType") String changeType,
        @Param("threshold") BigDecimal threshold,
        @Param("limit") int limit,
        @Param("offset") int offset
    );

    long countPriceChangeRanking(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("changeType") String changeType,
        @Param("threshold") BigDecimal threshold
    );

    StockRealtimeChange findRealtimeChangeByStockCode(@Param("stockCode") String stockCode);

    List<StockFavorite> findFavorites();

    long countFavorites();

    StockFavorite findFavoriteById(@Param("id") Long id);

    StockFavorite findFavoriteByStockCode(@Param("stockCode") String stockCode);

    int insertFavorite(@Param("stockCode") String stockCode);

    int deleteFavoriteById(@Param("id") Long id);

    int insert(Stock stock);

    int update(Stock stock);

    int updateDeleteFlag(
        @Param("id") Long id,
        @Param("delFlg") String delFlg
    );

    int markDeletedExceptCodes(@Param("stockCodes") List<String> stockCodes);
}

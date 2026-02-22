package com.kabu.admin.stock.repository;

import com.kabu.admin.stock.model.Stock;
import com.kabu.admin.stock.model.StockFavorite;
import com.kabu.admin.stock.model.StockOption;
import com.kabu.admin.stock.model.StockPriceChangeRanking;
import com.kabu.admin.stock.model.StockRealtimeChange;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockRepository {

    List<Stock> findByCriteria(
        String stockCode,
        String stockName,
        String typeCode,
        String market,
        String delFlg,
        String sortBy,
        String sortDirection,
        int limit,
        int offset
    );

    long countByCriteria(
        String stockCode,
        String stockName,
        String typeCode,
        String market,
        String delFlg
    );

    Optional<Stock> findById(Long id);

    Optional<Stock> findByStockCode(String stockCode);

    List<StockOption> findOptions(String keyword, String market, int limit);

    List<StockPriceChangeRanking> findPriceChangeRanking(
        LocalDate startDate,
        LocalDate endDate,
        String changeType,
        BigDecimal threshold,
        int limit,
        int offset
    );

    long countPriceChangeRanking(
        LocalDate startDate,
        LocalDate endDate,
        String changeType,
        BigDecimal threshold
    );

    Optional<StockRealtimeChange> findRealtimeChangeByStockCode(String stockCode);

    List<StockFavorite> findFavorites();

    long countFavorites();

    Optional<StockFavorite> findFavoriteById(Long id);

    Optional<StockFavorite> findFavoriteByStockCode(String stockCode);

    int insertFavorite(String stockCode);

    int deleteFavoriteById(Long id);

    int insert(Stock stock);

    int update(Stock stock);

    int updateDeleteFlag(Long id, String delFlg);

    int markDeletedExceptCodes(List<String> stockCodes);
}

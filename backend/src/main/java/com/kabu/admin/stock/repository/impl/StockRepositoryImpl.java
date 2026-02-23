package com.kabu.admin.stock.repository.impl;

import com.kabu.admin.stock.mapper.StockMapper;
import com.kabu.admin.stock.model.IndustryCodeOption;
import com.kabu.admin.stock.model.Stock;
import com.kabu.admin.stock.model.StockFavorite;
import com.kabu.admin.stock.model.StockOption;
import com.kabu.admin.stock.model.StockPriceChangeRanking;
import com.kabu.admin.stock.model.StockRealtimeChange;
import com.kabu.admin.stock.repository.StockRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class StockRepositoryImpl implements StockRepository {

    private final StockMapper stockMapper;

    public StockRepositoryImpl(StockMapper stockMapper) {
        this.stockMapper = stockMapper;
    }

    @Override
    public List<Stock> findByCriteria(
        String stockCode,
        String stockName,
        String typeName,
        String market,
        String stockPriceFrom,
        String stockPriceTo,
        String freeWord,
        String sortBy,
        String sortDirection,
        int limit,
        int offset
    ) {
        return stockMapper.findByCriteria(
            stockCode,
            stockName,
            typeName,
            market,
            stockPriceFrom,
            stockPriceTo,
            freeWord,
            sortBy,
            sortDirection,
            limit,
            offset
        );
    }

    @Override
    public long countByCriteria(
        String stockCode,
        String stockName,
        String typeName,
        String market,
        String stockPriceFrom,
        String stockPriceTo,
        String freeWord
    ) {
        return stockMapper.countByCriteria(stockCode, stockName, typeName, market, stockPriceFrom, stockPriceTo, freeWord);
    }

    @Override
    public Optional<Stock> findById(Long id) {
        return Optional.ofNullable(stockMapper.findById(id));
    }

    @Override
    public Optional<Stock> findByStockCode(String stockCode) {
        return Optional.ofNullable(stockMapper.findByStockCode(stockCode));
    }

    @Override
    public List<IndustryCodeOption> findIndustryCodeOptions() {
        return stockMapper.findIndustryCodeOptions();
    }

    @Override
    public List<StockOption> findOptions(String keyword, String market, int limit) {
        return stockMapper.findOptions(keyword, market, limit);
    }

    @Override
    public List<StockPriceChangeRanking> findPriceChangeRanking(
        LocalDate startDate,
        LocalDate endDate,
        String changeType,
        BigDecimal threshold,
        int limit,
        int offset
    ) {
        return stockMapper.findPriceChangeRanking(startDate, endDate, changeType, threshold, limit, offset);
    }

    @Override
    public long countPriceChangeRanking(
        LocalDate startDate,
        LocalDate endDate,
        String changeType,
        BigDecimal threshold
    ) {
        return stockMapper.countPriceChangeRanking(startDate, endDate, changeType, threshold);
    }

    @Override
    public Optional<StockRealtimeChange> findRealtimeChangeByStockCode(String stockCode) {
        return Optional.ofNullable(stockMapper.findRealtimeChangeByStockCode(stockCode));
    }

    @Override
    public List<StockFavorite> findFavorites() {
        return stockMapper.findFavorites();
    }

    @Override
    public long countFavorites() {
        return stockMapper.countFavorites();
    }

    @Override
    public Optional<StockFavorite> findFavoriteById(Long id) {
        return Optional.ofNullable(stockMapper.findFavoriteById(id));
    }

    @Override
    public Optional<StockFavorite> findFavoriteByStockCode(String stockCode) {
        return Optional.ofNullable(stockMapper.findFavoriteByStockCode(stockCode));
    }

    @Override
    public int insertFavorite(String stockCode) {
        return stockMapper.insertFavorite(stockCode);
    }

    @Override
    public int deleteFavoriteById(Long id) {
        return stockMapper.deleteFavoriteById(id);
    }

    @Override
    public int insert(Stock stock) {
        return stockMapper.insert(stock);
    }

    @Override
    public int update(Stock stock) {
        return stockMapper.update(stock);
    }

    @Override
    public int updateDeleteFlag(Long id, String delFlg) {
        return stockMapper.updateDeleteFlag(id, delFlg);
    }

    @Override
    public int markDeletedExceptCodes(List<String> stockCodes) {
        return stockMapper.markDeletedExceptCodes(stockCodes);
    }
}

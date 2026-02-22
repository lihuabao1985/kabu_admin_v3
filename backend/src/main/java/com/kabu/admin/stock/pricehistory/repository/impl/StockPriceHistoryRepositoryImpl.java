package com.kabu.admin.stock.pricehistory.repository.impl;

import com.kabu.admin.stock.pricehistory.mapper.StockPriceHistoryMapper;
import com.kabu.admin.stock.pricehistory.model.StockPriceHistory;
import com.kabu.admin.stock.pricehistory.repository.StockPriceHistoryRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class StockPriceHistoryRepositoryImpl implements StockPriceHistoryRepository {

    private final StockPriceHistoryMapper stockPriceHistoryMapper;

    public StockPriceHistoryRepositoryImpl(StockPriceHistoryMapper stockPriceHistoryMapper) {
        this.stockPriceHistoryMapper = stockPriceHistoryMapper;
    }

    @Override
    public List<StockPriceHistory> findByCriteria(
        String stockCode,
        LocalDate dateFrom,
        LocalDate dateTo,
        String sortBy,
        String sortDirection,
        int limit,
        int offset
    ) {
        return stockPriceHistoryMapper.findByCriteria(stockCode, dateFrom, dateTo, sortBy, sortDirection, limit, offset);
    }

    @Override
    public long countByCriteria(String stockCode, LocalDate dateFrom, LocalDate dateTo) {
        return stockPriceHistoryMapper.countByCriteria(stockCode, dateFrom, dateTo);
    }

    @Override
    public Optional<StockPriceHistory> findById(Long id) {
        return Optional.ofNullable(stockPriceHistoryMapper.findById(id));
    }

    @Override
    public Optional<StockPriceHistory> findByStockCodeAndTransDate(String stockCode, LocalDate transDate) {
        return Optional.ofNullable(stockPriceHistoryMapper.findByStockCodeAndTransDate(stockCode, transDate));
    }

    @Override
    public int insert(StockPriceHistory stockPriceHistory) {
        return stockPriceHistoryMapper.insert(stockPriceHistory);
    }

    @Override
    public int update(StockPriceHistory stockPriceHistory) {
        return stockPriceHistoryMapper.update(stockPriceHistory);
    }

    @Override
    public int deleteById(Long id) {
        return stockPriceHistoryMapper.deleteById(id);
    }
}

package com.kabu.admin.stock.dividendconfirmed.repository.impl;

import com.kabu.admin.stock.dividendconfirmed.mapper.StockDividendConfirmedMapper;
import com.kabu.admin.stock.dividendconfirmed.model.StockDividendConfirmed;
import com.kabu.admin.stock.dividendconfirmed.model.StockDividendConfirmedRightsLastDayStats;
import com.kabu.admin.stock.dividendconfirmed.repository.StockDividendConfirmedRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class StockDividendConfirmedRepositoryImpl implements StockDividendConfirmedRepository {

    private final StockDividendConfirmedMapper stockDividendConfirmedMapper;

    public StockDividendConfirmedRepositoryImpl(StockDividendConfirmedMapper stockDividendConfirmedMapper) {
        this.stockDividendConfirmedMapper = stockDividendConfirmedMapper;
    }

    @Override
    public List<StockDividendConfirmed> findByCriteria(
        String stockCode,
        String industryCode,
        LocalDate rightsLastDay,
        String sortBy,
        String sortDirection,
        int limit,
        int offset
    ) {
        return stockDividendConfirmedMapper.findByCriteria(
            stockCode,
            industryCode,
            rightsLastDay,
            sortBy,
            sortDirection,
            limit,
            offset
        );
    }

    @Override
    public long countByCriteria(
        String stockCode,
        String industryCode,
        LocalDate rightsLastDay
    ) {
        return stockDividendConfirmedMapper.countByCriteria(
            stockCode,
            industryCode,
            rightsLastDay
        );
    }

    @Override
    public Optional<StockDividendConfirmed> findById(Long id) {
        return Optional.ofNullable(stockDividendConfirmedMapper.findById(id));
    }

    @Override
    public Optional<StockDividendConfirmed> findByStockCodeAndRecordDate(String stockCode, LocalDate recordDate) {
        return Optional.ofNullable(stockDividendConfirmedMapper.findByStockCodeAndRecordDate(stockCode, recordDate));
    }

    @Override
    public int insert(StockDividendConfirmed stockDividendConfirmed) {
        return stockDividendConfirmedMapper.insert(stockDividendConfirmed);
    }

    @Override
    public int update(StockDividendConfirmed stockDividendConfirmed) {
        return stockDividendConfirmedMapper.update(stockDividendConfirmed);
    }

    @Override
    public int updateConfirmedFlag(Long id, String confirmedFlg) {
        return stockDividendConfirmedMapper.updateConfirmedFlag(id, confirmedFlg);
    }

    @Override
    public int deleteById(Long id) {
        return stockDividendConfirmedMapper.deleteById(id);
    }

    @Override
    public List<StockDividendConfirmedRightsLastDayStats> aggregateByRightsLastDay() {
        return stockDividendConfirmedMapper.aggregateByRightsLastDay();
    }
}

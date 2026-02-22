package com.kabu.admin.stock.dividendconfirmed.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StockDividendConfirmedRightsLastDayStats {
    private LocalDate rightsLastDay;
    private Long totalCount;
    private Long confirmedCount;
    private BigDecimal avgDividendAmount;
    private BigDecimal avgDividendYield;

    public LocalDate getRightsLastDay() {
        return rightsLastDay;
    }

    public void setRightsLastDay(LocalDate rightsLastDay) {
        this.rightsLastDay = rightsLastDay;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getConfirmedCount() {
        return confirmedCount;
    }

    public void setConfirmedCount(Long confirmedCount) {
        this.confirmedCount = confirmedCount;
    }

    public BigDecimal getAvgDividendAmount() {
        return avgDividendAmount;
    }

    public void setAvgDividendAmount(BigDecimal avgDividendAmount) {
        this.avgDividendAmount = avgDividendAmount;
    }

    public BigDecimal getAvgDividendYield() {
        return avgDividendYield;
    }

    public void setAvgDividendYield(BigDecimal avgDividendYield) {
        this.avgDividendYield = avgDividendYield;
    }
}

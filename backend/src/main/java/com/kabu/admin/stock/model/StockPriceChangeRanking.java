package com.kabu.admin.stock.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StockPriceChangeRanking {
    private String stockCode;
    private String stockName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal startClosePrice;
    private BigDecimal endClosePrice;
    private BigDecimal changeAmount;
    private BigDecimal changePercent;

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getStartClosePrice() {
        return startClosePrice;
    }

    public void setStartClosePrice(BigDecimal startClosePrice) {
        this.startClosePrice = startClosePrice;
    }

    public BigDecimal getEndClosePrice() {
        return endClosePrice;
    }

    public void setEndClosePrice(BigDecimal endClosePrice) {
        this.endClosePrice = endClosePrice;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }

    public BigDecimal getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(BigDecimal changePercent) {
        this.changePercent = changePercent;
    }
}

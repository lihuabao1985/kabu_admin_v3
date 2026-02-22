package com.kabu.admin.stock.dividendconfirmed.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StockDividendConfirmed {
    private Long id;
    private String stockCode;
    private String stockName;
    private String typeName;
    private String stockPrice;
    private BigDecimal dividendAmount;
    private BigDecimal dividendYield;
    private LocalDate rightsLastDay;
    private LocalDate exDividendDate;
    private LocalDate recordDate;
    private String confirmedFlg;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(String stockPrice) {
        this.stockPrice = stockPrice;
    }

    public BigDecimal getDividendAmount() {
        return dividendAmount;
    }

    public void setDividendAmount(BigDecimal dividendAmount) {
        this.dividendAmount = dividendAmount;
    }

    public BigDecimal getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(BigDecimal dividendYield) {
        this.dividendYield = dividendYield;
    }

    public LocalDate getRightsLastDay() {
        return rightsLastDay;
    }

    public void setRightsLastDay(LocalDate rightsLastDay) {
        this.rightsLastDay = rightsLastDay;
    }

    public LocalDate getExDividendDate() {
        return exDividendDate;
    }

    public void setExDividendDate(LocalDate exDividendDate) {
        this.exDividendDate = exDividendDate;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }

    public String getConfirmedFlg() {
        return confirmedFlg;
    }

    public void setConfirmedFlg(String confirmedFlg) {
        this.confirmedFlg = confirmedFlg;
    }
}

package com.kabu.admin.stock.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StockRealtimeChange {
    private String stockCode;
    private String stockName;
    private String currentPriceText;
    private LocalDate referenceDate;
    private BigDecimal referenceClosePrice;

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

    public String getCurrentPriceText() {
        return currentPriceText;
    }

    public void setCurrentPriceText(String currentPriceText) {
        this.currentPriceText = currentPriceText;
    }

    public LocalDate getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(LocalDate referenceDate) {
        this.referenceDate = referenceDate;
    }

    public BigDecimal getReferenceClosePrice() {
        return referenceClosePrice;
    }

    public void setReferenceClosePrice(BigDecimal referenceClosePrice) {
        this.referenceClosePrice = referenceClosePrice;
    }
}

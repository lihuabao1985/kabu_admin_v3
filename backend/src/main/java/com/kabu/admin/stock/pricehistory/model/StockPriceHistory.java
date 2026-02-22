package com.kabu.admin.stock.pricehistory.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StockPriceHistory {
    private Long id;
    private String stockCode;
    private LocalDate transDate;
    private Integer beforeDayPrice;
    private Integer openPrice;
    private Integer highPrice;
    private Integer lowPrice;
    private Integer closePrice;
    private Integer adjustedClosePrice;
    private Integer beforeDayDiff;
    private BigDecimal beforeDayDiffPercent;
    private Integer volume;
    private String remark;

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

    public LocalDate getTransDate() {
        return transDate;
    }

    public void setTransDate(LocalDate transDate) {
        this.transDate = transDate;
    }

    public Integer getBeforeDayPrice() {
        return beforeDayPrice;
    }

    public void setBeforeDayPrice(Integer beforeDayPrice) {
        this.beforeDayPrice = beforeDayPrice;
    }

    public Integer getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Integer openPrice) {
        this.openPrice = openPrice;
    }

    public Integer getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Integer highPrice) {
        this.highPrice = highPrice;
    }

    public Integer getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Integer lowPrice) {
        this.lowPrice = lowPrice;
    }

    public Integer getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Integer closePrice) {
        this.closePrice = closePrice;
    }

    public Integer getAdjustedClosePrice() {
        return adjustedClosePrice;
    }

    public void setAdjustedClosePrice(Integer adjustedClosePrice) {
        this.adjustedClosePrice = adjustedClosePrice;
    }

    public Integer getBeforeDayDiff() {
        return beforeDayDiff;
    }

    public void setBeforeDayDiff(Integer beforeDayDiff) {
        this.beforeDayDiff = beforeDayDiff;
    }

    public BigDecimal getBeforeDayDiffPercent() {
        return beforeDayDiffPercent;
    }

    public void setBeforeDayDiffPercent(BigDecimal beforeDayDiffPercent) {
        this.beforeDayDiffPercent = beforeDayDiffPercent;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}

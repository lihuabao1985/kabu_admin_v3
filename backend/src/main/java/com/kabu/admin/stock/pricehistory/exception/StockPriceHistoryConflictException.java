package com.kabu.admin.stock.pricehistory.exception;

public class StockPriceHistoryConflictException extends RuntimeException {

    public StockPriceHistoryConflictException(String stockCode, String transDate) {
        super("股票历史行情已存在: " + stockCode + "@" + transDate);
    }
}

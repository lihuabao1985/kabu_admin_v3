package com.kabu.admin.stock.exception;

public class StockNotFoundException extends RuntimeException {

    public StockNotFoundException(Long id) {
        super("股票不存在: " + id);
    }

    public StockNotFoundException(String stockCode) {
        super("股票不存在: " + stockCode);
    }
}

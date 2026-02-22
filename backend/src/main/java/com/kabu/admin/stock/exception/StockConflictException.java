package com.kabu.admin.stock.exception;

public class StockConflictException extends RuntimeException {

    public StockConflictException(String stockCode) {
        super("股票代码已存在: " + stockCode);
    }
}

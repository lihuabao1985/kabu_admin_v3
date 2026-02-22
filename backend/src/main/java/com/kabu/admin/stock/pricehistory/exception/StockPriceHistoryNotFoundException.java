package com.kabu.admin.stock.pricehistory.exception;

public class StockPriceHistoryNotFoundException extends RuntimeException {

    public StockPriceHistoryNotFoundException(Long id) {
        super("股票历史行情不存在: " + id);
    }
}

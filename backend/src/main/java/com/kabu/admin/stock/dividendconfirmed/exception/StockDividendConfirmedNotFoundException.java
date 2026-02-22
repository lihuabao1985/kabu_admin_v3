package com.kabu.admin.stock.dividendconfirmed.exception;

public class StockDividendConfirmedNotFoundException extends RuntimeException {

    public StockDividendConfirmedNotFoundException(Long id) {
        super("股票配当确权记录不存在: " + id);
    }
}

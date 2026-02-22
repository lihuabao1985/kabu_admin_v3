package com.kabu.admin.stock.dividendconfirmed.exception;

public class StockDividendConfirmedConflictException extends RuntimeException {

    public StockDividendConfirmedConflictException(String stockCode, String recordDate) {
        super("配当确权记录冲突: " + stockCode + "@" + recordDate);
    }
}

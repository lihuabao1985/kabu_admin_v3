package com.kabu.admin.stock.exception;

public class StockFavoriteConflictException extends RuntimeException {

    public StockFavoriteConflictException(String stockCode) {
        super("股票已在收藏列表中: " + stockCode);
    }
}

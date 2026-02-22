package com.kabu.admin.stock.exception;

public class StockFavoriteNotFoundException extends RuntimeException {

    public StockFavoriteNotFoundException(Long id) {
        super("股票收藏记录不存在: " + id);
    }
}

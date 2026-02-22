package com.kabu.admin.stock.dto;

public record StockQueryRequest(
    String stockCode,
    String stockName,
    String typeCode,
    String market,
    String delFlg,
    Integer page,
    Integer size,
    String sort
) {
}

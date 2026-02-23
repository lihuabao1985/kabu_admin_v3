package com.kabu.admin.stock.dto;

public record StockQueryRequest(
    String stockCode,
    String stockName,
    String typeName,
    String market,
    String stockPriceFrom,
    String stockPriceTo,
    String freeWord,
    Integer page,
    Integer size,
    String sort
) {
}

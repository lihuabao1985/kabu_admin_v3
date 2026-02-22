package com.kabu.admin.stock.pricehistory.dto;

public record StockPriceHistoryQueryRequest(
    String stockCode,
    String typeName,
    String dateFrom,
    String dateTo,
    Integer page,
    Integer size,
    String sort
) {
}

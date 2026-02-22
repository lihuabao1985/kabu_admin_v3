package com.kabu.admin.stock.dividendconfirmed.dto;

public record StockDividendConfirmedQueryRequest(
    String stockCode,
    String industryCode,
    String rightsLastDay,
    Integer page,
    Integer size,
    String sort
) {
}

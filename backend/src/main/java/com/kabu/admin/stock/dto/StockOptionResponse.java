package com.kabu.admin.stock.dto;

public record StockOptionResponse(
    Long id,
    String stockCode,
    String stockName,
    String market
) {
}

package com.kabu.admin.stock.dto;

public record StockImportFailure(
    String stockCode,
    String reason
) {
}

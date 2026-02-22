package com.kabu.admin.stock.dividendconfirmed.dto;

public record StockDividendConfirmedImportFailure(
    String stockCode,
    String recordDate,
    String reason
) {
}

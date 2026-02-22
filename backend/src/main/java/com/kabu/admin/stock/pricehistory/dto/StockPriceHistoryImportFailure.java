package com.kabu.admin.stock.pricehistory.dto;

public record StockPriceHistoryImportFailure(
    String stockCode,
    String transDate,
    String reason
) {
}

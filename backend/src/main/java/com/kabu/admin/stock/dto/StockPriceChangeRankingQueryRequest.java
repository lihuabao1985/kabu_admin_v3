package com.kabu.admin.stock.dto;

public record StockPriceChangeRankingQueryRequest(
    String startDate,
    String endDate,
    String changeType,
    String changePercent,
    Integer page,
    Integer size
) {
}

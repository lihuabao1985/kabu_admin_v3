package com.kabu.admin.stock.dividendconfirmed.dto;

import java.util.List;

public record StockDividendConfirmedRightsLastDayStatsListResponse(
    List<StockDividendConfirmedRightsLastDayStatsResponse> items,
    long totalDays
) {
}

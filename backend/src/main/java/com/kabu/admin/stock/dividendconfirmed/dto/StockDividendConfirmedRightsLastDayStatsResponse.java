package com.kabu.admin.stock.dividendconfirmed.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockDividendConfirmedRightsLastDayStatsResponse(
    LocalDate rightsLastDay,
    long totalCount,
    long confirmedCount,
    BigDecimal avgDividendAmount,
    BigDecimal avgDividendYield
) {
}

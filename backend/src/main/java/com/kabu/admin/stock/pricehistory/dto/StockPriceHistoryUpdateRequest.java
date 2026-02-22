package com.kabu.admin.stock.pricehistory.dto;

import java.math.BigDecimal;

public record StockPriceHistoryUpdateRequest(
    String stockCode,
    String transDate,
    Integer beforeDayPrice,
    Integer openPrice,
    Integer highPrice,
    Integer lowPrice,
    Integer closePrice,
    Integer adjustedClosePrice,
    Integer beforeDayDiff,
    BigDecimal beforeDayDiffPercent,
    Integer volume,
    String remark
) {
}

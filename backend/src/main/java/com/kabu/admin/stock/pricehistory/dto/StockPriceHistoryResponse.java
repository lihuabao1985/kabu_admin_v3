package com.kabu.admin.stock.pricehistory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockPriceHistoryResponse(
    Long id,
    String stockCode,
    LocalDate transDate,
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

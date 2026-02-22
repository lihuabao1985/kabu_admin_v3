package com.kabu.admin.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockPriceChangeRankingResponse(
    String stockCode,
    String stockName,
    LocalDate startDate,
    LocalDate endDate,
    BigDecimal startClosePrice,
    BigDecimal endClosePrice,
    BigDecimal changeAmount,
    BigDecimal changePercent
) {
}

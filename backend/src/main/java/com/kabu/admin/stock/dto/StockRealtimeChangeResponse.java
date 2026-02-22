package com.kabu.admin.stock.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockRealtimeChangeResponse(
    String stockCode,
    String stockName,
    BigDecimal currentPrice,
    LocalDate referenceDate,
    BigDecimal referenceClosePrice,
    BigDecimal changeAmount,
    BigDecimal changePercent
) {
}

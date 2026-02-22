package com.kabu.admin.stock.dividendconfirmed.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record StockDividendConfirmedResponse(
    Long id,
    String stockCode,
    String stockName,
    String typeName,
    String stockPrice,
    BigDecimal dividendAmount,
    BigDecimal dividendYield,
    LocalDate rightsLastDay,
    LocalDate exDividendDate,
    LocalDate recordDate,
    String confirmedFlg
) {
}

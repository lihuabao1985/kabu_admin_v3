package com.kabu.admin.stock.dividendconfirmed.dto;

import java.math.BigDecimal;

public record StockDividendConfirmedCreateRequest(
    String stockCode,
    BigDecimal dividendAmount,
    BigDecimal dividendYield,
    String rightsLastDay,
    String exDividendDate,
    String recordDate,
    String confirmedFlg
) {
}

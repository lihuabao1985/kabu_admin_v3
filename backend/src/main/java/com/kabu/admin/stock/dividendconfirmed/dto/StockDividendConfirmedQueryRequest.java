package com.kabu.admin.stock.dividendconfirmed.dto;

public record StockDividendConfirmedQueryRequest(
    String stockCode,
    String rightsLastDay,
    String recordDateFrom,
    String recordDateTo,
    String confirmedFlg,
    Integer page,
    Integer size,
    String sort
) {
}

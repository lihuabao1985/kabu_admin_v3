package com.kabu.admin.stock.dividendconfirmed.dto;

import java.util.List;

public record StockDividendConfirmedListResponse(
    List<StockDividendConfirmedResponse> items,
    long total,
    int page,
    int size
) {
}

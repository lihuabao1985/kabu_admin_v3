package com.kabu.admin.stock.pricehistory.dto;

import java.util.List;

public record StockPriceHistoryListResponse(
    List<StockPriceHistoryResponse> items,
    long total,
    int page,
    int size
) {
}

package com.kabu.admin.stock.dto;

import java.util.List;

public record StockPriceChangeRankingListResponse(
    List<StockPriceChangeRankingResponse> items,
    long total,
    int page,
    int size
) {
}

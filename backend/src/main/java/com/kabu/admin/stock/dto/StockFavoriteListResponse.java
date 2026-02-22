package com.kabu.admin.stock.dto;

import java.util.List;

public record StockFavoriteListResponse(
    List<StockFavoriteResponse> items,
    long total
) {
}

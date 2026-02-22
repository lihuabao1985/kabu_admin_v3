package com.kabu.admin.stock.dto;

import java.util.List;

public record StockListResponse(
    List<StockResponse> items,
    long total,
    int page,
    int size
) {
}

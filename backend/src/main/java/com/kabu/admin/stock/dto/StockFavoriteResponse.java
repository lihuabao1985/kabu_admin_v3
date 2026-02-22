package com.kabu.admin.stock.dto;

import java.time.LocalDateTime;

public record StockFavoriteResponse(
    Long id,
    String stockCode,
    String stockName,
    String typeName,
    String market,
    String stockPrice,
    LocalDateTime createdAt
) {
}

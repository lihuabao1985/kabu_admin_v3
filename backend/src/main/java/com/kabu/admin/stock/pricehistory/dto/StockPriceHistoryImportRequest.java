package com.kabu.admin.stock.pricehistory.dto;

import java.util.List;

public record StockPriceHistoryImportRequest(
    String mode,
    List<StockPriceHistoryCreateRequest> items
) {
}

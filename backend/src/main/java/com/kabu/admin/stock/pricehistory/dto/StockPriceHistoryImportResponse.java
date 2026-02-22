package com.kabu.admin.stock.pricehistory.dto;

import java.util.List;

public record StockPriceHistoryImportResponse(
    int total,
    int success,
    int created,
    int updated,
    int skipped,
    int failed,
    List<StockPriceHistoryImportFailure> failures
) {
}

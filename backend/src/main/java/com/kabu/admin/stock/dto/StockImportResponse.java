package com.kabu.admin.stock.dto;

import java.util.List;

public record StockImportResponse(
    int total,
    int success,
    int created,
    int updated,
    int failed,
    List<StockImportFailure> failures
) {
}

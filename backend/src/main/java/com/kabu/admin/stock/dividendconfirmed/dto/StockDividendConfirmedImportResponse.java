package com.kabu.admin.stock.dividendconfirmed.dto;

import java.util.List;

public record StockDividendConfirmedImportResponse(
    int total,
    int success,
    int created,
    int updated,
    int skipped,
    int failed,
    List<StockDividendConfirmedImportFailure> failures
) {
}

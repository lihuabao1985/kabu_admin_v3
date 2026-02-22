package com.kabu.admin.stock.dividendconfirmed.dto;

import java.util.List;

public record StockDividendConfirmedImportRequest(
    String mode,
    List<StockDividendConfirmedCreateRequest> items
) {
}

package com.kabu.admin.stock.dto;

import java.util.List;

public record StockImportRequest(
    String mode,
    List<StockCreateRequest> items
) {
}

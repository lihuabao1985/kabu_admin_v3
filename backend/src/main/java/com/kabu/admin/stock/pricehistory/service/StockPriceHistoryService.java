package com.kabu.admin.stock.pricehistory.service;

import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryCreateRequest;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryImportRequest;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryImportResponse;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryListResponse;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryQueryRequest;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryResponse;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryUpdateRequest;

public interface StockPriceHistoryService {

    StockPriceHistoryListResponse listByStockCode(StockPriceHistoryQueryRequest request);

    StockPriceHistoryResponse getById(Long id);

    StockPriceHistoryResponse create(StockPriceHistoryCreateRequest request);

    StockPriceHistoryResponse update(Long id, StockPriceHistoryUpdateRequest request);

    void delete(Long id);

    StockPriceHistoryImportResponse importData(StockPriceHistoryImportRequest request);
}

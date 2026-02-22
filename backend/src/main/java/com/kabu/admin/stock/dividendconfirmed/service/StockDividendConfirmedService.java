package com.kabu.admin.stock.dividendconfirmed.service;

import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedCreateRequest;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedImportRequest;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedImportResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedListResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedQueryRequest;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedRightsLastDayStatsListResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedUpdateRequest;

public interface StockDividendConfirmedService {

    StockDividendConfirmedListResponse list(StockDividendConfirmedQueryRequest request);

    StockDividendConfirmedResponse getById(Long id);

    StockDividendConfirmedResponse create(StockDividendConfirmedCreateRequest request);

    StockDividendConfirmedResponse update(Long id, StockDividendConfirmedUpdateRequest request);

    void delete(Long id);

    StockDividendConfirmedResponse confirm(Long id);

    StockDividendConfirmedResponse unconfirm(Long id);

    StockDividendConfirmedImportResponse importData(StockDividendConfirmedImportRequest request);

    StockDividendConfirmedRightsLastDayStatsListResponse rightsLastDayStats();
}

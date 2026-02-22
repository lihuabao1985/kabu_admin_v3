package com.kabu.admin.stock.service;

import com.kabu.admin.stock.dto.StockCreateRequest;
import com.kabu.admin.stock.dto.StockFavoriteCreateRequest;
import com.kabu.admin.stock.dto.StockFavoriteListResponse;
import com.kabu.admin.stock.dto.StockFavoriteResponse;
import com.kabu.admin.stock.dto.StockImportRequest;
import com.kabu.admin.stock.dto.StockImportResponse;
import com.kabu.admin.stock.dto.StockListResponse;
import com.kabu.admin.stock.dto.StockOptionResponse;
import com.kabu.admin.stock.dto.StockPriceChangeRankingListResponse;
import com.kabu.admin.stock.dto.StockPriceChangeRankingQueryRequest;
import com.kabu.admin.stock.dto.StockQueryRequest;
import com.kabu.admin.stock.dto.StockRealtimeChangeResponse;
import com.kabu.admin.stock.dto.StockResponse;
import com.kabu.admin.stock.dto.StockUpdateRequest;
import java.util.List;

public interface StockService {

    StockListResponse listStocks(StockQueryRequest request);

    StockResponse getStockById(Long id);

    StockResponse createStock(StockCreateRequest request);

    StockResponse updateStock(Long id, StockUpdateRequest request);

    StockResponse updateDeleteFlag(Long id, String delFlg);

    void deleteStock(Long id);

    List<StockOptionResponse> listOptions(String keyword, String market, Integer limit);

    StockPriceChangeRankingListResponse listPriceChangeRanking(StockPriceChangeRankingQueryRequest request);

    StockRealtimeChangeResponse getRealtimeChange(String stockCode);

    StockFavoriteListResponse listFavorites();

    StockFavoriteResponse addFavorite(StockFavoriteCreateRequest request);

    void removeFavorite(Long id);

    StockImportResponse importStocks(StockImportRequest request);
}

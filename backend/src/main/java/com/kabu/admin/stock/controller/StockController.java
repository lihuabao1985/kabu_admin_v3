package com.kabu.admin.stock.controller;

import com.kabu.admin.stock.dto.StockCreateRequest;
import com.kabu.admin.stock.dto.StockDeleteFlagPatchRequest;
import com.kabu.admin.stock.dto.StockFavoriteCreateRequest;
import com.kabu.admin.stock.dto.StockFavoriteListResponse;
import com.kabu.admin.stock.dto.StockFavoriteResponse;
import com.kabu.admin.stock.dto.IndustryCodeOptionResponse;
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
import com.kabu.admin.stock.service.StockService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:VIEW','STOCK:MANAGE','STOCK:IMPORT')")
    public StockListResponse listStocks(
        @RequestParam(required = false) String stockCode,
        @RequestParam(required = false) String stockName,
        @RequestParam(required = false) String typeName,
        @RequestParam(required = false) String market,
        @RequestParam(required = false) String stockPriceFrom,
        @RequestParam(required = false) String stockPriceTo,
        @RequestParam(required = false) String freeWord,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String sort
    ) {
        StockQueryRequest request = new StockQueryRequest(
            stockCode,
            stockName,
            typeName,
            market,
            stockPriceFrom,
            stockPriceTo,
            freeWord,
            page,
            size,
            sort
        );
        return stockService.listStocks(request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:VIEW','STOCK:MANAGE','STOCK:IMPORT')")
    public StockResponse getStockById(@PathVariable Long id) {
        return stockService.getStockById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:MANAGE')")
    public StockResponse createStock(@RequestBody StockCreateRequest request) {
        return stockService.createStock(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:MANAGE')")
    public StockResponse updateStock(@PathVariable Long id, @RequestBody StockUpdateRequest request) {
        return stockService.updateStock(id, request);
    }

    @PatchMapping("/{id}/delete-flag")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:MANAGE')")
    public StockResponse updateDeleteFlag(@PathVariable Long id, @RequestBody StockDeleteFlagPatchRequest request) {
        return stockService.updateDeleteFlag(id, request.delFlg());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:MANAGE')")
    public void deleteStock(@PathVariable Long id) {
        stockService.deleteStock(id);
    }

    @GetMapping("/industry-options")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:VIEW','STOCK:MANAGE','STOCK_PRICE_HISTORY:VIEW','STOCK_PRICE_HISTORY:MANAGE')")
    public List<IndustryCodeOptionResponse> listIndustryOptions() {
        return stockService.listIndustryCodeOptions();
    }

    @GetMapping("/options")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:VIEW','STOCK:MANAGE','STOCK:IMPORT')")
    public List<StockOptionResponse> listOptions(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) String market,
        @RequestParam(required = false) Integer limit
    ) {
        return stockService.listOptions(keyword, market, limit);
    }

    @PostMapping(":import")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:IMPORT')")
    public StockImportResponse importStocks(@RequestBody StockImportRequest request) {
        return stockService.importStocks(request);
    }

    @GetMapping("/price-change-ranking")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:VIEW','STOCK:MANAGE','STOCK_PRICE_HISTORY:VIEW','STOCK_PRICE_HISTORY:MANAGE')")
    public StockPriceChangeRankingListResponse listPriceChangeRanking(
        @RequestParam String startDate,
        @RequestParam String endDate,
        @RequestParam String changeType,
        @RequestParam(required = false) String changePercent,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size
    ) {
        StockPriceChangeRankingQueryRequest request = new StockPriceChangeRankingQueryRequest(
            startDate,
            endDate,
            changeType,
            changePercent,
            page,
            size
        );
        return stockService.listPriceChangeRanking(request);
    }

    @GetMapping("/realtime-change")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:VIEW','STOCK:MANAGE')")
    public StockRealtimeChangeResponse getRealtimeChange(@RequestParam String stockCode) {
        return stockService.getRealtimeChange(stockCode);
    }

    @GetMapping("/favorites")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:VIEW','STOCK:MANAGE')")
    public StockFavoriteListResponse listFavorites() {
        return stockService.listFavorites();
    }

    @PostMapping("/favorites")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:VIEW','STOCK:MANAGE')")
    public StockFavoriteResponse addFavorite(@RequestBody StockFavoriteCreateRequest request) {
        return stockService.addFavorite(request);
    }

    @DeleteMapping("/favorites/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK:VIEW','STOCK:MANAGE')")
    public void removeFavorite(@PathVariable Long id) {
        stockService.removeFavorite(id);
    }
}

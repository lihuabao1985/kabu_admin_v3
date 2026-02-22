package com.kabu.admin.stock.pricehistory.controller;

import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryCreateRequest;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryImportRequest;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryImportResponse;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryListResponse;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryQueryRequest;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryResponse;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryUpdateRequest;
import com.kabu.admin.stock.pricehistory.service.StockPriceHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class StockPriceHistoryController {

    private final StockPriceHistoryService stockPriceHistoryService;

    public StockPriceHistoryController(StockPriceHistoryService stockPriceHistoryService) {
        this.stockPriceHistoryService = stockPriceHistoryService;
    }

    @GetMapping("/stocks/{stockCode}/price-history")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_PRICE_HISTORY:VIEW','STOCK_PRICE_HISTORY:MANAGE','STOCK_PRICE_HISTORY:IMPORT')")
    public StockPriceHistoryListResponse listByStockCode(
        @PathVariable String stockCode,
        @RequestParam(required = false) String dateFrom,
        @RequestParam(required = false) String dateTo,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String sort
    ) {
        StockPriceHistoryQueryRequest request = new StockPriceHistoryQueryRequest(
            stockCode,
            dateFrom,
            dateTo,
            page,
            size,
            sort
        );
        return stockPriceHistoryService.listByStockCode(request);
    }

    @GetMapping("/stock-price-history/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_PRICE_HISTORY:VIEW','STOCK_PRICE_HISTORY:MANAGE','STOCK_PRICE_HISTORY:IMPORT')")
    public StockPriceHistoryResponse getById(@PathVariable Long id) {
        return stockPriceHistoryService.getById(id);
    }

    @PostMapping("/stock-price-history")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_PRICE_HISTORY:MANAGE')")
    public StockPriceHistoryResponse create(@RequestBody StockPriceHistoryCreateRequest request) {
        return stockPriceHistoryService.create(request);
    }

    @PutMapping("/stock-price-history/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_PRICE_HISTORY:MANAGE')")
    public StockPriceHistoryResponse update(@PathVariable Long id, @RequestBody StockPriceHistoryUpdateRequest request) {
        return stockPriceHistoryService.update(id, request);
    }

    @DeleteMapping("/stock-price-history/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable Long id) {
        stockPriceHistoryService.delete(id);
    }

    @PostMapping("/stock-price-history:import")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_PRICE_HISTORY:IMPORT')")
    public StockPriceHistoryImportResponse importData(@RequestBody StockPriceHistoryImportRequest request) {
        return stockPriceHistoryService.importData(request);
    }
}

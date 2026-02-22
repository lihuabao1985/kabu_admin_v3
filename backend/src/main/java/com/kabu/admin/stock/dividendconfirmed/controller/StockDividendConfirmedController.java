package com.kabu.admin.stock.dividendconfirmed.controller;

import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedCreateRequest;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedImportRequest;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedImportResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedListResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedQueryRequest;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedRightsLastDayStatsListResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedUpdateRequest;
import com.kabu.admin.stock.dividendconfirmed.service.StockDividendConfirmedService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
public class StockDividendConfirmedController {

    private final StockDividendConfirmedService stockDividendConfirmedService;

    public StockDividendConfirmedController(StockDividendConfirmedService stockDividendConfirmedService) {
        this.stockDividendConfirmedService = stockDividendConfirmedService;
    }

    @GetMapping("/stock-dividend-confirmed")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_DIVIDEND_CONFIRMED:VIEW','STOCK_DIVIDEND_CONFIRMED:MANAGE','STOCK_DIVIDEND_CONFIRMED:CONFIRM','STOCK_DIVIDEND_CONFIRMED:IMPORT')")
    public StockDividendConfirmedListResponse list(
        @RequestParam(required = false) String stockCode,
        @RequestParam(required = false) String industryCode,
        @RequestParam(required = false) String rightsLastDay,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(required = false) String sort
    ) {
        StockDividendConfirmedQueryRequest request = new StockDividendConfirmedQueryRequest(
            stockCode,
            industryCode,
            rightsLastDay,
            page,
            size,
            sort
        );
        return stockDividendConfirmedService.list(request);
    }

    @GetMapping("/stock-dividend-confirmed/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_DIVIDEND_CONFIRMED:VIEW','STOCK_DIVIDEND_CONFIRMED:MANAGE','STOCK_DIVIDEND_CONFIRMED:CONFIRM','STOCK_DIVIDEND_CONFIRMED:IMPORT')")
    public StockDividendConfirmedResponse getById(@PathVariable Long id) {
        return stockDividendConfirmedService.getById(id);
    }

    @PostMapping("/stock-dividend-confirmed")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_DIVIDEND_CONFIRMED:MANAGE')")
    public StockDividendConfirmedResponse create(@RequestBody StockDividendConfirmedCreateRequest request) {
        return stockDividendConfirmedService.create(request);
    }

    @PutMapping("/stock-dividend-confirmed/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_DIVIDEND_CONFIRMED:MANAGE')")
    public StockDividendConfirmedResponse update(@PathVariable Long id, @RequestBody StockDividendConfirmedUpdateRequest request) {
        return stockDividendConfirmedService.update(id, request);
    }

    @DeleteMapping("/stock-dividend-confirmed/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_DIVIDEND_CONFIRMED:MANAGE')")
    public void delete(@PathVariable Long id) {
        stockDividendConfirmedService.delete(id);
    }

    @PatchMapping("/stock-dividend-confirmed/{id}/confirmed")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_DIVIDEND_CONFIRMED:CONFIRM')")
    public StockDividendConfirmedResponse confirm(@PathVariable Long id) {
        return stockDividendConfirmedService.confirm(id);
    }

    @PatchMapping("/stock-dividend-confirmed/{id}/unconfirmed")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_DIVIDEND_CONFIRMED:CONFIRM')")
    public StockDividendConfirmedResponse unconfirm(@PathVariable Long id) {
        return stockDividendConfirmedService.unconfirm(id);
    }

    @PostMapping("/stock-dividend-confirmed:import")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_DIVIDEND_CONFIRMED:IMPORT')")
    public StockDividendConfirmedImportResponse importData(@RequestBody StockDividendConfirmedImportRequest request) {
        return stockDividendConfirmedService.importData(request);
    }

    @GetMapping("/stock-dividend-confirmed/stats/rights-last-day")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','STOCK_DIVIDEND_CONFIRMED:VIEW','STOCK_DIVIDEND_CONFIRMED:MANAGE','STOCK_DIVIDEND_CONFIRMED:CONFIRM','STOCK_DIVIDEND_CONFIRMED:IMPORT')")
    public StockDividendConfirmedRightsLastDayStatsListResponse rightsLastDayStats() {
        return stockDividendConfirmedService.rightsLastDayStats();
    }
}

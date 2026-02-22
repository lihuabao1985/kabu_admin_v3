package com.kabu.admin.stock.pricehistory.service.impl;

import com.kabu.admin.stock.repository.StockRepository;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryCreateRequest;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryImportFailure;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryImportRequest;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryImportResponse;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryListResponse;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryQueryRequest;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryResponse;
import com.kabu.admin.stock.pricehistory.dto.StockPriceHistoryUpdateRequest;
import com.kabu.admin.stock.pricehistory.exception.StockPriceHistoryConflictException;
import com.kabu.admin.stock.pricehistory.exception.StockPriceHistoryNotFoundException;
import com.kabu.admin.stock.pricehistory.model.StockPriceHistory;
import com.kabu.admin.stock.pricehistory.repository.StockPriceHistoryRepository;
import com.kabu.admin.stock.pricehistory.service.StockPriceHistoryService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockPriceHistoryServiceImpl implements StockPriceHistoryService {

    private static final Pattern STOCK_CODE_PATTERN = Pattern.compile("^[A-Z0-9._-]+$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final String SORT_BY_TRANS_DATE = "TRANS_DATE";
    private static final String SORT_BY_ID = "ID";
    private static final String SORT_ASC = "ASC";
    private static final String SORT_DESC = "DESC";
    private static final String IMPORT_MODE_OVERWRITE = "OVERWRITE";
    private static final String IMPORT_MODE_SKIP_DUPLICATE = "SKIP_DUPLICATE";

    private final StockPriceHistoryRepository stockPriceHistoryRepository;
    private final StockRepository stockRepository;

    public StockPriceHistoryServiceImpl(
        StockPriceHistoryRepository stockPriceHistoryRepository,
        StockRepository stockRepository
    ) {
        this.stockPriceHistoryRepository = stockPriceHistoryRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StockPriceHistoryListResponse listByStockCode(StockPriceHistoryQueryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("查询请求不能为空");
        }

        String stockCode = normalizeStockCode(request.stockCode());
        LocalDate dateFrom = normalizeDate(request.dateFrom(), "dateFrom", true);
        LocalDate dateTo = normalizeDate(request.dateTo(), "dateTo", true);
        if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
            throw new IllegalArgumentException("dateFrom 不能晚于 dateTo");
        }

        int page = normalizePage(request.page());
        int size = normalizeSize(request.size());
        int offset = (page - 1) * size;
        SortSpec sortSpec = normalizeSort(request.sort());

        List<StockPriceHistoryResponse> items = stockPriceHistoryRepository
            .findByCriteria(stockCode, dateFrom, dateTo, sortSpec.sortBy(), sortSpec.sortDirection(), size, offset)
            .stream()
            .map(this::toResponse)
            .toList();
        long total = stockPriceHistoryRepository.countByCriteria(stockCode, dateFrom, dateTo);
        return new StockPriceHistoryListResponse(items, total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public StockPriceHistoryResponse getById(Long id) {
        validateId(id);
        StockPriceHistory existing = stockPriceHistoryRepository.findById(id)
            .orElseThrow(() -> new StockPriceHistoryNotFoundException(id));
        return toResponse(existing);
    }

    @Override
    @Transactional
    public StockPriceHistoryResponse create(StockPriceHistoryCreateRequest request) {
        StockPriceHistory stockPriceHistory = buildFromCreateRequest(request);
        ensureStockExists(stockPriceHistory.getStockCode());
        stockPriceHistoryRepository.findByStockCodeAndTransDate(
            stockPriceHistory.getStockCode(),
            stockPriceHistory.getTransDate()
        ).ifPresent(existing -> {
            throw new StockPriceHistoryConflictException(
                stockPriceHistory.getStockCode(),
                stockPriceHistory.getTransDate().toString()
            );
        });

        int inserted = stockPriceHistoryRepository.insert(stockPriceHistory);
        if (inserted != 1 || stockPriceHistory.getId() == null) {
            throw new IllegalStateException("创建股票历史行情失败");
        }
        return getById(stockPriceHistory.getId());
    }

    @Override
    @Transactional
    public StockPriceHistoryResponse update(Long id, StockPriceHistoryUpdateRequest request) {
        validateId(id);
        StockPriceHistory existing = stockPriceHistoryRepository.findById(id)
            .orElseThrow(() -> new StockPriceHistoryNotFoundException(id));

        StockPriceHistory stockPriceHistory = buildFromUpdateRequest(request);
        ensureStockExists(stockPriceHistory.getStockCode());

        stockPriceHistoryRepository.findByStockCodeAndTransDate(
            stockPriceHistory.getStockCode(),
            stockPriceHistory.getTransDate()
        ).filter(item -> !item.getId().equals(id))
            .ifPresent(item -> {
                throw new StockPriceHistoryConflictException(
                    stockPriceHistory.getStockCode(),
                    stockPriceHistory.getTransDate().toString()
                );
            });

        stockPriceHistory.setId(existing.getId());
        int updated = stockPriceHistoryRepository.update(stockPriceHistory);
        if (updated != 1) {
            throw new StockPriceHistoryNotFoundException(id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        validateId(id);
        int deleted = stockPriceHistoryRepository.deleteById(id);
        if (deleted != 1) {
            throw new StockPriceHistoryNotFoundException(id);
        }
    }

    @Override
    @Transactional
    public StockPriceHistoryImportResponse importData(StockPriceHistoryImportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("导入请求不能为空");
        }

        String mode = normalizeImportMode(request.mode());
        List<StockPriceHistoryCreateRequest> items = request.items();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("导入数据不能为空");
        }

        int total = items.size();
        int success = 0;
        int created = 0;
        int updated = 0;
        int skipped = 0;
        int failed = 0;
        List<StockPriceHistoryImportFailure> failures = new ArrayList<>();

        for (StockPriceHistoryCreateRequest item : items) {
            try {
                StockPriceHistory stockPriceHistory = buildFromCreateRequest(item);
                ensureStockExists(stockPriceHistory.getStockCode());

                Optional<StockPriceHistory> existing = stockPriceHistoryRepository.findByStockCodeAndTransDate(
                    stockPriceHistory.getStockCode(),
                    stockPriceHistory.getTransDate()
                );
                if (existing.isPresent()) {
                    if (IMPORT_MODE_SKIP_DUPLICATE.equals(mode)) {
                        skipped++;
                        success++;
                        continue;
                    }
                    stockPriceHistory.setId(existing.get().getId());
                    int updateResult = stockPriceHistoryRepository.update(stockPriceHistory);
                    if (updateResult != 1) {
                        throw new IllegalStateException("覆盖更新失败");
                    }
                    updated++;
                    success++;
                    continue;
                }

                int insertResult = stockPriceHistoryRepository.insert(stockPriceHistory);
                if (insertResult != 1) {
                    throw new IllegalStateException("导入新增失败");
                }
                created++;
                success++;
            } catch (RuntimeException ex) {
                failed++;
                failures.add(new StockPriceHistoryImportFailure(
                    extractRawStockCode(item),
                    extractRawDate(item),
                    ex.getMessage()
                ));
            }
        }

        return new StockPriceHistoryImportResponse(total, success, created, updated, skipped, failed, failures);
    }

    private StockPriceHistory buildFromCreateRequest(StockPriceHistoryCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求不能为空");
        }
        StockPriceHistory stockPriceHistory = new StockPriceHistory();
        stockPriceHistory.setStockCode(normalizeStockCode(request.stockCode()));
        stockPriceHistory.setTransDate(normalizeDate(request.transDate(), "transDate", false));
        applyMutableFields(
            stockPriceHistory,
            request.beforeDayPrice(),
            request.openPrice(),
            request.highPrice(),
            request.lowPrice(),
            request.closePrice(),
            request.adjustedClosePrice(),
            request.beforeDayDiff(),
            request.beforeDayDiffPercent(),
            request.volume(),
            request.remark()
        );
        validateBusinessRules(stockPriceHistory);
        return stockPriceHistory;
    }

    private StockPriceHistory buildFromUpdateRequest(StockPriceHistoryUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求不能为空");
        }
        StockPriceHistory stockPriceHistory = new StockPriceHistory();
        stockPriceHistory.setStockCode(normalizeStockCode(request.stockCode()));
        stockPriceHistory.setTransDate(normalizeDate(request.transDate(), "transDate", false));
        applyMutableFields(
            stockPriceHistory,
            request.beforeDayPrice(),
            request.openPrice(),
            request.highPrice(),
            request.lowPrice(),
            request.closePrice(),
            request.adjustedClosePrice(),
            request.beforeDayDiff(),
            request.beforeDayDiffPercent(),
            request.volume(),
            request.remark()
        );
        validateBusinessRules(stockPriceHistory);
        return stockPriceHistory;
    }

    private void applyMutableFields(
        StockPriceHistory stockPriceHistory,
        Integer beforeDayPrice,
        Integer openPrice,
        Integer highPrice,
        Integer lowPrice,
        Integer closePrice,
        Integer adjustedClosePrice,
        Integer beforeDayDiff,
        BigDecimal beforeDayDiffPercent,
        Integer volume,
        String remark
    ) {
        stockPriceHistory.setBeforeDayPrice(normalizeNonNegative(beforeDayPrice, "beforeDayPrice"));
        stockPriceHistory.setOpenPrice(normalizeNonNegative(openPrice, "openPrice"));
        stockPriceHistory.setHighPrice(normalizeNonNegative(highPrice, "highPrice"));
        stockPriceHistory.setLowPrice(normalizeNonNegative(lowPrice, "lowPrice"));
        stockPriceHistory.setClosePrice(normalizeNonNegative(closePrice, "closePrice"));
        stockPriceHistory.setAdjustedClosePrice(normalizeNonNegative(adjustedClosePrice, "adjustedClosePrice"));
        stockPriceHistory.setBeforeDayDiff(beforeDayDiff);
        stockPriceHistory.setBeforeDayDiffPercent(normalizePercent(beforeDayDiffPercent, "beforeDayDiffPercent"));
        stockPriceHistory.setVolume(normalizeNonNegative(volume, "volume"));
        stockPriceHistory.setRemark(normalizeText(remark));
    }

    private void validateBusinessRules(StockPriceHistory item) {
        validateHighLowRule(item);
        validateBeforeDayDiffRule(item);
        validateBeforeDayDiffPercentRule(item);
    }

    private void validateHighLowRule(StockPriceHistory item) {
        Integer high = item.getHighPrice();
        Integer low = item.getLowPrice();
        Integer open = item.getOpenPrice();
        Integer close = item.getClosePrice();
        if (high != null) {
            if (open != null && high < open) {
                throw new IllegalArgumentException("HIGH_PRICE 必须大于等于 OPEN_PRICE");
            }
            if (close != null && high < close) {
                throw new IllegalArgumentException("HIGH_PRICE 必须大于等于 CLOSE_PRICE");
            }
            if (low != null && high < low) {
                throw new IllegalArgumentException("HIGH_PRICE 必须大于等于 LOW_PRICE");
            }
        }
        if (low != null) {
            if (open != null && low > open) {
                throw new IllegalArgumentException("LOW_PRICE 必须小于等于 OPEN_PRICE");
            }
            if (close != null && low > close) {
                throw new IllegalArgumentException("LOW_PRICE 必须小于等于 CLOSE_PRICE");
            }
            if (high != null && low > high) {
                throw new IllegalArgumentException("LOW_PRICE 必须小于等于 HIGH_PRICE");
            }
        }
    }

    private void validateBeforeDayDiffRule(StockPriceHistory item) {
        Integer beforeDayPrice = item.getBeforeDayPrice();
        Integer closePrice = item.getClosePrice();
        Integer beforeDayDiff = item.getBeforeDayDiff();
        if (beforeDayPrice == null || closePrice == null || beforeDayDiff == null) {
            return;
        }

        int expected = closePrice - beforeDayPrice;
        if (expected != beforeDayDiff) {
            throw new IllegalArgumentException("BEFORE_DAY_DIFF 必须等于 CLOSE_PRICE - BEFORE_DAY_PRICE");
        }
    }

    private void validateBeforeDayDiffPercentRule(StockPriceHistory item) {
        BigDecimal beforeDayDiffPercent = item.getBeforeDayDiffPercent();
        Integer beforeDayPrice = item.getBeforeDayPrice();
        Integer beforeDayDiff = item.getBeforeDayDiff();
        if (beforeDayDiffPercent == null || beforeDayPrice == null || beforeDayDiff == null) {
            return;
        }
        if (beforeDayPrice == 0) {
            throw new IllegalArgumentException("BEFORE_DAY_PRICE 为 0 时不能计算 BEFORE_DAY_DIFF_PERCENT");
        }

        BigDecimal expected = BigDecimal.valueOf(beforeDayDiff)
            .multiply(BigDecimal.valueOf(100))
            .divide(BigDecimal.valueOf(beforeDayPrice), 2, RoundingMode.HALF_UP);
        BigDecimal actual = beforeDayDiffPercent.setScale(2, RoundingMode.HALF_UP);
        if (expected.compareTo(actual) != 0) {
            throw new IllegalArgumentException("BEFORE_DAY_DIFF_PERCENT 与 BEFORE_DAY_DIFF 不一致");
        }
    }

    private StockPriceHistoryResponse toResponse(StockPriceHistory item) {
        return new StockPriceHistoryResponse(
            item.getId(),
            item.getStockCode(),
            item.getTransDate(),
            item.getBeforeDayPrice(),
            item.getOpenPrice(),
            item.getHighPrice(),
            item.getLowPrice(),
            item.getClosePrice(),
            item.getAdjustedClosePrice(),
            item.getBeforeDayDiff(),
            item.getBeforeDayDiffPercent(),
            item.getVolume(),
            item.getRemark()
        );
    }

    private int normalizePage(Integer page) {
        if (page == null || page < 1) {
            return DEFAULT_PAGE;
        }
        return page;
    }

    private int normalizeSize(Integer size) {
        if (size == null || size < 1) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    private Integer normalizeNonNegative(Integer value, String fieldName) {
        if (value == null) {
            return null;
        }
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " 不能为负数");
        }
        return value;
    }

    private BigDecimal normalizePercent(BigDecimal value, String fieldName) {
        if (value == null) {
            return null;
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private LocalDate normalizeDate(String value, String fieldName, boolean allowNull) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            if (allowNull) {
                return null;
            }
            throw new IllegalArgumentException(fieldName + " 不能为空");
        }
        try {
            return LocalDate.parse(normalized, DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " 日期格式必须为 yyyy-MM-dd");
        }
    }

    private String normalizeImportMode(String mode) {
        String normalized = normalizeText(mode);
        if (normalized == null) {
            return IMPORT_MODE_OVERWRITE;
        }
        String upper = normalized.toUpperCase();
        if (!IMPORT_MODE_OVERWRITE.equals(upper) && !IMPORT_MODE_SKIP_DUPLICATE.equals(upper)) {
            throw new IllegalArgumentException("导入模式必须为 OVERWRITE 或 SKIP_DUPLICATE");
        }
        return upper;
    }

    private SortSpec normalizeSort(String sort) {
        String normalized = normalizeText(sort);
        if (normalized == null) {
            return new SortSpec(SORT_BY_TRANS_DATE, SORT_DESC);
        }
        String[] parts = normalized.split(",");
        String field = parts[0].trim().toLowerCase();
        String direction = parts.length > 1 ? parts[1].trim().toUpperCase() : SORT_DESC;
        String sortBy = switch (field) {
            case "transdate", "trans_date", "date" -> SORT_BY_TRANS_DATE;
            case "id" -> SORT_BY_ID;
            default -> throw new IllegalArgumentException("不支持的排序字段: " + field);
        };
        String sortDirection = switch (direction) {
            case SORT_ASC -> SORT_ASC;
            case SORT_DESC -> SORT_DESC;
            default -> throw new IllegalArgumentException("排序方向必须是 asc 或 desc");
        };
        return new SortSpec(sortBy, sortDirection);
    }

    private String normalizeStockCode(String stockCode) {
        String normalized = requireText(stockCode, "股票代码不能为空").toUpperCase();
        if (!STOCK_CODE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("股票代码格式不正确，仅允许字母数字._-");
        }
        return normalized;
    }

    private void ensureStockExists(String stockCode) {
        if (stockRepository.findByStockCode(stockCode).isEmpty()) {
            throw new IllegalArgumentException("股票代码不存在: " + stockCode);
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String requireText(String value, String message) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private void validateId(Long id) {
        if (id == null || id < 1) {
            throw new IllegalArgumentException("ID必须为正整数");
        }
    }

    private String extractRawStockCode(StockPriceHistoryCreateRequest request) {
        if (request == null) {
            return "";
        }
        String stockCode = normalizeText(request.stockCode());
        return stockCode == null ? "" : stockCode;
    }

    private String extractRawDate(StockPriceHistoryCreateRequest request) {
        if (request == null) {
            return "";
        }
        String transDate = normalizeText(request.transDate());
        return transDate == null ? "" : transDate;
    }

    private record SortSpec(String sortBy, String sortDirection) {
    }
}

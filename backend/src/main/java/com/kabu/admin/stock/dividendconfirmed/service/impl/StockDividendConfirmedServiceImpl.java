package com.kabu.admin.stock.dividendconfirmed.service.impl;

import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedCreateRequest;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedImportFailure;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedImportRequest;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedImportResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedListResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedQueryRequest;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedRightsLastDayStatsListResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedRightsLastDayStatsResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedResponse;
import com.kabu.admin.stock.dividendconfirmed.dto.StockDividendConfirmedUpdateRequest;
import com.kabu.admin.stock.dividendconfirmed.exception.StockDividendConfirmedConflictException;
import com.kabu.admin.stock.dividendconfirmed.exception.StockDividendConfirmedNotFoundException;
import com.kabu.admin.stock.dividendconfirmed.model.StockDividendConfirmed;
import com.kabu.admin.stock.dividendconfirmed.model.StockDividendConfirmedRightsLastDayStats;
import com.kabu.admin.stock.dividendconfirmed.repository.StockDividendConfirmedRepository;
import com.kabu.admin.stock.dividendconfirmed.service.StockDividendConfirmedService;
import com.kabu.admin.stock.repository.StockRepository;
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
public class StockDividendConfirmedServiceImpl implements StockDividendConfirmedService {

    private static final Pattern STOCK_CODE_PATTERN = Pattern.compile("^[A-Z0-9._-]+$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final String SORT_BY_RECORD_DATE = "RECORD_DATE";
    private static final String SORT_BY_ID = "ID";
    private static final String SORT_BY_STOCK_CODE = "STOCK_CODE";
    private static final String SORT_BY_TYPE_NAME = "TYPE_NAME";
    private static final String SORT_BY_DIVIDEND_YIELD = "DIVIDEND_YIELD";
    private static final String SORT_BY_RIGHTS_LAST_DAY = "RIGHTS_LAST_DAY";
    private static final String SORT_ASC = "ASC";
    private static final String SORT_DESC = "DESC";
    private static final String IMPORT_MODE_OVERWRITE = "OVERWRITE";
    private static final String IMPORT_MODE_SKIP_DUPLICATE = "SKIP_DUPLICATE";
    private static final String CONFIRMED = "1";
    private static final String UNCONFIRMED = "0";

    private final StockDividendConfirmedRepository stockDividendConfirmedRepository;
    private final StockRepository stockRepository;

    public StockDividendConfirmedServiceImpl(
        StockDividendConfirmedRepository stockDividendConfirmedRepository,
        StockRepository stockRepository
    ) {
        this.stockDividendConfirmedRepository = stockDividendConfirmedRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StockDividendConfirmedListResponse list(StockDividendConfirmedQueryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("譟･隸｢隸ｷ豎ゆｸ崎・荳ｺ遨ｺ");
        }

        String stockCode = normalizeStockCodeOptional(request.stockCode());
        String industryCode = normalizeText(request.industryCode());
        LocalDate rightsLastDay = normalizeDate(request.rightsLastDay(), "rightsLastDay", true);

        int page = normalizePage(request.page());
        int size = normalizeSize(request.size());
        int offset = (page - 1) * size;
        SortSpec sortSpec = normalizeSort(request.sort());

        List<StockDividendConfirmedResponse> items = stockDividendConfirmedRepository
            .findByCriteria(
                stockCode,
                industryCode,
                rightsLastDay,
                sortSpec.sortBy(),
                sortSpec.sortDirection(),
                size,
                offset
            )
            .stream()
            .map(this::toResponse)
            .toList();
        long total = stockDividendConfirmedRepository.countByCriteria(
            stockCode,
            industryCode,
            rightsLastDay
        );
        return new StockDividendConfirmedListResponse(items, total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public StockDividendConfirmedResponse getById(Long id) {
        validateId(id);
        StockDividendConfirmed existing = stockDividendConfirmedRepository.findById(id)
            .orElseThrow(() -> new StockDividendConfirmedNotFoundException(id));
        return toResponse(existing);
    }

    @Override
    @Transactional
    public StockDividendConfirmedResponse create(StockDividendConfirmedCreateRequest request) {
        StockDividendConfirmed item = buildFromCreateRequest(request);
        ensureStockExists(item.getStockCode());

        stockDividendConfirmedRepository.findByStockCodeAndRecordDate(item.getStockCode(), item.getRecordDate())
            .ifPresent(existing -> {
                throw new StockDividendConfirmedConflictException(item.getStockCode(), item.getRecordDate().toString());
            });

        int inserted = stockDividendConfirmedRepository.insert(item);
        if (inserted != 1 || item.getId() == null) {
            throw new IllegalStateException("蛻帛ｻｺ閧｡逾ｨ驟榊ｽ鍋｡ｮ譚・､ｱ雍･");
        }
        return getById(item.getId());
    }

    @Override
    @Transactional
    public StockDividendConfirmedResponse update(Long id, StockDividendConfirmedUpdateRequest request) {
        validateId(id);
        StockDividendConfirmed existing = stockDividendConfirmedRepository.findById(id)
            .orElseThrow(() -> new StockDividendConfirmedNotFoundException(id));
        if (isConfirmed(existing.getConfirmedFlg())) {
            throw new IllegalArgumentException("Confirmed records cannot be updated");
        }

        StockDividendConfirmed item = buildFromUpdateRequest(request);
        ensureStockExists(item.getStockCode());

        stockDividendConfirmedRepository.findByStockCodeAndRecordDate(item.getStockCode(), item.getRecordDate())
            .filter(found -> !found.getId().equals(id))
            .ifPresent(found -> {
                throw new StockDividendConfirmedConflictException(item.getStockCode(), item.getRecordDate().toString());
            });

        item.setId(existing.getId());
        int updated = stockDividendConfirmedRepository.update(item);
        if (updated != 1) {
            throw new StockDividendConfirmedNotFoundException(id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        validateId(id);
        StockDividendConfirmed existing = stockDividendConfirmedRepository.findById(id)
            .orElseThrow(() -> new StockDividendConfirmedNotFoundException(id));
        if (isConfirmed(existing.getConfirmedFlg())) {
            throw new IllegalArgumentException("蟾ｲ遑ｮ譚・ｮｰ蠖穂ｸ榊庄蛻髯､");
        }

        int deleted = stockDividendConfirmedRepository.deleteById(id);
        if (deleted != 1) {
            throw new StockDividendConfirmedNotFoundException(id);
        }
    }

    @Override
    @Transactional
    public StockDividendConfirmedResponse confirm(Long id) {
        validateId(id);
        stockDividendConfirmedRepository.findById(id)
            .orElseThrow(() -> new StockDividendConfirmedNotFoundException(id));
        if (stockDividendConfirmedRepository.updateConfirmedFlag(id, CONFIRMED) != 1) {
            throw new StockDividendConfirmedNotFoundException(id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public StockDividendConfirmedResponse unconfirm(Long id) {
        validateId(id);
        stockDividendConfirmedRepository.findById(id)
            .orElseThrow(() -> new StockDividendConfirmedNotFoundException(id));
        if (stockDividendConfirmedRepository.updateConfirmedFlag(id, UNCONFIRMED) != 1) {
            throw new StockDividendConfirmedNotFoundException(id);
        }
        return getById(id);
    }

    @Override
    @Transactional
    public StockDividendConfirmedImportResponse importData(StockDividendConfirmedImportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("蟇ｼ蜈･隸ｷ豎ゆｸ崎・荳ｺ遨ｺ");
        }

        String mode = normalizeImportMode(request.mode());
        List<StockDividendConfirmedCreateRequest> items = request.items();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("蟇ｼ蜈･謨ｰ謐ｮ荳崎・荳ｺ遨ｺ");
        }

        int total = items.size();
        int success = 0;
        int created = 0;
        int updated = 0;
        int skipped = 0;
        int failed = 0;
        List<StockDividendConfirmedImportFailure> failures = new ArrayList<>();

        for (StockDividendConfirmedCreateRequest source : items) {
            try {
                StockDividendConfirmed item = buildFromCreateRequest(source);
                ensureStockExists(item.getStockCode());

                Optional<StockDividendConfirmed> existing = stockDividendConfirmedRepository
                    .findByStockCodeAndRecordDate(item.getStockCode(), item.getRecordDate());

                if (existing.isPresent()) {
                    if (IMPORT_MODE_SKIP_DUPLICATE.equals(mode)) {
                        skipped++;
                        success++;
                        continue;
                    }
                    if (isConfirmed(existing.get().getConfirmedFlg())) {
                        throw new IllegalArgumentException("蟾ｲ遑ｮ譚・ｮｰ蠖穂ｸ榊庄隕・尠");
                    }
                    item.setId(existing.get().getId());
                    int updatedCount = stockDividendConfirmedRepository.update(item);
                    if (updatedCount != 1) {
                        throw new IllegalStateException("隕・尠譖ｴ譁ｰ螟ｱ雍･");
                    }
                    updated++;
                    success++;
                    continue;
                }

                int insertedCount = stockDividendConfirmedRepository.insert(item);
                if (insertedCount != 1) {
                    throw new IllegalStateException("蟇ｼ蜈･譁ｰ蠅槫､ｱ雍･");
                }
                created++;
                success++;
            } catch (RuntimeException ex) {
                failed++;
                failures.add(new StockDividendConfirmedImportFailure(
                    extractRawStockCode(source),
                    extractRawRecordDate(source),
                    ex.getMessage()
                ));
            }
        }

        return new StockDividendConfirmedImportResponse(total, success, created, updated, skipped, failed, failures);
    }

    @Override
    @Transactional(readOnly = true)
    public StockDividendConfirmedRightsLastDayStatsListResponse rightsLastDayStats() {
        List<StockDividendConfirmedRightsLastDayStatsResponse> items = stockDividendConfirmedRepository
            .aggregateByRightsLastDay()
            .stream()
            .map(this::toRightsLastDayStatsResponse)
            .toList();

        return new StockDividendConfirmedRightsLastDayStatsListResponse(items, items.size());
    }

    private StockDividendConfirmed buildFromCreateRequest(StockDividendConfirmedCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("隸ｷ豎ゆｸ崎・荳ｺ遨ｺ");
        }
        StockDividendConfirmed item = new StockDividendConfirmed();
        item.setStockCode(normalizeStockCode(request.stockCode()));
        applyMutableFields(
            item,
            request.dividendAmount(),
            request.dividendYield(),
            request.rightsLastDay(),
            request.exDividendDate(),
            request.recordDate(),
            request.confirmedFlg()
        );
        return item;
    }

    private StockDividendConfirmed buildFromUpdateRequest(StockDividendConfirmedUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("隸ｷ豎ゆｸ崎・荳ｺ遨ｺ");
        }
        StockDividendConfirmed item = new StockDividendConfirmed();
        item.setStockCode(normalizeStockCode(request.stockCode()));
        applyMutableFields(
            item,
            request.dividendAmount(),
            request.dividendYield(),
            request.rightsLastDay(),
            request.exDividendDate(),
            request.recordDate(),
            request.confirmedFlg()
        );
        return item;
    }

    private void applyMutableFields(
        StockDividendConfirmed item,
        BigDecimal dividendAmount,
        BigDecimal dividendYield,
        String rightsLastDay,
        String exDividendDate,
        String recordDate,
        String confirmedFlg
    ) {
        item.setDividendAmount(normalizeAmount(dividendAmount, "dividendAmount", false));
        item.setDividendYield(normalizeAmount(dividendYield, "dividendYield", true));
        item.setRightsLastDay(normalizeDate(rightsLastDay, "rightsLastDay", true));
        item.setExDividendDate(normalizeDate(exDividendDate, "exDividendDate", true));
        item.setRecordDate(normalizeDate(recordDate, "recordDate", false));
        item.setConfirmedFlg(normalizeConfirmedFlg(confirmedFlg, true));

        validateDateRules(item);
    }

    private void validateDateRules(StockDividendConfirmed item) {
        LocalDate rightsLastDay = item.getRightsLastDay();
        LocalDate exDividendDate = item.getExDividendDate();
        LocalDate recordDate = item.getRecordDate();

        if (rightsLastDay != null && exDividendDate != null && rightsLastDay.isAfter(exDividendDate)) {
            throw new IllegalArgumentException("RIGHTS_LAST_DAY 荳崎・譎壻ｺ・EX_DIVIDEND_DATE");
        }
        if (exDividendDate != null && recordDate != null && exDividendDate.isAfter(recordDate)) {
            throw new IllegalArgumentException("EX_DIVIDEND_DATE 荳崎・譎壻ｺ・RECORD_DATE");
        }
        if (rightsLastDay != null && recordDate != null && rightsLastDay.isAfter(recordDate)) {
            throw new IllegalArgumentException("RIGHTS_LAST_DAY 荳崎・譎壻ｺ・RECORD_DATE");
        }
    }

    private StockDividendConfirmedResponse toResponse(StockDividendConfirmed item) {
        return new StockDividendConfirmedResponse(
            item.getId(),
            item.getStockCode(),
            item.getStockName(),
            item.getTypeName(),
            item.getStockPrice(),
            item.getDividendAmount(),
            item.getDividendYield(),
            item.getRightsLastDay(),
            item.getExDividendDate(),
            item.getRecordDate(),
            item.getConfirmedFlg()
        );
    }

    private StockDividendConfirmedRightsLastDayStatsResponse toRightsLastDayStatsResponse(
        StockDividendConfirmedRightsLastDayStats item
    ) {
        BigDecimal avgAmount = item.getAvgDividendAmount() == null
            ? null
            : item.getAvgDividendAmount().setScale(2, RoundingMode.HALF_UP);
        BigDecimal avgYield = item.getAvgDividendYield() == null
            ? null
            : item.getAvgDividendYield().setScale(2, RoundingMode.HALF_UP);

        return new StockDividendConfirmedRightsLastDayStatsResponse(
            item.getRightsLastDay(),
            item.getTotalCount() == null ? 0L : item.getTotalCount(),
            item.getConfirmedCount() == null ? 0L : item.getConfirmedCount(),
            avgAmount,
            avgYield
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

    private BigDecimal normalizeAmount(BigDecimal value, String fieldName, boolean allowNull) {
        if (value == null) {
            if (allowNull) {
                return null;
            }
            throw new IllegalArgumentException(fieldName + " 荳崎・荳ｺ遨ｺ");
        }
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(fieldName + " 荳崎・荳ｺ雍滓焚");
        }
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private LocalDate normalizeDate(String value, String fieldName, boolean allowNull) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            if (allowNull) {
                return null;
            }
            throw new IllegalArgumentException(fieldName + " 荳崎・荳ｺ遨ｺ");
        }
        try {
            return LocalDate.parse(normalized, DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " 譌･譛滓ｼ蠑丞ｿ・｡ｻ荳ｺ yyyy-MM-dd");
        }
    }

    private String normalizeImportMode(String mode) {
        String normalized = normalizeText(mode);
        if (normalized == null) {
            return IMPORT_MODE_OVERWRITE;
        }
        String upper = normalized.toUpperCase();
        if (!IMPORT_MODE_OVERWRITE.equals(upper) && !IMPORT_MODE_SKIP_DUPLICATE.equals(upper)) {
            throw new IllegalArgumentException("蟇ｼ蜈･讓｡蠑丞ｿ・｡ｻ荳ｺ OVERWRITE 謌・SKIP_DUPLICATE");
        }
        return upper;
    }

    private SortSpec normalizeSort(String sort) {
        String normalized = normalizeText(sort);
        if (normalized == null) {
            return new SortSpec(SORT_BY_RECORD_DATE, SORT_DESC);
        }
        String[] parts = normalized.split(",");
        String field = parts[0].trim().toLowerCase();
        String direction = parts.length > 1 ? parts[1].trim().toUpperCase() : SORT_DESC;

        String sortBy = switch (field) {
            case "stockcode", "stock_code", "code" -> SORT_BY_STOCK_CODE;
            case "typename", "type_name", "industry", "industryname" -> SORT_BY_TYPE_NAME;
            case "dividendyield", "dividend_yield", "yield" -> SORT_BY_DIVIDEND_YIELD;
            case "rightslastday", "rights_last_day", "rightsday" -> SORT_BY_RIGHTS_LAST_DAY;
            case "recorddate", "record_date", "date" -> SORT_BY_RECORD_DATE;
            case "id" -> SORT_BY_ID;
            default -> throw new IllegalArgumentException("荳肴髪謖∫噪謗貞ｺ丞ｭ玲ｮｵ: " + field);
        };
        String sortDirection = switch (direction) {
            case SORT_ASC -> SORT_ASC;
            case SORT_DESC -> SORT_DESC;
            default -> throw new IllegalArgumentException("謗貞ｺ乗婿蜷大ｿ・｡ｻ譏ｯ asc 謌・desc");
        };
        return new SortSpec(sortBy, sortDirection);
    }

    private String normalizeStockCode(String stockCode) {
        String normalized = requireText(stockCode, "閧｡逾ｨ莉｣遐∽ｸ崎・荳ｺ遨ｺ").toUpperCase();
        if (normalized.length() > 10) {
            throw new IllegalArgumentException("閧｡逾ｨ莉｣遐・柄蠎ｦ荳崎・雜・ｿ・0");
        }
        if (!STOCK_CODE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("閧｡逾ｨ莉｣遐∵ｼ蠑丈ｸ肴ｭ｣遑ｮ・御ｻ・・隶ｸ蟄玲ｯ肴焚蟄・_-");
        }
        return normalized;
    }

    private String normalizeStockCodeOptional(String stockCode) {
        String normalized = normalizeText(stockCode);
        if (normalized == null) {
            return null;
        }
        return normalizeStockCode(normalized);
    }

    private String normalizeConfirmedFlg(String confirmedFlg, boolean defaultUnconfirmed) {
        String normalized = normalizeText(confirmedFlg);
        if (normalized == null) {
            return defaultUnconfirmed ? UNCONFIRMED : null;
        }

        String upper = normalized.toUpperCase();
        return switch (upper) {
            case "1", "Y" -> CONFIRMED;
            case "0", "N" -> UNCONFIRMED;
            default -> throw new IllegalArgumentException("confirmedFlg 蜿ｪ閭ｽ譏ｯ 0/1 謌・N/Y");
        };
    }

    private String normalizeConfirmedFlgForQuery(String confirmedFlg) {
        return normalizeConfirmedFlg(confirmedFlg, false);
    }

    private boolean isConfirmed(String confirmedFlg) {
        return CONFIRMED.equals(normalizeConfirmedFlg(confirmedFlg, false));
    }

    private void ensureStockExists(String stockCode) {
        if (stockRepository.findByStockCode(stockCode).isEmpty()) {
            throw new IllegalArgumentException("閧｡逾ｨ莉｣遐∽ｸ榊ｭ伜惠: " + stockCode);
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
            throw new IllegalArgumentException("ID蠢・｡ｻ荳ｺ豁｣謨ｴ謨ｰ");
        }
    }

    private String extractRawStockCode(StockDividendConfirmedCreateRequest request) {
        if (request == null) {
            return "";
        }
        String stockCode = normalizeText(request.stockCode());
        return stockCode == null ? "" : stockCode;
    }

    private String extractRawRecordDate(StockDividendConfirmedCreateRequest request) {
        if (request == null) {
            return "";
        }
        String recordDate = normalizeText(request.recordDate());
        return recordDate == null ? "" : recordDate;
    }

    private record SortSpec(String sortBy, String sortDirection) {
    }
}

package com.kabu.admin.stock.service.impl;

import com.kabu.admin.stock.dto.StockCreateRequest;
import com.kabu.admin.stock.dto.StockFavoriteCreateRequest;
import com.kabu.admin.stock.dto.StockFavoriteListResponse;
import com.kabu.admin.stock.dto.StockFavoriteResponse;
import com.kabu.admin.stock.dto.IndustryCodeOptionResponse;
import com.kabu.admin.stock.dto.StockImportFailure;
import com.kabu.admin.stock.dto.StockImportRequest;
import com.kabu.admin.stock.dto.StockImportResponse;
import com.kabu.admin.stock.dto.StockListResponse;
import com.kabu.admin.stock.dto.StockOptionResponse;
import com.kabu.admin.stock.dto.StockPriceChangeRankingListResponse;
import com.kabu.admin.stock.dto.StockPriceChangeRankingQueryRequest;
import com.kabu.admin.stock.dto.StockPriceChangeRankingResponse;
import com.kabu.admin.stock.dto.StockQueryRequest;
import com.kabu.admin.stock.dto.StockRealtimeChangeResponse;
import com.kabu.admin.stock.dto.StockResponse;
import com.kabu.admin.stock.dto.StockUpdateRequest;
import com.kabu.admin.stock.exception.StockConflictException;
import com.kabu.admin.stock.exception.StockFavoriteConflictException;
import com.kabu.admin.stock.exception.StockFavoriteNotFoundException;
import com.kabu.admin.stock.exception.StockNotFoundException;
import com.kabu.admin.stock.model.Stock;
import com.kabu.admin.stock.model.StockFavorite;
import com.kabu.admin.stock.model.StockOption;
import com.kabu.admin.stock.model.StockPriceChangeRanking;
import com.kabu.admin.stock.model.StockRealtimeChange;
import com.kabu.admin.stock.repository.StockRepository;
import com.kabu.admin.stock.service.StockService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
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
public class StockServiceImpl implements StockService {

    private static final Pattern STOCK_CODE_PATTERN = Pattern.compile("^[A-Z0-9._-]+$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String SORT_BY_ID = "ID";
    private static final String SORT_BY_STOCK_CODE = "STOCK_CODE";
    private static final String SORT_BY_TYPE_CODE = "TYPE_CODE";
    private static final String SORT_BY_MARKET = "MARKET";
    private static final String SORT_BY_STOCK_PRICE = "STOCK_PRICE";
    private static final String SORT_ASC = "ASC";
    private static final String SORT_DESC = "DESC";
    private static final String IMPORT_MODE_INCREMENTAL = "INCREMENTAL";
    private static final String IMPORT_MODE_FULL = "FULL";
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final int DEFAULT_OPTION_LIMIT = 20;
    private static final int MAX_OPTION_LIMIT = 200;
    private static final String CHANGE_TYPE_RISE = "RISE";
    private static final String CHANGE_TYPE_FALL = "FALL";

    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public StockListResponse listStocks(StockQueryRequest request) {
        int page = normalizePage(request.page());
        int size = normalizeSize(request.size());
        int offset = (page - 1) * size;

        String stockCode = normalizeStockCodeForQuery(request.stockCode());
        String stockName = normalizeText(request.stockName());
        String typeName = normalizeText(request.typeName());
        String market = normalizeText(request.market());
        String stockPriceFrom = normalizeDecimalForQuery(request.stockPriceFrom(), "stockPriceFrom");
        String stockPriceTo = normalizeDecimalForQuery(request.stockPriceTo(), "stockPriceTo");
        validateStockPriceRange(stockPriceFrom, stockPriceTo);
        String freeWord = normalizeText(request.freeWord());
        SortSpec sortSpec = normalizeSort(request.sort());

        List<StockResponse> items = stockRepository
            .findByCriteria(
                stockCode,
                stockName,
                typeName,
                market,
                stockPriceFrom,
                stockPriceTo,
                freeWord,
                sortSpec.sortBy(),
                sortSpec.sortDirection(),
                size,
                offset
            )
            .stream()
            .map(this::toResponse)
            .toList();

        long total = stockRepository.countByCriteria(stockCode, stockName, typeName, market, stockPriceFrom, stockPriceTo, freeWord);
        return new StockListResponse(items, total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public StockResponse getStockById(Long id) {
        validateId(id);
        Stock stock = stockRepository.findById(id)
            .orElseThrow(() -> new StockNotFoundException(id));
        return toResponse(stock);
    }

    @Override
    @Transactional
    public StockResponse createStock(StockCreateRequest request) {
        Stock stock = buildFromCreateRequest(request);
        stockRepository.findByStockCode(stock.getStockCode())
            .ifPresent(existing -> {
                throw new StockConflictException(stock.getStockCode());
            });

        int inserted = stockRepository.insert(stock);
        if (inserted != 1 || stock.getId() == null) {
            throw new IllegalStateException("蛻帛ｻｺ閧｡逾ｨ螟ｱ雍･");
        }
        return getStockById(stock.getId());
    }

    @Override
    @Transactional
    public StockResponse updateStock(Long id, StockUpdateRequest request) {
        validateId(id);
        stockRepository.findById(id)
            .orElseThrow(() -> new StockNotFoundException(id));

        Stock stock = buildFromUpdateRequest(request);
        stockRepository.findByStockCode(stock.getStockCode())
            .filter(existing -> !existing.getId().equals(id))
            .ifPresent(existing -> {
                throw new StockConflictException(stock.getStockCode());
            });

        stock.setId(id);
        int updated = stockRepository.update(stock);
        if (updated != 1) {
            throw new StockNotFoundException(id);
        }
        return getStockById(id);
    }

    @Override
    @Transactional
    public StockResponse updateDeleteFlag(Long id, String delFlg) {
        validateId(id);
        stockRepository.findById(id)
            .orElseThrow(() -> new StockNotFoundException(id));

        String normalized = normalizeFlag(delFlg, "delFlg", false, "0");
        int updated = stockRepository.updateDeleteFlag(id, normalized);
        if (updated != 1) {
            throw new StockNotFoundException(id);
        }
        return getStockById(id);
    }

    @Override
    @Transactional
    public void deleteStock(Long id) {
        updateDeleteFlag(id, "1");
    }

    @Override
    @Transactional(readOnly = true)
    public List<IndustryCodeOptionResponse> listIndustryCodeOptions() {
        return stockRepository.findIndustryCodeOptions()
            .stream()
            .map(item -> new IndustryCodeOptionResponse(item.getCodeKey(), item.getCodeValue()))
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockOptionResponse> listOptions(String keyword, String market, Integer limit) {
        int normalizedLimit = normalizeOptionLimit(limit);
        String normalizedKeyword = normalizeText(keyword);
        String normalizedMarket = normalizeText(market);

        return stockRepository.findOptions(normalizedKeyword, normalizedMarket, normalizedLimit)
            .stream()
            .map(this::toOptionResponse)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public StockPriceChangeRankingListResponse listPriceChangeRanking(StockPriceChangeRankingQueryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("query request is required");
        }

        LocalDate startDate = normalizeDateRequired(request.startDate(), "startDate");
        LocalDate endDate = normalizeDateRequired(request.endDate(), "endDate");
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before or equal to endDate");
        }
        String changeType = normalizeChangeType(request.changeType());
        BigDecimal threshold = normalizeNonNegativeDecimal(request.changePercent(), "changePercent", BigDecimal.ZERO);

        int page = normalizePage(request.page());
        int size = normalizeSize(request.size());
        int offset = (page - 1) * size;

        List<StockPriceChangeRankingResponse> items = stockRepository
            .findPriceChangeRanking(startDate, endDate, changeType, threshold, size, offset)
            .stream()
            .map(this::toPriceChangeRankingResponse)
            .toList();

        long total = stockRepository.countPriceChangeRanking(startDate, endDate, changeType, threshold);
        return new StockPriceChangeRankingListResponse(items, total, page, size);
    }

    @Override
    @Transactional(readOnly = true)
    public StockRealtimeChangeResponse getRealtimeChange(String stockCode) {
        String normalizedStockCode = normalizeStockCode(stockCode);

        StockRealtimeChange item = stockRepository.findRealtimeChangeByStockCode(normalizedStockCode)
            .orElseThrow(() -> new StockNotFoundException(normalizedStockCode));

        BigDecimal currentPrice = parseNumericText(item.getCurrentPriceText(), "currentPrice");
        BigDecimal referenceClosePrice = item.getReferenceClosePrice();

        BigDecimal changeAmount = null;
        BigDecimal changePercent = null;
        if (currentPrice != null && referenceClosePrice != null && referenceClosePrice.compareTo(BigDecimal.ZERO) != 0) {
            changeAmount = currentPrice.subtract(referenceClosePrice).setScale(2, RoundingMode.HALF_UP);
            changePercent = changeAmount
                .multiply(BigDecimal.valueOf(100))
                .divide(referenceClosePrice, 2, RoundingMode.HALF_UP);
        }

        return new StockRealtimeChangeResponse(
            item.getStockCode(),
            item.getStockName(),
            currentPrice,
            item.getReferenceDate(),
            referenceClosePrice,
            changeAmount,
            changePercent
        );
    }

    @Override
    @Transactional(readOnly = true)
    public StockFavoriteListResponse listFavorites() {
        List<StockFavoriteResponse> items = stockRepository.findFavorites()
            .stream()
            .map(this::toFavoriteResponse)
            .toList();
        long total = stockRepository.countFavorites();
        return new StockFavoriteListResponse(items, total);
    }

    @Override
    @Transactional
    public StockFavoriteResponse addFavorite(StockFavoriteCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("request is required");
        }

        String stockCode = normalizeStockCode(request.stockCode());
        stockRepository.findByStockCode(stockCode)
            .orElseThrow(() -> new StockNotFoundException(stockCode));
        stockRepository.findFavoriteByStockCode(stockCode)
            .ifPresent(existing -> {
                throw new StockFavoriteConflictException(stockCode);
            });

        int inserted = stockRepository.insertFavorite(stockCode);
        if (inserted != 1) {
            throw new IllegalStateException("failed to insert stock favorite");
        }

        StockFavorite favorite = stockRepository.findFavoriteByStockCode(stockCode)
            .orElseThrow(() -> new IllegalStateException("failed to load inserted stock favorite"));
        return toFavoriteResponse(favorite);
    }

    @Override
    @Transactional
    public void removeFavorite(Long id) {
        validateId(id);
        stockRepository.findFavoriteById(id)
            .orElseThrow(() -> new StockFavoriteNotFoundException(id));
        int deleted = stockRepository.deleteFavoriteById(id);
        if (deleted != 1) {
            throw new StockFavoriteNotFoundException(id);
        }
    }

    @Override
    @Transactional
    public StockImportResponse importStocks(StockImportRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("蟇ｼ蜈･隸ｷ豎ゆｸ崎・荳ｺ遨ｺ");
        }

        String mode = normalizeImportMode(request.mode());
        List<StockCreateRequest> items = request.items();
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("蟇ｼ蜈･謨ｰ謐ｮ荳崎・荳ｺ遨ｺ");
        }

        int total = items.size();
        int success = 0;
        int created = 0;
        int updated = 0;
        int failed = 0;
        List<String> successStockCodes = new ArrayList<>();
        List<StockImportFailure> failures = new ArrayList<>();

        for (StockCreateRequest item : items) {
            try {
                Stock stock = buildFromCreateRequest(item);
                Optional<Stock> existing = stockRepository.findByStockCode(stock.getStockCode());
                if (existing.isPresent()) {
                    stock.setId(existing.get().getId());
                    int updateResult = stockRepository.update(stock);
                    if (updateResult != 1) {
                        throw new IllegalStateException("譖ｴ譁ｰ閧｡逾ｨ螟ｱ雍･");
                    }
                    updated++;
                } else {
                    int insertResult = stockRepository.insert(stock);
                    if (insertResult != 1) {
                        throw new IllegalStateException("譁ｰ蠅櫁ぃ逾ｨ螟ｱ雍･");
                    }
                    created++;
                }
                success++;
                successStockCodes.add(stock.getStockCode());
            } catch (RuntimeException ex) {
                failed++;
                failures.add(new StockImportFailure(extractRawStockCode(item), ex.getMessage()));
            }
        }

        if (IMPORT_MODE_FULL.equals(mode)) {
            stockRepository.markDeletedExceptCodes(successStockCodes);
        }

        return new StockImportResponse(total, success, created, updated, failed, failures);
    }

    private Stock buildFromCreateRequest(StockCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("隸ｷ豎ゆｸ崎・荳ｺ遨ｺ");
        }
        Stock stock = new Stock();
        stock.setStockCode(normalizeStockCode(request.stockCode()));
        applyMutableFields(
            stock,
            request.stockName(),
            request.typeCode(),
            request.typeName(),
            request.market(),
            request.securities(),
            request.bank(),
            request.date(),
            request.settlement(),
            request.settlementDay(),
            request.established(),
            request.listed(),
            request.stockPrice(),
            request.tradingUnit(),
            request.totalZika(),
            request.dividend(),
            request.expectedDividend(),
            request.fixedDividend(),
            request.per(),
            request.eps(),
            request.pbr(),
            request.roe(),
            request.roa(),
            request.preferentialFlg(),
            request.dividendFlg(),
            request.ichimokuUpperFlg(),
            request.ichimokuDowloadFlg(),
            request.delFlg(),
            request.homepage(),
            request.newsUrl(),
            request.irUrl(),
            request.tekusyoku(),
            request.renketuZigyo(),
            request.supplier(),
            request.salesDestination()
        );
        return stock;
    }

    private Stock buildFromUpdateRequest(StockUpdateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("隸ｷ豎ゆｸ崎・荳ｺ遨ｺ");
        }
        Stock stock = new Stock();
        stock.setStockCode(normalizeStockCode(request.stockCode()));
        applyMutableFields(
            stock,
            request.stockName(),
            request.typeCode(),
            request.typeName(),
            request.market(),
            request.securities(),
            request.bank(),
            request.date(),
            request.settlement(),
            request.settlementDay(),
            request.established(),
            request.listed(),
            request.stockPrice(),
            request.tradingUnit(),
            request.totalZika(),
            request.dividend(),
            request.expectedDividend(),
            request.fixedDividend(),
            request.per(),
            request.eps(),
            request.pbr(),
            request.roe(),
            request.roa(),
            request.preferentialFlg(),
            request.dividendFlg(),
            request.ichimokuUpperFlg(),
            request.ichimokuDowloadFlg(),
            request.delFlg(),
            request.homepage(),
            request.newsUrl(),
            request.irUrl(),
            request.tekusyoku(),
            request.renketuZigyo(),
            request.supplier(),
            request.salesDestination()
        );
        return stock;
    }

    private void applyMutableFields(
        Stock stock,
        String stockName,
        String typeCode,
        String typeName,
        String market,
        String securities,
        String bank,
        String date,
        String settlement,
        String settlementDay,
        String established,
        String listed,
        String stockPrice,
        String tradingUnit,
        String totalZika,
        String dividend,
        String expectedDividend,
        String fixedDividend,
        String per,
        String eps,
        String pbr,
        String roe,
        String roa,
        String preferentialFlg,
        String dividendFlg,
        String ichimokuUpperFlg,
        String ichimokuDowloadFlg,
        String delFlg,
        String homepage,
        String newsUrl,
        String irUrl,
        String tekusyoku,
        String renketuZigyo,
        String supplier,
        String salesDestination
    ) {
        stock.setStockName(requireText(stockName, "閧｡逾ｨ蜷咲ｧｰ荳崎・荳ｺ遨ｺ"));
        stock.setTypeCode(normalizeText(typeCode));
        stock.setTypeName(normalizeText(typeName));
        stock.setMarket(normalizeText(market));
        stock.setSecurities(normalizeText(securities));
        stock.setBank(normalizeText(bank));
        stock.setDate(normalizeDateValue(date, "date"));
        stock.setSettlement(normalizeText(settlement));
        stock.setSettlementDay(normalizeDateValue(settlementDay, "settlementDay"));
        stock.setEstablished(normalizeDateValue(established, "established"));
        stock.setListed(normalizeDateValue(listed, "listed"));
        stock.setStockPrice(normalizeDecimalLike(stockPrice, "stockPrice"));
        stock.setTradingUnit(normalizeDecimalLike(tradingUnit, "tradingUnit"));
        stock.setTotalZika(normalizeDecimalLike(totalZika, "totalZika"));
        stock.setDividend(normalizeText(dividend));
        stock.setExpectedDividend(normalizeText(expectedDividend));
        stock.setFixedDividend(normalizeText(fixedDividend));
        stock.setPer(normalizeDecimalLike(per, "per"));
        stock.setEps(normalizeDecimalLike(eps, "eps"));
        stock.setPbr(normalizeDecimalLike(pbr, "pbr"));
        stock.setRoe(normalizeDecimalLike(roe, "roe"));
        stock.setRoa(normalizeDecimalLike(roa, "roa"));
        stock.setPreferentialFlg(normalizeFlag(preferentialFlg, "preferentialFlg", true, "0"));
        stock.setDividendFlg(normalizeFlag(dividendFlg, "dividendFlg", true, "0"));
        stock.setIchimokuUpperFlg(normalizeFlag(ichimokuUpperFlg, "ichimokuUpperFlg", true, "0"));
        stock.setIchimokuDowloadFlg(normalizeFlag(ichimokuDowloadFlg, "ichimokuDowloadFlg", true, "0"));
        stock.setDelFlg(normalizeFlag(delFlg, "delFlg", true, "0"));
        stock.setHomepage(normalizeUrl(homepage, "homepage"));
        stock.setNewsUrl(normalizeUrl(newsUrl, "newsUrl"));
        stock.setIrUrl(normalizeUrl(irUrl, "irUrl"));
        stock.setTekusyoku(normalizeText(tekusyoku));
        stock.setRenketuZigyo(normalizeText(renketuZigyo));
        stock.setSupplier(normalizeText(supplier));
        stock.setSalesDestination(normalizeText(salesDestination));
    }

    private StockResponse toResponse(Stock stock) {
        return new StockResponse(
            stock.getId(),
            stock.getStockCode(),
            stock.getStockName(),
            stock.getTypeCode(),
            stock.getTypeName(),
            stock.getMarket(),
            stock.getSecurities(),
            stock.getBank(),
            stock.getDate(),
            stock.getSettlement(),
            stock.getSettlementDay(),
            stock.getEstablished(),
            stock.getListed(),
            stock.getStockPrice(),
            stock.getTradingUnit(),
            stock.getTotalZika(),
            stock.getDividend(),
            stock.getExpectedDividend(),
            stock.getFixedDividend(),
            stock.getPer(),
            stock.getEps(),
            stock.getPbr(),
            stock.getRoe(),
            stock.getRoa(),
            stock.getPreferentialFlg(),
            stock.getDividendFlg(),
            stock.getIchimokuUpperFlg(),
            stock.getIchimokuDowloadFlg(),
            stock.getDelFlg(),
            stock.getHomepage(),
            stock.getNewsUrl(),
            stock.getIrUrl(),
            stock.getTekusyoku(),
            stock.getRenketuZigyo(),
            stock.getSupplier(),
            stock.getSalesDestination()
        );
    }

    private StockOptionResponse toOptionResponse(StockOption option) {
        return new StockOptionResponse(
            option.getId(),
            option.getStockCode(),
            option.getStockName(),
            option.getMarket()
        );
    }

    private StockPriceChangeRankingResponse toPriceChangeRankingResponse(StockPriceChangeRanking item) {
        return new StockPriceChangeRankingResponse(
            item.getStockCode(),
            item.getStockName(),
            item.getStartDate(),
            item.getEndDate(),
            item.getStartClosePrice(),
            item.getEndClosePrice(),
            item.getChangeAmount(),
            item.getChangePercent()
        );
    }

    private StockFavoriteResponse toFavoriteResponse(StockFavorite item) {
        return new StockFavoriteResponse(
            item.getId(),
            item.getStockCode(),
            item.getStockName(),
            item.getTypeName(),
            item.getMarket(),
            item.getStockPrice(),
            item.getCreatedAt()
        );
    }

    private String normalizeStockCode(String stockCode) {
        String normalized = requireText(stockCode, "閧｡逾ｨ莉｣遐∽ｸ崎・荳ｺ遨ｺ").toUpperCase();
        if (!STOCK_CODE_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException("閧｡逾ｨ莉｣遐∵ｼ蠑丈ｸ肴ｭ｣遑ｮ・御ｻ・・隶ｸ蟄玲ｯ肴焚蟄・_-");
        }
        return normalized;
    }

    private String normalizeStockCodeForQuery(String stockCode) {
        String normalized = normalizeText(stockCode);
        if (normalized == null) {
            return null;
        }
        return normalized.toUpperCase();
    }

    private String normalizeDeleteFlagForQuery(String delFlg) {
        String normalized = normalizeText(delFlg);
        if (normalized == null) {
            return "0";
        }
        if ("ALL".equalsIgnoreCase(normalized) || "*".equals(normalized)) {
            return null;
        }
        return normalizeFlag(normalized, "delFlg", false, "0");
    }

    private SortSpec normalizeSort(String sort) {
        String normalized = normalizeText(sort);
        if (normalized == null) {
            return new SortSpec(SORT_BY_STOCK_CODE, SORT_ASC);
        }

        String[] parts = normalized.split(",");
        String field = parts[0].trim().toLowerCase();
        String direction = parts.length > 1 ? parts[1].trim().toUpperCase() : SORT_DESC;

        String sortBy = switch (field) {
            case "id" -> SORT_BY_ID;
            case "stockcode", "stock_code" -> SORT_BY_STOCK_CODE;
            case "typecode", "type_code" -> SORT_BY_TYPE_CODE;
            case "market" -> SORT_BY_MARKET;
            case "stockprice", "stock_price" -> SORT_BY_STOCK_PRICE;
            default -> throw new IllegalArgumentException("荳肴髪謖∫噪謗貞ｺ丞ｭ玲ｮｵ: " + field);
        };

        String sortDirection = switch (direction) {
            case SORT_ASC -> SORT_ASC;
            case SORT_DESC -> SORT_DESC;
            default -> throw new IllegalArgumentException("謗貞ｺ乗婿蜷大ｿ・｡ｻ譏ｯ asc 謌・desc");
        };

        return new SortSpec(sortBy, sortDirection);
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

    private int normalizeOptionLimit(Integer limit) {
        if (limit == null || limit < 1) {
            return DEFAULT_OPTION_LIMIT;
        }
        return Math.min(limit, MAX_OPTION_LIMIT);
    }

    private LocalDate normalizeDateRequired(String value, String fieldName) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        try {
            return LocalDate.parse(normalized, DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " must be yyyy-MM-dd");
        }
    }

    private String normalizeChangeType(String changeType) {
        String normalized = requireText(changeType, "changeType is required").toUpperCase();
        return switch (normalized) {
            case CHANGE_TYPE_RISE -> CHANGE_TYPE_RISE;
            case CHANGE_TYPE_FALL -> CHANGE_TYPE_FALL;
            default -> throw new IllegalArgumentException("changeType must be RISE or FALL");
        };
    }


    private String normalizeDecimalForQuery(String value, String fieldName) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            return null;
        }

        String sanitized = normalized.replace(",", "").replace("，", "").trim();
        try {
            BigDecimal parsed = new BigDecimal(sanitized);
            return parsed.toPlainString();
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be numeric");
        }
    }

    private void validateStockPriceRange(String stockPriceFrom, String stockPriceTo) {
        if (stockPriceFrom == null || stockPriceTo == null) {
            return;
        }

        BigDecimal from = new BigDecimal(stockPriceFrom);
        BigDecimal to = new BigDecimal(stockPriceTo);
        if (from.compareTo(to) > 0) {
            throw new IllegalArgumentException("stockPriceFrom must be <= stockPriceTo");
        }
    }

    private BigDecimal normalizeNonNegativeDecimal(String value, String fieldName, BigDecimal defaultValue) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            return defaultValue;
        }
        try {
            BigDecimal parsed = new BigDecimal(normalized);
            if (parsed.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException(fieldName + " must be >= 0");
            }
            return parsed.setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be numeric");
        }
    }

    private String normalizeImportMode(String mode) {
        String normalized = normalizeText(mode);
        if (normalized == null) {
            return IMPORT_MODE_INCREMENTAL;
        }
        String upper = normalized.toUpperCase();
        if (!IMPORT_MODE_INCREMENTAL.equals(upper) && !IMPORT_MODE_FULL.equals(upper)) {
            throw new IllegalArgumentException("蟇ｼ蜈･讓｡蠑丞ｿ・｡ｻ荳ｺ INCREMENTAL 謌・FULL");
        }
        return upper;
    }

    private String normalizeDateValue(String value, String fieldName) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            return null;
        }
        try {
            LocalDate.parse(normalized, DATE_FORMATTER);
            return normalized;
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException(fieldName + " 譌･譛滓ｼ蠑丞ｿ・｡ｻ荳ｺ yyyy-MM-dd");
        }
    }

    private String normalizeDecimalLike(String value, String fieldName) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            return null;
        }

        String sanitized = normalized.replace(",", "").replace("%", "");
        if (sanitized.isEmpty()) {
            return null;
        }

        try {
            new BigDecimal(sanitized);
            return normalized;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " 蠢・｡ｻ荳ｺ謨ｰ蛟ｼ");
        }
    }

    private BigDecimal parseNumericText(String value, String fieldName) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            return null;
        }

        String sanitized = normalized
            .replace(",", "")
            .replace("，", "")
            .replace("%", "")
            .replace("％", "")
            .replace("円", "")
            .replace("¥", "")
            .replace("￥", "")
            .replaceAll("\\s+", "");
        if (sanitized.isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(sanitized).setScale(2, RoundingMode.HALF_UP);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " is not numeric: " + normalized);
        }
    }

    private String normalizeUrl(String value, String fieldName) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            return null;
        }

        try {
            URI uri = new URI(normalized);
            String scheme = uri.getScheme();
            if (scheme == null || (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme))) {
                throw new IllegalArgumentException(fieldName + " 蠢・｡ｻ譏ｯ http/https URL");
            }
            return normalized;
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException(fieldName + " URL 譬ｼ蠑丈ｸ肴ｭ｣遑ｮ");
        }
    }

    private String normalizeFlag(String value, String fieldName, boolean allowNull, String defaultValue) {
        String normalized = normalizeText(value);
        if (normalized == null) {
            if (allowNull) {
                return defaultValue;
            }
            throw new IllegalArgumentException(fieldName + " 荳崎・荳ｺ遨ｺ");
        }

        String upper = normalized.toUpperCase();
        return switch (upper) {
            case "1", "Y" -> "1";
            case "0", "N" -> "0";
            default -> throw new IllegalArgumentException(fieldName + " 蠢・｡ｻ荳ｺ 0/1 謌・Y/N");
        };
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

    private String extractRawStockCode(StockCreateRequest request) {
        if (request == null) {
            return "";
        }
        String code = normalizeText(request.stockCode());
        return code == null ? "" : code;
    }

    private record SortSpec(String sortBy, String sortDirection) {
    }
}


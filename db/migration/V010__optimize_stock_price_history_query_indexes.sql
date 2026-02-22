-- Optimize stock price history filtering by industry code and ordering by stock code.
-- Target: MySQL 8.x

ALTER TABLE `M_STOCK`
  ADD INDEX `idx_type_name_stock_code` (`TYPE_NAME`, `STOCK_CODE`),
  ADD INDEX `idx_type_code_stock_code` (`TYPE_CODE`, `STOCK_CODE`);

ALTER TABLE `M_STOCK_PRICE_HISTORY`
  ADD INDEX `idx_trans_date_stock_code` (`TRANS_DATE`, `STOCK_CODE`);

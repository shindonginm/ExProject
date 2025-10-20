/* 새 환경에서도 필요하면 item_tbl은 만들어 둠 */
CREATE TABLE IF NOT EXISTS item_tbl (
                                        item_no     BIGINT PRIMARY KEY,
                                        item_name   VARCHAR(255) NOT NULL,
    item_price  BIGINT NOT NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/* ---------- order_tbl 보정: 컬럼이 없으면 추가 ---------- */

/* order_state (승인 상태) */
SET @col := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME='order_tbl'
    AND COLUMN_NAME='order_state'
);
SET @sql := IF(@col=0,
  'ALTER TABLE order_tbl ADD COLUMN order_state VARCHAR(50) DEFAULT ''승인대기'' AFTER order_date',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

/* order_deli_state (납품 상태) */
SET @col := (
  SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME='order_tbl'
    AND COLUMN_NAME='order_deli_state'
);
SET @sql := IF(@col=0,
  'ALTER TABLE order_tbl ADD COLUMN order_deli_state VARCHAR(50) DEFAULT ''납품대기'' AFTER order_state',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

/* ---------- order_tbl 보정: 인덱스가 없으면 생성 ---------- */

/* 날짜 */
SET @exists := (
  SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME='order_tbl' AND INDEX_NAME='idx_order_date'
);
SET @sql := IF(@exists=0,
  'CREATE INDEX idx_order_date ON order_tbl(order_date)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

/* 품목 */
SET @exists := (
  SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME='order_tbl' AND INDEX_NAME='idx_order_item'
);
SET @sql := IF(@exists=0,
  'CREATE INDEX idx_order_item ON order_tbl(item_no)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

/* 납품 상태 */
SET @exists := (
  SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME='order_tbl' AND INDEX_NAME='idx_order_deli_state'
);
SET @sql := IF(@exists=0,
  'CREATE INDEX idx_order_deli_state ON order_tbl(order_deli_state)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

/* 승인 상태 */
SET @exists := (
  SELECT COUNT(1) FROM INFORMATION_SCHEMA.STATISTICS
  WHERE TABLE_SCHEMA = DATABASE()
    AND TABLE_NAME='order_tbl' AND INDEX_NAME='idx_order_state'
);
SET @sql := IF(@exists=0,
  'CREATE INDEX idx_order_state ON order_tbl(order_state)',
  'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

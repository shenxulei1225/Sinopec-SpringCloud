-- ent_equipment 等早期 Dedicated 表由 V1 导入 schema，未含 status；EntityDO 查询会 SELECT status 导致 500。
-- 与 DynamicTableServiceImpl / 其余 ent_* 表对齐：status INTEGER DEFAULT 1。

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  r RECORD;
  idx_name text;
BEGIN
  FOR r IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_name LIKE 'ent\_%' ESCAPE '\'
      AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns c
        WHERE c.table_schema = 'dynamicbusiness'
          AND c.table_name = t.table_name
          AND c.column_name = 'status'
      )
  LOOP
    EXECUTE format(
      'ALTER TABLE %I.%I ADD COLUMN status INTEGER DEFAULT 1',
      'dynamicbusiness',
      r.table_name
    );
    EXECUTE format(
      'COMMENT ON COLUMN %I.%I.status IS %L',
      'dynamicbusiness',
      r.table_name,
      '状态（1-启用，0-禁用）'
    );
    EXECUTE format(
      'UPDATE %I.%I SET status = 1 WHERE status IS NULL',
      'dynamicbusiness',
      r.table_name
    );
    idx_name := 'idx_' || r.table_name || '_status';
    EXECUTE format(
      'CREATE INDEX IF NOT EXISTS %I ON %I.%I (status)',
      idx_name,
      'dynamicbusiness',
      r.table_name
    );
  END LOOP;
END $$;

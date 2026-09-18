-- V112: SOP 发布状态语义收敛为 UNPUBLISHED / PUBLISHED（替代 DRAFT / PUBLISHED）
-- 说明：
-- 1) 撤回发布后状态应为未发布（UNPUBLISHED），不是草稿。
-- 2) 草稿编辑态由前端临时态承载，不写入 publish_status。

SET search_path TO dynamicbusiness, public;

-- 主表默认值
ALTER TABLE IF EXISTS dynamicbusiness.ent_sop
  ALTER COLUMN publish_status SET DEFAULT 'UNPUBLISHED';

-- 历史数据：DRAFT -> UNPUBLISHED
UPDATE dynamicbusiness.ent_sop
SET publish_status = 'UNPUBLISHED'
WHERE upper(coalesce(publish_status, '')) = 'DRAFT'
  AND deleted = FALSE;

-- 租户分表统一处理
DO $$
DECLARE
  r record;
BEGIN
  FOR r IN
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness'
      AND table_name ~ '^ent_sop_t[0-9]+$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ALTER COLUMN publish_status SET DEFAULT ''UNPUBLISHED''',
      r.table_name
    );
    EXECUTE format(
      'UPDATE dynamicbusiness.%I SET publish_status = ''UNPUBLISHED'' WHERE upper(coalesce(publish_status, '''')) = ''DRAFT'' AND deleted = FALSE',
      r.table_name
    );
  END LOOP;
END $$;


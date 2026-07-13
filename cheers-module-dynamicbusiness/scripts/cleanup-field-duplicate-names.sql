-- ============================================================================
-- 字段库历史脏数据清理（本地/开发库一次性脚本，非 Flyway）
--
-- 说明：
-- - 字段库允许「一名称多条记录」：同一显示名可被不同业务字段使用，靠分组与字段 id 区分
-- - 同一字段（同一 id）可关联多个分组，在多个分组中出现不算重复
-- - 本脚本只清理：无引用的 legacy SYSTEM 字段行（历史种子冗余）
-- - 不再强制重命名去重；若已执行过旧版重命名，需另行按需还原名称
-- ============================================================================

SET search_path TO dynamicbusiness;

BEGIN;

-- A. 软删 orphan SYSTEM 字段
UPDATE dynamic_field f
SET deleted      = true,
    updater      = 'cleanup-field-dedupe',
    update_time  = CURRENT_TIMESTAMP
WHERE f.deleted = false
  AND f.source = 'SYSTEM'
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model_field_assignment a
      WHERE a.field_id = f.id AND a.deleted = false
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_entity_type_base_field b
      WHERE b.library_field_id = f.id AND b.deleted = false
  );

-- B. BASE 字段：重名时追加业务类型名（仅处理仍重名的条目）
UPDATE dynamic_field f
SET name        = src.new_name,
    updater     = 'cleanup-field-dedupe',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT bf.library_field_id AS field_id,
           f2.name || '（' || et.name || '）' AS new_name
    FROM dynamic_entity_type_base_field bf
    JOIN dynamic_field f2 ON f2.id = bf.library_field_id AND f2.deleted = false
    JOIN dynamic_entity_type et ON et.code = bf.entity_type_code AND et.deleted = false
    WHERE bf.deleted = false
      AND f2.source = 'BASE'
      AND f2.name IN (
          SELECT name FROM dynamic_field WHERE deleted = false GROUP BY name HAVING COUNT(*) > 1
      )
) src
WHERE f.id = src.field_id
  AND f.deleted = false
  AND f.name NOT LIKE '%（%）';

-- C. 仍被引用的 legacy 字段（巡检域等）
UPDATE dynamic_field SET name = '所属设施（巡检对象）', updater = 'cleanup-field-dedupe', update_time = CURRENT_TIMESTAMP
WHERE code = 'FLD-POB-001' AND deleted = false;

UPDATE dynamic_field SET name = '关联设备（巡检对象）', updater = 'cleanup-field-dedupe', update_time = CURRENT_TIMESTAMP
WHERE code = 'FLD-POB-002' AND deleted = false;

UPDATE dynamic_field SET name = '所属分区（巡检对象）', updater = 'cleanup-field-dedupe', update_time = CURRENT_TIMESTAMP
WHERE code = 'FLD-POB-003' AND deleted = false;

UPDATE dynamic_field SET name = '所属设施（巡检点）', updater = 'cleanup-field-dedupe', update_time = CURRENT_TIMESTAMP
WHERE code = 'FLD-PPT-001' AND deleted = false;

UPDATE dynamic_field SET name = '所属设施（巡检任务）', updater = 'cleanup-field-dedupe', update_time = CURRENT_TIMESTAMP
WHERE code = 'FLD-TSK-024' AND deleted = false;

UPDATE dynamic_field SET name = '优先级（巡检任务）', updater = 'cleanup-field-dedupe', update_time = CURRENT_TIMESTAMP
WHERE code = 'FLD-TSK-005' AND deleted = false;

UPDATE dynamic_field SET name = '投运日期（设施）', updater = 'cleanup-field-dedupe', update_time = CURRENT_TIMESTAMP
WHERE code = 'FLD-FAC-EXT-002' AND deleted = false;

-- D. 同步基础字段登记表的显示名
UPDATE dynamic_entity_type_base_field bf
SET field_name  = f.name,
    updater     = 'cleanup-field-dedupe',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE bf.library_field_id = f.id
  AND bf.deleted = false
  AND f.deleted = false
  AND bf.field_name IS DISTINCT FROM f.name;

COMMIT;

-- 验证：应无 active 重名
SELECT name, COUNT(*) AS cnt
FROM dynamic_field
WHERE deleted = false
GROUP BY name
HAVING COUNT(*) > 1
ORDER BY cnt DESC;

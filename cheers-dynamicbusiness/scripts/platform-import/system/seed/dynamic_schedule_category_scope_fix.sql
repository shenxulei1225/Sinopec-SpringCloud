-- SCOPED 域入口共用主数据（storage）分类树：将 patrol_schedule 误建分类并入 schedule。
-- 执行后请运行 system/evict_category_tree_cache.sh 清理 Redis 分类树缓存。

SET search_path TO dynamicbusiness;

BEGIN;

DO $$
DECLARE
  schedule_root_id bigint;
  patrol_root_id bigint;
BEGIN
  SELECT id INTO schedule_root_id
  FROM dynamic_category
  WHERE category_type_code = 'schedule'
    AND parent_id IS NULL
    AND deleted = false
  ORDER BY id
  LIMIT 1;

  SELECT id INTO patrol_root_id
  FROM dynamic_category
  WHERE category_type_code = 'patrol_schedule'
    AND parent_id IS NULL
    AND deleted = false
  ORDER BY id
  LIMIT 1;

  IF schedule_root_id IS NULL THEN
    RAISE EXCEPTION 'schedule 分类根不存在，请先 bootstrap schedule 分类';
  END IF;

  IF patrol_root_id IS NOT NULL THEN
    UPDATE dynamic_category
    SET category_type_code = 'schedule',
        parent_id = schedule_root_id,
        level = 2,
        tree_path = '/' || schedule_root_id::text || '/' || id::text || '/',
        parent_code = (SELECT code FROM dynamic_category WHERE id = schedule_root_id),
        updater = 'scope-fix',
        update_time = CURRENT_TIMESTAMP
    WHERE category_type_code = 'patrol_schedule'
      AND parent_id = patrol_root_id
      AND deleted = false;

    UPDATE dynamic_category c
    SET category_type_code = 'schedule',
        level = 3,
        tree_path = '/' || schedule_root_id::text || '/' || c.parent_id::text || '/' || c.id::text || '/',
        parent_code = (SELECT code FROM dynamic_category p WHERE p.id = c.parent_id),
        updater = 'scope-fix',
        update_time = CURRENT_TIMESTAMP
    WHERE c.category_type_code = 'patrol_schedule'
      AND c.parent_id IN (
        SELECT id FROM dynamic_category
        WHERE category_type_code = 'schedule'
          AND parent_id = schedule_root_id
          AND deleted = false
      )
      AND c.deleted = false;

    DELETE FROM dynamic_category WHERE id = patrol_root_id;
  END IF;
END $$;

DELETE FROM dynamic_category_type
WHERE category_type_code = 'patrol_schedule'
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_category
    WHERE category_type_code = 'patrol_schedule'
      AND deleted = false
  );

UPDATE dm_entity_dimension
SET category_dimension_meta = jsonb_set(
      category_dimension_meta,
      '{categoryTypeCode}',
      to_jsonb('schedule'::text),
      true
    ),
    updater = 'scope-fix',
    update_time = CURRENT_TIMESTAMP
WHERE dimension_kind = 'CATEGORY'
  AND category_dimension_meta->>'categoryTypeCode' = 'patrol_schedule';

UPDATE dm_entity_dimension d
SET category_dimension_meta = jsonb_set(
      COALESCE(category_dimension_meta, '{}'::jsonb),
      '{categoryTypeCode}',
      to_jsonb(et.base_entity_type_code::text),
      true
    ),
    updater = 'scope-fix',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_entity_type et
WHERE d.entity_type_code = et.code
  AND et.entry_kind = 'SCOPED'
  AND et.base_entity_type_code IS NOT NULL
  AND d.dimension_kind = 'CATEGORY'
  AND (
    d.category_dimension_meta->>'categoryTypeCode' IS NULL
    OR d.category_dimension_meta->>'categoryTypeCode' = et.code
  );

COMMIT;

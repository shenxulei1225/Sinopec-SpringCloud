-- Normalize legacy business_type_code values to canonical codes in dynamic_business_type.
-- zhgl 迁移后模型/实体/关联表可能仍使用拼音旧 code（如 ke_hu、jian_cha_nei_rong）。
-- Idempotent: only updates rows where legacy code exists in mapping table.

SET search_path TO dynamicbusiness;

CREATE TABLE IF NOT EXISTS legacy_business_type_code_map (
    legacy_code    VARCHAR(64) PRIMARY KEY,
    canonical_code VARCHAR(64) NOT NULL
);

INSERT INTO legacy_business_type_code_map (legacy_code, canonical_code) VALUES
    ('jian_cha_nei_rong',   'inspection_item'),
    ('shou_fei',            'billing'),
    ('ke_hu',               'customer'),
    ('ying_ji_zi_yuan',     'emergency_resource'),
    ('ying_ji_dui_wu',      'emergency_team'),
    ('dian_wei',            'inspection_point'),
    ('lu_xian_guan_li',     'route'),
    ('spare_part',          'spare_parts'),
    ('xun_jian',            'patrol'),
    ('customer_management', 'customer')
ON CONFLICT (legacy_code) DO UPDATE SET canonical_code = EXCLUDED.canonical_code;

-- ---------------------------------------------------------------------------
-- 1. 模型与字段分配
-- ---------------------------------------------------------------------------
UPDATE dynamic_model m
SET business_type_code = map.canonical_code,
    updater            = 'migration',
    update_time        = CURRENT_TIMESTAMP
FROM legacy_business_type_code_map map
WHERE m.business_type_code = map.legacy_code
  AND m.deleted = false;

UPDATE dynamic_model_field_assignment a
SET target_business_type = map.canonical_code,
    updater              = 'migration',
    update_time          = CURRENT_TIMESTAMP
FROM legacy_business_type_code_map map
WHERE a.target_business_type = map.legacy_code
  AND a.deleted = false;

UPDATE dynamic_model_category_relation r
SET business_type_code = map.canonical_code,
    updater            = 'migration',
    update_time        = CURRENT_TIMESTAMP
FROM legacy_business_type_code_map map
WHERE r.business_type_code = map.legacy_code
  AND r.deleted = false;

-- ---------------------------------------------------------------------------
-- 2. 业务类型配置（V2 seed 中的旧 code）
-- ---------------------------------------------------------------------------
UPDATE dynamic_business_type_base_field f
SET business_type_code = map.canonical_code,
    updater            = 'migration',
    update_time        = CURRENT_TIMESTAMP
FROM legacy_business_type_code_map map
WHERE f.business_type_code = map.legacy_code
  AND f.deleted = false;

UPDATE dynamic_business_type_config c
SET business_type_code = map.canonical_code,
    updater            = 'migration',
    update_time        = CURRENT_TIMESTAMP
FROM legacy_business_type_code_map map
WHERE c.business_type_code = map.legacy_code
  AND c.deleted = false;

UPDATE dynamic_business_type_relation r
SET source_business_type_code = map.canonical_code,
    updater                   = 'migration',
    update_time               = CURRENT_TIMESTAMP
FROM legacy_business_type_code_map map
WHERE r.source_business_type_code = map.legacy_code
  AND r.deleted = false;

UPDATE dynamic_business_type_relation r
SET target_business_type_code = map.canonical_code,
    updater                   = 'migration',
    update_time               = CURRENT_TIMESTAMP
FROM legacy_business_type_code_map map
WHERE r.target_business_type_code = map.legacy_code
  AND r.deleted = false;

-- ---------------------------------------------------------------------------
-- 3. 实体侧关联
-- ---------------------------------------------------------------------------
UPDATE dynamic_entity_category_relation r
SET business_type_code = map.canonical_code,
    updater            = 'migration',
    update_time        = CURRENT_TIMESTAMP
FROM legacy_business_type_code_map map
WHERE r.business_type_code = map.legacy_code
  AND r.deleted = false;

UPDATE dynamic_entity_relation r
SET source_business_type_code = map.canonical_code,
    updater                   = 'migration',
    update_time               = CURRENT_TIMESTAMP
FROM legacy_business_type_code_map map
WHERE r.source_business_type_code = map.legacy_code
  AND r.deleted = false;

UPDATE dynamic_entity_relation r
SET target_business_type_code = map.canonical_code,
    updater                   = 'migration',
    update_time               = CURRENT_TIMESTAMP
FROM legacy_business_type_code_map map
WHERE r.target_business_type_code = map.legacy_code
  AND r.deleted = false;

-- ---------------------------------------------------------------------------
-- 4. 专用实体表 biz_*（存在 business_type_code 列时更新）
-- ---------------------------------------------------------------------------
DO $$
DECLARE
  tbl text;
  biz_tables text[] := ARRAY[
    'biz_equipment', 'biz_region', 'biz_task', 'biz_inspection_item',
    'biz_emergency_resource', 'biz_customer', 'biz_inspection_point',
    'biz_emergency_team', 'biz_route', 'biz_billing', 'biz_spare_part',
    'biz_pipeline', 'biz_personnel', 'biz_maintenance', 'biz_patrol',
    'biz_fault', 'biz_emergency'
  ];
  updated bigint;
BEGIN
  FOREACH tbl IN ARRAY biz_tables LOOP
    IF EXISTS (
      SELECT 1 FROM information_schema.columns
      WHERE table_schema = 'dynamicbusiness' AND table_name = tbl AND column_name = 'business_type_code'
    ) THEN
      EXECUTE format(
        'UPDATE dynamicbusiness.%I e
         SET business_type_code = map.canonical_code,
             updater = ''migration'',
             update_time = CURRENT_TIMESTAMP
         FROM legacy_business_type_code_map map
         WHERE e.business_type_code = map.legacy_code',
        tbl
      );
      GET DIAGNOSTICS updated = ROW_COUNT;
      IF updated > 0 THEN
        RAISE NOTICE 'normalized business_type_code in %: % rows', tbl, updated;
      END IF;
    END IF;
  END LOOP;
END $$;

-- ---------------------------------------------------------------------------
-- 5. 通用实体表（若使用 GENERIC 存储）
-- ---------------------------------------------------------------------------
UPDATE dynamic_entity e
SET business_type_code = map.canonical_code,
    updater            = 'migration',
    update_time        = CURRENT_TIMESTAMP
FROM legacy_business_type_code_map map
WHERE e.business_type_code = map.legacy_code
  AND e.deleted = false;

-- 映射表保留供排查；不需要可手动 DROP

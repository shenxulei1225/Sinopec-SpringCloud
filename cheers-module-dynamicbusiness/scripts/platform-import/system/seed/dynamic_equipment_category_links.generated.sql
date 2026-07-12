-- equipment：补全 model↔category（legacy MODEL-*）+ entity↔category（按 model 主分类）
-- Regenerate: python export_equipment_category_entity_links.py
-- 依赖：dynamic_model_equipment / dynamic_model_category_equipment / dynamic_entity_equipment 已导入

SET search_path TO dynamicbusiness;

-- ========== 1) legacy MODEL-* → 标准设备分类 ==========
WITH legacy_map(model_code, category_code) AS (
  VALUES
  ('MODEL-6671ca830f4742229c0a00ff531f974c', 'EQCAT-DEV-FIRE-BOX'),
  ('MODEL-7fc9012b394749a3b30e1788e4057324', 'EQCAT-DEV-MAINT-BOX'),
  ('MODEL-56b09ad2ef96439c87bda429e2bb9404', 'EQCAT-DEV-IP-PHONE'),
  ('MODEL-50991eab2a714ed288d00c58ff01c810', 'EQCAT-DEV-IP-PHONE'),
  ('MODEL-3ae58694e5ab4dc4a078c6e4b548f617', 'EQCAT-DEV-AEROSOL'),
  ('MODEL-81132ce00d2041c6a4ee06ec1c01dac8', 'EQCAT-DEV-ACCESS-CTRL'),
  ('MODEL-9b557c9404684caab2820eb0aec2cf15', 'EQCAT-DEV-BUTTON-BOX'),
  ('MODEL-c6bc32646f3e4bde8409120c5c290eee', 'EQCAT-DEV-MEDIA-CONVERTER'),
  ('MODEL-05e5c6a6c8fe4f3c9bd1dd86dd24a659', 'EQCAT-DEV-SW-RING'),
  ('MODEL-f5270d478dd849389e59a178cd512629', 'EQCAT-DEV-POS-TERMINAL'),
  ('MODEL-3130eba3747e4a5aa0862c94807822ba', 'EQCAT-DEV-RADIO-SPLITTER'),
  ('MODEL-a62c727c0a024e19a8221c68fcf0c09a', 'EQCAT-DEV-ALARM-HOST')
)
INSERT INTO dynamic_model_category_relation (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT
  m.id,
  c.id,
  'equipment',
  m.code,
  c.code,
  9000 + ROW_NUMBER() OVER (ORDER BY lm.model_code),
  1,
  'seed'
FROM legacy_map lm
JOIN dynamic_model m
  ON m.deleted = false
 AND m.tenant_id = 1
 AND m.entity_type_code = 'equipment'
 AND m.code = lm.model_code
JOIN dynamic_category c
  ON c.deleted = false
 AND c.tenant_id = 1
 AND c.category_type_code = 'equipment'
 AND c.code = lm.category_code
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_model_category_relation r
  WHERE r.deleted = false
    AND r.tenant_id = 1
    AND r.entity_type_code = 'equipment'
    AND r.model_id = m.id
    AND r.category_id = c.id
)
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- ========== 2) entity↔category：每个实体挂其 model 的主分类（sort 最小） ==========
WITH model_primary_category AS (
  SELECT DISTINCT ON (mcr.model_id)
    mcr.model_id,
    mcr.category_id,
    mcr.sort AS mcr_sort
  FROM dynamic_model_category_relation mcr
  WHERE mcr.deleted = false
    AND mcr.tenant_id = 1
    AND mcr.entity_type_code = 'equipment'
  ORDER BY mcr.model_id, mcr.sort ASC NULLS LAST, mcr.id ASC
)
INSERT INTO dynamic_entity_category_relation (
  entity_id, category_id, entity_type_code, sort, tenant_id, creator
)
SELECT
  e.id,
  mpc.category_id,
  'equipment',
  COALESCE(e.sort, 0),
  1,
  'seed'
FROM ent_equipment e
JOIN model_primary_category mpc ON mpc.model_id = e.model_id
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_entity_category_relation ecr
    WHERE ecr.deleted = false
      AND ecr.tenant_id = 1
      AND ecr.entity_type_code = 'equipment'
      AND ecr.entity_id = e.id
      AND ecr.category_id = mpc.category_id
  );

-- 所属站场系统字段展示名 + 样例全网目录
-- 字段库 code=facility_id 已存在时只改 name；站场级列挂接由运行时 FacilityOwningFieldEnsureService 负责（禁止 fld_base_*）

UPDATE dynamicbusiness.dynamic_field
SET name = '所属站场',
    description = COALESCE(NULLIF(btrim(description), ''), '站场级业务归属站场；列存 facility 实体 id'),
    provider_code = COALESCE(NULLIF(btrim(provider_code), ''), 'dynamic-entity:facility'),
    type = 'ENTITY_REF',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'facility_id'
  AND (name IS DISTINCT FROM '所属站场'
       OR COALESCE(provider_code, '') <> 'dynamic-entity:facility'
       OR type IS DISTINCT FROM 'ENTITY_REF');

-- 样例全网对象：有则标 NETWORK（编码以实库为准；无行则本语句无影响）
UPDATE dynamicbusiness.dynamic_entity_type
SET work_scope = 'NETWORK',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code IN (
    'emergentcy_plan',
    'emergency',
    'standard',
    'inspection_item',
    'inspection_method'
  )
  AND COALESCE(work_scope, 'FACILITY') <> 'NETWORK';

-- 统一 facility_id 展示名为「所属场站」（引用设施类型不变）
-- V48 曾写入「所属站场」；产品口径统一为「所属场站」

UPDATE dynamicbusiness.dynamic_field
SET name = '所属场站',
    description = COALESCE(NULLIF(btrim(description), ''), '站场级业务归属设施；列存 facility 实体 id'),
    provider_code = COALESCE(NULLIF(btrim(provider_code), ''), 'dynamic-entity:facility'),
    type = 'ENTITY_REF',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'facility_id'
  AND (name IS DISTINCT FROM '所属场站'
       OR COALESCE(provider_code, '') <> 'dynamic-entity:facility'
       OR type IS DISTINCT FROM 'ENTITY_REF');

UPDATE dynamicbusiness.dynamic_entity_type_base_field
SET field_name = '所属场站',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND field_code = 'facility_id'
  AND field_name IS DISTINCT FROM '所属场站';

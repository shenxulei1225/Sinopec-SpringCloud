-- ============================================================================
-- region 定稿修订：Pattern C + 分类树（category），不预置区划/示例 region 数据
-- 国家管网区域分布由产品配置 dynamic_category(category_type=region)；
-- ent_region 仅在分类节点绑实体时创建；facility 仍 REF_REGION 关联 region.id
-- 依赖：V22
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1. 停用 V22 误加的行政区划模型（不在 SQL 预置 region 模型/实例）
UPDATE dynamic_model
SET status = 0,
    description = COALESCE(description, '') || ' [已废弃 2026-07-07：region 改由 category 树 + Pattern C 配置，不按省市区预置]',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE code IN (
    'MODEL-REGION-COUNTRY',
    'MODEL-REGION-PROVINCE',
    'MODEL-REGION-CITY',
    'MODEL-REGION-DISTRICT'
)
  AND tenant_id = 1
  AND deleted = false
  AND COALESCE(description, '') NOT LIKE '%region 改由 category 树%';

-- 2. 停用行政区划专用基础字段（树层级由 category 表达，不用 region_level/admin_code）
UPDATE dynamic_entity_type_base_field
SET status = 0,
    description = COALESCE(description, '') || ' [已废弃：改用 category 树层级]',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'region'
  AND field_code IN ('region_level', 'admin_code')
  AND tenant_id = 1
  AND deleted = false;

-- 3. region 实体类型语义：管网运营区域 + Pattern C
UPDATE dynamic_entity_type
SET name = '运营区域',
    alias = '区域',
    description = '国家管网运营/管理区域；树由分类（category_type=region）维护；Pattern C 分类即实体，不在 Flyway 预置 region 行',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'region' AND tenant_id = 1 AND deleted = false;

UPDATE dynamic_entity_type_config
SET name = '运营区域',
    description = 'ent_region；树= dynamic_category；实例由分类绑实体产生',
    physical_column_mapping = COALESCE(physical_column_mapping, '{}'::jsonb) - 'region_level' - 'admin_code',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'region' AND tenant_id = 1 AND deleted = false;

-- 4. facility → region：仍存 region_id，文案改为「所属区域」
UPDATE dynamic_entity_type_base_field
SET field_name = '所属区域',
    description = '设施所属管网运营区域（ent_region.id）；区域树来自 category，非 parent_id 区划树',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'facility'
  AND field_code = 'REF_REGION'
  AND tenant_id = 1
  AND deleted = false;

UPDATE dynamic_field
SET name = '所属区域',
    description = '设施所属管网运营区域（region 实体）',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'F-spatial-facility-ref-region'
  AND tenant_id = 1
  AND deleted = false;

UPDATE dynamic_entity_type_relation
SET relation_name = '所属区域',
    default_field_name = '所属区域',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE source_entity_type_code = 'facility'
  AND target_entity_type_code = 'region'
  AND tenant_id = 1
  AND deleted = false;

-- ============================================================================
-- 系统 · region 实体模型（集团 / 省公司 / 作业区）
-- 运营区域用于区分组织层级，非省市区行政区划；定稿见 docs/动态业务/地理区域-设施-站内分区定稿.md
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, tenant_id, creator
) VALUES
  (
    'MODEL-REGION-GROUP', '集团', 'region',
    '集团级运营区域', 1, 1, 1, 'seed'
  ),
  (
    'MODEL-REGION-PROVINCIAL', '省公司', 'region',
    '省级公司运营区域', 1, 2, 1, 'seed'
  ),
  (
    'MODEL-REGION-OPERATION', '作业区', 'region',
    '省公司下辖作业区；设施 REF_REGION 通常挂此层（对标国家管网省公司—作业区两级管理）', 1, 3, 1, 'seed'
  )
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 废弃旧「分公司」模型码（改革前四级架构遗留）
UPDATE dynamic_model
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false AND tenant_id = 1 AND code = 'MODEL-REGION-BRANCH';

-- ============================================================================
-- 系统 · region 实体模型（集团 / 省公司 / 作业区）
-- 管道线路已并入 facility（MODEL-FACILITY-PIPELINE-*），见 dynamic_model_facility.sql
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

-- 退役非组织层级的 region 模型（管廊构筑物/舱室、旧省市区语义等）
-- 定稿仅保留：集团 / 省公司 / 作业区。管廊租户的空间单元应使用 zone（或该租户独立库），不得挂在 region。
UPDATE dynamic_model
SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND entity_type_code = 'region'
  AND code NOT IN (
    'MODEL-REGION-GROUP',
    'MODEL-REGION-PROVINCIAL',
    'MODEL-REGION-OPERATION'
  );

-- 平台地理坐标只认字段库「坐标」（FLD-LOC-014）。
-- 经度 / 纬度 / 海拔三条小数不再作为可选主字段；未分配过型号，软删即可。
SET search_path TO dynamicbusiness;

UPDATE dynamic_field
SET deleted = true,
    status = 0,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code IN ('FLD-LOC-008', 'FLD-LOC-009', 'FLD-LOC-010');

UPDATE dynamic_group_relation
SET deleted = true,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND group_type = 'FIELD'
  AND target_code IN ('FLD-LOC-008', 'FLD-LOC-009', 'FLD-LOC-010');

-- ============================================================================
-- 系统 · facility 实体模型（站场 / 厂区 / 油库 / 成品油库 / 天然气接收站 /
--   天然气分输站 / 阀室 / 机关楼宇）
-- 定稿见 docs/动态业务/地理区域-设施-站内分区定稿.md
-- ============================================================================

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
) VALUES
  (
    'MODEL-FACILITY-STATION', '站场', 'facility',
    '油气储运站场等设施点', 1, 1, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-PLANT', '厂区', 'facility',
    '工厂、化工园区厂区', 1, 2,
    '{"groups":[{"id":"group-1783655949062","name":"基础信息","color":"#409eff","sort":1024,"fields":[]}]}',
    1, 'seed'
  ),
  (
    'MODEL-FACILITY-DEPOT', '油库', 'facility',
    '油库设施点', 1, 3, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-OFFICE', '机关/楼宇', 'facility',
    '机关办公与楼宇设施点', 1, 4, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-REFINED-DEPOT', '成品油库', 'facility',
    '成品油储运库等设施点', 1, 5, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-NG-RECEIVING', '天然气接收站', 'facility',
    '天然气管道接收、进气等设施点', 1, 6, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-NG-DISTRIBUTION', '天然气分输站', 'facility',
    '天然气管道分输、调压等设施点', 1, 7, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-VALVE-CHAMBER', '阀室', 'facility',
    '长输管道沿线阀室等设施点', 1, 8, NULL, 1, 'seed'
  )
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = EXCLUDED.status,
  sort = EXCLUDED.sort,
  field_groups_config = EXCLUDED.field_groups_config,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

-- 模型扩展字段分配（FLD-FAC-EXT-* 需已存在于 dynamic_field）

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, model_code, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  tenant_id, creator
)
SELECT m.id, f.id, m.code, f.code,
  false, false, false, false, v.sort,
  1, 'seed'
FROM (
  VALUES
    ('MODEL-FACILITY-STATION', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-STATION', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-PLANT', 'FLD-FAC-EXT-003', 10),
    ('MODEL-FACILITY-PLANT', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-DEPOT', 'FLD-FAC-EXT-004', 10),
    ('MODEL-FACILITY-DEPOT', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-OFFICE', 'FLD-FAC-EXT-005', 10),
    ('MODEL-FACILITY-OFFICE', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-REFINED-DEPOT', 'FLD-FAC-EXT-004', 10),
    ('MODEL-FACILITY-REFINED-DEPOT', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-NG-RECEIVING', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-NG-RECEIVING', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-NG-DISTRIBUTION', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-NG-DISTRIBUTION', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-VALVE-CHAMBER', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-VALVE-CHAMBER', 'FLD-FAC-EXT-002', 20)
) AS v(model_code, field_code, sort)
JOIN dynamic_model m
  ON m.deleted = false AND m.tenant_id = 1 AND m.code = v.model_code
JOIN dynamic_field f
  ON f.deleted = false AND f.tenant_id = 1 AND f.code = v.field_code
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  sort = EXCLUDED.sort,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

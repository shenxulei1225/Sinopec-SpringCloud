-- ============================================================================
-- 系统 · facility 实体模型（站场 / 厂区 / 油库 / 成品油库 / 天然气接收站 /
--   天然气分输站 / 阀室 / 机关楼宇；以及按介质区分的原油/成品油/天然气站场子类）
-- 站场子类：介质（CR/CP/NG）× 站型（分输 / 分输清管 / 清管 / 末站 / 首站 / 接收 / 阀室）
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
    '天然气管道接收站（站名含「接收」；不含末站/首站）', 1, 6, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-NG-TERMINAL', '天然气末站', 'facility',
    '天然气管道末站（站名含「末站」）', 1, 7, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-NG-HEAD', '天然气首站', 'facility',
    '天然气管道首站（站名含「首站」）', 1, 8, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', '天然气分输清管站', 'facility',
    '天然气管道分输清管站（站名含「分输清管」）', 1, 9, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-NG-PIGGING', '天然气清管站', 'facility',
    '天然气管道清管站（站名含「清管」且不含「分输」）', 1, 10, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-NG-DISTRIBUTION', '天然气分输站', 'facility',
    '天然气管道分输站（站名含「分输」且不含「清管」）', 1, 11, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-VALVE-CHAMBER', '阀室', 'facility',
    '长输管道沿线阀室等设施点（介质未区分时使用）', 1, 12, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CR-DISTRIBUTION-PIGGING', '原油分输清管站', 'facility',
    '原油管道分输清管站', 1, 21, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CR-PIGGING', '原油清管站', 'facility',
    '原油管道清管站', 1, 22, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CR-DISTRIBUTION', '原油分输站', 'facility',
    '原油管道分输站', 1, 23, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CR-TERMINAL', '原油末站', 'facility',
    '原油管道末站', 1, 24, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CR-HEAD', '原油首站', 'facility',
    '原油管道首站', 1, 25, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CR-RECEIVING', '原油接收站', 'facility',
    '原油管道接收站', 1, 26, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CR-VALVE-CHAMBER', '原油阀室', 'facility',
    '原油管道阀室', 1, 27, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CP-DISTRIBUTION-PIGGING', '成品油分输清管站', 'facility',
    '成品油管道分输清管站', 1, 31, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CP-PIGGING', '成品油清管站', 'facility',
    '成品油管道清管站', 1, 32, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CP-DISTRIBUTION', '成品油分输站', 'facility',
    '成品油管道分输站', 1, 33, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CP-TERMINAL', '成品油末站', 'facility',
    '成品油管道末站', 1, 34, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CP-HEAD', '成品油首站', 'facility',
    '成品油管道首站', 1, 35, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CP-RECEIVING', '成品油接收站', 'facility',
    '成品油管道接收站', 1, 36, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-CP-VALVE-CHAMBER', '成品油阀室', 'facility',
    '成品油管道阀室', 1, 37, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-NG-VALVE-CHAMBER', '天然气阀室', 'facility',
    '天然气管道阀室', 1, 38, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-PIPELINE-CR', '原油管道线路', 'facility',
    '长输原油管道线路（Pattern C 中间层；下辖站场 facility）', 1, 50, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-PIPELINE-CP', '成品油管道线路', 'facility',
    '长输成品油管道线路（Pattern C 中间层；下辖站场 facility）', 1, 51, NULL, 1, 'seed'
  ),
  (
    'MODEL-FACILITY-PIPELINE-NG', '天然气管道线路', 'facility',
    '长输天然气管道线路（Pattern C 中间层；下辖站场 facility）', 1, 52, NULL, 1, 'seed'
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
    ('MODEL-FACILITY-NG-TERMINAL', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-NG-TERMINAL', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-NG-HEAD', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-NG-HEAD', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-NG-DISTRIBUTION-PIGGING', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-NG-PIGGING', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-NG-PIGGING', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-NG-DISTRIBUTION', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-NG-DISTRIBUTION', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-VALVE-CHAMBER', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-VALVE-CHAMBER', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CR-DISTRIBUTION-PIGGING', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CR-DISTRIBUTION-PIGGING', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CR-PIGGING', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CR-PIGGING', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CR-DISTRIBUTION', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CR-DISTRIBUTION', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CR-TERMINAL', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CR-TERMINAL', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CR-HEAD', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CR-HEAD', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CR-RECEIVING', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CR-RECEIVING', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CR-VALVE-CHAMBER', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CR-VALVE-CHAMBER', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CP-DISTRIBUTION-PIGGING', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CP-DISTRIBUTION-PIGGING', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CP-PIGGING', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CP-PIGGING', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CP-DISTRIBUTION', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CP-DISTRIBUTION', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CP-TERMINAL', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CP-TERMINAL', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CP-HEAD', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CP-HEAD', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CP-RECEIVING', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CP-RECEIVING', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-CP-VALVE-CHAMBER', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-CP-VALVE-CHAMBER', 'FLD-FAC-EXT-002', 20),
    ('MODEL-FACILITY-NG-VALVE-CHAMBER', 'FLD-FAC-EXT-001', 10),
    ('MODEL-FACILITY-NG-VALVE-CHAMBER', 'FLD-FAC-EXT-002', 20)
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

-- 开发联调 · 巡检管理主数据样例（排期 / 对象 / 巡检点模板）

-- Pattern B：实体↔分类写入 dynamic_entity_category_relation_t1（与运营区域 Pattern C 的 link 表区分）

-- 定稿示例 id：schedule=201, object=301, point=501/502



SET search_path TO dynamicbusiness;



-- 幂等：清理本批样例实体的分类绑定（含误写入 Pattern C link 的历史行）

UPDATE dynamic_entity_category_relation_t1 r

SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP

WHERE r.deleted = false AND r.tenant_id = 1

  AND r.entity_id IN (201, 301, 501, 502);



UPDATE dynamic_category_entity_link_t1 l

SET deleted = true, updater = 'seed', update_time = CURRENT_TIMESTAMP

WHERE l.deleted = false AND l.tenant_id = 1

  AND l.entity_id IN (201, 301, 501, 502);



-- patrol_schedule 201：工作日早 8 点

INSERT INTO ent_patrol_schedule (

  id, entity_type_code, model_id, name, code,

  tenant_id, creator, tree_path, sort, status, deleted,

  attrs, custom_fields

)

OVERRIDING SYSTEM VALUE

SELECT

  201, 'patrol_schedule', m.id, '工作日早 8 点', 'PSCH-201',

  1, 'seed', '/201/', 1, 1, false,

  '{"is_template": true}'::jsonb,

  jsonb_build_object(

    'FLD-PSC-001', 'cron',

    'FLD-PSC-002', '0 8 * * 1-5',

    'FLD-PSC-003', 'Asia/Shanghai'

  )

FROM dynamic_model m

WHERE m.code = 'patrol_schedule' AND m.deleted = false AND m.tenant_id = 1

ON CONFLICT (id) DO UPDATE SET

  model_id = EXCLUDED.model_id,

  name = EXCLUDED.name,

  attrs = EXCLUDED.attrs,

  custom_fields = EXCLUDED.custom_fields,

  deleted = false,

  updater = 'seed',

  update_time = CURRENT_TIMESTAMP;



INSERT INTO dynamic_entity_category_relation_t1 (entity_id, category_id, entity_type_code, sort, tenant_id, creator)

SELECT 201, c.id, 'patrol_schedule', 1, 1, 'seed'

FROM dynamic_category c

WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'psch_cat_cron';



-- patrol_object 301：储罐 A 区

INSERT INTO ent_patrol_object (

  id, entity_type_code, model_id, name, code,

  tenant_id, creator, tree_path, sort, status, deleted,

  attrs, custom_fields

)

OVERRIDING SYSTEM VALUE

SELECT

  301, 'patrol_object', m.id, '储罐 A 区', 'POBJ-301',

  1, 'seed', '/301/', 1, 1, false,

  '{"is_template": true}'::jsonb,

  jsonb_build_object(

    'FLD-POB-001', '{"entityTypeCode":"facility","id":1}',

    'FLD-POB-002', '[{"entityTypeCode":"equipment","id":900001},{"entityTypeCode":"equipment","id":900002}]'

  )

FROM dynamic_model m

WHERE m.code = 'patrol_object' AND m.deleted = false AND m.tenant_id = 1

ON CONFLICT (id) DO UPDATE SET

  model_id = EXCLUDED.model_id,

  name = EXCLUDED.name,

  attrs = EXCLUDED.attrs,

  custom_fields = EXCLUDED.custom_fields,

  deleted = false,

  updater = 'seed',

  update_time = CURRENT_TIMESTAMP;



INSERT INTO dynamic_entity_category_relation_t1 (entity_id, category_id, entity_type_code, sort, tenant_id, creator)

SELECT 301, c.id, 'patrol_object', 1, 1, 'seed'

FROM dynamic_category c

WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'pobj_cat_tank';



-- patrol_point 501：东营作业区 · 储罐 A 区巡检点

INSERT INTO ent_patrol_point (

  id, entity_type_code, model_id, name, code,

  tenant_id, creator, tree_path, sort, status, deleted,

  attrs, custom_fields

)

OVERRIDING SYSTEM VALUE

SELECT

  501, 'patrol_point', m.id, '储罐 A 区巡检点', 'PPT-501',

  1, 'seed', '/501/', 1, 1, false,

  '{"is_template": true}'::jsonb,

  jsonb_build_object(

    'FLD-PPT-001', '{"entityTypeCode":"facility","id":1}',

    'FLD-PPT-002', 'topo_facility001_v1',

    'FLD-PPT-003', '["n_sta_001","n_sta_002"]',

    'FLD-PPT-004', 'ROBOT_GROUND'

  )

FROM dynamic_model m

WHERE m.code = 'patrol_point' AND m.deleted = false AND m.tenant_id = 1

ON CONFLICT (id) DO UPDATE SET

  model_id = EXCLUDED.model_id,

  name = EXCLUDED.name,

  attrs = EXCLUDED.attrs,

  custom_fields = EXCLUDED.custom_fields,

  deleted = false,

  updater = 'seed',

  update_time = CURRENT_TIMESTAMP;



INSERT INTO dynamic_entity_category_relation_t1 (entity_id, category_id, entity_type_code, sort, tenant_id, creator)

SELECT 501, c.id, 'patrol_point', 1, 1, 'seed'

FROM dynamic_category c

WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'PP-CAT-OP-SD-DY';



-- patrol_point 502：德州作业区 · 泵房管廊巡检点（演示多区域分类）

INSERT INTO ent_patrol_point (

  id, entity_type_code, model_id, name, code,

  tenant_id, creator, tree_path, sort, status, deleted,

  attrs, custom_fields

)

OVERRIDING SYSTEM VALUE

SELECT

  502, 'patrol_point', m.id, '泵房管廊巡检点', 'PPT-502',

  1, 'seed', '/502/', 2, 1, false,

  '{"is_template": true}'::jsonb,

  jsonb_build_object(

    'FLD-PPT-001', '{"entityTypeCode":"facility","id":1}',

    'FLD-PPT-002', 'topo_facility001_v1',

    'FLD-PPT-003', '["n_sta_003","n_sta_004"]',

    'FLD-PPT-004', 'HUMAN'

  )

FROM dynamic_model m

WHERE m.code = 'patrol_point' AND m.deleted = false AND m.tenant_id = 1

ON CONFLICT (id) DO UPDATE SET

  model_id = EXCLUDED.model_id,

  name = EXCLUDED.name,

  attrs = EXCLUDED.attrs,

  custom_fields = EXCLUDED.custom_fields,

  deleted = false,

  updater = 'seed',

  update_time = CURRENT_TIMESTAMP;



INSERT INTO dynamic_entity_category_relation_t1 (entity_id, category_id, entity_type_code, sort, tenant_id, creator)

SELECT 502, c.id, 'patrol_point', 2, 1, 'seed'

FROM dynamic_category c

WHERE c.deleted = false AND c.tenant_id = 1 AND c.code = 'PP-CAT-OP-SD-DZ';



SELECT setval(

  pg_get_serial_sequence('dynamicbusiness.ent_patrol_schedule', 'id'),

  GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_patrol_schedule), 201)

);

SELECT setval(

  pg_get_serial_sequence('dynamicbusiness.ent_patrol_object', 'id'),

  GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_patrol_object), 301)

);

SELECT setval(

  pg_get_serial_sequence('dynamicbusiness.ent_patrol_point', 'id'),

  GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_patrol_point), 502)

);



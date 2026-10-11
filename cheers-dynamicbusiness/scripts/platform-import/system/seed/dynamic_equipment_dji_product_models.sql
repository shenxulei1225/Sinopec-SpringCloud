-- 大疆产品各一个设备型号。
-- 负责：按官方领域、主类型、子类型建飞机、遥控器、机场、负载型号，并挂到巡检设备。
-- 不负责：负载挂到哪一架飞机的哪个口。位置在挂接上，不在本文件。
-- 主类型、子类型的选项值写成「领域:主类型」和「领域:主类型:子类型」。
-- 官方数字在不同领域会重复（飞机 67 与负载 67），下拉文字仍显示官方数字。

SET search_path TO dynamicbusiness;

BEGIN;

CREATE TEMP TABLE dji_product (
  code text PRIMARY KEY,
  name text NOT NULL,
  domain_no int NOT NULL,
  main_type int NOT NULL,
  sub_type int NOT NULL,
  family_label text NOT NULL,
  sort_no int NOT NULL
) ON COMMIT DROP;

INSERT INTO dji_product (code, name, domain_no, main_type, sub_type, family_label, sort_no) VALUES
  ('dji-m400', 'Matrice 400', 0, 103, 0, 'Matrice 400', 1),
  ('dji-m350-rtk', 'Matrice 350 RTK', 0, 89, 0, 'Matrice 350 RTK', 2),
  ('dji-m300-rtk', 'Matrice 300 RTK', 0, 60, 0, 'Matrice 300 RTK', 3),
  ('dji-m30', 'Matrice 30', 0, 67, 0, 'Matrice 30 系列', 4),
  ('dji-m30t', 'Matrice 30T', 0, 67, 1, 'Matrice 30 系列', 5),
  ('dji-m3e', 'Mavic 3 行业系列（M3E）', 0, 77, 0, 'Mavic 3 行业系列', 6),
  ('dji-m3t', 'Mavic 3 行业系列（M3T）', 0, 77, 1, 'Mavic 3 行业系列', 7),
  ('dji-m3ta', 'Mavic 3 行业系列（M3TA）', 0, 77, 3, 'Mavic 3 行业系列', 8),
  ('dji-m3d', 'Matrice 3D', 0, 91, 0, 'Matrice 3D 系列', 9),
  ('dji-m3td', 'Matrice 3TD', 0, 91, 1, 'Matrice 3D 系列', 10),
  ('dji-m4d', 'Matrice 4D', 0, 100, 0, 'Matrice 4D 系列', 11),
  ('dji-m4td', 'Matrice 4TD', 0, 100, 1, 'Matrice 4D 系列', 12),
  ('dji-m4e', 'DJI Matrice 4 系列（M4E）', 0, 99, 0, 'DJI Matrice 4 系列', 13),
  ('dji-m4t', 'DJI Matrice 4 系列（M4T）', 0, 99, 1, 'DJI Matrice 4 系列', 14),
  ('dji-rc-screen', 'DJI 带屏遥控器行业版', 2, 56, 0, 'DJI 带屏遥控器行业版', 15),
  ('dji-rc-plus', 'DJI RC Plus', 2, 119, 0, 'DJI RC Plus', 16),
  ('dji-rc-plus-2', 'DJI RC Plus 2', 2, 174, 0, 'DJI RC Plus 2', 17),
  ('dji-rc-pro', 'DJI RC Pro 行业版', 2, 144, 0, 'DJI RC Pro 行业版', 18),
  ('dji-dock-1', '大疆机场', 3, 1, 0, '大疆机场', 19),
  ('dji-dock-2', '大疆机场 2', 3, 2, 0, '大疆机场 2', 20),
  ('dji-dock-3', '大疆机场 3', 3, 3, 0, '大疆机场 3', 21),
  ('dji-pl-fpv', 'FPV', 1, 39, 0, 'FPV', 22),
  ('dji-pl-assist', '辅助影像', 1, 176, 0, '辅助影像', 23),
  ('dji-pl-z30', '禅思 Z30', 1, 20, 0, '禅思 Z30', 24),
  ('dji-pl-xt2', '禅思 XT2', 1, 26, 0, '禅思 XT2', 25),
  ('dji-pl-xts', '禅思 XTS', 1, 41, 0, '禅思 XTS', 26),
  ('dji-pl-h20', '禅思 H20', 1, 42, 0, '禅思 H20', 27),
  ('dji-pl-h20t', '禅思 H20T', 1, 43, 0, '禅思 H20T', 28),
  ('dji-pl-h20n', '禅思 H20N', 1, 61, 0, '禅思 H20N', 29),
  ('dji-pl-h30', '禅思 H30', 1, 82, 0, '禅思 H30', 30),
  ('dji-pl-h30t', '禅思 H30T', 1, 83, 0, '禅思 H30T', 31),
  ('dji-pl-m30-cam', 'Matrice 30 相机', 1, 52, 0, 'Matrice 30 相机', 32),
  ('dji-pl-m30t-cam', 'Matrice 30T 相机', 1, 53, 0, 'Matrice 30T 相机', 33),
  ('dji-pl-m3e-cam', 'Mavic 3E 相机', 1, 66, 0, 'Mavic 3E 相机', 34),
  ('dji-pl-m3t-cam', 'Mavic 3T 相机', 1, 67, 0, 'Mavic 3T 相机', 35),
  ('dji-pl-m3ta-cam', 'Mavic 3TA 相机', 1, 129, 0, 'Mavic 3TA 相机', 36),
  ('dji-pl-m3d-cam', 'Matrice 3D 相机', 1, 80, 0, 'Matrice 3D 相机', 37),
  ('dji-pl-m3td-cam', 'Matrice 3TD 相机', 1, 81, 0, 'Matrice 3TD 相机', 38),
  ('dji-pl-m4e-cam', 'Matrice 4E 相机', 1, 88, 0, 'Matrice 4E 相机', 39),
  ('dji-pl-m4t-cam', 'Matrice 4T 相机', 1, 89, 0, 'Matrice 4T 相机', 40),
  ('dji-pl-m4d-cam', 'Matrice 4D 相机', 1, 98, 0, 'Matrice 4D 相机', 41),
  ('dji-pl-m4td-cam', 'Matrice 4TD 相机', 1, 99, 0, 'Matrice 4TD 相机', 42),
  ('dji-pl-dock-cam', '机场相机', 1, 165, 0, '机场相机', 43);

INSERT INTO dynamic_field (
  code, name, type, description, source, status, index_strategy, options, tenant_id, creator
)
SELECT
  'dji_domain',
  '领域',
  'ENUM',
  '官方 domain。0 飞机，1 负载，2 遥控器，3 机场。',
  'USER',
  1,
  'NONE',
  '[
    {"label":"0 飞机","value":"0"},
    {"label":"1 负载","value":"1"},
    {"label":"2 遥控器","value":"2"},
    {"label":"3 机场","value":"3"}
  ]'::jsonb,
  1,
  'seed'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  source = EXCLUDED.source,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, description, source, status, index_strategy, options, tenant_id, creator
)
SELECT
  'dji_main_type',
  '主类型',
  'ENUM',
  '官方 type。选项值是「领域:主类型」。同一个数字在不同领域不是同一款设备，下拉文字仍是官方数字加名称。必须和领域、子类型一起看。',
  'USER',
  1,
  'NONE',
  (
    SELECT jsonb_agg(
      jsonb_build_object('label', label, 'value', value)
      ORDER BY ord
    )
    FROM (
      SELECT DISTINCT ON (domain_no, main_type)
        domain_no::text || ':' || main_type::text AS value,
        main_type::text || ' ' || family_label AS label,
        domain_no * 1000 + main_type AS ord
      FROM dji_product
      ORDER BY domain_no, main_type, sort_no
    ) s
  ),
  1,
  'seed'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  source = EXCLUDED.source,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_field (
  code, name, type, description, source, status, index_strategy, options, tenant_id, creator
)
SELECT
  'dji_sub_type',
  '子类型',
  'ENUM',
  '官方 sub_type。选项值是「领域:主类型:子类型」。和主类型一起才确定一款产品。Mavic 3 行业系列没有子类型 2，M3TA 是 3。',
  'USER',
  1,
  'NONE',
  (
    SELECT jsonb_agg(
      jsonb_build_object('label', sub_type::text || ' ' || name, 'value', domain_no::text || ':' || main_type::text || ':' || sub_type::text)
      ORDER BY domain_no, main_type, sub_type, sort_no
    )
    FROM dji_product
  ),
  1,
  'seed'
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  source = EXCLUDED.source,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model (
  code, name, entity_type_code, description, status, sort, field_groups_config, tenant_id, creator
)
SELECT
  p.code,
  p.name,
  'equipment',
  '领域 ' || p.domain_no || '，主类型 ' || p.main_type || '，子类型 ' || p.sub_type,
  1,
  170000 + p.sort_no,
  '[]',
  1,
  'seed'
FROM dji_product p
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  entity_type_code = EXCLUDED.entity_type_code,
  description = EXCLUDED.description,
  status = 1,
  sort = EXCLUDED.sort,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_category_relation_t1 (
  model_id, category_id, entity_type_code, model_code, category_code, sort, tenant_id, creator
)
SELECT m.id, c.id, 'equipment', m.code, c.code, 1024, 1, 'seed'
FROM dji_product p
JOIN dynamic_model m ON m.code = p.code AND m.tenant_id = 1 AND m.deleted = false
JOIN dynamic_category c ON c.code = 'EQCAT-L1-MISC' AND c.tenant_id = 1 AND c.deleted = false
ON CONFLICT (model_code, category_code, entity_type_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  category_id = EXCLUDED.category_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  field_source, default_value, tenant_id, creator
)
SELECT
  m.id, m.code, a.field_id, a.field_code,
  a.required, a.is_searchable, a.is_filterable, a.is_sortable, a.sort,
  a.field_source, a.default_value, 1, 'seed'
FROM dji_product p
JOIN dynamic_model m ON m.code = p.code AND m.tenant_id = 1 AND m.deleted = false
JOIN dynamic_model_field_assignment a
  ON a.model_id = 1770 AND a.tenant_id = 1 AND a.deleted = false
WHERE a.field_code NOT IN ('dji_domain', 'dji_main_type', 'dji_sub_type')
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, tenant_id, creator
)
SELECT
  m.id,
  m.code,
  f.id,
  f.code,
  true,
  false,
  true,
  false,
  CASE f.code
    WHEN 'dji_domain' THEN 40
    WHEN 'dji_main_type' THEN 41
    ELSE 42
  END,
  CASE f.code
    WHEN 'dji_domain' THEN p.domain_no::text
    WHEN 'dji_main_type' THEN p.domain_no::text || ':' || p.main_type::text
    ELSE p.domain_no::text || ':' || p.main_type::text || ':' || p.sub_type::text
  END,
  1,
  'seed'
FROM dji_product p
JOIN dynamic_model m ON m.code = p.code AND m.tenant_id = 1 AND m.deleted = false
JOIN dynamic_field f
  ON f.code IN ('dji_domain', 'dji_main_type', 'dji_sub_type')
 AND f.tenant_id = 1
 AND f.deleted = false
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  default_value = EXCLUDED.default_value,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

COMMIT;

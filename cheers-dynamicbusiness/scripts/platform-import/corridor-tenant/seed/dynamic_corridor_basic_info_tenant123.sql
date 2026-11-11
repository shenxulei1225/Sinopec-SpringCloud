-- 智慧管廊租户 123 · 管廊设施 / 管廊分区按型号补字段，并写入运营维护手册第 1.3 节已有事实。
-- 手册没有的业主、设计、施工、开工完工、附图、断面宽高不填。
-- 空间根与管廊工程同名，不把工程字段再写一份到空间根。
-- 监控中心是武汉管廊里的构筑物，不在管廊设施下建目录或记录。

SET search_path TO dynamicbusiness;

BEGIN;

-- ---------- 字段库（型号扩展。位置/类型/备注沿用已有列，不另造同义字段） ----------

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, index_strategy, options, tenant_id, creator
) VALUES
  ('ut_route', '管廊线路', 'TEXT', NULL, '这条管廊经过的道路', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic'),
  ('ut_owner_org', '业主单位', 'STRING', NULL, '管廊业主', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic'),
  ('ut_design_org', '设计单位', 'STRING', NULL, '管廊设计单位', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic'),
  ('ut_construction_org', '施工单位', 'STRING', NULL, '管廊施工单位', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic'),
  ('ut_construction_date', '建设日期', 'DATE', NULL, '开工或建设日期', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic'),
  ('ut_completion_date', '完工日期', 'DATE', NULL, '完工日期', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic'),
  ('ut_operation_mode', '运营模式', 'TEXT', NULL, '建设、运营、移交等方式', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic'),
  ('ut_drawing', '附图', 'IMAGE', NULL, '管廊附图', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic'),
  ('ut_length_km', '长度', 'NUMBER', '千米', '管廊全长', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic'),
  ('ut_segment_location', '位置', 'TEXT', NULL, '管廊段起止范围', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic'),
  ('ut_gallery_class', '类型', 'ENUM', NULL, '干支、支或电力专用', 'USER', 1, 'NONE',
   '[{"label":"干支","value":"干支"},{"label":"支","value":"支"},{"label":"电力专用","value":"电力专用"},{"label":"支/电力专用","value":"支/电力专用"}]',
   123, 'corridor-basic'),
  ('ut_length_m', '长度', 'NUMBER', '米', '这一段或这一舱的长度', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic'),
  ('ut_width_m', '宽度', 'NUMBER', '米', '断面宽度', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic'),
  ('ut_height_m', '高度', 'NUMBER', '米', '断面高度', 'USER', 1, 'NONE', NULL, 123, 'corridor-basic')
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  options = EXCLUDED.options,
  updater = 'corridor-basic',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_field
SET options = '[{"label":"综合管廊","value":"综合管廊"}]',
    updater = 'corridor-basic',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND code = 'facility_type';

-- ---------- 已有物理列登记为基础字段，保存写列，不写进扩展 JSON ----------

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required,
  sort_order, status, tenant_id, creator, is_searchable, is_filterable, is_sortable
)
SELECT v.entity_type_code, f.id, f.code, v.field_name, f.type, false,
       v.sort_order, 1, 123, 'corridor-basic', v.searchable, v.filterable, v.sortable
FROM (VALUES
  ('facility', 'address', '地址', 20, true, false, false),
  ('facility', 'facility_type', '设施类型', 30, false, true, false),
  ('facility', 'remark', '备注', 90, true, false, false),
  ('zone', 'remark', '备注', 90, true, false, false)
) AS v(entity_type_code, field_code, field_name, sort_order, searchable, filterable, sortable)
JOIN dynamic_field f
  ON f.code = v.field_code AND f.tenant_id = 123 AND f.deleted = false
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  updater = 'corridor-basic',
  update_time = CURRENT_TIMESTAMP;

-- ---------- 按型号分配。管廊工程记工程，管廊段记路段，舱室和防火区只记断面 ----------

CREATE TEMP TABLE _ut_assign (
  model_name text NOT NULL,
  field_code text NOT NULL,
  sort_ord int NOT NULL,
  field_source text NOT NULL,
  searchable boolean NOT NULL,
  filterable boolean NOT NULL,
  sortable boolean NOT NULL,
  alias text
);

INSERT INTO _ut_assign (model_name, field_code, sort_ord, field_source, searchable, filterable, sortable, alias) VALUES
  ('管廊工程', 'address', 20, 'BASE', true, false, false, '位置'),
  ('管廊工程', 'facility_type', 30, 'BASE', false, true, false, '类型'),
  ('管廊工程', 'ut_route', 40, 'CUSTOM', true, false, false, NULL),
  ('管廊工程', 'ut_owner_org', 50, 'CUSTOM', true, false, false, NULL),
  ('管廊工程', 'ut_design_org', 60, 'CUSTOM', true, false, false, NULL),
  ('管廊工程', 'ut_construction_org', 70, 'CUSTOM', true, false, false, NULL),
  ('管廊工程', 'ut_construction_date', 80, 'CUSTOM', false, true, true, NULL),
  ('管廊工程', 'ut_completion_date', 90, 'CUSTOM', false, true, true, NULL),
  ('管廊工程', 'ut_operation_mode', 100, 'CUSTOM', true, false, false, NULL),
  ('管廊工程', 'ut_drawing', 110, 'CUSTOM', false, false, false, NULL),
  ('管廊工程', 'remark', 120, 'BASE', true, false, false, NULL),
  ('管廊工程', 'ut_length_km', 200, 'CUSTOM', false, true, true, NULL),
  ('管廊工程', 'ut_width_m', 210, 'CUSTOM', false, true, true, NULL),
  ('管廊工程', 'ut_height_m', 220, 'CUSTOM', false, true, true, NULL);

INSERT INTO _ut_assign (model_name, field_code, sort_ord, field_source, searchable, filterable, sortable, alias)
SELECT m.model_name, v.field_code, v.sort_ord, v.field_source, v.searchable, v.filterable, v.sortable, NULL
FROM (VALUES
  ('管廊段'), ('防火区'), ('舱段'),
  ('综合管线舱'), ('电力专用舱'), ('电力信息舱'), ('综合管道仓'),
  ('燃气舱'), ('污水舱'), ('给水舱'), ('通信舱')
) AS m(model_name)
CROSS JOIN (VALUES
  ('remark', 120, 'BASE', true, false, false),
  ('ut_length_m', 200, 'CUSTOM', false, true, true),
  ('ut_width_m', 210, 'CUSTOM', false, true, true),
  ('ut_height_m', 220, 'CUSTOM', false, true, true)
) AS v(field_code, sort_ord, field_source, searchable, filterable, sortable);

INSERT INTO _ut_assign (model_name, field_code, sort_ord, field_source, searchable, filterable, sortable, alias) VALUES
  ('管廊段', 'ut_segment_location', 30, 'CUSTOM', true, false, false, NULL),
  ('管廊段', 'ut_gallery_class', 40, 'CUSTOM', false, true, false, NULL);

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  sort, field_source, model_code, field_code, tenant_id, creator,
  create_visible, edit_visible, detail_visible, collection_point, alias
)
SELECT
  m.id, f.id, false, a.searchable, a.filterable, a.sortable,
  a.sort_ord, a.field_source, m.code, f.code, 123, 'corridor-basic',
  true, true, true, false, a.alias
FROM _ut_assign a
JOIN dynamic_model m
  ON m.name = a.model_name AND m.tenant_id = 123 AND m.deleted = false
JOIN dynamic_field f
  ON f.code = a.field_code AND f.tenant_id = 123 AND f.deleted = false
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_model_field_assignment x
  WHERE x.tenant_id = 123 AND x.deleted = false
    AND x.model_code = m.code AND x.field_code = f.code
);

-- ---------- 表单分组。空的「扩展信息」换成客户的三组 ----------

CREATE TEMP TABLE _ut_grp (
  model_name text NOT NULL,
  group_id text NOT NULL,
  group_name text NOT NULL,
  group_sort int NOT NULL,
  field_code text NOT NULL,
  field_sort int NOT NULL
);

INSERT INTO _ut_grp VALUES
  ('管廊工程', 'ut-basic', '基本信息', 10, 'parentId', 10),
  ('管廊工程', 'ut-basic', '基本信息', 10, 'address', 20),
  ('管廊工程', 'ut-basic', '基本信息', 10, 'facility_type', 30),
  ('管廊工程', 'ut-profile', '基础信息', 20, 'ut_route', 10),
  ('管廊工程', 'ut-profile', '基础信息', 20, 'ut_owner_org', 20),
  ('管廊工程', 'ut-profile', '基础信息', 20, 'ut_design_org', 30),
  ('管廊工程', 'ut-profile', '基础信息', 20, 'ut_construction_org', 40),
  ('管廊工程', 'ut-profile', '基础信息', 20, 'ut_construction_date', 50),
  ('管廊工程', 'ut-profile', '基础信息', 20, 'ut_completion_date', 60),
  ('管廊工程', 'ut-profile', '基础信息', 20, 'ut_operation_mode', 70),
  ('管廊工程', 'ut-profile', '基础信息', 20, 'ut_drawing', 80),
  ('管廊工程', 'ut-profile', '基础信息', 20, 'remark', 90),
  ('管廊工程', 'ut-structure', '结构信息', 30, 'ut_length_km', 10),
  ('管廊工程', 'ut-structure', '结构信息', 30, 'ut_width_m', 20),
  ('管廊工程', 'ut-structure', '结构信息', 30, 'ut_height_m', 30),
  ('管廊段', 'ut-basic', '基本信息', 10, 'parentId', 10),
  ('管廊段', 'ut-basic', '基本信息', 10, 'facility_id', 20),
  ('管廊段', 'ut-basic', '基本信息', 10, 'F-ac7a1961c40f4e84971b75263dc3ac52', 30),
  ('管廊段', 'ut-basic', '基本信息', 10, 'ut_segment_location', 40),
  ('管廊段', 'ut-basic', '基本信息', 10, 'ut_gallery_class', 50),
  ('管廊段', 'ut-basic', '基本信息', 10, 'remark', 60),
  ('管廊段', 'ut-structure', '结构信息', 30, 'ut_length_m', 10),
  ('管廊段', 'ut-structure', '结构信息', 30, 'ut_width_m', 20),
  ('管廊段', 'ut-structure', '结构信息', 30, 'ut_height_m', 30);

INSERT INTO _ut_grp (model_name, group_id, group_name, group_sort, field_code, field_sort)
SELECT m.model_name, v.group_id, v.group_name, v.group_sort, v.field_code, v.field_sort
FROM (VALUES
  ('防火区'), ('舱段'),
  ('综合管线舱'), ('电力专用舱'), ('电力信息舱'), ('综合管道仓'),
  ('燃气舱'), ('污水舱'), ('给水舱'), ('通信舱')
) AS m(model_name)
CROSS JOIN (VALUES
  ('ut-basic', '基本信息', 10, 'parentId', 10),
  ('ut-basic', '基本信息', 10, 'facility_id', 20),
  ('ut-basic', '基本信息', 10, 'remark', 30),
  ('ut-structure', '结构信息', 30, 'ut_length_m', 10),
  ('ut-structure', '结构信息', 30, 'ut_width_m', 20),
  ('ut-structure', '结构信息', 30, 'ut_height_m', 30)
) AS v(group_id, group_name, group_sort, field_code, field_sort);

UPDATE dynamic_model m
SET field_groups_config = src.doc,
    updater = 'corridor-basic',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT g.model_name,
    jsonb_build_object(
      'groups',
      jsonb_agg(
        jsonb_build_object(
          'id', g.group_id,
          'name', g.group_name,
          'sort', g.group_sort,
          'fields', g.fields
        )
        ORDER BY g.group_sort
      )
    )::text AS doc
  FROM (
    SELECT r.model_name, r.group_id, r.group_name, r.group_sort,
      jsonb_agg(
        jsonb_build_object('fieldId', f.id, 'sort', r.field_sort, 'extra', '{}'::jsonb)
        ORDER BY r.field_sort
      ) AS fields
    FROM _ut_grp r
    JOIN dynamic_field f
      ON f.code = r.field_code AND f.tenant_id = 123 AND f.deleted = false
    GROUP BY r.model_name, r.group_id, r.group_name, r.group_sort
  ) g
  GROUP BY g.model_name
) src
WHERE m.tenant_id = 123 AND m.deleted = false AND m.name = src.model_name;

-- ---------- 手册 1.3 已有事实 ----------

UPDATE ent_facility_t123
SET name = '武汉光谷中心城综合管廊',
    address = '武汉光谷中心城',
    facility_type = '综合管廊',
    remark = '合作期限20年（建设期3年、运营期17年）。投资概算203042.93万元，其中工程费159004.38万元。地下空间部分约4.51km，非地下空间部分约18.51km。规划入廊管线主要包括给水、再生水、冷/热水、电力、通信、压力污水，并预留垃圾气力管。',
    custom_fields = COALESCE(custom_fields, '{}'::jsonb) || jsonb_build_object(
      'ut_route', '神墩三路、托月路、神墩五路、清湾路、高新大道、神墩一路、光谷五路、高科园路、光谷六路北段、光谷六路南段、南新街/虎山东街',
      'ut_operation_mode', 'BOT（建设、运营、移交）。付费方式为使用者付费+可行性缺口补助。',
      'ut_length_km', 23.08
    ),
    updater = 'corridor-basic',
    update_time = CURRENT_TIMESTAMP
WHERE id = 1 AND deleted = false;

UPDATE ent_zone_t123
SET name = '武汉光谷中心城综合管廊',
    updater = 'corridor-basic',
    update_time = CURRENT_TIMESTAMP
WHERE id = 1 AND deleted = false;

UPDATE dynamic_category
SET name = '武汉光谷中心城综合管廊',
    updater = 'corridor-basic',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND code IN ('FAC-CORRIDOR-WH-GGGL', 'ZONE-CAT-WH-GGGL');

-- 名称与手册道路一致的管廊段。北/南拆开的道路手册没有对应拆分，不把整路范围写到其中一段。

UPDATE ent_zone_t123 z
SET remark = v.cabin_note,
    custom_fields = COALESCE(z.custom_fields, '{}'::jsonb) || jsonb_build_object(
      'ut_segment_location', v.location,
      'ut_gallery_class', v.gallery_class
    ),
    updater = 'corridor-basic',
    update_time = CURRENT_TIMESTAMP
FROM (VALUES
  ('神墩三路', '豹溪路－光谷七路', '干支', '三舱（高压电力舱、电力信息舱和管道舱）'),
  ('托月路', '九峰变电站－光谷五路', '电力专用', '双舱（高压电力舱）'),
  ('神墩五路', '豹溪路－高科园路', '支', '双舱（电力信息舱和管道舱）'),
  ('虎山东街', '光谷五路－高科园路', '干支', '双舱（电力信息舱和管道舱）'),
  ('高新大道', '光谷三路－光谷五路', '支/电力专用', '光谷三路－光谷四路段为单舱（高压电力舱），光谷四路－光谷五路段为三舱（高压电力舱、电力信息舱和管道舱）')
) AS v(seg_name, location, gallery_class, cabin_note)
WHERE z.tenant_id = 123 AND z.deleted = false AND z.name = v.seg_name
  AND z.model_id = (SELECT id FROM dynamic_model WHERE tenant_id = 123 AND deleted = false AND name = '管廊段');

-- 手册有、分区树里还没有的道路

INSERT INTO ent_zone_t123 (
  id, tenant_id, entity_type_code, model_id, name, code, status, parent_id,
  custom_fields, creator, tree_path, sort, facility_id, remark
)
SELECT
  v.id, 123, 'zone', m.id, v.name, v.code, 1, 1,
  jsonb_build_object('ut_segment_location', v.location, 'ut_gallery_class', v.gallery_class),
  'corridor-basic', '/1/' || v.id || '/', v.sort_ord, 1, v.cabin_note
FROM (VALUES
  (459, '光谷四路', 'ZONE-SEG-GG4L', '神墩三路－九峰变电', '电力专用', '单舱（高压电力舱）', 11),
  (460, '清湾路', 'ZONE-SEG-QINGWAN', '高新二路－高新三路', '支', '双舱（电力信息舱和管道舱）', 12),
  (461, '神墩一路', 'ZONE-SEG-SD1', '豹溪路－光谷六路', '支', '双舱（电力信息舱和管道舱）', 13)
) AS v(id, name, code, location, gallery_class, cabin_note, sort_ord)
JOIN dynamic_model m ON m.tenant_id = 123 AND m.deleted = false AND m.name = '管廊段'
WHERE NOT EXISTS (
  SELECT 1 FROM ent_zone_t123 z WHERE z.id = v.id OR (z.tenant_id = 123 AND z.deleted = false AND z.code = v.code)
);

INSERT INTO dynamic_category (
  id, parent_id, name, code, category_type_code, tree_path, level, sort, status, tenant_id, creator
)
SELECT
  v.id, 2105219, v.name, v.code, 'zone',
  '/2105216/2105219/' || v.id || '/', 3, v.sort_ord, 1, 123, 'corridor-basic'
FROM (VALUES
  (2107592, '光谷四路', 'ZONE-SEG-GG4L', 11),
  (2107593, '清湾路', 'ZONE-SEG-QINGWAN', 12),
  (2107594, '神墩一路', 'ZONE-SEG-SD1', 13)
) AS v(id, name, code, sort_ord)
WHERE NOT EXISTS (
  SELECT 1 FROM dynamic_category c WHERE c.tenant_id = 123 AND c.deleted = false AND c.code = v.code
);

SELECT setval('ent_zone_t123_id_seq', (SELECT MAX(id) FROM ent_zone_t123));
SELECT setval('ent_facility_t123_id_seq', (SELECT MAX(id) FROM ent_facility_t123));
SELECT setval('dynamic_category_id_seq', (SELECT MAX(id) FROM dynamic_category));

COMMIT;

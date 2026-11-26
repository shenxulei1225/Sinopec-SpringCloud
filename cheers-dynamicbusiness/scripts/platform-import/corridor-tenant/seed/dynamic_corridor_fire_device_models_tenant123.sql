-- 设备目录补上干粉、灭火器箱、防火门三个型号。字段分配与气溶胶相同。
-- 干粉、灭火器箱按消防设计说明的台数建实体。规格写入规格型号。安装位置留空。所属分区写管廊段。
-- 名称末尾序号只用来区分各条，不是图上的位置编号。
-- 防火门说明只写了甲级、设在防火分隔处，没有扇数，不插入实体。

BEGIN;

INSERT INTO dynamicbusiness.dynamic_model (
    code, name, entity_type_code, status, sort, field_groups_config,
    creator, updater, tenant_id, governance_status
)
SELECT
    'MODEL-' || upper(replace(gen_random_uuid()::text, '-', '')),
    src.name,
    'equipment',
    1,
    src.sort,
    '{"groups":[{"id":"group-fire-' || src.name || '","name":"扩展信息","color":"#409eff","sort":1024,"parentId":null,"fields":[]}]}',
    'corridor-fire-device',
    'corridor-fire-device',
    123,
    'COMPANY'
FROM (VALUES
    ('干粉', 12289),
    ('灭火器箱', 12290),
    ('防火门', 12291)
) AS src(name, sort)
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dynamic_model m
    WHERE m.tenant_id = 123
      AND m.entity_type_code = 'equipment'
      AND m.name = src.name
      AND COALESCE(m.deleted, false) = false
);

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    default_value, validation_rules, sort, field_group_id, field_source,
    ref_library_id, model_relation_id, target_entity_type,
    creator, updater, tenant_id, model_code, field_code, alias,
    create_visible, edit_visible, detail_visible, collection_point
)
SELECT
    nm.id,
    a.field_id, a.required, a.is_searchable, a.is_filterable, a.is_sortable,
    a.default_value, a.validation_rules, a.sort, NULL, a.field_source,
    a.ref_library_id, a.model_relation_id, a.target_entity_type,
    'corridor-fire-device', 'corridor-fire-device', 123, nm.code, a.field_code, a.alias,
    a.create_visible, a.edit_visible, a.detail_visible, a.collection_point
FROM dynamicbusiness.dynamic_model nm
JOIN dynamicbusiness.dynamic_model_field_assignment a
  ON a.model_id = 2542 AND COALESCE(a.deleted, false) = false
WHERE nm.tenant_id = 123
  AND nm.entity_type_code = 'equipment'
  AND nm.name IN ('干粉', '灭火器箱', '防火门')
  AND COALESCE(nm.deleted, false) = false
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_model_field_assignment x
      WHERE x.model_id = nm.id
        AND x.field_code = a.field_code
        AND COALESCE(x.deleted, false) = false
  );

INSERT INTO dynamicbusiness.ent_equipment_t123 (
    entity_type_code, model_id, name, custom_fields,
    creator, updater, code, host_sop_param_pack, execution_means,
    tenant_id, facility_id, zone_id, status, equipment_model
)
SELECT
    'equipment',
    m.id,
    src.seg_name || src.kind || '-' || lpad(gs.n::text, 3, '0'),
    jsonb_build_object('zone_id', jsonb_build_array(jsonb_build_object('id', src.seg_id, 'entityTypeCode', 'zone'))),
    'corridor-fire-device',
    'corridor-fire-device',
    upper(replace(gen_random_uuid()::text, '-', '')),
    '{}'::jsonb,
    '[]'::jsonb,
    123,
    1,
    src.seg_id,
    1,
    src.spec
FROM (
    VALUES
        (5::bigint, '神墩五路', '干粉', 74, 'MF/ABC4X2，充装4kg'),
        (460::bigint, '清湾路', '干粉', 54, 'MF/ABC4X2，充装4kg'),
        (5::bigint, '神墩五路', '灭火器箱', 74, '内置2具灭火器'),
        (460::bigint, '清湾路', '灭火器箱', 54, '内置2具灭火器')
) AS src(seg_id, seg_name, kind, qty, spec)
JOIN dynamicbusiness.dynamic_model m
  ON m.tenant_id = 123
 AND m.entity_type_code = 'equipment'
 AND m.name = src.kind
 AND COALESCE(m.deleted, false) = false
CROSS JOIN LATERAL generate_series(1, src.qty) AS gs(n)
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.ent_equipment_t123 e
    WHERE e.model_id = m.id
      AND e.tenant_id = 123
      AND COALESCE(e.deleted, false) = false
      AND e.name = src.seg_name || src.kind || '-' || lpad(gs.n::text, 3, '0')
);

INSERT INTO dynamicbusiness.dynamic_entity_field_index (
    entity_id, model_id, field_code, value_string, value_number,
    creator, updater, tenant_id
)
SELECT e.id, e.model_id, 'equipment_model', e.equipment_model, NULL,
       'corridor-fire-device', 'corridor-fire-device', 123
FROM dynamicbusiness.ent_equipment_t123 e
WHERE e.creator = 'corridor-fire-device'
  AND COALESCE(e.deleted, false) = false
  AND e.equipment_model IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_field_index i
      WHERE i.entity_id = e.id AND i.field_code = 'equipment_model'
        AND i.tenant_id = 123 AND COALESCE(i.deleted, false) = false
  );

INSERT INTO dynamicbusiness.dynamic_entity_field_index (
    entity_id, model_id, field_code, value_string, value_number,
    creator, updater, tenant_id
)
SELECT e.id, e.model_id, 'zone_id', e.zone_id::text, e.zone_id,
       'corridor-fire-device', 'corridor-fire-device', 123
FROM dynamicbusiness.ent_equipment_t123 e
WHERE e.creator = 'corridor-fire-device'
  AND COALESCE(e.deleted, false) = false
  AND e.zone_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_field_index i
      WHERE i.entity_id = e.id AND i.field_code = 'zone_id'
        AND i.tenant_id = 123 AND COALESCE(i.deleted, false) = false
  );

COMMIT;

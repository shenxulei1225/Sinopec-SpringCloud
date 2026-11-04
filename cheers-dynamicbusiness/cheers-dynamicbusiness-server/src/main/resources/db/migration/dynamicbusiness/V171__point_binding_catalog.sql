-- 点位对照数据目录。
-- 一行是一个防火区里的一个完整标签名，对到哪一台设备的哪一个型号字段。
-- Gateway 按 zone_code + tag_name 读这一行上的 equipment_id、point_field_code、read_write。
-- 不按采样去联查防火区名称或设备名称。zone_id、equipment_id 是页面上选防火区和设备时用的编号，和上面两列是同一份值。
-- 不存读数，不存 OPC 节点号。地址段和 PLC 地址在防火区型号上，不写在每一行。

SET search_path TO dynamicbusiness, public;

CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.ent_point_binding_t123_id_seq;

CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_point_binding_t123 (
    id bigint PRIMARY KEY DEFAULT nextval('dynamicbusiness.ent_point_binding_t123_id_seq'),
    tenant_id bigint NOT NULL DEFAULT 123,
    entity_type_code varchar(64) NOT NULL DEFAULT 'point_binding',
    model_id bigint NOT NULL,
    domain varchar(128),
    name varchar(255) NOT NULL,
    code varchar(255),
    parent_id bigint DEFAULT 0,
    tree_path varchar(1024),
    sort integer DEFAULT 0,
    status integer DEFAULT 1,
    custom_fields jsonb DEFAULT '{}'::jsonb,
    creator varchar(64),
    create_time timestamp DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64),
    update_time timestamp DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    zone_code varchar(32) NOT NULL,
    tag_name varchar(128) NOT NULL,
    zone_id bigint,
    equipment_id bigint NOT NULL,
    point_field_code varchar(64) NOT NULL,
    read_write varchar(16)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_point_binding_t123_code
    ON dynamicbusiness.ent_point_binding_t123 (tenant_id, code)
    WHERE deleted = false AND code IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_point_binding_t123_zone_tag
    ON dynamicbusiness.ent_point_binding_t123 (tenant_id, zone_code, tag_name)
    WHERE deleted = false;

CREATE INDEX IF NOT EXISTS idx_ent_point_binding_t123_equipment
    ON dynamicbusiness.ent_point_binding_t123 (tenant_id, equipment_id, point_field_code)
    WHERE deleted = false;

COMMENT ON TABLE dynamicbusiness.ent_point_binding_t123 IS
    '点位对照。Gateway 用分区编号加完整标签名读取设备编号和型号字段编号。';
COMMENT ON COLUMN dynamicbusiness.ent_point_binding_t123.zone_code IS
    '防火区的分区编号，例如 GKYB01。Gateway 用它区分 PLC。';
COMMENT ON COLUMN dynamicbusiness.ent_point_binding_t123.tag_name IS
    'PLC 上的完整标签名。';
COMMENT ON COLUMN dynamicbusiness.ent_point_binding_t123.zone_id IS
    '防火区实体编号。页面选防火区时写入，和 zone_code 是同一条防火区。';
COMMENT ON COLUMN dynamicbusiness.ent_point_binding_t123.equipment_id IS
    '设备编号。历史行用这个编号加 point_field_code。';
COMMENT ON COLUMN dynamicbusiness.ent_point_binding_t123.point_field_code IS
    '设备型号上那个采集点的字段编号。';

INSERT INTO dynamicbusiness.dynamic_entity_type (
    code, name, description, alias, sort, status, type_level, association_fields,
    storage_type, dedicated_table_name, enable_rule_engine, creator, updater, deleted,
    tenant_id, group_name, entry_kind, model_workbench_mode, work_scope
)
SELECT
    'point_binding', '点位对照', '一个防火区里的一个标签对到一台设备的一个采集点', '点位对照',
    90, 'active', 'USER', '{}'::jsonb, 'DEDICATED', 'ent_point_binding_t123', false,
    'point-table-seed', 'point-table-seed', false, 123, '采集', 'NATIVE', 'MULTI', 'FACILITY'
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_entity_type
    WHERE tenant_id = 123 AND code = 'point_binding' AND deleted = false
);

INSERT INTO dynamicbusiness.dynamic_model (
    code, name, entity_type_code, description, status, sort, creator, updater,
    deleted, tenant_id, governance_status
)
SELECT
    'MODEL-point-binding', '点位对照', 'point_binding', '点位对照', 1, 0,
    'point-table-seed', 'point-table-seed', false, 123, 'COMPANY'
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_model
    WHERE tenant_id = 123 AND entity_type_code = 'point_binding' AND deleted = false
);

INSERT INTO dynamicbusiness.dynamic_field (
    code, name, type, description, source, status, provider_code, tenant_id,
    creator, updater, deleted, governance_status
)
SELECT v.code, v.name, v.type, v.description, 'CUSTOM', 1, v.provider_code, 123,
       'point-table-seed', 'point-table-seed', false, 'COMPANY'
FROM (VALUES
    ('zone_code', '分区编号', 'TEXT', '防火区的分区编号。Gateway 用它区分 PLC。', NULL),
    ('tag_name', '标签名', 'TEXT', 'PLC 上的完整标签名。', NULL),
    ('zone_id', '防火区', 'ENTITY_REF', '这一行属于哪一个防火区。', 'dynamic-entity:zone'),
    ('equipment_id', '设备', 'ENTITY_REF', '这一行对到哪一台设备。', 'dynamic-entity:equipment'),
    ('point_field_code', '点编号', 'TEXT', '设备型号上那个采集点的字段编号。', NULL),
    ('read_write', '读写', 'TEXT', '这个标签是读还是写。', NULL),
    ('plc_host', 'PLC地址', 'TEXT', '这个防火区那一台 PLC 的地址。不从地址段推算。', NULL),
    ('address_segment', '地址段', 'TEXT', '这个防火区的网络地址段。由人填写，不从 PLC 地址推算。', NULL)
) AS v(code, name, type, description, provider_code)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_field f
    WHERE f.tenant_id = 123 AND f.code = v.code AND f.deleted = false
);

INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
    entity_type_code, library_field_id, field_code, field_name, data_type, required,
    description, sort_order, status, tenant_id, creator, updater, deleted,
    is_searchable, is_filterable, is_sortable
)
SELECT
    'point_binding', field.id, field.code, field.name, field.type,
    field.code IN ('zone_code', 'tag_name', 'equipment_id', 'point_field_code'),
    field.description,
    CASE field.code
        WHEN 'zone_code' THEN 10
        WHEN 'tag_name' THEN 20
        WHEN 'zone_id' THEN 30
        WHEN 'equipment_id' THEN 40
        WHEN 'point_field_code' THEN 50
        ELSE 60
    END,
    1, 123, 'point-table-seed', 'point-table-seed', false,
    field.code IN ('zone_code', 'tag_name'), true, false
FROM dynamicbusiness.dynamic_field field
WHERE field.tenant_id = 123
  AND field.deleted = false
  AND field.code IN ('zone_code', 'tag_name', 'zone_id', 'equipment_id', 'point_field_code', 'read_write')
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field base
      WHERE base.tenant_id = 123
        AND base.entity_type_code = 'point_binding'
        AND base.field_code = field.code
        AND base.deleted = false
  );

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
    creator, updater, deleted, tenant_id, model_code, field_code, collection_point,
    create_visible, edit_visible, detail_visible, field_source
)
SELECT
    model.id, field.id,
    field.code IN ('zone_code', 'tag_name', 'equipment_id', 'point_field_code'),
    field.code IN ('zone_code', 'tag_name'),
    true, false,
    CASE field.code
        WHEN 'zone_code' THEN 10
        WHEN 'tag_name' THEN 20
        WHEN 'zone_id' THEN 30
        WHEN 'equipment_id' THEN 40
        WHEN 'point_field_code' THEN 50
        ELSE 60
    END,
    'point-table-seed', 'point-table-seed', false, 123,
    model.code, field.code, false, true, true, true, 'USER_ADDED'
FROM dynamicbusiness.dynamic_model model
JOIN dynamicbusiness.dynamic_field field
  ON field.tenant_id = 123 AND field.deleted = false
 AND field.code IN ('zone_code', 'tag_name', 'zone_id', 'equipment_id', 'point_field_code', 'read_write')
WHERE model.tenant_id = 123
  AND model.entity_type_code = 'point_binding'
  AND model.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment a
      WHERE a.tenant_id = 123 AND a.model_id = model.id AND a.field_code = field.code AND a.deleted = false
  );

-- PLC地址、地址段只挂在防火区型号上。舱室和管廊段不出现这两项。
INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
    creator, updater, deleted, tenant_id, model_code, field_code, collection_point,
    create_visible, edit_visible, detail_visible, field_source
)
SELECT
    model.id, field.id, false, false, false, false,
    CASE field.code WHEN 'plc_host' THEN 210 ELSE 220 END,
    'point-table-seed', 'point-table-seed', false, 123,
    model.code, field.code, false, true, true, true, 'USER_ADDED'
FROM dynamicbusiness.dynamic_model model
JOIN dynamicbusiness.dynamic_field field
  ON field.tenant_id = 123 AND field.deleted = false
 AND field.code IN ('plc_host', 'address_segment')
WHERE model.tenant_id = 123
  AND model.id = 2313
  AND model.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment a
      WHERE a.tenant_id = 123 AND a.model_id = model.id AND a.field_code = field.code AND a.deleted = false
  );

-- 点位行已经在 ent_point_binding_t123。这里不再从草稿表复制。

SELECT setval(
    'dynamicbusiness.ent_point_binding_t123_id_seq',
    GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_point_binding_t123), 1)
);

-- 车辆管理：名单、车道，以及进出账上的识别结果。
-- 只给已经有车辆进出账的租户补齐。开通时新租户从模板复制，不在这里扫全部租户。
SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  rec record;
  col record;
BEGIN
  FOR rec IN
    SELECT tenant_id, dedicated_table_name
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code = 'vehicle_pass'
      AND deleted = false
      AND dedicated_table_name IS NOT NULL
  LOOP
    FOR col IN
      SELECT *
      FROM (VALUES
        ('plate_color', 'text'),
        ('pass_direction', 'text'),
        ('lane_id', 'bigint'),
        ('match_result', 'text'),
        ('barrier_result', 'text'),
        ('release_reason', 'text'),
        ('scene_photo', 'text'),
        ('plate_photo', 'text'),
        ('driver_name', 'text'),
        ('driver_phone', 'text'),
        ('host_person_id', 'bigint'),
        ('visit_reason', 'text'),
        ('registered_at', 'timestamp'),
        ('agreed_at', 'timestamp'),
        ('rejected_at', 'timestamp'),
        ('valid_from', 'timestamp'),
        ('valid_to', 'timestamp'),
        ('alarm_note', 'text')
      ) AS v(name, kind)
    LOOP
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS %I %s',
        rec.dedicated_table_name, col.name, col.kind);
    END LOOP;
  END LOOP;
END $$;

INSERT INTO dynamicbusiness.dynamic_field (
    code, name, type, description, source, status, provider_code, tenant_id,
    creator, updater, deleted, governance_status
)
SELECT v.code, v.name, v.type, v.description, 'CUSTOM', 1, v.provider_code, tenant.tenant_id,
       'vehicle-pass', 'vehicle-pass', false, 'COMPANY'
FROM (
    SELECT DISTINCT tenant_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code = 'vehicle_pass' AND deleted = false
) tenant
CROSS JOIN (VALUES
    ('plate_color', '车牌颜色', 'TEXT', '这一次识别到的车牌颜色。', NULL),
    ('pass_direction', '方向', 'TEXT', '这一条是进场还是出场。', NULL),
    ('lane_id', '车道', 'ENTITY_REF', '这一次过车走的车道。', 'dynamic-entity:vehicle_lane'),
    ('match_result', '比对结果', 'TEXT', '自动放行、拒绝、待人工确认或人工放行。', NULL),
    ('barrier_result', '抬杆结果', 'TEXT', '道闸有没有按这一次放行抬起来。', NULL),
    ('release_reason', '放行原因', 'TEXT', '门卫确认放行时写下的原因。', NULL),
    ('scene_photo', '全景照片', 'TEXT', '这一次识别的全景照片。', NULL),
    ('plate_photo', '车牌照片', 'TEXT', '这一次识别的车牌照片。', NULL),
    ('driver_name', '驾驶人', 'TEXT', '这辆车的驾驶人。', NULL),
    ('driver_phone', '联系电话', 'TEXT', '驾驶人的电话。', NULL),
    ('host_person_id', '被访人', 'ENTITY_REF', '外来车要找的人。只记这个人的编号。', 'dynamic-entity:user_account'),
    ('visit_reason', '来访事由', 'TEXT', '这一次外来车为什么来。', NULL),
    ('registered_at', '登记时间', 'DATETIME', '外来车登记的时间。', NULL),
    ('agreed_at', '同意时间', 'DATETIME', '被访人同意的时间。', NULL),
    ('rejected_at', '拒绝时间', 'DATETIME', '被访人拒绝的时间。', NULL),
    ('valid_from', '有效期起', 'DATETIME', '允许进入从什么时候开始。', NULL),
    ('valid_to', '有效期止', 'DATETIME', '允许进入到什么时候为止。', NULL),
    ('alarm_note', '告警说明', 'TEXT', '禁止进入时告警没有写入的原因。', NULL),
    ('vehicle_type', '车辆类型', 'TEXT', '小型车、货车、危化品车或工程车。', NULL),
    ('org_name', '所属单位', 'TEXT', '这辆车属于哪一家单位。', NULL),
    ('list_status', '名单', 'TEXT', '允许进入或禁止进入。', NULL),
    ('allowed_lane_ids', '允许车道', 'TEXT', '允许走的车道编号，用逗号隔开。空着表示车道不限。', NULL),
    ('camera_id', '识别摄像机', 'ENTITY_REF', '这条车道上的车牌识别摄像机。', 'dynamic-entity:equipment'),
    ('barrier_id', '道闸', 'ENTITY_REF', '这条车道上的道闸。', 'dynamic-entity:equipment')
) AS v(code, name, type, description, provider_code)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_field own
    WHERE own.tenant_id = tenant.tenant_id AND own.code = v.code AND own.deleted = false
);

INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
    entity_type_code, library_field_id, field_code, field_name, data_type, required,
    description, sort_order, status, tenant_id, creator, updater, deleted,
    is_searchable, is_filterable, is_sortable
)
SELECT
    'vehicle_pass', field.id, field.code, field.name,
    CASE field.type WHEN 'ENTITY_REF' THEN 'REF' WHEN 'DATETIME' THEN 'DATETIME' ELSE 'TEXT' END,
    false, field.description,
    CASE field.code
        WHEN 'plate_color' THEN 20
        WHEN 'pass_direction' THEN 21
        WHEN 'lane_id' THEN 22
        WHEN 'match_result' THEN 23
        WHEN 'barrier_result' THEN 24
        WHEN 'release_reason' THEN 25
        WHEN 'scene_photo' THEN 26
        WHEN 'plate_photo' THEN 27
        WHEN 'driver_name' THEN 28
        WHEN 'driver_phone' THEN 29
        WHEN 'host_person_id' THEN 30
        WHEN 'visit_reason' THEN 31
        WHEN 'registered_at' THEN 32
        WHEN 'agreed_at' THEN 33
        WHEN 'rejected_at' THEN 34
        WHEN 'valid_from' THEN 35
        WHEN 'valid_to' THEN 36
        WHEN 'alarm_note' THEN 37
        ELSE 90
    END,
    1, field.tenant_id, 'vehicle-pass', 'vehicle-pass', false,
    field.code IN ('match_result', 'pass_direction'), true, false
FROM dynamicbusiness.dynamic_field field
WHERE field.deleted = false
  AND field.code IN (
      'plate_color', 'pass_direction', 'lane_id', 'match_result', 'barrier_result', 'release_reason',
      'scene_photo', 'plate_photo', 'driver_name', 'driver_phone', 'host_person_id', 'visit_reason',
      'registered_at', 'agreed_at', 'rejected_at', 'valid_from', 'valid_to', 'alarm_note')
  AND EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_type catalog
      WHERE catalog.tenant_id = field.tenant_id AND catalog.code = 'vehicle_pass' AND catalog.deleted = false)
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field base
      WHERE base.tenant_id = field.tenant_id
        AND base.entity_type_code = 'vehicle_pass'
        AND base.field_code = field.code
        AND base.deleted = false);

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
    creator, updater, deleted, tenant_id, model_code, field_code, collection_point,
    create_visible, edit_visible, detail_visible, field_source
)
SELECT
    model.id, field.id, false,
    field.code IN ('match_result', 'pass_direction'), true, false,
    base.sort_order,
    'vehicle-pass', 'vehicle-pass', false, model.tenant_id, model.code, field.code, false,
    true, true, true, 'BASE'
FROM dynamicbusiness.dynamic_model model
JOIN dynamicbusiness.dynamic_entity_type_base_field base
  ON base.tenant_id = model.tenant_id
 AND base.entity_type_code = 'vehicle_pass'
 AND base.deleted = false
 AND base.field_code IN (
      'plate_color', 'pass_direction', 'lane_id', 'match_result', 'barrier_result', 'release_reason',
      'scene_photo', 'plate_photo', 'driver_name', 'driver_phone', 'host_person_id', 'visit_reason',
      'registered_at', 'agreed_at', 'rejected_at', 'valid_from', 'valid_to', 'alarm_note')
JOIN dynamicbusiness.dynamic_field field
  ON field.tenant_id = model.tenant_id AND field.code = base.field_code AND field.deleted = false
WHERE model.entity_type_code = 'vehicle_pass'
  AND model.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment own
      WHERE own.tenant_id = model.tenant_id
        AND own.model_id = model.id
        AND own.field_code = field.code
        AND own.deleted = false);

DO $$
DECLARE
  rec record;
  list_table text;
  lane_table text;
BEGIN
  FOR rec IN
    SELECT tenant_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code = 'vehicle_pass' AND deleted = false
  LOOP
    list_table := 'ent_vehicle_list_t' || rec.tenant_id::text;
    lane_table := 'ent_vehicle_lane_t' || rec.tenant_id::text;

    EXECUTE format('CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.%I', list_table || '_id_seq');
    EXECUTE format($sql$
        CREATE TABLE IF NOT EXISTS dynamicbusiness.%I (
            id bigint PRIMARY KEY DEFAULT nextval(%L),
            tenant_id bigint NOT NULL DEFAULT %s,
            entity_type_code varchar(64) NOT NULL DEFAULT 'vehicle_list',
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
            facility_id bigint,
            vehicle_type text,
            org_name text,
            driver_name text,
            driver_phone text,
            list_status text,
            valid_from timestamp,
            valid_to timestamp,
            allowed_lane_ids text
        )$sql$, list_table, 'dynamicbusiness.' || list_table || '_id_seq', rec.tenant_id::text);

    EXECUTE format('CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.%I', lane_table || '_id_seq');
    EXECUTE format($sql$
        CREATE TABLE IF NOT EXISTS dynamicbusiness.%I (
            id bigint PRIMARY KEY DEFAULT nextval(%L),
            tenant_id bigint NOT NULL DEFAULT %s,
            entity_type_code varchar(64) NOT NULL DEFAULT 'vehicle_lane',
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
            facility_id bigint,
            pass_direction text,
            camera_id bigint,
            barrier_id bigint
        )$sql$, lane_table, 'dynamicbusiness.' || lane_table || '_id_seq', rec.tenant_id::text);

    INSERT INTO dynamicbusiness.dynamic_entity_type (
        code, name, description, alias, sort, status, type_level, association_fields,
        storage_type, dedicated_table_name, enable_rule_engine, creator, updater, deleted,
        tenant_id, group_name, entry_kind, model_workbench_mode, work_scope
    )
    SELECT
        'vehicle_list', '车辆名单', '一辆车允许进入还是禁止进入', '车辆名单',
        COALESCE((SELECT MAX(sort) FROM dynamicbusiness.dynamic_entity_type WHERE tenant_id = rec.tenant_id AND deleted = false), 0) + 1,
        'active', 'USER', '{}'::jsonb, 'DEDICATED', list_table, false,
        'vehicle-pass', 'vehicle-pass', false, rec.tenant_id, '安防管理', 'NATIVE', 'MULTI', 'FACILITY'
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_entity_type
        WHERE tenant_id = rec.tenant_id AND code = 'vehicle_list' AND deleted = false
    );

    INSERT INTO dynamicbusiness.dynamic_entity_type (
        code, name, description, alias, sort, status, type_level, association_fields,
        storage_type, dedicated_table_name, enable_rule_engine, creator, updater, deleted,
        tenant_id, group_name, entry_kind, model_workbench_mode, work_scope
    )
    SELECT
        'vehicle_lane', '车道', '一个出入口的一条进场或出场车道', '车道',
        COALESCE((SELECT MAX(sort) FROM dynamicbusiness.dynamic_entity_type WHERE tenant_id = rec.tenant_id AND deleted = false), 0) + 1,
        'active', 'USER', '{}'::jsonb, 'DEDICATED', lane_table, false,
        'vehicle-pass', 'vehicle-pass', false, rec.tenant_id, '安防管理', 'NATIVE', 'MULTI', 'FACILITY'
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_entity_type
        WHERE tenant_id = rec.tenant_id AND code = 'vehicle_lane' AND deleted = false
    );

    INSERT INTO dynamicbusiness.dynamic_model (
        code, name, entity_type_code, description, status, sort, creator, updater,
        deleted, tenant_id, governance_status
    )
    SELECT 'MODEL-vehicle-list', '车辆名单', 'vehicle_list', '车辆名单', 1, 0,
           'vehicle-pass', 'vehicle-pass', false, rec.tenant_id, 'COMPANY'
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_model
        WHERE tenant_id = rec.tenant_id AND entity_type_code = 'vehicle_list' AND deleted = false
    );

    INSERT INTO dynamicbusiness.dynamic_model (
        code, name, entity_type_code, description, status, sort, creator, updater,
        deleted, tenant_id, governance_status
    )
    SELECT 'MODEL-vehicle-lane', '车道', 'vehicle_lane', '车道', 1, 0,
           'vehicle-pass', 'vehicle-pass', false, rec.tenant_id, 'COMPANY'
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_model
        WHERE tenant_id = rec.tenant_id AND entity_type_code = 'vehicle_lane' AND deleted = false
    );
  END LOOP;
END $$;

INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
    entity_type_code, library_field_id, field_code, field_name, data_type, required,
    description, sort_order, status, tenant_id, creator, updater, deleted,
    is_searchable, is_filterable, is_sortable
)
SELECT
    catalog.code,
    field.id,
    field.code,
    field.name,
    CASE field.type WHEN 'ENTITY_REF' THEN 'REF' WHEN 'DATETIME' THEN 'DATETIME' ELSE 'TEXT' END,
    field.code IN ('list_status', 'pass_direction'),
    field.description,
    CASE field.code
        WHEN 'vehicle_type' THEN 10
        WHEN 'org_name' THEN 11
        WHEN 'driver_name' THEN 12
        WHEN 'driver_phone' THEN 13
        WHEN 'list_status' THEN 14
        WHEN 'valid_from' THEN 15
        WHEN 'valid_to' THEN 16
        WHEN 'allowed_lane_ids' THEN 17
        WHEN 'pass_direction' THEN 10
        WHEN 'camera_id' THEN 11
        WHEN 'barrier_id' THEN 12
        ELSE 20
    END,
    1, catalog.tenant_id, 'vehicle-pass', 'vehicle-pass', false, false, true, false
FROM dynamicbusiness.dynamic_entity_type catalog
JOIN dynamicbusiness.dynamic_field field
  ON field.tenant_id = catalog.tenant_id AND field.deleted = false
 AND (
      (catalog.code = 'vehicle_list' AND field.code IN (
          'vehicle_type', 'org_name', 'driver_name', 'driver_phone', 'list_status',
          'valid_from', 'valid_to', 'allowed_lane_ids'))
      OR (catalog.code = 'vehicle_lane' AND field.code IN ('pass_direction', 'camera_id', 'barrier_id'))
 )
WHERE catalog.code IN ('vehicle_list', 'vehicle_lane')
  AND catalog.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field base
      WHERE base.tenant_id = catalog.tenant_id
        AND base.entity_type_code = catalog.code
        AND base.field_code = field.code
        AND base.deleted = false);

INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
    entity_type_code, library_field_id, field_code, field_name, data_type, required,
    description, sort_order, status, tenant_id, creator, updater, deleted,
    is_searchable, is_filterable, is_sortable
)
SELECT
    catalog.code, field.id, field.code, field.name, 'REF', false, field.description,
    1, 1, catalog.tenant_id, 'vehicle-pass', 'vehicle-pass', false, false, true, false
FROM dynamicbusiness.dynamic_entity_type catalog
JOIN dynamicbusiness.dynamic_field field
  ON field.tenant_id = catalog.tenant_id AND field.code = 'facility_id' AND field.deleted = false
WHERE catalog.code IN ('vehicle_list', 'vehicle_lane')
  AND catalog.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field base
      WHERE base.tenant_id = catalog.tenant_id
        AND base.entity_type_code = catalog.code
        AND base.field_code = 'facility_id'
        AND base.deleted = false);

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
    creator, updater, deleted, tenant_id, model_code, field_code, collection_point,
    create_visible, edit_visible, detail_visible, field_source
)
SELECT
    model.id, field.id, base.required, false, true, false, base.sort_order,
    'vehicle-pass', 'vehicle-pass', false, model.tenant_id, model.code, field.code, false,
    true, true, true, 'BASE'
FROM dynamicbusiness.dynamic_model model
JOIN dynamicbusiness.dynamic_entity_type_base_field base
  ON base.tenant_id = model.tenant_id
 AND base.entity_type_code = model.entity_type_code
 AND base.deleted = false
JOIN dynamicbusiness.dynamic_field field
  ON field.tenant_id = model.tenant_id AND field.code = base.field_code AND field.deleted = false
WHERE model.entity_type_code IN ('vehicle_list', 'vehicle_lane')
  AND model.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment own
      WHERE own.tenant_id = model.tenant_id
        AND own.model_id = model.id
        AND own.field_code = field.code
        AND own.deleted = false);

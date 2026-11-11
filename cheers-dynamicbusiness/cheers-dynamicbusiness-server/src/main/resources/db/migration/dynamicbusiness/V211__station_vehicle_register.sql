-- 场站管理的车辆登记：公司车辆和调度。个人名单仍用车辆名单。
-- 已经有车辆进出账的租户补上这两本。开通时新租户从模板复制。
-- 车辆登记页挂到场站资产包。已经开过该包的租户补上这一页。
SET search_path TO dynamicbusiness, public;

INSERT INTO dynamicbusiness.dynamic_field (
    code, name, type, description, source, status, provider_code, tenant_id,
    creator, updater, deleted, governance_status
)
SELECT v.code, v.name, v.type, v.description, 'CUSTOM', 1, v.provider_code, tenant.tenant_id,
       'vehicle-register', 'vehicle-register', false, 'COMPANY'
FROM (
    SELECT DISTINCT tenant_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code = 'vehicle_pass' AND deleted = false
) tenant
CROSS JOIN (VALUES
    ('use_status', '使用状态', 'TEXT', '公司车辆在用或停用。', NULL),
    ('company_vehicle_id', '车辆', 'ENTITY_REF', '这一次调度的公司车辆。只记这辆车的编号。', 'dynamic-entity:company_vehicle'),
    ('driver_user_id', '调度人', 'ENTITY_REF', '这一次谁开这辆车。只记这个人的编号。', 'dynamic-entity:user_account'),
    ('dispatch_reason', '调度事由', 'TEXT', '这一次为什么派这辆车。', NULL),
    ('ended_at', '结束时间', 'DATETIME', '车还回来的时间。空着表示这一次调度还没结束。', NULL)
) AS v(code, name, type, description, provider_code)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_field own
    WHERE own.tenant_id = tenant.tenant_id AND own.code = v.code AND own.deleted = false
);

DO $$
DECLARE
  rec record;
  car_table text;
  job_table text;
BEGIN
  FOR rec IN
    SELECT tenant_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code = 'vehicle_pass' AND deleted = false
  LOOP
    car_table := 'ent_company_vehicle_t' || rec.tenant_id::text;
    job_table := 'ent_vehicle_dispatch_t' || rec.tenant_id::text;

    EXECUTE format('CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.%I', car_table || '_id_seq');
    EXECUTE format($sql$
        CREATE TABLE IF NOT EXISTS dynamicbusiness.%I (
            id bigint PRIMARY KEY DEFAULT nextval(%L),
            tenant_id bigint NOT NULL DEFAULT %s,
            entity_type_code varchar(64) NOT NULL DEFAULT 'company_vehicle',
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
            use_status text
        )$sql$, car_table, 'dynamicbusiness.' || car_table || '_id_seq', rec.tenant_id::text);

    EXECUTE format('CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.%I', job_table || '_id_seq');
    EXECUTE format($sql$
        CREATE TABLE IF NOT EXISTS dynamicbusiness.%I (
            id bigint PRIMARY KEY DEFAULT nextval(%L),
            tenant_id bigint NOT NULL DEFAULT %s,
            entity_type_code varchar(64) NOT NULL DEFAULT 'vehicle_dispatch',
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
            company_vehicle_id bigint,
            driver_user_id bigint,
            driver_name text,
            valid_from timestamp,
            valid_to timestamp,
            dispatch_reason text,
            ended_at timestamp
        )$sql$, job_table, 'dynamicbusiness.' || job_table || '_id_seq', rec.tenant_id::text);

    INSERT INTO dynamicbusiness.dynamic_entity_type (
        code, name, description, alias, sort, status, type_level, association_fields,
        storage_type, dedicated_table_name, enable_rule_engine, creator, updater, deleted,
        tenant_id, group_name, entry_kind, model_workbench_mode, work_scope
    )
    SELECT
        'company_vehicle', '公司车辆', '公司的车，不固定驾驶人', '公司车辆',
        COALESCE((SELECT MAX(sort) FROM dynamicbusiness.dynamic_entity_type WHERE tenant_id = rec.tenant_id AND deleted = false), 0) + 1,
        'active', 'USER', '{}'::jsonb, 'DEDICATED', car_table, false,
        'vehicle-register', 'vehicle-register', false, rec.tenant_id, '场站管理', 'NATIVE', 'MULTI', 'FACILITY'
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_entity_type
        WHERE tenant_id = rec.tenant_id AND code = 'company_vehicle' AND deleted = false
    );

    INSERT INTO dynamicbusiness.dynamic_entity_type (
        code, name, description, alias, sort, status, type_level, association_fields,
        storage_type, dedicated_table_name, enable_rule_engine, creator, updater, deleted,
        tenant_id, group_name, entry_kind, model_workbench_mode, work_scope
    )
    SELECT
        'vehicle_dispatch', '车辆调度', '这一次谁开哪辆公司车', '车辆调度',
        COALESCE((SELECT MAX(sort) FROM dynamicbusiness.dynamic_entity_type WHERE tenant_id = rec.tenant_id AND deleted = false), 0) + 1,
        'active', 'USER', '{}'::jsonb, 'DEDICATED', job_table, false,
        'vehicle-register', 'vehicle-register', false, rec.tenant_id, '场站管理', 'NATIVE', 'MULTI', 'FACILITY'
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_entity_type
        WHERE tenant_id = rec.tenant_id AND code = 'vehicle_dispatch' AND deleted = false
    );

    INSERT INTO dynamicbusiness.dynamic_model (
        code, name, entity_type_code, description, status, sort, creator, updater,
        deleted, tenant_id, governance_status
    )
    SELECT 'MODEL-company-vehicle', '公司车辆', 'company_vehicle', '公司车辆', 1, 0,
           'vehicle-register', 'vehicle-register', false, rec.tenant_id, 'COMPANY'
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_model
        WHERE tenant_id = rec.tenant_id AND entity_type_code = 'company_vehicle' AND deleted = false
    );

    INSERT INTO dynamicbusiness.dynamic_model (
        code, name, entity_type_code, description, status, sort, creator, updater,
        deleted, tenant_id, governance_status
    )
    SELECT 'MODEL-vehicle-dispatch', '车辆调度', 'vehicle_dispatch', '车辆调度', 1, 0,
           'vehicle-register', 'vehicle-register', false, rec.tenant_id, 'COMPANY'
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamicbusiness.dynamic_model
        WHERE tenant_id = rec.tenant_id AND entity_type_code = 'vehicle_dispatch' AND deleted = false
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
    false,
    field.description,
    CASE field.code
        WHEN 'vehicle_type' THEN 10
        WHEN 'org_name' THEN 11
        WHEN 'use_status' THEN 12
        WHEN 'company_vehicle_id' THEN 10
        WHEN 'driver_user_id' THEN 11
        WHEN 'driver_name' THEN 12
        WHEN 'valid_from' THEN 13
        WHEN 'valid_to' THEN 14
        WHEN 'dispatch_reason' THEN 15
        WHEN 'ended_at' THEN 16
        ELSE 20
    END,
    1, catalog.tenant_id, 'vehicle-register', 'vehicle-register', false, false, true, false
FROM dynamicbusiness.dynamic_entity_type catalog
JOIN dynamicbusiness.dynamic_field field
  ON field.tenant_id = catalog.tenant_id AND field.deleted = false
 AND (
      (catalog.code = 'company_vehicle' AND field.code IN ('vehicle_type', 'org_name', 'use_status'))
      OR (catalog.code = 'vehicle_dispatch' AND field.code IN (
          'company_vehicle_id', 'driver_user_id', 'driver_name', 'valid_from', 'valid_to',
          'dispatch_reason', 'ended_at'))
 )
WHERE catalog.code IN ('company_vehicle', 'vehicle_dispatch')
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
    1, 1, catalog.tenant_id, 'vehicle-register', 'vehicle-register', false, false, true, false
FROM dynamicbusiness.dynamic_entity_type catalog
JOIN dynamicbusiness.dynamic_field field
  ON field.tenant_id = catalog.tenant_id AND field.code = 'facility_id' AND field.deleted = false
WHERE catalog.code IN ('company_vehicle', 'vehicle_dispatch')
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
    'vehicle-register', 'vehicle-register', false, model.tenant_id, model.code, field.code, false,
    true, true, true, 'BASE'
FROM dynamicbusiness.dynamic_model model
JOIN dynamicbusiness.dynamic_entity_type_base_field base
  ON base.tenant_id = model.tenant_id
 AND base.entity_type_code = model.entity_type_code
 AND base.deleted = false
JOIN dynamicbusiness.dynamic_field field
  ON field.tenant_id = model.tenant_id AND field.code = base.field_code AND field.deleted = false
WHERE model.entity_type_code IN ('company_vehicle', 'vehicle_dispatch')
  AND model.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment own
      WHERE own.tenant_id = model.tenant_id
        AND own.model_id = model.id
        AND own.field_code = field.code
        AND own.deleted = false);

INSERT INTO dynamicbusiness.dynamic_catalog_role_grant (
    role_id, registry_code, subject, operation, tenant_id, creator, updater, deleted
)
SELECT src.role_id, target.code, src.subject, src.operation, src.tenant_id,
       'vehicle-register', 'vehicle-register', false
FROM dynamicbusiness.dynamic_catalog_role_grant src
CROSS JOIN (VALUES ('company_vehicle'), ('vehicle_dispatch')) AS target(code)
WHERE src.registry_code = 'vehicle_list'
  AND src.deleted = false
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_catalog_role_grant own
      WHERE own.role_id = src.role_id
        AND own.registry_code = target.code
        AND own.subject = src.subject
        AND own.operation = src.operation
        AND own.tenant_id = src.tenant_id
        AND own.deleted = false
  );

UPDATE dynamicbusiness.tenant_capability_pack
SET opened_page_codes = opened_page_codes || ',station-vehicle-register',
    updater = 'vehicle-register',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND pack_code = 'facility-ops'
  AND opened_page_codes IS NOT NULL
  AND btrim(opened_page_codes) <> ''
  AND opened_page_codes NOT LIKE '%station-vehicle-register%';

INSERT INTO dynamicbusiness.dynamic_page_role_grant (
    role_id, page_code, action, tenant_id, creator, updater, deleted
)
SELECT src.role_id, 'station-vehicle-register', src.action, src.tenant_id,
       'vehicle-register', 'vehicle-register', false
FROM dynamicbusiness.dynamic_page_role_grant src
WHERE src.page_code = 'security-vehicle-pass'
  AND src.action IN ('view', 'maintain')
  AND src.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_page_role_grant own
      WHERE own.role_id = src.role_id
        AND own.page_code = 'station-vehicle-register'
        AND own.action = src.action
        AND own.tenant_id = src.tenant_id
        AND own.deleted = false
  );

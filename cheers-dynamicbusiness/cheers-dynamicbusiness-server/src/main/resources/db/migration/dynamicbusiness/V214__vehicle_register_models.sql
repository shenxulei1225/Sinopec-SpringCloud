-- 车辆登记只留一本目录。个人车辆、公司车辆是这本目录上的两个型号。
-- 车辆调度不是数据目录，从目录里拿掉。不新建目录。
SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.dynamic_entity_type
SET name = '车辆登记',
    alias = '车辆登记',
    group_name = '车辆管理',
    updater = 'vehicle-register',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'vehicle_list';

UPDATE dynamicbusiness.dynamic_model
SET name = '个人车辆',
    updater = 'vehicle-register',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND entity_type_code = 'vehicle_list';

UPDATE dynamicbusiness.dynamic_model
SET entity_type_code = 'vehicle_list',
    name = '公司车辆',
    updater = 'vehicle-register',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND entity_type_code = 'company_vehicle';

INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
    entity_type_code, field_code, field_name, data_type, required, default_value,
    description, type_config, sort_order, status, creator, create_time, updater,
    update_time, deleted, tenant_id, library_field_id, is_searchable, is_filterable, is_sortable
)
SELECT 'vehicle_list', src.field_code, src.field_name, src.data_type, src.required, src.default_value,
       src.description, src.type_config, src.sort_order, src.status, 'vehicle-register', CURRENT_TIMESTAMP,
       'vehicle-register', CURRENT_TIMESTAMP, false, src.tenant_id, src.library_field_id,
       src.is_searchable, src.is_filterable, src.is_sortable
FROM dynamicbusiness.dynamic_entity_type_base_field src
WHERE src.deleted = false
  AND src.entity_type_code = 'company_vehicle'
  AND src.field_code = 'use_status'
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_entity_type_base_field own
      WHERE own.deleted = false
        AND own.tenant_id = src.tenant_id
        AND own.entity_type_code = 'vehicle_list'
        AND own.field_code = 'use_status'
  );

DO $$
DECLARE
    table_name text;
BEGIN
    FOR table_name IN
        SELECT tablename
        FROM pg_tables
        WHERE schemaname = 'dynamicbusiness'
          AND tablename LIKE 'ent\_vehicle\_list\_t%' ESCAPE '\'
    LOOP
        EXECUTE format(
            'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS use_status text',
            table_name
        );
    END LOOP;
END $$;

UPDATE dynamicbusiness.dynamic_entity_type
SET deleted = true,
    updater = 'vehicle-register',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code IN ('company_vehicle', 'vehicle_dispatch');

UPDATE dynamicbusiness.dynamic_group
SET deleted = true,
    updater = 'vehicle-register',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND group_type = 'ENTITY_TYPE'
  AND name = '车辆登记'
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_entity_type t
      WHERE t.deleted = false
        AND t.tenant_id = dynamic_group.tenant_id
        AND btrim(t.group_name) = '车辆登记'
  );

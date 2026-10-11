-- 飞机、遥控器、机场各一条设备，详情里能看到领域、主类型、子类型。
-- 负责：按这些型号写入设备，以及这三个扩展字段的值。
-- 不负责：负载。相机、FPV、辅助影像只有挂到某一架飞机或机场上才有设备，不单独建一条没挂上的记录。

SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO ent_equipment_t1 (
  entity_type_code, model_id, name, code, guid, custom_fields,
  tenant_id, facility_id, status, creator
)
SELECT
  'equipment',
  m.id,
  m.name,
  'EQ-' || upper(m.code),
  'guid-eq-' || m.code,
  jsonb_build_object(
    'dji_domain', dom.default_value,
    'dji_main_type', mt.default_value,
    'dji_sub_type', st.default_value
  ),
  1,
  44,
  1,
  'seed'
FROM dynamic_model m
JOIN dynamic_model_field_assignment dom
  ON dom.model_id = m.id AND dom.field_code = 'dji_domain' AND dom.deleted = false AND dom.tenant_id = 1
JOIN dynamic_model_field_assignment mt
  ON mt.model_id = m.id AND mt.field_code = 'dji_main_type' AND mt.deleted = false AND mt.tenant_id = 1
JOIN dynamic_model_field_assignment st
  ON st.model_id = m.id AND st.field_code = 'dji_sub_type' AND st.deleted = false AND st.tenant_id = 1
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.entity_type_code = 'equipment'
  AND m.code LIKE 'dji-%'
  AND m.code NOT LIKE 'dji-pl-%'
ON CONFLICT (code, tenant_id) WHERE deleted = false AND code IS NOT NULL
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  name = EXCLUDED.name,
  custom_fields = EXCLUDED.custom_fields,
  facility_id = EXCLUDED.facility_id,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_entity_category_relation_t1 (
  entity_id, category_id, entity_type_code, tenant_id, creator
)
SELECT e.id, 409, 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN ent_equipment_t1 e
  ON e.code = 'EQ-' || upper(m.code)
 AND e.tenant_id = 1
 AND e.deleted = false
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.entity_type_code = 'equipment'
  AND m.code LIKE 'dji-%'
  AND m.code NOT LIKE 'dji-pl-%'
ON CONFLICT (tenant_id, entity_type_code, entity_id, category_id)
DO UPDATE SET
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

DELETE FROM dynamic_entity_field_index i
USING dynamic_model m
JOIN ent_equipment_t1 e
  ON e.code = 'EQ-' || upper(m.code)
 AND e.tenant_id = 1
 AND e.deleted = false
WHERE i.entity_id = e.id
  AND i.tenant_id = 1
  AND i.field_code IN ('dji_domain', 'dji_main_type', 'dji_sub_type')
  AND m.deleted = false
  AND m.tenant_id = 1
  AND m.code LIKE 'dji-%'
  AND m.code NOT LIKE 'dji-pl-%';

INSERT INTO dynamic_entity_field_index (
  entity_id, model_id, field_code, value_string, tenant_id, creator
)
SELECT e.id, e.model_id, kv.key, kv.value, 1, 'seed'
FROM dynamic_model m
JOIN ent_equipment_t1 e
  ON e.code = 'EQ-' || upper(m.code)
 AND e.tenant_id = 1
 AND e.deleted = false
CROSS JOIN LATERAL jsonb_each_text(e.custom_fields) kv
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code LIKE 'dji-%'
  AND m.code NOT LIKE 'dji-pl-%'
  AND kv.key IN ('dji_domain', 'dji_main_type', 'dji_sub_type');

COMMIT;

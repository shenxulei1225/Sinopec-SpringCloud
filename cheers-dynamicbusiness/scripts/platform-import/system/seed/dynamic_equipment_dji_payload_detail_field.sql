-- 无人机和机场的详情只显示本型号已分配的字段。
-- 把已挂上的相机写进「负载设备」，详情才能列出这些相机。
-- 不负责：改挂接。挂接仍是子设备的 parent_id。这里只把同一批子设备填进父设备的详情字段。

SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO dynamic_field (
  code, name, type, description, source, status, index_strategy,
  provider_code, tenant_id, creator
) VALUES (
  'dji_mounted_payloads',
  '负载设备',
  'ENTITY_REF_MULTI',
  '这台飞机或机场当前挂着的相机。详情列出这些设备。位置在每台相机自己的负载位置上。',
  'USER',
  1,
  'NONE',
  'dynamic-entity:equipment',
  1,
  'seed'
)
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  provider_code = EXCLUDED.provider_code,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  target_entity_type, tenant_id, creator
)
SELECT m.id, m.code, f.id, f.code, false, false, false, false, 45, 'equipment', 1, 'seed'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'dji_mounted_payloads' AND f.tenant_id = 1 AND f.deleted = false
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.code IN (
    'dji-m300-rtk', 'dji-m350-rtk', 'dji-m400',
    'dji-m30', 'dji-m30t',
    'dji-m3e', 'dji-m3t', 'dji-m3ta',
    'dji-m3d', 'dji-m3td', 'dji-m4d', 'dji-m4td', 'dji-m4e', 'dji-m4t',
    'dji-dock-1', 'dji-dock-2', 'dji-dock-3'
  )
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  target_entity_type = EXCLUDED.target_entity_type,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE ent_equipment_t1 host
SET custom_fields = COALESCE(host.custom_fields, '{}'::jsonb) || jsonb_build_object(
      'dji_mounted_payloads',
      (
        SELECT jsonb_agg(
          jsonb_build_object('id', child.id, 'entityTypeCode', 'equipment')
          ORDER BY child.name
        )
        FROM ent_equipment_t1 child
        WHERE child.parent_id = host.id
          AND child.deleted = false
          AND child.tenant_id = 1
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE host.deleted = false
  AND host.tenant_id = 1
  AND host.code IN (
    'EQ-DJI-M300-RTK', 'EQ-DJI-M350-RTK', 'EQ-DJI-M400',
    'EQ-DJI-M30', 'EQ-DJI-M30T',
    'EQ-DJI-M3E', 'EQ-DJI-M3T', 'EQ-DJI-M3TA',
    'EQ-DJI-M3D', 'EQ-DJI-M3TD', 'EQ-DJI-M4D', 'EQ-DJI-M4TD', 'EQ-DJI-M4E', 'EQ-DJI-M4T',
    'EQ-DJI-DOCK-1', 'EQ-DJI-DOCK-2', 'EQ-DJI-DOCK-3'
  );

COMMIT;

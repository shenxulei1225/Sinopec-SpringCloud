-- 清理设备「假 REF」基础字段 → ENUM / DATE（本机修复，幂等）
-- 口径：运行状态/健康度=字段管理 ENUM；维保/检验时间=DATE；删假类型能力投影
SET search_path TO dynamicbusiness;

BEGIN;

-- 1) 字段库：把 *_id ENTITY_REF 就地改为短编码 + 正确类型
UPDATE dynamic_field
SET code = 'operation_status',
    type = 'ENUM',
    options = '[{"label":"运行","value":"运行"},{"label":"备用","value":"备用"},{"label":"检修","value":"检修"},{"label":"停用","value":"停用"},{"label":"报废","value":"报废"}]',
    description = '设备业务运行态（非实体启用/禁用 status）',
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'operation_status_id' AND deleted = false;

UPDATE dynamic_field
SET code = 'health_score',
    type = 'ENUM',
    options = '[{"label":"优","value":"优"},{"label":"良","value":"良"},{"label":"中","value":"中"},{"label":"差","value":"差"}]',
    description = '设备健康度（字段管理选项）',
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'health_score_id' AND deleted = false;

UPDATE dynamic_field
SET code = 'last_maintenance',
    type = 'DATE',
    options = NULL,
    description = '上次维保时间',
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'last_maintenance_id' AND deleted = false;

UPDATE dynamic_field
SET code = 'next_maintenance',
    type = 'DATE',
    options = NULL,
    description = '下次维保时间',
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'next_maintenance_id' AND deleted = false;

UPDATE dynamic_field
SET code = 'last_inspection',
    type = 'DATE',
    options = NULL,
    description = '上次检验时间',
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'last_inspection_id' AND deleted = false;

UPDATE dynamic_field
SET code = 'next_inspection',
    type = 'DATE',
    options = NULL,
    description = '下次检验时间',
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'next_inspection_id' AND deleted = false;

-- 2) 设备基础字段
UPDATE dynamic_entity_type_base_field b
SET field_code = 'operation_status',
    data_type = 'ENUM',
    type_config = NULL,
    description = '设备业务运行态',
    library_field_id = f.id,
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE b.entity_type_code = 'equipment'
  AND b.field_code = 'operation_status_id'
  AND b.deleted = false
  AND f.deleted = false
  AND f.code = 'operation_status'
  AND f.tenant_id = b.tenant_id;

UPDATE dynamic_entity_type_base_field b
SET field_code = 'health_score',
    data_type = 'ENUM',
    type_config = NULL,
    description = '设备健康度',
    library_field_id = f.id,
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE b.entity_type_code = 'equipment'
  AND b.field_code = 'health_score_id'
  AND b.deleted = false
  AND f.deleted = false
  AND f.code = 'health_score'
  AND f.tenant_id = b.tenant_id;

UPDATE dynamic_entity_type_base_field b
SET field_code = 'last_maintenance',
    data_type = 'DATE',
    type_config = NULL,
    description = '上次维保时间',
    library_field_id = f.id,
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE b.entity_type_code = 'equipment'
  AND b.field_code = 'last_maintenance_id'
  AND b.deleted = false
  AND f.deleted = false
  AND f.code = 'last_maintenance'
  AND f.tenant_id = b.tenant_id;

UPDATE dynamic_entity_type_base_field b
SET field_code = 'next_maintenance',
    data_type = 'DATE',
    type_config = NULL,
    description = '下次维保时间',
    library_field_id = f.id,
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE b.entity_type_code = 'equipment'
  AND b.field_code = 'next_maintenance_id'
  AND b.deleted = false
  AND f.deleted = false
  AND f.code = 'next_maintenance'
  AND f.tenant_id = b.tenant_id;

UPDATE dynamic_entity_type_base_field b
SET field_code = 'last_inspection',
    data_type = 'DATE',
    type_config = NULL,
    description = '上次检验时间',
    library_field_id = f.id,
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE b.entity_type_code = 'equipment'
  AND b.field_code = 'last_inspection_id'
  AND b.deleted = false
  AND f.deleted = false
  AND f.code = 'last_inspection'
  AND f.tenant_id = b.tenant_id;

UPDATE dynamic_entity_type_base_field b
SET field_code = 'next_inspection',
    data_type = 'DATE',
    type_config = NULL,
    description = '下次检验时间',
    library_field_id = f.id,
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE b.entity_type_code = 'equipment'
  AND b.field_code = 'next_inspection_id'
  AND b.deleted = false
  AND f.deleted = false
  AND f.code = 'next_inspection'
  AND f.tenant_id = b.tenant_id;

-- 3) 型号字段分配：改编码、挂新 field_id、清假目标类型
UPDATE dynamic_model_field_assignment a
SET field_code = CASE a.field_code
      WHEN 'operation_status_id' THEN 'operation_status'
      WHEN 'health_score_id' THEN 'health_score'
      WHEN 'last_maintenance_id' THEN 'last_maintenance'
      WHEN 'next_maintenance_id' THEN 'next_maintenance'
      WHEN 'last_inspection_id' THEN 'last_inspection'
      WHEN 'next_inspection_id' THEN 'next_inspection'
    END,
    field_id = f.id,
    target_entity_type = NULL,
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE a.deleted = false
  AND a.field_code IN (
    'operation_status_id','health_score_id',
    'last_maintenance_id','next_maintenance_id',
    'last_inspection_id','next_inspection_id'
  )
  AND f.deleted = false
  AND f.tenant_id = a.tenant_id
  AND f.code = CASE a.field_code
      WHEN 'operation_status_id' THEN 'operation_status'
      WHEN 'health_score_id' THEN 'health_score'
      WHEN 'last_maintenance_id' THEN 'last_maintenance'
      WHEN 'next_maintenance_id' THEN 'next_maintenance'
      WHEN 'last_inspection_id' THEN 'last_inspection'
      WHEN 'next_inspection_id' THEN 'next_inspection'
    END;

-- 4) 专用表：废弃旧 *_id bigint 列，新增标量列
DO $$
DECLARE
  t text;
  pairs text[][] := ARRAY[
    ARRAY['operation_status_id','operation_status','VARCHAR(100)'],
    ARRAY['health_score_id','health_score','VARCHAR(100)'],
    ARRAY['last_maintenance_id','last_maintenance','DATE'],
    ARRAY['next_maintenance_id','next_maintenance','DATE'],
    ARRAY['last_inspection_id','last_inspection','DATE'],
    ARRAY['next_inspection_id','next_inspection','DATE']
  ];
  i int;
  old_col text;
  new_col text;
  col_type text;
  dep_col text;
BEGIN
  FOREACH t IN ARRAY ARRAY['ent_equipment','ent_equipment_t1','ent_equipment_t2']
  LOOP
    IF NOT EXISTS (
      SELECT 1 FROM information_schema.tables
      WHERE table_schema = 'dynamicbusiness' AND table_name = t
    ) THEN
      CONTINUE;
    END IF;

    FOR i IN 1..array_length(pairs, 1) LOOP
      old_col := pairs[i][1];
      new_col := pairs[i][2];
      col_type := pairs[i][3];
      dep_col := '_deprecated_' || old_col;
      IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema='dynamicbusiness' AND table_name=t AND column_name=old_col
      ) AND NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema='dynamicbusiness' AND table_name=t AND column_name=dep_col
      ) THEN
        EXECUTE format('ALTER TABLE dynamicbusiness.%I RENAME COLUMN %I TO %I', t, old_col, dep_col);
      END IF;
      EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS %I %s', t, new_col, col_type);
    END LOOP;
  END LOOP;
END $$;

-- 5) 软删假类型能力投影 / 全集（无对应 dynamic_entity_type）
UPDATE capability_component_projection
SET deleted = true,
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND entity_type_code IN (
    'operation_status','health_score',
    'last_maintenance','next_maintenance',
    'last_inspection','next_inspection'
  );

UPDATE business_capability
SET deleted = true,
    updater = 'fix-fake-ref',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND entity_type_code IN (
    'operation_status','health_score',
    'last_maintenance','next_maintenance',
    'last_inspection','next_inspection'
  );

COMMIT;

-- 核对
SELECT 'field' AS kind, code, type FROM dynamic_field
WHERE deleted=false AND code IN ('operation_status','health_score','last_maintenance','next_maintenance','last_inspection','next_inspection')
ORDER BY tenant_id, code;

SELECT 'base' AS kind, field_code, data_type FROM dynamic_entity_type_base_field
WHERE entity_type_code='equipment' AND deleted=false
  AND field_code IN ('operation_status','health_score','last_maintenance','next_maintenance','last_inspection','next_inspection')
ORDER BY field_code;

SELECT 'assign' AS kind, field_code, count(*) FROM dynamic_model_field_assignment
WHERE deleted=false AND field_code IN ('operation_status','health_score','last_maintenance','next_maintenance','last_inspection','next_inspection')
GROUP BY field_code ORDER BY 1;

SELECT 'fake_proj' AS kind, count(*) FROM capability_component_projection
WHERE deleted=false AND entity_type_code IN ('operation_status','health_score','last_maintenance','next_maintenance','last_inspection','next_inspection');

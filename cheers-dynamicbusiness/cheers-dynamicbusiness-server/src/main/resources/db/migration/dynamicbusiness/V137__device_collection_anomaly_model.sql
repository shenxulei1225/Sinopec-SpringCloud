-- 设备采集异常：挂在异常记录这本账上，所属设备由采集写入。
-- 不建业务域门，也不建分类。打开异常记录就能看到这个型号下的行。

INSERT INTO dynamicbusiness.dynamic_model (
    code, name, entity_type_code, description, status, sort,
    creator, updater, deleted, tenant_id, domain, governance_status
)
SELECT
    'exec_device_anomaly',
    '设备采集异常',
    'anomaly',
    '设备上报故障时自动写入，并挂在对上的那台设备上。不进分类，也不进任务执行记录。',
    1,
    20,
    'flyway',
    'flyway',
    false,
    1,
    NULL,
    'COMPANY'
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dynamic_model
    WHERE code = 'exec_device_anomaly'
      AND deleted = false
);

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    sort, field_source, target_entity_type, creator, updater, deleted, tenant_id,
    model_code, field_code
)
SELECT
    m.id,
    f.id,
    true,
    false,
    true,
    false,
    10,
    'LIBRARY',
    'equipment',
    'flyway',
    'flyway',
    false,
    m.tenant_id,
    m.code,
    f.code
FROM dynamicbusiness.dynamic_model m
JOIN dynamicbusiness.dynamic_field f
    ON f.code = 'equipment_id'
   AND f.deleted = false
   AND f.tenant_id = m.tenant_id
WHERE m.code = 'exec_device_anomaly'
  AND m.deleted = false
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_model_field_assignment a
      WHERE a.model_code = m.code
        AND a.field_code = f.code
        AND a.tenant_id = m.tenant_id
        AND a.deleted = false
  );

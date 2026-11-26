-- 热气溶胶写入设备型号「气溶胶」。
-- 权威是消防设计说明上的整段台数和规格。神墩五路 137 套，清湾路 99 套。规格都是 S 型挂壁式、每台充装量 8kg。
-- 说明没有每一台的位置编号，安装位置留空，不编造编号。
-- 说明没有把每一台分到某一个舱，所属分区写这条管廊段。
-- 名称末尾的序号只用来区分各条记录，不是图上的位置编号。
-- 不写备注。干粉灭火器、灭火器箱、防火门没有设备型号，不在这里插入。

BEGIN;

INSERT INTO dynamicbusiness.ent_equipment_t123 (
    entity_type_code, model_id, name, custom_fields,
    creator, updater, code, host_sop_param_pack, execution_means,
    tenant_id, facility_id, zone_id, status, equipment_model
)
SELECT
    'equipment',
    2542,
    src.seg_name || '热气溶胶-' || lpad(gs.n::text, 3, '0'),
    jsonb_build_object('zone_id', jsonb_build_array(jsonb_build_object('id', src.seg_id, 'entityTypeCode', 'zone'))),
    'corridor-fire-aerosol',
    'corridor-fire-aerosol',
    upper(replace(gen_random_uuid()::text, '-', '')),
    '{}'::jsonb,
    '[]'::jsonb,
    123,
    1,
    src.seg_id,
    1,
    'S型挂壁式，每台充装量8kg'
FROM (
    VALUES
        (5::bigint, '神墩五路', 137),
        (460::bigint, '清湾路', 99)
) AS src(seg_id, seg_name, qty)
CROSS JOIN LATERAL generate_series(1, src.qty) AS gs(n)
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.ent_equipment_t123 e
    WHERE e.model_id = 2542
      AND e.tenant_id = 123
      AND COALESCE(e.deleted, false) = false
      AND e.name = src.seg_name || '热气溶胶-' || lpad(gs.n::text, 3, '0')
);

INSERT INTO dynamicbusiness.dynamic_entity_field_index (
    entity_id, model_id, field_code, value_string, value_number,
    creator, updater, tenant_id
)
SELECT
    e.id,
    2542,
    'equipment_model',
    e.equipment_model,
    NULL,
    'corridor-fire-aerosol',
    'corridor-fire-aerosol',
    123
FROM dynamicbusiness.ent_equipment_t123 e
WHERE e.creator = 'corridor-fire-aerosol'
  AND e.model_id = 2542
  AND COALESCE(e.deleted, false) = false
  AND e.equipment_model IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_entity_field_index i
      WHERE i.entity_id = e.id
        AND i.field_code = 'equipment_model'
        AND i.tenant_id = 123
        AND COALESCE(i.deleted, false) = false
  );

INSERT INTO dynamicbusiness.dynamic_entity_field_index (
    entity_id, model_id, field_code, value_string, value_number,
    creator, updater, tenant_id
)
SELECT
    e.id,
    2542,
    'zone_id',
    e.zone_id::text,
    e.zone_id,
    'corridor-fire-aerosol',
    'corridor-fire-aerosol',
    123
FROM dynamicbusiness.ent_equipment_t123 e
WHERE e.creator = 'corridor-fire-aerosol'
  AND e.model_id = 2542
  AND COALESCE(e.deleted, false) = false
  AND e.zone_id IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_entity_field_index i
      WHERE i.entity_id = e.id
        AND i.field_code = 'zone_id'
        AND i.tenant_id = 123
        AND COALESCE(i.deleted, false) = false
  );

COMMIT;

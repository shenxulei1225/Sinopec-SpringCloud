-- 智慧巡检（租户 1）和智慧管廊（租户 123）：各数据目录不再把实体通用列 status 当成自己的状态。
-- 告警用告警状态，设备用设备状态，管线用管线状态。详情标题后关掉启用/停用，改挂本目录自己的状态字段。
-- 实体通用列 status 仍是 1/0，不写入业务状态。可重复执行。

SET search_path TO dynamicbusiness, public;

ALTER TABLE dynamicbusiness.ent_pipeline_t1
    ADD COLUMN IF NOT EXISTS pipeline_status character varying;

ALTER TABLE dynamicbusiness.ent_pipeline_t123
    ADD COLUMN IF NOT EXISTS pipeline_status character varying;

UPDATE dynamicbusiness.ent_pipeline_t123
SET pipeline_status = operation_status
WHERE pipeline_status IS NULL
  AND operation_status IS NOT NULL;

UPDATE dynamicbusiness.dynamic_field
SET name = '告警状态',
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id IN (1, 123)
  AND code = 'FLD-ALM-006'
  AND name IS DISTINCT FROM '告警状态';

UPDATE dynamicbusiness.dynamic_entity_type_base_field
SET field_name = '告警状态',
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id IN (1, 123)
  AND entity_type_code = 'alarm'
  AND field_code = 'FLD-ALM-006'
  AND field_name IS DISTINCT FROM '告警状态';

UPDATE dynamicbusiness.dynamic_field
SET name = '设备状态',
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND code = 'operation_status'
  AND name IS DISTINCT FROM '设备状态';

UPDATE dynamicbusiness.dynamic_entity_type_base_field
SET field_name = '设备状态',
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND entity_type_code = 'equipment'
  AND field_code = 'operation_status'
  AND field_name IS DISTINCT FROM '设备状态';

UPDATE dynamicbusiness.dynamic_field AS target
SET code = 'pipeline_status',
    name = '管线状态',
    type = source.type,
    options = source.options,
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.dynamic_field AS source
WHERE target.id = 9838
  AND target.tenant_id = 123
  AND target.deleted = false
  AND target.code IN ('status', 'pipeline_status')
  AND source.id = 9364
  AND source.tenant_id = 123
  AND source.deleted = false;

UPDATE dynamicbusiness.dynamic_model_field_assignment AS assignment
SET field_id = 9838,
    field_code = 'pipeline_status',
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.dynamic_model AS model
WHERE assignment.model_id = model.id
  AND assignment.tenant_id = model.tenant_id
  AND assignment.deleted = false
  AND model.deleted = false
  AND model.tenant_id = 123
  AND model.entity_type_code = 'pipeline'
  AND assignment.field_code = 'operation_status';

UPDATE dynamicbusiness.dynamic_entity_type_base_field
SET field_code = 'pipeline_status',
    field_name = '管线状态',
    library_field_id = 9838,
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 123
  AND entity_type_code = 'pipeline'
  AND field_code IN ('operation_status', 'pipeline_status');

UPDATE dynamicbusiness.dynamic_field
SET name = '设备状态',
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND id = 9364
  AND tenant_id = 123
  AND code = 'operation_status'
  AND name IS DISTINCT FROM '设备状态';

UPDATE dynamicbusiness.dynamic_entity_type_base_field
SET field_name = '设备状态',
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 123
  AND entity_type_code = 'equipment'
  AND field_code = 'operation_status'
  AND field_name IS DISTINCT FROM '设备状态';

UPDATE dynamicbusiness.dynamic_field
SET code = 'pipeline_status',
    name = '管线状态',
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND id = 3711
  AND tenant_id = 1
  AND code IN ('status', 'pipeline_status');

UPDATE dynamicbusiness.dynamic_model_field_assignment
SET field_code = 'pipeline_status',
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND field_id = 3711
  AND field_code IS DISTINCT FROM 'pipeline_status';

UPDATE dynamicbusiness.dynamic_entity_type_base_field
SET field_code = 'pipeline_status',
    field_name = '管线状态',
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND entity_type_code = 'pipeline'
  AND library_field_id = 3711
  AND field_code IS DISTINCT FROM 'pipeline_status';

-- 应急预案这份详情配置被两个租户共用。管廊没有生效状态，先拆出自己的一份，再改巡检那一份。
WITH inserted AS (
    INSERT INTO platformresource.pr_component_props (
        is_template, component_code, schema_version, props, name, status, deleted, tenant_id,
        creator, create_time, updater, update_time
    )
    SELECT false,
           'entity-detail',
           'entity-detail@1',
           '{}',
           'detail-status-plan-t123',
           1,
           false,
           0,
           'status-split',
           CURRENT_TIMESTAMP,
           'status-split',
           CURRENT_TIMESTAMP
    WHERE EXISTS (
        SELECT 1
        FROM dynamicbusiness.dm_data_tab_layout
        WHERE id = 1418
          AND deleted = false
          AND props_id = 16965
    )
    RETURNING id
)
UPDATE dynamicbusiness.dm_data_tab_layout AS layout
SET props_id = inserted.id
FROM inserted
WHERE layout.id = 1418
  AND layout.props_id = 16965;

WITH needed AS (
    SELECT layout.id AS layout_id
    FROM dynamicbusiness.dynamic_entity_type AS entity_type
    JOIN dynamicbusiness.dm_data_tab_layout AS layout
      ON layout.layout_id = entity_type.data_layout_id
     AND layout.deleted = false
     AND layout.column_kind = 'DETAIL'
    WHERE entity_type.deleted = false
      AND entity_type.tenant_id IN (1, 123)
      AND layout.props_id IS NULL
),
inserted AS (
    INSERT INTO platformresource.pr_component_props (
        is_template, component_code, schema_version, props, name, status, deleted, tenant_id,
        creator, create_time, updater, update_time
    )
    SELECT false,
           'entity-detail',
           'entity-detail@1',
           '{}',
           'detail-status-' || needed.layout_id,
           1,
           false,
           0,
           'status-split',
           CURRENT_TIMESTAMP,
           'status-split',
           CURRENT_TIMESTAMP
    FROM needed
    RETURNING id, name
)
UPDATE dynamicbusiness.dm_data_tab_layout AS layout
SET props_id = inserted.id
FROM inserted
WHERE layout.props_id IS NULL
  AND inserted.name = 'detail-status-' || layout.id;

UPDATE platformresource.pr_component_props AS props
SET props = (
        jsonb_set(
            jsonb_set(
                CASE
                    WHEN props.props IS NULL OR btrim(props.props) IN ('', '{}')
                    THEN '{"title":{"title":"","showTitle":true,"titleOnOwnRow":false}}'::jsonb
                    ELSE props.props::jsonb
                END,
                '{showStatusAfterTitle}',
                'false'::jsonb,
                true
            ),
            '{titleAfterFields}',
            mapped.fields,
            true
        )
    )::text,
    updater = 'status-split',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT layout.props_id,
           CASE
               WHEN entity_type.tenant_id = 1 AND entity_type.code = 'alarm'
                   THEN '["FLD-ALM-002","FLD-ALM-006","F-8fc43c2efd40458183c5f4171d98f7e8"]'::jsonb
               WHEN entity_type.tenant_id = 123 AND entity_type.code = 'alarm'
                   THEN '["FLD-ALM-002","FLD-ALM-006","F-eab3948990634cea86b887e0585e19a4"]'::jsonb
               WHEN entity_type.code = 'equipment'
                   THEN '["operation_status"]'::jsonb
               WHEN entity_type.code = 'pipeline'
                   THEN '["pipeline_status"]'::jsonb
               WHEN entity_type.tenant_id = 1 AND entity_type.code = 'emergentcy_plan'
                   THEN '["plan_status"]'::jsonb
               WHEN entity_type.tenant_id = 1 AND entity_type.code = 'billing'
                   THEN '["billing_status"]'::jsonb
               WHEN entity_type.tenant_id = 1 AND entity_type.code = 'maintenance'
                   THEN '["order_status"]'::jsonb
               WHEN entity_type.tenant_id = 1 AND entity_type.code = 'protocol_mapping'
                   THEN '["protocol_mapping_status"]'::jsonb
               WHEN entity_type.tenant_id = 1 AND entity_type.code IN ('region', 'zone')
                   THEN '["boundary_status"]'::jsonb
               WHEN entity_type.tenant_id = 1 AND entity_type.code = 'sop'
                   THEN '["publish_status"]'::jsonb
               WHEN entity_type.tenant_id = 1 AND entity_type.code = 'task'
                   THEN '["FLD-TSK-004"]'::jsonb
               WHEN entity_type.tenant_id = 1 AND entity_type.code = 'task_execution_step'
                   THEN '["step_status"]'::jsonb
               ELSE '[]'::jsonb
           END AS fields
    FROM dynamicbusiness.dynamic_entity_type AS entity_type
    JOIN dynamicbusiness.dm_data_tab_layout AS layout
      ON layout.layout_id = entity_type.data_layout_id
     AND layout.deleted = false
     AND layout.column_kind = 'DETAIL'
    WHERE entity_type.deleted = false
      AND entity_type.tenant_id IN (1, 123)
      AND layout.props_id IS NOT NULL
) AS mapped
WHERE props.id = mapped.props_id;

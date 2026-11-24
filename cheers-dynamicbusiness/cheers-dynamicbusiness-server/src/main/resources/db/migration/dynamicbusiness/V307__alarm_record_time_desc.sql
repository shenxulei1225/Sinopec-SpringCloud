-- 智慧管廊告警记录按告警时间从近到远排，并按一段时间筛选。
-- 告警时间原先只在字段库里，目录上没有这一列，记录上也没写下这个时间。
-- 只改租户 123。已经写下的记录，用当时写入记录的时间补这一次。
SET search_path TO dynamicbusiness;

ALTER TABLE dynamicbusiness.ent_alarm_t123
    ADD COLUMN IF NOT EXISTS fld_alm_004 timestamp;

INSERT INTO dynamic_entity_type_base_field (
    entity_type_code, library_field_id, field_code, field_name, data_type, required,
    sort_order, status, tenant_id, creator, updater, deleted,
    is_searchable, is_filterable, is_sortable)
SELECT 'alarm', 9517, 'FLD-ALM-004', '告警时间', 'DATETIME', false,
       0, 1, 123, 'V307', 'V307', false,
       false, true, true
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_entity_type_base_field own
    WHERE own.tenant_id = 123
      AND own.deleted = false
      AND own.entity_type_code = 'alarm'
      AND own.field_code = 'FLD-ALM-004'
);

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    sort, field_source, tenant_id, creator, updater, deleted, model_code, field_code)
SELECT 2604, 9517, false, false, true, true,
       0, 'BASE', 123, 'V307', 'V307', false,
       'MODEL-d1366d7d7ddd4ef0ba2c4cfaf5d7e702', 'FLD-ALM-004'
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_model_field_assignment own
    WHERE own.tenant_id = 123
      AND own.deleted = false
      AND own.model_id = 2604
      AND own.field_code = 'FLD-ALM-004'
);

UPDATE dynamicbusiness.ent_alarm_t123
SET fld_alm_004 = create_time
WHERE tenant_id = 123
  AND deleted = false
  AND fld_alm_004 IS NULL
  AND create_time IS NOT NULL;

UPDATE platformresource.pr_component_props
SET props = jsonb_set(
        jsonb_set(
            props::jsonb,
            '{sort}',
            '{"orderByColumn":"FLD-ALM-004","isAsc":false}'::jsonb,
            true
        ),
        '{filter,filterSelectedOptions}',
        CASE
            WHEN COALESCE(props::jsonb #> '{filter,filterSelectedOptions}', '[]'::jsonb) ? 'FLD-ALM-004'
                THEN props::jsonb #> '{filter,filterSelectedOptions}'
            ELSE COALESCE(props::jsonb #> '{filter,filterSelectedOptions}', '[]'::jsonb) || '["FLD-ALM-004"]'::jsonb
        END,
        true
    )::text
WHERE id = 17068;

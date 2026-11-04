-- 事件主题的字段说明是载荷契约，不是数据协议那张「字段说明」用途。
-- 协议解析不得再认领这一列。详情按事件载荷说明画表。
SET search_path TO dynamicbusiness, public;

UPDATE dynamic_field
SET semantic_type = 'EVENT_PAYLOAD_SCHEMA',
    description = '运行时事件 attributes 的字段契约。不挂协议解析用途。',
    updater = 'flyway-v189',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND code = 'event_payload_schema';

INSERT INTO dynamic_model_field_assignment (
    model_id, model_code, field_id, field_code, required,
    is_searchable, is_filterable, is_sortable, sort, field_source,
    tenant_id, creator
)
SELECT m.id, m.code, f.id, f.code, FALSE,
       FALSE, FALSE, FALSE, cfg.sort, 'CUSTOM',
       m.tenant_id, 'flyway-v189'
FROM dynamic_model m
JOIN (
    VALUES
        ('event_category', 10),
        ('event_source_type', 20),
        ('event_phases', 30),
        ('event_payload_schema', 40)
) cfg(field_code, sort) ON TRUE
JOIN dynamic_field f
  ON f.tenant_id = m.tenant_id AND f.code = cfg.field_code AND f.deleted = FALSE
WHERE m.entity_type_code = 'event_topic' AND m.deleted = FALSE
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = FALSE
DO UPDATE SET field_id = EXCLUDED.field_id, sort = EXCLUDED.sort,
              deleted = FALSE, updater = 'flyway-v189', update_time = CURRENT_TIMESTAMP;

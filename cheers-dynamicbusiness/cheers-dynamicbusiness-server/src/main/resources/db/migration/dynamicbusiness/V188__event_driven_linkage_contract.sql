-- 事件驱动联动。结构走 Flyway。
-- 联动配置仍是 V187 已打标签的四格：联动事件、触发方式、联动方式、执行动作。
-- 禁止：另建一列顶替这四格；卸下这四格；新建绑定子型号来保存一对。
-- 禁止：运行时 Ensure；把全部成立改成任一触发；解析联动事件旧长文本。
SET search_path TO dynamicbusiness, public;

CREATE INDEX IF NOT EXISTS idx_entity_relation_topic_reverse
    ON dynamic_entity_relation (
        tenant_id, target_entity_id, field_code, source_entity_type_code
    )
    WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS platform_event_outbox (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(100) NOT NULL,
    event_topic_id BIGINT NOT NULL,
    event_code VARCHAR(100) NOT NULL,
    event_kind VARCHAR(64) NOT NULL,
    facility_id BIGINT NOT NULL,
    source_entity_type_code VARCHAR(64) NOT NULL,
    source_entity_id BIGINT NOT NULL,
    phase VARCHAR(32),
    occurred_at TIMESTAMP NOT NULL,
    payload JSONB NOT NULL DEFAULT '{}'::jsonb,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    attempts INTEGER NOT NULL DEFAULT 0,
    available_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,
    last_error TEXT,
    tenant_id BIGINT NOT NULL,
    creator VARCHAR(64) NOT NULL DEFAULT 'system',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_platform_event_outbox_event
    ON platform_event_outbox (tenant_id, event_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_platform_event_outbox_pending
    ON platform_event_outbox (status, available_at, id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_linkage_execution (
    id BIGSERIAL PRIMARY KEY,
    event_id VARCHAR(100) NOT NULL,
    event_topic_id BIGINT NOT NULL,
    linkage_rule_id BIGINT NOT NULL,
    facility_id BIGINT NOT NULL,
    source_entity_type_code VARCHAR(64) NOT NULL,
    source_entity_id BIGINT NOT NULL,
    status VARCHAR(24) NOT NULL,
    item_count INTEGER NOT NULL DEFAULT 0,
    success_count INTEGER NOT NULL DEFAULT 0,
    failure_count INTEGER NOT NULL DEFAULT 0,
    last_error TEXT,
    tenant_id BIGINT NOT NULL,
    creator VARCHAR(64) NOT NULL DEFAULT 'system',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_linkage_execution_event_rule
    ON dynamic_linkage_execution (tenant_id, event_id, linkage_rule_id)
    WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_action_execution (
    id BIGSERIAL PRIMARY KEY,
    idempotency_key VARCHAR(240) NOT NULL,
    caller_type VARCHAR(64) NOT NULL,
    caller_reference VARCHAR(160) NOT NULL,
    facility_id BIGINT NOT NULL,
    action_id BIGINT NOT NULL,
    target_entity_type_code VARCHAR(64) NOT NULL,
    target_entity_id BIGINT NOT NULL,
    parameters JSONB NOT NULL DEFAULT '{}'::jsonb,
    status VARCHAR(24) NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    available_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    finished_at TIMESTAMP,
    error_code VARCHAR(64),
    error_message TEXT,
    tenant_id BIGINT NOT NULL,
    creator VARCHAR(64) NOT NULL DEFAULT 'system',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_action_execution_idempotency
    ON dynamic_action_execution (tenant_id, idempotency_key)
    WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_action_execution_pending
    ON dynamic_action_execution (status, available_at, id) WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_linkage_execution_item (
    id BIGSERIAL PRIMARY KEY,
    linkage_execution_id BIGINT NOT NULL,
    pair_index BIGINT NOT NULL,
    action_execution_id BIGINT,
    status VARCHAR(24) NOT NULL,
    last_error TEXT,
    tenant_id BIGINT NOT NULL,
    creator VARCHAR(64) NOT NULL DEFAULT 'system',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE UNIQUE INDEX IF NOT EXISTS uk_linkage_execution_item_pair
    ON dynamic_linkage_execution_item (tenant_id, linkage_execution_id, pair_index)
    WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_linkage_execution_item_action
    ON dynamic_linkage_execution_item (tenant_id, action_execution_id)
    WHERE deleted = FALSE AND action_execution_id IS NOT NULL;

INSERT INTO dynamic_field (
    code, name, type, description, source, status, index_strategy,
    options, provider_code, semantic_type, tenant_id, creator
)
SELECT cfg.code, cfg.name, cfg.type, cfg.description, 'SYSTEM', 1, 'NONE',
       cfg.options, cfg.provider_code, cfg.semantic_type, topic.tenant_id, 'flyway-v188'
FROM (
    VALUES
        ('event_category', '事件分类', 'TEXT', '只用于管理和选用，不参与运行时路由。', NULL, NULL, 'EVENT_CATEGORY'),
        ('event_source_type', '来源对象类型', 'TEXT', '运行时事件来源的数据目录编码。', NULL, NULL, 'EVENT_SOURCE_TYPE'),
        ('event_phases', '支持阶段', 'ENUM', '事件支持的生命周期阶段。', '[{"label":"成立","value":"RAISED"},{"label":"恢复","value":"CLEARED"}]', NULL, 'EVENT_PHASES'),
        ('event_payload_schema', '字段说明', 'JSON', '运行时事件 attributes 的字段契约。', NULL, NULL, 'FIELD_DESCRIPTION'),
        ('alarm_raised_event_topic_ref', '成立事件', 'ENTITY_REF', '告警从未成立变为成立时发布的事件主题。', NULL, 'dynamic-entity:event_topic', 'ALARM_RAISED_EVENT_TOPIC_REF'),
        ('alarm_cleared_event_topic_ref', '恢复事件', 'ENTITY_REF', '可空。告警恢复时发布的另一条事件主题。', NULL, 'dynamic-entity:event_topic', 'ALARM_CLEARED_EVENT_TOPIC_REF')
) AS cfg(code, name, type, description, options, provider_code, semantic_type)
JOIN (
    SELECT DISTINCT tenant_id FROM dynamic_entity_type
    WHERE code IN ('event_topic', 'alarm_rule') AND deleted = FALSE
) topic ON TRUE
ON CONFLICT (code, tenant_id) WHERE deleted = FALSE
DO UPDATE SET
    name = EXCLUDED.name,
    type = EXCLUDED.type,
    description = EXCLUDED.description,
    options = EXCLUDED.options,
    provider_code = EXCLUDED.provider_code,
    semantic_type = EXCLUDED.semantic_type,
    status = 1,
    updater = 'flyway-v188',
    update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
    model_id, model_code, field_id, field_code, required,
    is_searchable, is_filterable, is_sortable, sort, field_source,
    tenant_id, creator
)
SELECT m.id, m.code, f.id, f.code, FALSE,
       FALSE, FALSE, FALSE, cfg.sort, 'CUSTOM',
       m.tenant_id, 'flyway-v188'
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
              deleted = FALSE, updater = 'flyway-v188', update_time = CURRENT_TIMESTAMP;

-- 原来的联动事件这一格改成事件主题多引用。不另建列。旧长文本清掉，不解析成主题。
UPDATE dynamic_field
SET type = 'ENTITY_REF_MULTI',
    provider_code = 'dynamic-entity:event_topic',
    description = '触发这条联动的事件主题。',
    updater = 'flyway-v188',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND code = 'F-52da82e319184f80b6cb40d030d5c017'
  AND semantic_type = 'LINKAGE_EVENT';

UPDATE dynamic_entity_type_base_field
SET data_type = 'ENTITY_REF_MULTI',
    updater = 'flyway-v188',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND field_code = 'F-52da82e319184f80b6cb40d030d5c017';

UPDATE dynamic_model_field_assignment a
SET target_entity_type = 'event_topic',
    deleted = FALSE,
    updater = 'flyway-v188',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m, dynamic_field f
WHERE a.model_id = m.id
  AND a.field_id = f.id
  AND m.deleted = FALSE
  AND f.deleted = FALSE
  AND m.entity_type_code = 'linkage'
  AND f.code = 'F-52da82e319184f80b6cb40d030d5c017'
  AND f.semantic_type = 'LINKAGE_EVENT';

-- 触发方式仍是原来那一格。选项只留任一事件触发。库里的全部成立原样留下，不改成任一。
UPDATE dynamic_field
SET options = '[{"label":"任一事件触发","value":"ANY"}]',
    updater = 'flyway-v188',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND code = 'F-4caa3bbacc99442b9cefdc192af6a362'
  AND semantic_type = 'LINKAGE_MATCH';

UPDATE dynamic_model
SET domain = 'alarm', updater = 'flyway-v188', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code IN ('linkage', 'alarm_rule') AND deleted = FALSE
  AND (domain IS NULL OR btrim(domain) = '');

INSERT INTO dynamic_model_field_assignment (
    model_id, model_code, field_id, field_code, required,
    is_searchable, is_filterable, is_sortable, sort, field_source,
    target_entity_type, tenant_id, creator
)
SELECT m.id, m.code, f.id, f.code, cfg.required,
       FALSE, FALSE, FALSE, cfg.sort, 'CUSTOM',
       'event_topic', m.tenant_id, 'flyway-v188'
FROM dynamic_model m
JOIN (
    VALUES
        ('alarm_raised_event_topic_ref', TRUE, 90),
        ('alarm_cleared_event_topic_ref', FALSE, 100)
) cfg(field_code, required, sort) ON TRUE
JOIN dynamic_field f
  ON f.tenant_id = m.tenant_id
 AND f.code = cfg.field_code
 AND f.deleted = FALSE
WHERE m.entity_type_code = 'alarm_rule' AND m.deleted = FALSE
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = FALSE
DO UPDATE SET field_id = EXCLUDED.field_id, required = EXCLUDED.required,
              target_entity_type = 'event_topic', deleted = FALSE,
              updater = 'flyway-v188', update_time = CURRENT_TIMESTAMP;

DO $$
DECLARE
    rec RECORD;
    col_name TEXT := 'f_52da82e319184f80b6cb40d030d5c017';
BEGIN
    FOR rec IN
        SELECT dedicated_table_name
        FROM dynamic_entity_type
        WHERE code = 'linkage' AND deleted = FALSE AND dedicated_table_name IS NOT NULL
    LOOP
        IF EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_schema = 'dynamicbusiness'
              AND table_name = rec.dedicated_table_name
              AND column_name = col_name
              AND data_type <> 'jsonb'
        ) THEN
            EXECUTE format(
                'ALTER TABLE dynamicbusiness.%I ALTER COLUMN %I TYPE jsonb USING CASE
                    WHEN %I IS NULL OR btrim(%I::text) = '''' THEN NULL
                    WHEN btrim(%I::text) LIKE ''[%%'' OR btrim(%I::text) LIKE ''{%%'' THEN %I::jsonb
                    ELSE NULL
                 END',
                rec.dedicated_table_name, col_name,
                col_name, col_name, col_name, col_name, col_name);
        END IF;
        IF EXISTS (
            SELECT 1 FROM information_schema.tables
            WHERE table_schema = 'dynamicbusiness' AND table_name = rec.dedicated_table_name
        ) THEN
            EXECUTE format(
                'UPDATE dynamicbusiness.%I SET domain = %L, updater = %L, update_time = CURRENT_TIMESTAMP
                 WHERE deleted = FALSE AND (domain IS NULL OR btrim(domain) = '''')',
                rec.dedicated_table_name, 'alarm', 'flyway-v188');
        END IF;
    END LOOP;
END $$;

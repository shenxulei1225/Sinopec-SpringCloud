-- V51: 五维编排 bundle（定义层）与 Who 槽位表；与 dm_data_tab_layout 分表、分 API

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamicbusiness.dm_five_w_orchestration (
    id                  BIGSERIAL PRIMARY KEY,
    entity_type_code    VARCHAR(64)  NOT NULL,
    enabled             BOOLEAN      NOT NULL DEFAULT TRUE,
    selection_level     VARCHAR(16)  NOT NULL,
    what_mode           VARCHAR(32)  NOT NULL,
    what_config         JSONB,
    how_mode            VARCHAR(32)  NOT NULL DEFAULT 'NONE',
    how_config          JSONB,
    creator             VARCHAR(64),
    create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64),
    update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

COMMENT ON TABLE dynamicbusiness.dm_five_w_orchestration IS
    '五维编排语义块（选层、What/How 模式）；与 data-tab-layout 并行、互不覆盖';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_five_w_orchestration_registry
    ON dynamicbusiness.dm_five_w_orchestration (tenant_id, entity_type_code)
    WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamicbusiness.dm_five_w_who_layout (
    id                  BIGSERIAL PRIMARY KEY,
    entity_type_code    VARCHAR(64)  NOT NULL,
    column_kind         VARCHAR(16)  NOT NULL,
    slot_ref            VARCHAR(128),
    perspective_id      VARCHAR(128),
    props_id            BIGINT,
    enabled             BOOLEAN      NOT NULL DEFAULT TRUE,
    context_outputs     JSONB,
    entity_id_rule      VARCHAR(32),
    category_column     JSONB,
    creator             VARCHAR(64),
    create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64),
    update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      DEFAULT FALSE,
    tenant_id           BIGINT       NOT NULL DEFAULT 0
);

COMMENT ON TABLE dynamicbusiness.dm_five_w_who_layout IS
    '五维编排 Who 槽位（含 contextOutputs / entityIdRule）';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_five_w_who_layout_scope
    ON dynamicbusiness.dm_five_w_who_layout (
        tenant_id, entity_type_code, column_kind, COALESCE(perspective_id, ''), COALESCE(slot_ref, '')
    )
    WHERE deleted = FALSE;

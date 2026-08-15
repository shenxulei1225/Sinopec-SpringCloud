-- V57: 分类筛选槽从 Who 布局拆出；分类即实体仍用 entity_id_rule = categoryLinkedEntity 做 1:1 同步

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamicbusiness.dm_five_w_filter_layout (
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

COMMENT ON TABLE dynamicbusiness.dm_five_w_filter_layout IS
    '五维编排筛选槽（分类器）；entity_id_rule=categoryLinkedEntity 时与 Who 实体 1:1 同步';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_five_w_filter_layout_scope
    ON dynamicbusiness.dm_five_w_filter_layout (
        tenant_id, entity_type_code, column_kind, COALESCE(perspective_id, ''), COALESCE(slot_ref, '')
    )
    WHERE deleted = FALSE;

INSERT INTO dynamicbusiness.dm_five_w_filter_layout (
    entity_type_code, column_kind, slot_ref, perspective_id, props_id,
    enabled, context_outputs, entity_id_rule, category_column,
    creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT
    entity_type_code,
    column_kind,
    slot_ref,
    perspective_id,
    props_id,
    enabled,
    '["categoryId"]'::jsonb,
    entity_id_rule,
    category_column,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    tenant_id
FROM dynamicbusiness.dm_five_w_who_layout
WHERE column_kind = 'CATEGORY'
  AND deleted = FALSE
  AND NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dm_five_w_filter_layout f
    WHERE f.tenant_id = dm_five_w_who_layout.tenant_id
      AND f.entity_type_code = dm_five_w_who_layout.entity_type_code
      AND f.column_kind = dm_five_w_who_layout.column_kind
      AND COALESCE(f.perspective_id, '') = COALESCE(dm_five_w_who_layout.perspective_id, '')
      AND COALESCE(f.slot_ref, '') = COALESCE(dm_five_w_who_layout.slot_ref, '')
      AND f.deleted = FALSE
  );

UPDATE dynamicbusiness.dm_five_w_who_layout
SET deleted = TRUE,
    updater = 'v57-filter-split',
    update_time = CURRENT_TIMESTAMP
WHERE column_kind = 'CATEGORY'
  AND deleted = FALSE;

COMMENT ON TABLE dynamicbusiness.dm_five_w_who_layout IS
    '五维编排 Who 槽位（MODEL / ENTITY）；分类槽已迁至 dm_five_w_filter_layout';

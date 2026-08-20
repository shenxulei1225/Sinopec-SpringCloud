-- V65: 栏间关系声明（哪两栏有关联、开哪些操作）；按页配置，非业务写死块

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamicbusiness.dm_data_tab_column_relation (
    id                      BIGSERIAL PRIMARY KEY,
    entity_type_code        VARCHAR(64)  NOT NULL,
    layout_scene            VARCHAR(32)  NOT NULL DEFAULT 'DATA_TAB',
    edge_id                 VARCHAR(128) NOT NULL,
    from_column_identity    VARCHAR(256) NOT NULL,
    to_column_identity      VARCHAR(256) NOT NULL,
    relation_kind           VARCHAR(32)  NOT NULL,
    from_type_code          VARCHAR(64)  NOT NULL,
    to_type_code            VARCHAR(64)  NOT NULL,
    -- enabledInteractions / linkKeys / presentation 等扩展
    relation_meta           JSONB,
    creator                 VARCHAR(64),
    create_time             TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updater                 VARCHAR(64),
    update_time             TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    deleted                 BOOLEAN      DEFAULT FALSE,
    tenant_id               BIGINT       NOT NULL DEFAULT 0
);

COMMENT ON TABLE dynamicbusiness.dm_data_tab_column_relation IS
    '数据 Tab 栏间关系声明：列身份对 + 关联种类 + 启用交互等；按数据类型与布局场景分页配置';

COMMENT ON COLUMN dynamicbusiness.dm_data_tab_column_relation.relation_meta IS
    '扩展 jsonb：enabledInteractions（数组）、linkKeys（可选，空则运行时合并同目标引用字段）、presentation';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dm_data_tab_column_relation_edge
    ON dynamicbusiness.dm_data_tab_column_relation (
        tenant_id, entity_type_code, layout_scene, edge_id
    )
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dm_data_tab_column_relation_scope
    ON dynamicbusiness.dm_data_tab_column_relation (
        tenant_id, entity_type_code, layout_scene
    )
    WHERE deleted = FALSE;

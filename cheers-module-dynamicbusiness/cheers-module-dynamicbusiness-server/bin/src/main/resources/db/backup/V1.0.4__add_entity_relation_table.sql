-- 业务实体关联关系表
-- 用于定义和管理业务实体之间的关联关系
-- 支持一对一、一对多、多对多三种关联类型

CREATE TABLE IF NOT EXISTS system_entity_relation (
    id BIGSERIAL PRIMARY KEY,
    source_entity_id BIGINT NOT NULL,
    target_entity_id BIGINT NOT NULL,
    relation_type VARCHAR(32) NOT NULL,
    relation_name VARCHAR(255),
    description TEXT,
    relation_attributes VARCHAR(4096),
    status INTEGER DEFAULT 1,
    tenant_id BIGINT DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_entity_relation_source ON system_entity_relation(source_entity_id);
CREATE INDEX IF NOT EXISTS idx_entity_relation_target ON system_entity_relation(target_entity_id);
CREATE INDEX IF NOT EXISTS idx_entity_relation_type ON system_entity_relation(relation_type);
CREATE INDEX IF NOT EXISTS idx_entity_relation_tenant ON system_entity_relation(tenant_id);

-- 添加注释
COMMENT ON TABLE system_entity_relation IS '业务实体关联关系表';
COMMENT ON COLUMN system_entity_relation.id IS '关联关系ID';
COMMENT ON COLUMN system_entity_relation.source_entity_id IS '源实体ID';
COMMENT ON COLUMN system_entity_relation.target_entity_id IS '目标实体ID';
COMMENT ON COLUMN system_entity_relation.relation_type IS '关联类型（ONE_TO_ONE/ONE_TO_MANY/MANY_TO_MANY）';
COMMENT ON COLUMN system_entity_relation.relation_name IS '关联名称';
COMMENT ON COLUMN system_entity_relation.description IS '关联描述';
COMMENT ON COLUMN system_entity_relation.relation_attributes IS '关联属性（JSON格式）';
COMMENT ON COLUMN system_entity_relation.status IS '状态（1-启用,0-禁用）';
COMMENT ON COLUMN system_entity_relation.tenant_id IS '租户ID';
COMMENT ON COLUMN system_entity_relation.creator IS '创建者';
COMMENT ON COLUMN system_entity_relation.create_time IS '创建时间';
COMMENT ON COLUMN system_entity_relation.updater IS '更新者';
COMMENT ON COLUMN system_entity_relation.update_time IS '更新时间';
COMMENT ON COLUMN system_entity_relation.deleted IS '是否删除';

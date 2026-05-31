-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.1
-- 日期: 2026-01-03
-- 描述: 添加字段分组表和实体分类关联表
-- =====================================================

-- 字段分组表
CREATE TABLE IF NOT EXISTS dynamic_field_group (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    parent_id BIGINT,
    path VARCHAR(1024),
    level INT DEFAULT 1,
    sort INT DEFAULT 0,
    status INT NOT NULL DEFAULT 1,
    creator VARCHAR(64) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0
);

-- 字段分组表索引
CREATE INDEX IF NOT EXISTS idx_field_group_code ON dynamic_field_group(code);
CREATE INDEX IF NOT EXISTS idx_field_group_parent_id ON dynamic_field_group(parent_id);
CREATE INDEX IF NOT EXISTS idx_field_group_tenant_id ON dynamic_field_group(tenant_id);
CREATE INDEX IF NOT EXISTS idx_field_group_status ON dynamic_field_group(status);

-- 字段分组表注释
COMMENT ON TABLE dynamic_field_group IS '字段分组表';
COMMENT ON COLUMN dynamic_field_group.id IS '主键ID';
COMMENT ON COLUMN dynamic_field_group.code IS '分组编码（全局唯一）';
COMMENT ON COLUMN dynamic_field_group.name IS '分组名称';
COMMENT ON COLUMN dynamic_field_group.description IS '分组描述';
COMMENT ON COLUMN dynamic_field_group.parent_id IS '父分组ID';
COMMENT ON COLUMN dynamic_field_group.path IS '分组路径（自动生成）';
COMMENT ON COLUMN dynamic_field_group.level IS '分组层级';
COMMENT ON COLUMN dynamic_field_group.sort IS '排序顺序';
COMMENT ON COLUMN dynamic_field_group.status IS '状态（1启用,0禁用）';
COMMENT ON COLUMN dynamic_field_group.tenant_id IS '租户ID';

-- 字段与分组关联表
CREATE TABLE IF NOT EXISTS dynamic_field_group_relation (
    id BIGSERIAL PRIMARY KEY,
    field_id BIGINT NOT NULL,
    field_group_id BIGINT NOT NULL,
    sort INT DEFAULT 0,
    creator VARCHAR(64) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0
);

-- 字段与分组关联表索引
CREATE INDEX IF NOT EXISTS idx_field_group_relation_field_id ON dynamic_field_group_relation(field_id);
CREATE INDEX IF NOT EXISTS idx_field_group_relation_group_id ON dynamic_field_group_relation(field_group_id);
CREATE INDEX IF NOT EXISTS idx_field_group_relation_tenant_id ON dynamic_field_group_relation(tenant_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_field_group_relation ON dynamic_field_group_relation(field_id, field_group_id, tenant_id) WHERE deleted = FALSE;

-- 字段与分组关联表注释
COMMENT ON TABLE dynamic_field_group_relation IS '字段与分组关联表';
COMMENT ON COLUMN dynamic_field_group_relation.id IS '主键ID';
COMMENT ON COLUMN dynamic_field_group_relation.field_id IS '字段ID';
COMMENT ON COLUMN dynamic_field_group_relation.field_group_id IS '字段分组ID';
COMMENT ON COLUMN dynamic_field_group_relation.sort IS '排序顺序';
COMMENT ON COLUMN dynamic_field_group_relation.tenant_id IS '租户ID';

-- 实体与分类关联表
CREATE TABLE IF NOT EXISTS dynamic_entity_category_relation (
    id BIGSERIAL PRIMARY KEY,
    entity_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    creator VARCHAR(64) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0
);

-- 实体与分类关联表索引
CREATE INDEX IF NOT EXISTS idx_entity_category_relation_entity_id ON dynamic_entity_category_relation(entity_id);
CREATE INDEX IF NOT EXISTS idx_entity_category_relation_category_id ON dynamic_entity_category_relation(category_id);
CREATE INDEX IF NOT EXISTS idx_entity_category_relation_tenant_id ON dynamic_entity_category_relation(tenant_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_entity_category_relation ON dynamic_entity_category_relation(entity_id, category_id, tenant_id) WHERE deleted = FALSE;

-- 实体与分类关联表注释
COMMENT ON TABLE dynamic_entity_category_relation IS '实体与分类关联表';
COMMENT ON COLUMN dynamic_entity_category_relation.id IS '主键ID';
COMMENT ON COLUMN dynamic_entity_category_relation.entity_id IS '实体ID';
COMMENT ON COLUMN dynamic_entity_category_relation.category_id IS '分类ID';
COMMENT ON COLUMN dynamic_entity_category_relation.tenant_id IS '租户ID';

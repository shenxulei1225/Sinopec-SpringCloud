-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.2
-- 日期: 2026-01-03
-- 描述: 添加单位分组表和单位与分组关联表
-- =====================================================

-- 单位分组表
CREATE TABLE IF NOT EXISTS system_unit_group (
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

-- 单位分组表索引
CREATE INDEX IF NOT EXISTS idx_unit_group_code ON system_unit_group(code);
CREATE INDEX IF NOT EXISTS idx_unit_group_parent_id ON system_unit_group(parent_id);
CREATE INDEX IF NOT EXISTS idx_unit_group_tenant_id ON system_unit_group(tenant_id);
CREATE INDEX IF NOT EXISTS idx_unit_group_status ON system_unit_group(status);

-- 单位分组表注释
COMMENT ON TABLE system_unit_group IS '单位分组表';
COMMENT ON COLUMN system_unit_group.id IS '主键ID';
COMMENT ON COLUMN system_unit_group.code IS '分组编码（全局唯一）';
COMMENT ON COLUMN system_unit_group.name IS '分组名称';
COMMENT ON COLUMN system_unit_group.description IS '分组描述';
COMMENT ON COLUMN system_unit_group.parent_id IS '父分组ID';
COMMENT ON COLUMN system_unit_group.path IS '分组路径（自动生成）';
COMMENT ON COLUMN system_unit_group.level IS '分组层级';
COMMENT ON COLUMN system_unit_group.sort IS '排序顺序';
COMMENT ON COLUMN system_unit_group.status IS '状态（1启用,0禁用）';
COMMENT ON COLUMN system_unit_group.tenant_id IS '租户ID';

-- 单位与分组关联表
CREATE TABLE IF NOT EXISTS system_unit_group_relation (
    id BIGSERIAL PRIMARY KEY,
    unit_id BIGINT NOT NULL,
    unit_group_id BIGINT NOT NULL,
    sort INT DEFAULT 0,
    creator VARCHAR(64) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0
);

-- 单位与分组关联表索引
CREATE INDEX IF NOT EXISTS idx_unit_group_relation_unit_id ON system_unit_group_relation(unit_id);
CREATE INDEX IF NOT EXISTS idx_unit_group_relation_group_id ON system_unit_group_relation(unit_group_id);
CREATE INDEX IF NOT EXISTS idx_unit_group_relation_tenant_id ON system_unit_group_relation(tenant_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_unit_group_relation ON system_unit_group_relation(unit_id, unit_group_id, tenant_id) WHERE deleted = FALSE;

-- 单位与分组关联表注释
COMMENT ON TABLE system_unit_group_relation IS '单位与分组关联表';
COMMENT ON COLUMN system_unit_group_relation.id IS '主键ID';
COMMENT ON COLUMN system_unit_group_relation.unit_id IS '单位ID';
COMMENT ON COLUMN system_unit_group_relation.unit_group_id IS '单位分组ID';
COMMENT ON COLUMN system_unit_group_relation.sort IS '排序顺序';
COMMENT ON COLUMN system_unit_group_relation.tenant_id IS '租户ID';

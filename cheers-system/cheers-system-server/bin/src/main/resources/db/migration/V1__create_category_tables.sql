-- =====================================================
-- Category / CategoryType 最终基础结构
-- 说明：
--   1. 仅保留纯分类体系能力
--   2. 不包含 CategoryEntityLink / EntityCategoryRelation / ModelCategoryRelation
--   3. tree / drag 依赖 system_category 的 parent_id/tree_path/level/sort 等字段，无需额外表
-- =====================================================

CREATE SEQUENCE IF NOT EXISTS system_category_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS system_category_type_seq START WITH 1000 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS system_category (
    id BIGINT PRIMARY KEY DEFAULT nextval('system_category_seq'),
    parent_id BIGINT,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL,
    category_type_code VARCHAR(64),
    tree_path VARCHAR(512),
    level INT,
    sort INT NOT NULL DEFAULT 1,
    status INT NOT NULL DEFAULT 1,
    description VARCHAR(500),
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE system_category IS '分类表';
COMMENT ON COLUMN system_category.id IS '分类ID';
COMMENT ON COLUMN system_category.parent_id IS '父分类ID';
COMMENT ON COLUMN system_category.name IS '分类名称';
COMMENT ON COLUMN system_category.code IS '分类编码';
COMMENT ON COLUMN system_category.category_type_code IS '分类类型编码';
COMMENT ON COLUMN system_category.tree_path IS '树路径';
COMMENT ON COLUMN system_category.level IS '层级';
COMMENT ON COLUMN system_category.sort IS '同级排序';
COMMENT ON COLUMN system_category.status IS '状态（1启用,0禁用）';
COMMENT ON COLUMN system_category.description IS '描述';
COMMENT ON COLUMN system_category.creator IS '创建者';
COMMENT ON COLUMN system_category.create_time IS '创建时间';
COMMENT ON COLUMN system_category.updater IS '更新者';
COMMENT ON COLUMN system_category.update_time IS '更新时间';
COMMENT ON COLUMN system_category.deleted IS '是否删除';
COMMENT ON COLUMN system_category.tenant_id IS '租户ID';

ALTER TABLE system_category ADD COLUMN IF NOT EXISTS parent_id BIGINT;
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS name VARCHAR(100);
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS code VARCHAR(100);
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS category_type_code VARCHAR(64);
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS tree_path VARCHAR(512);
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS level INT;
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS sort INT DEFAULT 1;
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS status INT DEFAULT 1;
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS description VARCHAR(500);
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS creator VARCHAR(64) DEFAULT '';
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS updater VARCHAR(64) DEFAULT '';
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS tenant_id BIGINT NOT NULL DEFAULT 0;

CREATE UNIQUE INDEX IF NOT EXISTS uk_system_category_code ON system_category (code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_system_category_parent ON system_category (parent_id, sort);
CREATE INDEX IF NOT EXISTS idx_system_category_type_code ON system_category (category_type_code, deleted);
CREATE INDEX IF NOT EXISTS idx_system_category_tree_path ON system_category (tree_path);
CREATE INDEX IF NOT EXISTS idx_system_category_tenant_deleted ON system_category (tenant_id, deleted);

CREATE TABLE IF NOT EXISTS system_category_type (
    id BIGINT PRIMARY KEY DEFAULT nextval('system_category_type_seq'),
    category_type_code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    status INTEGER NOT NULL DEFAULT 1,
    top_level_category_id BIGINT,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE system_category_type IS '分类类型表';
COMMENT ON COLUMN system_category_type.id IS '分类类型ID';
COMMENT ON COLUMN system_category_type.category_type_code IS '分类类型编码';
COMMENT ON COLUMN system_category_type.name IS '分类类型名称';
COMMENT ON COLUMN system_category_type.description IS '分类类型描述';
COMMENT ON COLUMN system_category_type.status IS '状态（1启用,0禁用）';
COMMENT ON COLUMN system_category_type.top_level_category_id IS '顶层分类ID';
COMMENT ON COLUMN system_category_type.creator IS '创建者';
COMMENT ON COLUMN system_category_type.create_time IS '创建时间';
COMMENT ON COLUMN system_category_type.updater IS '更新者';
COMMENT ON COLUMN system_category_type.update_time IS '更新时间';
COMMENT ON COLUMN system_category_type.deleted IS '是否删除';
COMMENT ON COLUMN system_category_type.tenant_id IS '租户ID';

ALTER TABLE system_category_type ADD COLUMN IF NOT EXISTS category_type_code VARCHAR(50);
ALTER TABLE system_category_type ADD COLUMN IF NOT EXISTS name VARCHAR(100);
ALTER TABLE system_category_type ADD COLUMN IF NOT EXISTS description VARCHAR(500);
ALTER TABLE system_category_type ADD COLUMN IF NOT EXISTS status INTEGER DEFAULT 1;
ALTER TABLE system_category_type ADD COLUMN IF NOT EXISTS top_level_category_id BIGINT;
ALTER TABLE system_category_type ADD COLUMN IF NOT EXISTS creator VARCHAR(64) DEFAULT '';
ALTER TABLE system_category_type ADD COLUMN IF NOT EXISTS create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE system_category_type ADD COLUMN IF NOT EXISTS updater VARCHAR(64) DEFAULT '';
ALTER TABLE system_category_type ADD COLUMN IF NOT EXISTS update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE system_category_type ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE system_category_type ADD COLUMN IF NOT EXISTS tenant_id BIGINT NOT NULL DEFAULT 0;

CREATE UNIQUE INDEX IF NOT EXISTS uk_system_category_type_code ON system_category_type(category_type_code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_system_category_type_status ON system_category_type(status) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_system_category_type_tenant_deleted ON system_category_type(tenant_id, deleted);

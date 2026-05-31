-- 业务实体权限管理表
-- 用于实现访问权限、操作权限、字段级权限、分类级权限控制

-- 1. 实体访问权限表（角色-实体访问权限）
-- 控制不同角色可以访问哪些业务实体
CREATE TABLE IF NOT EXISTS system_entity_access_permission (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    entity_id BIGINT NOT NULL,
    can_view BOOLEAN DEFAULT TRUE,
    tenant_id BIGINT DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    UNIQUE(role_id, entity_id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_entity_access_role ON system_entity_access_permission(role_id);
CREATE INDEX IF NOT EXISTS idx_entity_access_entity ON system_entity_access_permission(entity_id);
CREATE INDEX IF NOT EXISTS idx_entity_access_tenant ON system_entity_access_permission(tenant_id);

-- 添加注释
COMMENT ON TABLE system_entity_access_permission IS '实体访问权限表';
COMMENT ON COLUMN system_entity_access_permission.id IS '权限ID';
COMMENT ON COLUMN system_entity_access_permission.role_id IS '角色ID';
COMMENT ON COLUMN system_entity_access_permission.entity_id IS '实体ID';
COMMENT ON COLUMN system_entity_access_permission.can_view IS '是否可查看';
COMMENT ON COLUMN system_entity_access_permission.tenant_id IS '租户ID';

-- 2. 实体操作权限表（角色-实体操作权限）
-- 控制不同角色可以对实体执行哪些操作
CREATE TABLE IF NOT EXISTS system_entity_operation_permission (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    entity_id BIGINT,
    model_id BIGINT,
    can_create BOOLEAN DEFAULT FALSE,
    can_update BOOLEAN DEFAULT FALSE,
    can_delete BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_entity_operation_role ON system_entity_operation_permission(role_id);
CREATE INDEX IF NOT EXISTS idx_entity_operation_entity ON system_entity_operation_permission(entity_id);
CREATE INDEX IF NOT EXISTS idx_entity_operation_model ON system_entity_operation_permission(model_id);
CREATE INDEX IF NOT EXISTS idx_entity_operation_tenant ON system_entity_operation_permission(tenant_id);

-- 添加注释
COMMENT ON TABLE system_entity_operation_permission IS '实体操作权限表';
COMMENT ON COLUMN system_entity_operation_permission.id IS '权限ID';
COMMENT ON COLUMN system_entity_operation_permission.role_id IS '角色ID';
COMMENT ON COLUMN system_entity_operation_permission.entity_id IS '实体ID（为空时表示对模型下所有实体的权限）';
COMMENT ON COLUMN system_entity_operation_permission.model_id IS '模型ID（用于模型级别的权限控制）';
COMMENT ON COLUMN system_entity_operation_permission.can_create IS '是否可创建';
COMMENT ON COLUMN system_entity_operation_permission.can_update IS '是否可更新';
COMMENT ON COLUMN system_entity_operation_permission.can_delete IS '是否可删除';
COMMENT ON COLUMN system_entity_operation_permission.tenant_id IS '租户ID';

-- 3. 字段级权限表（角色-字段权限）
-- 控制不同角色可以查看/编辑哪些字段
CREATE TABLE IF NOT EXISTS system_entity_field_permission (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    model_id BIGINT NOT NULL,
    field_id BIGINT NOT NULL,
    can_view BOOLEAN DEFAULT TRUE,
    can_edit BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    UNIQUE(role_id, model_id, field_id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_field_permission_role ON system_entity_field_permission(role_id);
CREATE INDEX IF NOT EXISTS idx_field_permission_model ON system_entity_field_permission(model_id);
CREATE INDEX IF NOT EXISTS idx_field_permission_field ON system_entity_field_permission(field_id);
CREATE INDEX IF NOT EXISTS idx_field_permission_tenant ON system_entity_field_permission(tenant_id);

-- 添加注释
COMMENT ON TABLE system_entity_field_permission IS '字段级权限表';
COMMENT ON COLUMN system_entity_field_permission.id IS '权限ID';
COMMENT ON COLUMN system_entity_field_permission.role_id IS '角色ID';
COMMENT ON COLUMN system_entity_field_permission.model_id IS '模型ID';
COMMENT ON COLUMN system_entity_field_permission.field_id IS '字段ID';
COMMENT ON COLUMN system_entity_field_permission.can_view IS '是否可查看';
COMMENT ON COLUMN system_entity_field_permission.can_edit IS '是否可编辑';
COMMENT ON COLUMN system_entity_field_permission.tenant_id IS '租户ID';

-- 4. 分类级权限表（角色-分类权限）
-- 控制不同角色可以访问哪些分类
CREATE TABLE IF NOT EXISTS system_category_permission (
    id BIGSERIAL PRIMARY KEY,
    role_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    can_view BOOLEAN DEFAULT TRUE,
    can_manage BOOLEAN DEFAULT FALSE,
    tenant_id BIGINT DEFAULT 0,
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE,
    UNIQUE(role_id, category_id)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_category_permission_role ON system_category_permission(role_id);
CREATE INDEX IF NOT EXISTS idx_category_permission_category ON system_category_permission(category_id);
CREATE INDEX IF NOT EXISTS idx_category_permission_tenant ON system_category_permission(tenant_id);

-- 添加注释
COMMENT ON TABLE system_category_permission IS '分类级权限表';
COMMENT ON COLUMN system_category_permission.id IS '权限ID';
COMMENT ON COLUMN system_category_permission.role_id IS '角色ID';
COMMENT ON COLUMN system_category_permission.category_id IS '分类ID';
COMMENT ON COLUMN system_category_permission.can_view IS '是否可查看';
COMMENT ON COLUMN system_category_permission.can_manage IS '是否可管理（创建、编辑、删除子分类）';
COMMENT ON COLUMN system_category_permission.tenant_id IS '租户ID';

-- =====================================================
-- 模板管理表（Template-Model 复制机制）
-- 版本：V1.0.16
-- 日期：2026-01-06
-- 需求：FR-200、FR-201
-- =====================================================

-- 字段模板表
-- 业务含义：字段组合的预设模板,作为创建 Model 的起点
-- Template 与 Model 是"复制"关系而非"继承"关系
CREATE TABLE IF NOT EXISTS system_template (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(64) NOT NULL,
    name VARCHAR(200) NOT NULL,
    business_type_code VARCHAR(64) NOT NULL,
    description VARCHAR(500),
    status SMALLINT NOT NULL DEFAULT 1,
    is_system BOOLEAN NOT NULL DEFAULT FALSE,
    -- 通用字段
    creator VARCHAR(64) NOT NULL DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

-- 添加注释
COMMENT ON TABLE system_template IS '字段模板表';
COMMENT ON COLUMN system_template.id IS '模板ID';
COMMENT ON COLUMN system_template.code IS '模板编码（全局唯一,系统自动生成）';
COMMENT ON COLUMN system_template.name IS '模板名称';
COMMENT ON COLUMN system_template.business_type_code IS '业务类型编码';
COMMENT ON COLUMN system_template.description IS '模板描述';
COMMENT ON COLUMN system_template.status IS '模板状态（1-启用,0-禁用）';
COMMENT ON COLUMN system_template.is_system IS '是否为系统预设模板';
COMMENT ON COLUMN system_template.creator IS '创建者';
COMMENT ON COLUMN system_template.create_time IS '创建时间';
COMMENT ON COLUMN system_template.updater IS '更新者';
COMMENT ON COLUMN system_template.update_time IS '更新时间';
COMMENT ON COLUMN system_template.deleted IS '是否删除';
COMMENT ON COLUMN system_template.tenant_id IS '租户ID';

-- 创建索引
CREATE UNIQUE INDEX IF NOT EXISTS idx_template_code ON system_template(code) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_template_business_type ON system_template(business_type_code);
CREATE INDEX IF NOT EXISTS idx_template_status ON system_template(status);
CREATE INDEX IF NOT EXISTS idx_template_tenant ON system_template(tenant_id, deleted);

-- 模板字段分配表
-- 业务含义：将 Template 与 Field 进行多对多关联
CREATE TABLE IF NOT EXISTS system_template_field_assignment (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL,
    field_id BIGINT NOT NULL,
    sort_order INTEGER NOT NULL DEFAULT 0,
    required BOOLEAN NOT NULL DEFAULT FALSE,
    default_value VARCHAR(500),
    -- 通用字段
    creator VARCHAR(64) NOT NULL DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

-- 添加注释
COMMENT ON TABLE system_template_field_assignment IS '模板字段分配表';
COMMENT ON COLUMN system_template_field_assignment.id IS '分配ID';
COMMENT ON COLUMN system_template_field_assignment.template_id IS '模板ID';
COMMENT ON COLUMN system_template_field_assignment.field_id IS '字段ID';
COMMENT ON COLUMN system_template_field_assignment.sort_order IS '排序值';
COMMENT ON COLUMN system_template_field_assignment.required IS '是否必填';
COMMENT ON COLUMN system_template_field_assignment.default_value IS '默认值';
COMMENT ON COLUMN system_template_field_assignment.creator IS '创建者';
COMMENT ON COLUMN system_template_field_assignment.create_time IS '创建时间';
COMMENT ON COLUMN system_template_field_assignment.updater IS '更新者';
COMMENT ON COLUMN system_template_field_assignment.update_time IS '更新时间';
COMMENT ON COLUMN system_template_field_assignment.deleted IS '是否删除';
COMMENT ON COLUMN system_template_field_assignment.tenant_id IS '租户ID';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_tfa_template ON system_template_field_assignment(template_id);
CREATE INDEX IF NOT EXISTS idx_tfa_field ON system_template_field_assignment(field_id);
CREATE UNIQUE INDEX IF NOT EXISTS idx_tfa_template_field ON system_template_field_assignment(template_id, field_id) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_tfa_tenant ON system_template_field_assignment(tenant_id, deleted);

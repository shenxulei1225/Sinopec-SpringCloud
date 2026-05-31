-- =====================================================
-- 创建 system_category_type 表
-- 版本：V1.0.52
-- 描述：创建分类类型表,定义分类的维度体系
-- =====================================================

-- 创建序列
CREATE SEQUENCE IF NOT EXISTS system_category_type_seq START WITH 1 INCREMENT BY 1;

-- 创建分类类型表
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

-- 表注释
COMMENT ON TABLE system_category_type IS '分类类型表,定义分类的维度体系,如 region（区域）、equipment_type（设备类型）等';
COMMENT ON COLUMN system_category_type.id IS '分类类型ID';
COMMENT ON COLUMN system_category_type.category_type_code IS '分类类型编码,全局唯一,例如：region、equipment_type、organization、project_phase';
COMMENT ON COLUMN system_category_type.name IS '分类类型名称';
COMMENT ON COLUMN system_category_type.description IS '分类类型描述';
COMMENT ON COLUMN system_category_type.status IS '状态（1启用,0禁用）';
COMMENT ON COLUMN system_category_type.top_level_category_id IS '顶层分类ID（该分类类型的根节点）';
COMMENT ON COLUMN system_category_type.creator IS '创建者';
COMMENT ON COLUMN system_category_type.create_time IS '创建时间';
COMMENT ON COLUMN system_category_type.updater IS '更新者';
COMMENT ON COLUMN system_category_type.update_time IS '更新时间';
COMMENT ON COLUMN system_category_type.deleted IS '是否删除';
COMMENT ON COLUMN system_category_type.tenant_id IS '租户ID';

-- 创建唯一索引（分类类型编码全局唯一,考虑软删除）
CREATE UNIQUE INDEX IF NOT EXISTS uk_system_category_type_code ON system_category_type(category_type_code) WHERE deleted = FALSE;

-- 创建其他索引
CREATE INDEX IF NOT EXISTS idx_system_category_type_status ON system_category_type(status) WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_system_category_type_tenant_deleted ON system_category_type(tenant_id, deleted);

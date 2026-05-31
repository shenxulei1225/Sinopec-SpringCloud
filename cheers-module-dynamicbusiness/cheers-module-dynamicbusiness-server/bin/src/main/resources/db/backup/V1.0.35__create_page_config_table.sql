-- =====================================================
-- 页面配置表 (system_page_config)
-- 用于存储每个页面的配置信息,支持不同页面类型的配置
-- 
-- 设计说明：
-- 1. 每个菜单（页面）可以有一个配置记录
-- 2. 不同 page_type 有完全不同的配置结构,因此使用 JSONB 字段存储
-- 3. 数据管理页面的 config 包含 pattern、leftTreeType、rightContentType 等
-- =====================================================

-- 创建页面配置表
CREATE TABLE IF NOT EXISTS system_page_config (
    id BIGSERIAL PRIMARY KEY,
    menu_id BIGINT NOT NULL,
    page_type VARCHAR(50) NOT NULL DEFAULT 'data_management',
    config JSONB NOT NULL DEFAULT '{}',
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

-- 添加表注释
COMMENT ON TABLE system_page_config IS '页面配置表';
COMMENT ON COLUMN system_page_config.id IS '主键ID';
COMMENT ON COLUMN system_page_config.menu_id IS '关联菜单ID';
COMMENT ON COLUMN system_page_config.page_type IS '页面类型：data_management-数据管理, dashboard-驾驶舱, statistics-统计, monitor-实时监控';
COMMENT ON COLUMN system_page_config.config IS '配置内容(JSON),结构取决于page_type';
COMMENT ON COLUMN system_page_config.creator IS '创建者';
COMMENT ON COLUMN system_page_config.create_time IS '创建时间';
COMMENT ON COLUMN system_page_config.updater IS '更新者';
COMMENT ON COLUMN system_page_config.update_time IS '更新时间';
COMMENT ON COLUMN system_page_config.deleted IS '是否删除：false-未删除,true-已删除';
COMMENT ON COLUMN system_page_config.tenant_id IS '租户编号';

-- 创建唯一索引（menu_id + deleted 组合唯一）
CREATE UNIQUE INDEX IF NOT EXISTS uk_page_config_menu_id 
    ON system_page_config(menu_id) WHERE deleted = false;

-- 创建租户索引
CREATE INDEX IF NOT EXISTS idx_page_config_tenant_id 
    ON system_page_config(tenant_id);

-- 创建页面类型索引
CREATE INDEX IF NOT EXISTS idx_page_config_page_type 
    ON system_page_config(page_type);

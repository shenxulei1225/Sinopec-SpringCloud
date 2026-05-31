-- =====================================================
-- 页面管理表 (system_page)
-- 用于存储页面元数据与 A2UI 配置
-- =====================================================

CREATE TABLE IF NOT EXISTS system_page (
    id BIGSERIAL PRIMARY KEY,
    page_code VARCHAR(100) NOT NULL,
    page_name VARCHAR(100) NOT NULL,
    page_type VARCHAR(50) NOT NULL,
    description VARCHAR(255) DEFAULT '',
    status SMALLINT NOT NULL DEFAULT 0,
    parent_menu_id BIGINT,
    menu_id BIGINT,
    page_config_id BIGINT,
    icon VARCHAR(100) DEFAULT '',
    tags VARCHAR(255) DEFAULT '',
    route_path VARCHAR(255) DEFAULT '',
    component VARCHAR(255) DEFAULT '',
    layout VARCHAR(50) DEFAULT '',
    ui_schema JSONB NOT NULL DEFAULT '{}',
    ui_version VARCHAR(50) DEFAULT '',
    data_source JSONB NOT NULL DEFAULT '{}',
    remark VARCHAR(255) DEFAULT '',
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT false,
    tenant_id BIGINT NOT NULL DEFAULT 0
);

COMMENT ON TABLE system_page IS '页面管理表';
COMMENT ON COLUMN system_page.page_code IS '页面代码（唯一标识）';
COMMENT ON COLUMN system_page.page_name IS '页面名称';
COMMENT ON COLUMN system_page.page_type IS '页面类型';
COMMENT ON COLUMN system_page.description IS '页面描述';
COMMENT ON COLUMN system_page.status IS '页面状态（1-发布，0-停用）';
COMMENT ON COLUMN system_page.parent_menu_id IS '业务归属父菜单ID';
COMMENT ON COLUMN system_page.menu_id IS '页面挂载菜单ID';
COMMENT ON COLUMN system_page.page_config_id IS '关联页面配置ID';
COMMENT ON COLUMN system_page.icon IS '页面图标';
COMMENT ON COLUMN system_page.tags IS '页面标签';
COMMENT ON COLUMN system_page.route_path IS '路由地址';
COMMENT ON COLUMN system_page.component IS '前端组件路径';
COMMENT ON COLUMN system_page.layout IS '页面布局';
COMMENT ON COLUMN system_page.ui_schema IS 'A2UI Schema';
COMMENT ON COLUMN system_page.ui_version IS 'A2UI Schema 版本';
COMMENT ON COLUMN system_page.data_source IS 'A2UI 数据源配置';
COMMENT ON COLUMN system_page.remark IS '备注';
COMMENT ON COLUMN system_page.creator IS '创建者';
COMMENT ON COLUMN system_page.create_time IS '创建时间';
COMMENT ON COLUMN system_page.updater IS '更新者';
COMMENT ON COLUMN system_page.update_time IS '更新时间';
COMMENT ON COLUMN system_page.deleted IS '是否删除：false-未删除,true-已删除';
COMMENT ON COLUMN system_page.tenant_id IS '租户编号';

CREATE UNIQUE INDEX IF NOT EXISTS uk_page_code ON system_page(page_code) WHERE deleted = false;
CREATE INDEX IF NOT EXISTS idx_page_parent_menu_id ON system_page(parent_menu_id);
CREATE INDEX IF NOT EXISTS idx_page_status ON system_page(status);
CREATE INDEX IF NOT EXISTS idx_page_page_type ON system_page(page_type);

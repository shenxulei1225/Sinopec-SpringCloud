-- 创建应急保障表
CREATE TABLE IF NOT EXISTS emergency_guarantee (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    guarantee_code VARCHAR(64) NOT NULL COMMENT '保障编号（唯一标识）',
    guarantee_name VARCHAR(200) NOT NULL COMMENT '保障名称',
    guarantee_type VARCHAR(50) NOT NULL COMMENT '保障类型：personnel/material/technology/transportation/communication/medical等',
    description TEXT COMMENT '保障描述',
    status VARCHAR(50) NOT NULL DEFAULT 'available' COMMENT '保障状态：available/in_use/reserved/maintenance',
    contact_person VARCHAR(100) COMMENT '联系人',
    contact_phone VARCHAR(50) COMMENT '联系电话',
    location VARCHAR(500) COMMENT '保障位置',
    capacity VARCHAR(200) COMMENT '保障能力/容量',
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_guarantee_code_tenant (guarantee_code, tenant_id),
    KEY idx_guarantee_type (guarantee_type, deleted),
    KEY idx_status (status, deleted),
    KEY idx_is_enabled (is_enabled, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应急保障表';

-- 创建保障资源关联表
CREATE TABLE IF NOT EXISTS emergency_guarantee_resource (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    guarantee_id BIGINT NOT NULL COMMENT '保障ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID（关联resource_pool表）',
    resource_type VARCHAR(50) NOT NULL COMMENT '资源类型',
    quantity INT DEFAULT 1 COMMENT '数量',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    PRIMARY KEY (id),
    KEY idx_guarantee_id (guarantee_id, deleted),
    KEY idx_resource_id (resource_id, deleted),
    CONSTRAINT fk_guarantee_resource_guarantee FOREIGN KEY (guarantee_id) REFERENCES emergency_guarantee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='保障资源关联表';




-- 创建应急联络通讯录表
CREATE TABLE IF NOT EXISTS emergency_contact (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    contact_code VARCHAR(64) NOT NULL COMMENT '联系人编号（唯一标识）',
    contact_name VARCHAR(100) NOT NULL COMMENT '联系人姓名',
    contact_type VARCHAR(50) NOT NULL COMMENT '联系人类型：internal/external',
    organization VARCHAR(200) COMMENT '所属组织/机构',
    department VARCHAR(200) COMMENT '部门',
    position VARCHAR(100) COMMENT '职位',
    phone VARCHAR(50) COMMENT '电话',
    mobile VARCHAR(50) COMMENT '手机',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(500) COMMENT '地址',
    emergency_level VARCHAR(20) COMMENT '应急级别：primary/secondary',
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    remark TEXT COMMENT '备注',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_contact_code_tenant (contact_code, tenant_id),
    KEY idx_contact_type (contact_type, deleted),
    KEY idx_organization (organization, deleted),
    KEY idx_is_enabled (is_enabled, deleted),
    KEY idx_emergency_level (emergency_level, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应急联络通讯录表';




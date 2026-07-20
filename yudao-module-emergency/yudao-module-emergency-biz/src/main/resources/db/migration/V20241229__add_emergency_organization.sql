-- 创建应急组织表
CREATE TABLE IF NOT EXISTS emergency_organization (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    org_code VARCHAR(64) NOT NULL COMMENT '组织编号（唯一标识）',
    org_name VARCHAR(200) NOT NULL COMMENT '组织名称',
    org_type VARCHAR(50) NOT NULL COMMENT '组织类型：leadership_group/office/work_group/expert_group/sub_unit',
    parent_id BIGINT DEFAULT NULL COMMENT '上级组织ID（构建组织树）',
    description TEXT COMMENT '组织职责描述',
    is_dynamic BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否动态组织（现场工作组、专家组为动态组织）',
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_org_code_tenant (org_code, tenant_id),
    KEY idx_org_type (org_type, deleted),
    KEY idx_parent_id (parent_id, deleted),
    KEY idx_is_dynamic (is_dynamic, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='应急组织表';

-- 创建组织成员表
CREATE TABLE IF NOT EXISTS emergency_organization_member (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    org_id BIGINT NOT NULL COMMENT '关联组织ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role VARCHAR(50) NOT NULL COMMENT '角色：director/deputy_director/member/leader/deputy_leader/expert',
    contact_info JSON DEFAULT NULL COMMENT '联系方式（JSON格式，包含电话、邮箱等）',
    is_enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户ID',
    PRIMARY KEY (id),
    UNIQUE KEY uk_org_user_tenant (org_id, user_id, tenant_id, deleted),
    KEY idx_org_id (org_id, deleted),
    KEY idx_user_id (user_id, deleted),
    KEY idx_role (role, deleted),
    CONSTRAINT fk_org_member_org FOREIGN KEY (org_id) REFERENCES emergency_organization(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='组织成员表';




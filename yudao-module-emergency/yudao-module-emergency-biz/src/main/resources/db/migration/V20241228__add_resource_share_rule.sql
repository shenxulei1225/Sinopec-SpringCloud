-- 创建资源类型共享规则配置表
CREATE TABLE IF NOT EXISTS resource_type_share_rule (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    resource_type VARCHAR(50) NOT NULL COMMENT '资源类型：personnel/vehicle/equipment/material',
    allow_multi_event_share BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否允许多事件共享：true-允许，false-不允许',
    max_share_count INT DEFAULT NULL COMMENT '最大共享数量（当允许多事件共享时，限制最多可被多少个事件共享，NULL表示无限制）',
    description VARCHAR(500) DEFAULT NULL COMMENT '规则描述',
    creator VARCHAR(64) DEFAULT '' COMMENT '创建者',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater VARCHAR(64) DEFAULT '' COMMENT '更新者',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted BIT(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    tenant_id BIGINT NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (id),
    UNIQUE KEY uk_resource_type (resource_type, tenant_id, deleted),
    KEY idx_tenant_id (tenant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源类型共享规则配置表';

-- 插入默认共享规则配置
INSERT INTO resource_type_share_rule (resource_type, allow_multi_event_share, max_share_count, description, creator, create_time, updater, update_time, tenant_id)
VALUES
    ('personnel', TRUE, 3, '人员资源允许多事件共享，最多3个事件', 'system', NOW(), 'system', NOW(), 1),
    ('vehicle', FALSE, NULL, '车辆资源不允许多事件共享', 'system', NOW(), 'system', NOW(), 1),
    ('equipment', TRUE, 5, '设备资源允许多事件共享，最多5个事件', 'system', NOW(), 'system', NOW(), 1),
    ('material', TRUE, NULL, '物资资源允许多事件共享，无数量限制', 'system', NOW(), 'system', NOW(), 1)
ON DUPLICATE KEY UPDATE
    allow_multi_event_share = VALUES(allow_multi_event_share),
    max_share_count = VALUES(max_share_count),
    description = VALUES(description),
    update_time = NOW();




-- 引用 Provider 注册中心 + 字段扩展

CREATE TABLE IF NOT EXISTS system_reference_provider (
    id BIGSERIAL PRIMARY KEY,
    provider_code VARCHAR(64) NOT NULL,
    provider_name VARCHAR(128) NOT NULL,
    provider_type VARCHAR(32) NOT NULL DEFAULT 'INTERNAL',
    semantic_type VARCHAR(64) DEFAULT NULL,
    capability_flags TEXT DEFAULT NULL,
    config_json TEXT DEFAULT NULL,
    status SMALLINT NOT NULL DEFAULT 1,
    tenant_scope VARCHAR(32) DEFAULT 'GLOBAL',
    priority INT DEFAULT 100,
    health_status VARCHAR(32) DEFAULT 'UNKNOWN',
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BIT NOT NULL DEFAULT B'0',
    tenant_id BIGINT NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_system_reference_provider_code
    ON system_reference_provider (provider_code, tenant_id, deleted);

ALTER TABLE system_field
    ADD COLUMN IF NOT EXISTS provider_code VARCHAR(64);

ALTER TABLE system_field
    ADD COLUMN IF NOT EXISTS semantic_type VARCHAR(64);

COMMENT ON COLUMN system_field.provider_code IS '引用 Provider 编码（REFERENCE/ENTITY_REF 可选）';
COMMENT ON COLUMN system_field.semantic_type IS '引用语义类型（USER/DEPT/ROLE/MATERIAL...）';

-- 初始化默认 SYSTEM_USER provider
INSERT INTO system_reference_provider
(provider_code, provider_name, provider_type, semantic_type, capability_flags, status, tenant_scope, priority, health_status, tenant_id)
SELECT 'SYSTEM_USER', '系统用户', 'INTERNAL', 'USER', '{"supports_search":true,"supports_batch_validate":false}', 1, 'GLOBAL', 100, 'UNKNOWN', 0
WHERE NOT EXISTS (
    SELECT 1 FROM system_reference_provider
    WHERE provider_code = 'SYSTEM_USER' AND tenant_id = 0 AND deleted = B'0'
);

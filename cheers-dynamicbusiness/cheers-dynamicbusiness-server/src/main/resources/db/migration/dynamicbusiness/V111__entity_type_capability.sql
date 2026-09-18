-- V111: 数据目录能力开关（插件化能力治理）
-- 目标：版本管理等能力由统一入口按目录启停，不侵入创建流程与通用 CRUD 主链路。

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamic_entity_type_capability (
  id BIGSERIAL PRIMARY KEY,
  tenant_id BIGINT NOT NULL,
  entity_type_code VARCHAR(100) NOT NULL,
  capability_code VARCHAR(64) NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  creator VARCHAR(64),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updater VARCHAR(64),
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  deleted BOOLEAN DEFAULT FALSE
);

COMMENT ON TABLE dynamic_entity_type_capability IS
  '数据目录能力开关：按目录启停可选能力（如 version-management）。';
COMMENT ON COLUMN dynamic_entity_type_capability.entity_type_code IS '数据目录编码（例如 sop）';
COMMENT ON COLUMN dynamic_entity_type_capability.capability_code IS '能力编码（例如 version-management）';
COMMENT ON COLUMN dynamic_entity_type_capability.enabled IS '能力是否启用';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_entity_type_capability_identity
  ON dynamic_entity_type_capability (tenant_id, entity_type_code, capability_code)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_type_capability_lookup
  ON dynamic_entity_type_capability (tenant_id, entity_type_code, deleted);

-- 为现有 SOP 目录补默认能力开关（仅首次插入）
INSERT INTO dynamic_entity_type_capability (
  tenant_id, entity_type_code, capability_code, enabled, deleted
)
SELECT et.tenant_id, et.code, 'version-management', TRUE, FALSE
FROM dynamic_entity_type et
WHERE et.deleted = FALSE
  AND lower(et.code) = 'sop'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_entity_type_capability cap
    WHERE cap.tenant_id = et.tenant_id
      AND cap.entity_type_code = et.code
      AND cap.capability_code = 'version-management'
      AND cap.deleted = FALSE
  );

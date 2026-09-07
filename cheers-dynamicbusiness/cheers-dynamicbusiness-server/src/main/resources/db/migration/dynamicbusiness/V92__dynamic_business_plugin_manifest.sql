-- V92: 动态业务插件清单（后端权威启停）
-- 目的：由后端下发可启用插件列表，前端只按清单安装，不再写死全量启用

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamic_business_plugin_manifest (
  id                      BIGSERIAL PRIMARY KEY,
  tenant_id               BIGINT        NOT NULL,
  plugin_id               VARCHAR(128)  NOT NULL,
  version                 VARCHAR(32)   NOT NULL,
  enabled                 BOOLEAN       NOT NULL DEFAULT TRUE,
  platform_range          VARCHAR(128),
  entry                   VARCHAR(255),
  depends_on              JSONB,
  permissions             JSONB,
  semantic_contributions  JSONB,
  creator                 VARCHAR(64),
  create_time             TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
  updater                 VARCHAR(64),
  update_time             TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
  deleted                 BOOLEAN       DEFAULT FALSE
);

COMMENT ON TABLE dynamic_business_plugin_manifest IS '动态业务插件清单：后端权威插件启停与语义贡献声明';
COMMENT ON COLUMN dynamic_business_plugin_manifest.plugin_id IS '插件唯一标识';
COMMENT ON COLUMN dynamic_business_plugin_manifest.version IS '插件版本';
COMMENT ON COLUMN dynamic_business_plugin_manifest.enabled IS '是否启用';
COMMENT ON COLUMN dynamic_business_plugin_manifest.platform_range IS '平台兼容版本范围';
COMMENT ON COLUMN dynamic_business_plugin_manifest.entry IS '插件注册入口（审计用）';
COMMENT ON COLUMN dynamic_business_plugin_manifest.depends_on IS '依赖插件列表（JSON 数组）';
COMMENT ON COLUMN dynamic_business_plugin_manifest.permissions IS '权限声明列表（JSON 数组）';
COMMENT ON COLUMN dynamic_business_plugin_manifest.semantic_contributions IS '语义贡献列表（JSON 数组）';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_business_plugin_manifest_tid_plugin
  ON dynamic_business_plugin_manifest (tenant_id, plugin_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_business_plugin_manifest_enabled
  ON dynamic_business_plugin_manifest (enabled, tenant_id)
  WHERE deleted = FALSE;

-- 默认内置插件：动作库
UPDATE dynamic_business_plugin_manifest
SET
  version = '1.0.0',
  enabled = TRUE,
  platform_range = '>=1.0.0',
  entry = 'features/action/plugin/manifest.ts',
  depends_on = '[]'::jsonb,
  permissions = '["work-blocks:detail","structured-fields:register"]'::jsonb,
  semantic_contributions = '["ACTION_TREE","ACTION_PARAM_SCHEMA"]'::jsonb,
  updater = 'flyway',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND tenant_id = 1
  AND plugin_id = 'action-library';

INSERT INTO dynamic_business_plugin_manifest (
  tenant_id, plugin_id, version, enabled, platform_range, entry,
  depends_on, permissions, semantic_contributions,
  creator, create_time, updater, update_time, deleted
)
SELECT
  1, 'action-library', '1.0.0', TRUE, '>=1.0.0', 'features/action/plugin/manifest.ts',
  '[]'::jsonb,
  '["work-blocks:detail","structured-fields:register"]'::jsonb,
  '["ACTION_TREE","ACTION_PARAM_SCHEMA"]'::jsonb,
  'flyway', CURRENT_TIMESTAMP, 'flyway', CURRENT_TIMESTAMP, FALSE
WHERE NOT EXISTS (
  SELECT 1
  FROM dynamic_business_plugin_manifest
  WHERE deleted = FALSE
    AND tenant_id = 1
    AND plugin_id = 'action-library'
);

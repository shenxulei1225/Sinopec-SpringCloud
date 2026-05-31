-- ====================================================================
-- V1.0.56: 同步 system_business_type 表缺失的列
-- 目的：让数据库结构与 BusinessTypeMapper / 代码期望保持一致
-- ====================================================================

-- 1. 父业务类型 ID,用于业务类型树结构
ALTER TABLE system_business_type
    ADD COLUMN IF NOT EXISTS parent_id BIGINT;

COMMENT ON COLUMN system_business_type.parent_id IS '父业务类型 ID,支持业务类型树结构';

-- 2. 业务类型级别：SYSTEM=系统内置,USER=用户创建
-- 注意：V1.0.55 已经添加 type_level VARCHAR(16),此处仅防御性补充,避免老环境遗漏
ALTER TABLE system_business_type
    ADD COLUMN IF NOT EXISTS type_level VARCHAR(16) NOT NULL DEFAULT 'USER';

COMMENT ON COLUMN system_business_type.type_level IS '业务类型级别：SYSTEM=系统内置,USER=用户创建';

-- 3. 可用的关联字段定义（JSONB）,如果之前的 1.0.44 未正确执行,则补上
ALTER TABLE system_business_type
    ADD COLUMN IF NOT EXISTS association_fields JSONB;

COMMENT ON COLUMN system_business_type.association_fields IS '可用的关联字段定义（JSONB 格式）。';

CREATE INDEX IF NOT EXISTS idx_system_business_type_association_fields
    ON system_business_type USING GIN (association_fields);

-- 4. 存储类型、专用表名、是否启用规则引擎、物理列映射配置
ALTER TABLE system_business_type
    ADD COLUMN IF NOT EXISTS storage_type VARCHAR(30);

ALTER TABLE system_business_type
    ADD COLUMN IF NOT EXISTS dedicated_table_name VARCHAR(100);

ALTER TABLE system_business_type
    ADD COLUMN IF NOT EXISTS enable_rule_engine BOOLEAN DEFAULT FALSE;

ALTER TABLE system_business_type
    ADD COLUMN IF NOT EXISTS physical_column_mapping JSONB;

COMMENT ON COLUMN system_business_type.storage_type IS '存储类型：GENERIC/DEDICATED_STATIC/DEDICATED_DYNAMIC';
COMMENT ON COLUMN system_business_type.dedicated_table_name IS '专用表名';
COMMENT ON COLUMN system_business_type.enable_rule_engine IS '是否启用规则引擎';
COMMENT ON COLUMN system_business_type.physical_column_mapping IS '物理列映射配置（JSON 格式）。';

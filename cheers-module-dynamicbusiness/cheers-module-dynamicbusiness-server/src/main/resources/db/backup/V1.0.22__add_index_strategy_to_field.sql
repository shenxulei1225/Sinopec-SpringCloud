-- =====================================================
-- 添加 index_strategy 字段到 dynamic_field 表
-- 需求：FR-BDA-080~083 智能默认可查询
-- =====================================================

-- 添加 index_strategy 字段
ALTER TABLE dynamic_field ADD COLUMN IF NOT EXISTS index_strategy VARCHAR(32) DEFAULT 'NONE';

-- 添加字段注释
COMMENT ON COLUMN dynamic_field.index_strategy IS '索引策略：GIN-GIN索引（等值/包含查询）,BTREE-B-Tree索引（范围查询/排序）,NONE-不索引';

-- 根据现有字段类型更新 index_strategy 默认值
-- 文本、布尔、枚举、关联字段使用 GIN 索引
UPDATE dynamic_field 
SET index_strategy = 'GIN' 
WHERE type IN ('TEXT', 'BOOLEAN', 'ENUM', 'ENTITY_REF', 'REFERENCE')
  AND index_strategy IS NULL OR index_strategy = 'NONE';

-- 数字、日期字段使用 BTREE 索引
UPDATE dynamic_field 
SET index_strategy = 'BTREE' 
WHERE type IN ('NUMBER', 'INTEGER', 'DATE', 'DATETIME')
  AND index_strategy IS NULL OR index_strategy = 'NONE';

-- 大文本、JSON、文件类型不索引
UPDATE dynamic_field 
SET index_strategy = 'NONE' 
WHERE type IN ('LONG_TEXT', 'JSON', 'FILE', 'IMAGE')
  AND index_strategy IS NULL;

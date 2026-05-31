-- ============================================================================
-- 将 dynamic_entity 表的 custom_fields 列升级为 JSONB 类型
-- 版本：V1.0.25
-- 日期：2026-01-07
-- 说明：
--   1. 将 custom_fields 从 TEXT 转换为 JSONB 类型
--   2. 创建 GIN 索引以加速 JSONB 查询
--   3. 使 PostgresQueryEngine 的 JSONB 查询能够直接生效
-- 
-- 注意：
--   - 此迁移会自动处理空值和空字符串,转换为空 JSON 对象 '{}'
--   - 现有数据必须是有效的 JSON 格式,否则迁移会失败
--   - 建议在执行前备份数据库
-- ============================================================================

-- 1. 将 TEXT 列转换为 JSONB 类型
-- 使用 CASE 表达式处理 NULL 和空字符串的情况
ALTER TABLE dynamic_entity 
ALTER COLUMN custom_fields TYPE JSONB USING 
    CASE 
        WHEN custom_fields IS NULL OR custom_fields = '' THEN '{}'::jsonb
        ELSE custom_fields::jsonb 
    END;

-- 2. 设置默认值为空 JSON 对象
ALTER TABLE dynamic_entity 
ALTER COLUMN custom_fields SET DEFAULT '{}'::jsonb;

-- 3. 创建 GIN 索引以加速 JSONB 查询
-- 使用 IF NOT EXISTS 避免重复创建
CREATE INDEX IF NOT EXISTS idx_dynamic_entity_custom_fields 
    ON dynamic_entity USING GIN (custom_fields);

-- 4. 更新列注释
COMMENT ON COLUMN dynamic_entity.custom_fields IS '自定义字段数据（JSONB格式,支持 @> 操作符查询）';

-- 5. 分析表以更新统计信息,优化查询计划
ANALYZE dynamic_entity;

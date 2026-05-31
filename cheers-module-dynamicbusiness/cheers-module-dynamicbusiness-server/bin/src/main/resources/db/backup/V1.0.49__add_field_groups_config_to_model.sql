-- ========================================
-- 添加字段分组配置字段到 system_model 表
-- 创建日期: 2026-01-27
-- 说明: 为模型添加字段分组配置字段,用于存储模型字段分组的 JSON 配置
-- ========================================

-- 为 system_model 表添加字段分组配置字段
-- 用于存储模型字段分组的 JSON 配置
ALTER TABLE system_model 
ADD COLUMN IF NOT EXISTS field_groups_config TEXT NULL;

-- 添加注释（PostgreSQL 使用 COMMENT 语法）
COMMENT ON COLUMN system_model.field_groups_config IS '字段分组配置（JSON格式）';

-- 字段分组配置格式示例：
-- {
--   "groups": [
--     {
--       "id": "group-1",
--       "name": "基础字段",
--       "color": "#409eff",
--       "sort": 1
--     },
--     {
--       "id": "group-2",
--       "name": "扩展字段",
--       "color": "#67c23a",
--       "sort": 2
--     }
--   ]
-- }

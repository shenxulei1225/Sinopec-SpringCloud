-- ============================================================
-- V1.0.31: 删除 Model 字段分配表的 is_inherited 字段
-- ============================================================
-- 背景：已移除 Model-Template 继承机制,is_inherited 字段不再需要
-- ============================================================

-- 1. 删除继承相关的索引
DROP INDEX IF EXISTS idx_model_field_assignment_inherited;

-- 2. 删除字段
ALTER TABLE dynamic_model_field_assignment 
    DROP COLUMN IF EXISTS is_inherited;

-- 3. 更新表注释
COMMENT ON TABLE dynamic_model_field_assignment IS '模型字段表,定义 Model 有哪些字段';

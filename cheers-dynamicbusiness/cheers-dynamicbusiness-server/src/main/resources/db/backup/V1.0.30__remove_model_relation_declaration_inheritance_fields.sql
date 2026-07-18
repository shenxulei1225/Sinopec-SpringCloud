-- ============================================================
-- V1.0.30: 删除 Model 关联声明表的继承相关字段
-- ============================================================
-- 背景：已移除 Model-Template 继承机制,is_inherited 和 source_model_id 字段不再需要
-- ============================================================

-- 1. 删除继承相关的索引
DROP INDEX IF EXISTS idx_mrd_inherited;
DROP INDEX IF EXISTS idx_mrd_source_model;

-- 2. 删除外键约束（如果存在）
ALTER TABLE dynamic_model_relation_declaration 
    DROP CONSTRAINT IF EXISTS fk_mrd_source_model;

-- 3. 删除字段
ALTER TABLE dynamic_model_relation_declaration 
    DROP COLUMN IF EXISTS is_inherited;

ALTER TABLE dynamic_model_relation_declaration 
    DROP COLUMN IF EXISTS source_model_id;

-- 4. 更新表注释
COMMENT ON TABLE dynamic_model_relation_declaration IS 'Model 关联声明表,声明 Model 可以关联哪些业务类型';

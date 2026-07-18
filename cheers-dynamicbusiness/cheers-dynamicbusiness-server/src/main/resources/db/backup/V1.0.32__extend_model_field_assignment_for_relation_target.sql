-- ============================================================================
-- V1.0.32: 扩展 dynamic_model_field_assignment 表支持直接存储关联目标信息
-- 描述：优化业务关联流程,在字段分配表中直接存储关联目标信息,避免多次查询
-- 需求：1.1, 2.1 - 扩展字段分配表存储关联目标信息、优化 Model 关联创建流程
-- 来源：基于 sql/postgresql/V20260109_001__extend_model_field_assignment_for_relation_target.sql
-- ============================================================================

-- 1. 添加 model_relation_id 字段
-- 用途：记录关联的 Model 关联 ID,用于级联删除时快速定位
ALTER TABLE dynamic_model_field_assignment 
ADD COLUMN IF NOT EXISTS model_relation_id BIGINT;

-- 2. 添加 target_business_type 字段
-- 用途：当字段类型为 ENTITY_REF 时,直接存储目标业务类型编码
ALTER TABLE dynamic_model_field_assignment 
ADD COLUMN IF NOT EXISTS target_business_type VARCHAR(100);

-- 3. 添加 target_model_code 字段
-- 用途：当字段类型为 ENTITY_REF 时,直接存储目标 Model 编码
ALTER TABLE dynamic_model_field_assignment 
ADD COLUMN IF NOT EXISTS target_model_code VARCHAR(100);

-- 4. 添加 display_field_code 字段
-- 用途：指定在下拉选择时显示目标实体的哪个字段
ALTER TABLE dynamic_model_field_assignment 
ADD COLUMN IF NOT EXISTS display_field_code VARCHAR(100);

-- 5. 添加字段注释
COMMENT ON COLUMN dynamic_model_field_assignment.model_relation_id 
IS '关联的 Model 关联 ID,用于级联操作';

COMMENT ON COLUMN dynamic_model_field_assignment.target_business_type 
IS '关联目标业务类型编码（ENTITY_REF 类型字段使用）';

COMMENT ON COLUMN dynamic_model_field_assignment.target_model_code 
IS '关联目标 Model 编码（ENTITY_REF 类型字段使用）';

COMMENT ON COLUMN dynamic_model_field_assignment.display_field_code 
IS '展示字段编码（ENTITY_REF 类型字段使用）';

-- 6. 创建索引
-- 索引1：通过 model_relation_id 快速定位关联字段（用于级联删除）
CREATE INDEX IF NOT EXISTS idx_model_field_assignment_relation 
    ON dynamic_model_field_assignment(model_relation_id) 
    WHERE deleted = FALSE AND model_relation_id IS NOT NULL;

-- 索引2：通过目标业务类型和模型编码查询（用于关联字段查询）
CREATE INDEX IF NOT EXISTS idx_model_field_assignment_target 
    ON dynamic_model_field_assignment(target_business_type, target_model_code) 
    WHERE deleted = FALSE AND target_business_type IS NOT NULL;


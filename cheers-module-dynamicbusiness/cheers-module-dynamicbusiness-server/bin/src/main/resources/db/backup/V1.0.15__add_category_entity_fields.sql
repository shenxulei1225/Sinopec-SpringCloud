-- =====================================================
-- 模式G 支持：为 Category 表添加实体相关字段
-- 支持分类节点本身作为业务实体（区域管理、组织架构、库位管理等场景）
-- =====================================================

-- 添加 is_entity 字段：标记该分类是否作为实体
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS is_entity BOOLEAN DEFAULT FALSE;
COMMENT ON COLUMN system_category.is_entity IS '是否作为实体：true=分类节点本身是业务实体,false=纯分类节点';

-- 添加 entity_model_id 字段：关联的 Model ID,定义该分类的字段结构
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS entity_model_id BIGINT;
COMMENT ON COLUMN system_category.entity_model_id IS '关联的 Model ID,定义该分类节点的字段结构';

-- 添加 entity_id 字段：关联的 Entity ID,存储字段值
ALTER TABLE system_category ADD COLUMN IF NOT EXISTS entity_id BIGINT;
COMMENT ON COLUMN system_category.entity_id IS '关联的 Entity ID,存储该分类节点的字段值';

-- 创建索引：按 is_entity 过滤
CREATE INDEX IF NOT EXISTS idx_category_is_entity ON system_category(is_entity) WHERE deleted = FALSE;

-- 创建索引：按 entity_model_id 查询
CREATE INDEX IF NOT EXISTS idx_category_entity_model_id ON system_category(entity_model_id) WHERE deleted = FALSE;

-- 创建索引：按 entity_id 查询
CREATE INDEX IF NOT EXISTS idx_category_entity_id ON system_category(entity_id) WHERE deleted = FALSE;

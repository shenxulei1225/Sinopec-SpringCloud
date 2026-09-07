-- ============================================================================
-- V1.0.102: 为模型字段表增加 is_filterable
-- 描述：支持在模型字段层独立配置“是否可筛选”
-- ============================================================================

-- 1) 新增列
ALTER TABLE dynamic_model_field_assignment
ADD COLUMN IF NOT EXISTS is_filterable BOOLEAN;

-- 2) 列注释
COMMENT ON COLUMN dynamic_model_field_assignment.is_filterable
IS '是否可筛选（如果为 NULL，则表示未显式配置）';

-- 3) 数据回填（与当前行为兼容）
--   规则：当 is_filterable 为空时，先回填为 is_searchable。
UPDATE dynamic_model_field_assignment
SET is_filterable = is_searchable
WHERE is_filterable IS NULL;

-- 4) 可选索引（按 model_id + is_filterable 查询筛选元信息时可加速）
CREATE INDEX IF NOT EXISTS idx_mfa_model_filterable
ON dynamic_model_field_assignment (model_id, is_filterable)
WHERE deleted = FALSE;

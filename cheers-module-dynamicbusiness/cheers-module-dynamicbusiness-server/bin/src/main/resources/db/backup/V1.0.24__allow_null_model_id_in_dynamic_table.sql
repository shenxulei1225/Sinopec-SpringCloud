-- 允许 system_dynamic_table 表的 model_id 为空
-- 原因：业务类型级别的动态表不关联具体 Model,model_id 应为 NULL
-- 只有 Model 级别的动态表才需要关联 model_id

ALTER TABLE system_dynamic_table ALTER COLUMN model_id DROP NOT NULL;

-- 添加注释说明
COMMENT ON COLUMN system_dynamic_table.model_id IS '关联的业务模型ID（业务类型级别动态表时为NULL）';

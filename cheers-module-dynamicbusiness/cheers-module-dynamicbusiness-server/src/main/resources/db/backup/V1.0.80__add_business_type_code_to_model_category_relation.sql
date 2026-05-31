-- 为模型-分类关联表引入 business_type_code（严格非空）
-- 目标：与 EntityCategoryRelation 维度保持一致，支持按业务类型隔离查询与写入。

-- 1) 新增列（先给默认值，保证历史数据可回填）
ALTER TABLE dynamic_model_category_relation
    ADD COLUMN IF NOT EXISTS business_type_code VARCHAR(100);

-- 2) 回填历史数据（兜底值，后续由业务写入真实值）
UPDATE dynamic_model_category_relation
SET business_type_code = 'UNKNOWN'
WHERE business_type_code IS NULL OR business_type_code = '';

-- 3) 严格约束：非空
ALTER TABLE dynamic_model_category_relation
    ALTER COLUMN business_type_code SET NOT NULL;

-- 4) 索引：按业务类型+分类+排序查询
CREATE INDEX IF NOT EXISTS idx_smcr_biz_category_sort_id
    ON dynamic_model_category_relation (business_type_code, category_id, sort, id);

-- 5) 索引：按模型+分类+业务类型做存在性校验/恢复/更新
CREATE INDEX IF NOT EXISTS idx_smcr_model_category_biz
    ON dynamic_model_category_relation (model_id, category_id, business_type_code);

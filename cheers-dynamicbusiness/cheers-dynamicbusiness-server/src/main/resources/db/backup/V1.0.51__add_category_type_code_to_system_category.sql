-- 为分类表增加 category_type_code 字段,用于按 CategoryTypeCode 进行分类树隔离
-- 注意：仅做结构变更,不在此处批量回填数据,避免影响已有环境的数据语义

ALTER TABLE dynamic_category
    ADD COLUMN IF NOT EXISTS category_type_code varchar(64);


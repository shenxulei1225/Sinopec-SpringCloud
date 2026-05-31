-- 删除 Model 表中的冗余字段 business_type_id
-- 原因：Model 已通过 business_type_code 关联业务类型,business_type_id 字段冗余且未被使用
-- 保留 business_type_code 作为唯一关联字段

ALTER TABLE system_model
    DROP COLUMN IF EXISTS business_type_id;

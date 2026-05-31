-- 来源：sql/postgresql/V20260403_001__drop_system_field_ref_model_codes.sql
-- 移除字段定义表中的 ref_model_codes（目标信息由模型字段分配与关联字段库等承载）
-- Flyway 版本：V1.0.101（与 V1.0.100 其它迁移区分，避免同版本冲突）

ALTER TABLE system_field DROP COLUMN IF EXISTS ref_model_codes;

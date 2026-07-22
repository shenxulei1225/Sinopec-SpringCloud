-- 数据库迁移脚本：为指令模板表添加form_id字段
-- 日期：2024-12-29
-- 说明：为emergency_command_template表添加form_id字段，用于关联自定义配置表单ID

-- 为指令模板表添加form_id字段
ALTER TABLE emergency_command_template 
ADD COLUMN IF NOT EXISTS form_id BIGINT;

-- 添加字段注释
COMMENT ON COLUMN emergency_command_template.form_id IS '关联自定义配置表单ID（可选，仅创建时设置，更新时不允许修改）- 用于从模板创建指令时自动继承表单配置。当设置时必须验证表单配置ID的有效性（存在且未删除）';

-- 为form_id创建索引
CREATE INDEX IF NOT EXISTS idx_command_template_form_id ON emergency_command_template(form_id);













































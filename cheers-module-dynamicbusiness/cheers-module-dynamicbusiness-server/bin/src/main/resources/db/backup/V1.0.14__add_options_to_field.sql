-- 为 system_field 表添加 options 字段（枚举类型选项列表）
-- 需求：FR-015-A - 系统必须支持枚举类型字段存储选项列表

-- 添加 options 列
ALTER TABLE system_field ADD COLUMN IF NOT EXISTS options VARCHAR(2048);

-- 添加注释
COMMENT ON COLUMN system_field.options IS '枚举选项列表（ENUM 类型专用,JSON 数组格式）';

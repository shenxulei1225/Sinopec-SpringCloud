-- 数据库迁移脚本：为预案步骤表添加command字段（JSONB类型）
-- 日期：2024-12-26
-- 说明：根据需求规范，预案步骤需要支持关联多个指令，通过command字段（JSONB类型）存储指令ID列表

-- 为预案步骤表添加command字段（JSONB类型），用于存储指令ID列表
ALTER TABLE emergency_plan_step 
ADD COLUMN IF NOT EXISTS command JSONB COMMENT '关联指令ID列表（JSONB数组类型，可选，用于存储多个指令ID，如：[1, 2, 3]）';

-- 为command字段创建GIN索引，提高JSONB查询性能
CREATE INDEX IF NOT EXISTS idx_plan_step_command ON emergency_plan_step USING GIN(command) WHERE command IS NOT NULL;

-- 添加注释说明
COMMENT ON COLUMN emergency_plan_step.command IS '关联指令ID列表（JSONB数组类型，可选）。预案步骤可以关联多个应急指令，执行人员可以快速找到对应的指令内容和执行要求。格式为JSON数组，如：[1, 2, 3]';


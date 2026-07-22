-- 数据库迁移脚本：为预案步骤表添加version字段（乐观锁字段）
-- 日期：2025-12-26
-- 说明：根据2025-12-26规范澄清结果，实现预案步骤的并发控制（乐观锁）

-- 为emergency_plan_step表添加version字段（乐观锁字段）
ALTER TABLE emergency_plan_step 
ADD COLUMN IF NOT EXISTS version INTEGER NOT NULL DEFAULT 0 
COMMENT '版本号（乐观锁字段，用于并发控制）。每次更新时自动递增，用于检测并发修改冲突';

-- 添加注释说明
COMMENT ON COLUMN emergency_plan_step.version IS '版本号（乐观锁字段，用于并发控制）。每次更新时自动递增，用于检测并发修改冲突。如果更新时版本号不匹配，说明记录已被其他用户修改，需要提示用户刷新后重试';







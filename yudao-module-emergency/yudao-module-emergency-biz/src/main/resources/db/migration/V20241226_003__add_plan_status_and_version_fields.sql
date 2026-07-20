-- 数据库迁移脚本：为应急预案表添加状态和版本管理字段
-- 日期：2025-12-26
-- 说明：根据2025-12-26规范澄清结果，实现预案状态管理（草稿、已发布、已停用）和版本管理（为未来扩展预留）

-- 1. 为emergency_plan表添加status字段（如果不存在）
-- 注意：如果status字段已存在，此语句不会报错（使用IF NOT EXISTS）
ALTER TABLE emergency_plan 
ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'DRAFT';

-- 2. 为status字段创建索引，提高查询性能
CREATE INDEX IF NOT EXISTS idx_plan_status ON emergency_plan(status);

-- 3. 为emergency_plan表添加版本相关字段（为未来扩展预留）
ALTER TABLE emergency_plan 
ADD COLUMN IF NOT EXISTS version_number VARCHAR(50),
ADD COLUMN IF NOT EXISTS is_version_locked BOOLEAN DEFAULT FALSE;

-- 4. 为emergency_response表添加预案快照字段（为未来扩展预留）
-- 注意：如果emergency_response表不存在，此语句会报错，需要先创建表
-- ALTER TABLE emergency_response 
-- ADD COLUMN IF NOT EXISTS plan_snapshot JSONB COMMENT '预案快照（JSONB类型，当预案版本锁定时保存）';

-- 添加注释说明
COMMENT ON COLUMN emergency_plan.status IS '预案状态（DRAFT草稿/PUBLISHED已发布/DISABLED已停用）。PO阶段实现简化状态管理，系统设计保留版本化状态管理的扩展能力';
COMMENT ON COLUMN emergency_plan.version_number IS '版本号（可选，用于版本管理）';
COMMENT ON COLUMN emergency_plan.is_version_locked IS '版本是否锁定（锁定后创建响应时使用快照）';








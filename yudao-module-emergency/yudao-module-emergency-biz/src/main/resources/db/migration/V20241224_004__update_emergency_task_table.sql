-- 数据库迁移脚本：统一emergency_task表结构
-- 日期：2024-12-24
-- 说明：按照数据模型规范统一emergency_task表结构，确保与规范一致

-- 1. 重命名和添加字段
DO $$
BEGIN
    -- 如果存在task_no字段，重命名为task_code
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'task_no'
    ) THEN
        ALTER TABLE emergency_task RENAME COLUMN task_no TO task_code;
    END IF;
    
    -- 如果不存在task_code字段，添加它
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'task_code'
    ) THEN
        ALTER TABLE emergency_task ADD COLUMN task_code VARCHAR(50);
        -- 为现有数据生成task_code
        UPDATE emergency_task 
        SET task_code = 'TASK-' || TO_CHAR(create_time, 'YYYYMMDD') || '-' || LPAD(id::TEXT, 6, '0')
        WHERE task_code IS NULL;
        -- 添加唯一约束
        ALTER TABLE emergency_task ALTER COLUMN task_code SET NOT NULL;
        CREATE UNIQUE INDEX IF NOT EXISTS uk_task_code ON emergency_task(task_code);
    END IF;
    
    -- 如果存在name字段，重命名为title
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'name'
    ) THEN
        ALTER TABLE emergency_task RENAME COLUMN name TO title;
    END IF;
    
    -- 如果不存在title字段，添加它
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'title'
    ) THEN
        ALTER TABLE emergency_task ADD COLUMN title VARCHAR(200) NOT NULL DEFAULT '未命名任务';
    END IF;
    
    -- 添加content字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'content'
    ) THEN
        ALTER TABLE emergency_task ADD COLUMN content TEXT;
    END IF;
    
    -- 重命名时间字段
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'actual_start_time'
    ) THEN
        ALTER TABLE emergency_task RENAME COLUMN actual_start_time TO start_time;
    END IF;
    
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'actual_end_time'
    ) THEN
        ALTER TABLE emergency_task RENAME COLUMN actual_end_time TO complete_time;
    END IF;
    
    -- 删除不需要的字段（如果存在）
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'scheduled_start_time'
    ) THEN
        ALTER TABLE emergency_task DROP COLUMN scheduled_start_time;
    END IF;
    
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'planned_start_time'
    ) THEN
        ALTER TABLE emergency_task DROP COLUMN planned_start_time;
    END IF;
    
    -- 添加priority字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'priority'
    ) THEN
        ALTER TABLE emergency_task ADD COLUMN priority VARCHAR(20) DEFAULT 'NORMAL';
    END IF;
    
    -- 添加is_key字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'is_key'
    ) THEN
        ALTER TABLE emergency_task ADD COLUMN is_key BOOLEAN DEFAULT FALSE;
    END IF;
    
    -- 添加assignee字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'assignee'
    ) THEN
        ALTER TABLE emergency_task ADD COLUMN assignee VARCHAR(100);
    END IF;
    
    -- 添加due_time字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'due_time'
    ) THEN
        ALTER TABLE emergency_task ADD COLUMN due_time TIMESTAMP;
    END IF;
    
    -- 添加time_limit字段（如果不存在）
    -- 执行时限（分钟），从预案步骤传递，用于计算due_time和超时告警
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'time_limit'
    ) THEN
        ALTER TABLE emergency_task ADD COLUMN time_limit INTEGER;
    END IF;
    
    -- 添加actual_duration字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'actual_duration'
    ) THEN
        ALTER TABLE emergency_task ADD COLUMN actual_duration INTEGER;
    END IF;
    
    -- 重命名execution_info为execution_records（如果存在）
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'execution_info'
    ) THEN
        ALTER TABLE emergency_task RENAME COLUMN execution_info TO execution_records;
    END IF;
    
    -- 添加execution_records字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'execution_records'
    ) THEN
        ALTER TABLE emergency_task ADD COLUMN execution_records JSONB;
    END IF;
    
    -- 添加version字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'version'
    ) THEN
        ALTER TABLE emergency_task ADD COLUMN version INTEGER DEFAULT 0;
    END IF;
    
    -- 删除不需要的字段
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'parent_id'
    ) THEN
        ALTER TABLE emergency_task DROP COLUMN parent_id;
    END IF;
    
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'plan_action_id'
    ) THEN
        ALTER TABLE emergency_task DROP COLUMN plan_action_id;
    END IF;
    
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'location'
    ) THEN
        ALTER TABLE emergency_task DROP COLUMN location;
    END IF;
    
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'allocation_info'
    ) THEN
        ALTER TABLE emergency_task DROP COLUMN allocation_info;
    END IF;
    
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'responsible_role'
    ) THEN
        ALTER TABLE emergency_task DROP COLUMN responsible_role;
    END IF;
    
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'responsible_user_id'
    ) THEN
        ALTER TABLE emergency_task DROP COLUMN responsible_user_id;
    END IF;
    
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'resources'
    ) THEN
        ALTER TABLE emergency_task DROP COLUMN resources;
    END IF;
    
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'contacts'
    ) THEN
        ALTER TABLE emergency_task DROP COLUMN contacts;
    END IF;
    
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'emergency_task' 
        AND column_name = 'terminated'
    ) THEN
        ALTER TABLE emergency_task DROP COLUMN terminated;
    END IF;
END $$;

-- 2. 更新字段注释
COMMENT ON COLUMN emergency_task.task_code IS '任务编号（唯一，自动生成，格式：TASK-YYYYMMDD-XXXXXX）';
COMMENT ON COLUMN emergency_task.title IS '任务标题';
COMMENT ON COLUMN emergency_task.content IS '任务内容';
COMMENT ON COLUMN emergency_task.start_time IS '开始时间';
COMMENT ON COLUMN emergency_task.complete_time IS '完成时间';
COMMENT ON COLUMN emergency_task.due_time IS '截止时间';
COMMENT ON COLUMN emergency_task.time_limit IS '执行时限（分钟，从预案步骤传递，用于计算due_time和超时告警）';
COMMENT ON COLUMN emergency_task.actual_duration IS '实际耗时（分钟）';
COMMENT ON COLUMN emergency_task.execution_records IS '执行记录（JSON数组）';
COMMENT ON COLUMN emergency_task.priority IS '优先级（LOW/NORMAL/HIGH/URGENT）';
COMMENT ON COLUMN emergency_task.is_key IS '是否关键任务';
COMMENT ON COLUMN emergency_task.assignee IS '分配人';
COMMENT ON COLUMN emergency_task.version IS '乐观锁版本号';

-- 3. 创建索引（如果不存在）
CREATE UNIQUE INDEX IF NOT EXISTS uk_task_code ON emergency_task(task_code);
CREATE INDEX IF NOT EXISTS idx_task_event_id ON emergency_task(event_id);
CREATE INDEX IF NOT EXISTS idx_task_response_id ON emergency_task(response_id);
CREATE INDEX IF NOT EXISTS idx_task_plan_step_id ON emergency_task(plan_step_id);
CREATE INDEX IF NOT EXISTS idx_task_status ON emergency_task(status);
CREATE INDEX IF NOT EXISTS idx_task_stage ON emergency_task(stage);


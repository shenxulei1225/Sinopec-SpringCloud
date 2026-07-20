-- 数据库迁移脚本：同步emergency_plan_step表结构与实体类
-- 日期：2024-12-26
-- 说明：根据EmergencyPlanStepDO实体类，确保数据库表结构完全匹配

-- 1. 确保所有必需字段存在
DO $$
BEGIN
    -- 检查并添加plan_id字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'plan_id'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN plan_id BIGINT NOT NULL;
    END IF;

    -- 检查并添加plan_level字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'plan_level'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN plan_level VARCHAR(10);
    END IF;

    -- 检查并添加parent_id字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'parent_id'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN parent_id BIGINT;
    END IF;

    -- 检查并添加step_order字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'step_order'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN step_order INTEGER;
    END IF;

    -- 检查并添加step_title字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'step_title'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN step_title VARCHAR(200);
    END IF;

    -- 检查并添加step_stage字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'step_stage'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN step_stage VARCHAR(50);
    END IF;

    -- 检查并添加step_description字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'step_description'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN step_description TEXT;
    END IF;

    -- 检查并添加scheduled_start_time字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'scheduled_start_time'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN scheduled_start_time INTEGER DEFAULT 0;
    END IF;

    -- 检查并添加responsible_post_id字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'responsible_post_id'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN responsible_post_id BIGINT;
    END IF;

    -- 检查并添加responsible_dept_id字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'responsible_dept_id'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN responsible_dept_id BIGINT;
    END IF;

    -- 检查并添加responsible_user_id字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'responsible_user_id'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN responsible_user_id BIGINT;
    END IF;

    -- 检查并添加name字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'name'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN name VARCHAR(200);
    END IF;

    -- 检查并添加responsible_role字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'responsible_role'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN responsible_role VARCHAR(200);
    END IF;

    -- 检查并添加resources字段（如果不存在，类型为TEXT以匹配String类型）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'resources'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN resources TEXT;
    ELSE
        -- 如果字段存在但类型不是TEXT，则修改类型
        IF EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'emergency_plan_step' 
            AND column_name = 'resources' 
            AND data_type != 'text'
        ) THEN
            ALTER TABLE emergency_plan_step ALTER COLUMN resources TYPE TEXT USING resources::text;
        END IF;
    END IF;

    -- 检查并添加contacts字段（如果不存在，类型为TEXT以匹配String类型）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'contacts'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN contacts TEXT;
    ELSE
        -- 如果字段存在但类型不是TEXT，则修改类型
        IF EXISTS (
            SELECT 1 FROM information_schema.columns 
            WHERE table_name = 'emergency_plan_step' 
            AND column_name = 'contacts' 
            AND data_type != 'text'
        ) THEN
            ALTER TABLE emergency_plan_step ALTER COLUMN contacts TYPE TEXT USING contacts::text;
        END IF;
    END IF;

    -- 检查并添加planned_start_time字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'planned_start_time'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN planned_start_time TIMESTAMP;
    END IF;

    -- 检查并添加command字段（JSONB类型，如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'command'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN command JSONB;
    END IF;

    -- 检查并添加time_limit字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'time_limit'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN time_limit INTEGER;
    END IF;

    -- 检查并添加version字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'version'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN version INTEGER NOT NULL DEFAULT 0;
    END IF;

    -- 检查并添加tenant_id字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'tenant_id'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN tenant_id BIGINT DEFAULT 0;
    END IF;

    -- 检查并添加creator字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'creator'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN creator VARCHAR(64) DEFAULT '';
    END IF;

    -- 检查并添加updater字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'updater'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN updater VARCHAR(64) DEFAULT '';
    END IF;

    -- 检查并添加create_time字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'create_time'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;

    -- 检查并添加update_time字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'update_time'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;

    -- 检查并添加deleted字段（如果不存在）
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'emergency_plan_step' AND column_name = 'deleted'
    ) THEN
        ALTER TABLE emergency_plan_step ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;
    END IF;
END $$;

-- 2. 确保所有索引存在
CREATE INDEX IF NOT EXISTS idx_plan_step_plan ON emergency_plan_step(plan_id, tenant_id);
CREATE INDEX IF NOT EXISTS idx_plan_step_level ON emergency_plan_step(plan_id, plan_level, tenant_id);
CREATE INDEX IF NOT EXISTS idx_plan_step_parent ON emergency_plan_step(parent_id);
CREATE INDEX IF NOT EXISTS idx_plan_step_command ON emergency_plan_step USING GIN(command) WHERE command IS NOT NULL;

-- 3. 添加字段注释
COMMENT ON COLUMN emergency_plan_step.plan_id IS '关联预案ID，指向所属应急预案的ID';
COMMENT ON COLUMN emergency_plan_step.plan_level IS '预案级别，标识步骤所属的应急预案级别';
COMMENT ON COLUMN emergency_plan_step.parent_id IS '父步骤ID，用于构建步骤的层级结构，支持多级步骤嵌套';
COMMENT ON COLUMN emergency_plan_step.step_order IS '步骤序号，在同级步骤中的执行顺序编号，从1开始递增';
COMMENT ON COLUMN emergency_plan_step.step_title IS '步骤标题，步骤的简短标题名称';
COMMENT ON COLUMN emergency_plan_step.step_stage IS '步骤阶段，标识步骤所属的应急处置阶段';
COMMENT ON COLUMN emergency_plan_step.step_description IS '步骤描述，对步骤的详细说明';
COMMENT ON COLUMN emergency_plan_step.scheduled_start_time IS '计划启动时间（分钟），步骤计划从应急事件发生后多少分钟开始执行';
COMMENT ON COLUMN emergency_plan_step.responsible_post_id IS '负责人岗位ID，执行该步骤负责人的岗位标识';
COMMENT ON COLUMN emergency_plan_step.responsible_dept_id IS '负责人部门ID，执行该步骤负责人的部门标识';
COMMENT ON COLUMN emergency_plan_step.responsible_user_id IS '负责人用户ID，执行该步骤的具体负责人用户标识';
COMMENT ON COLUMN emergency_plan_step.name IS '任务名称，步骤的别名或更详细的名称标识';
COMMENT ON COLUMN emergency_plan_step.responsible_role IS '责任角色，执行该步骤所需承担的具体角色描述';
COMMENT ON COLUMN emergency_plan_step.resources IS '资源，执行该步骤所需的资源清单（TEXT类型）';
COMMENT ON COLUMN emergency_plan_step.contacts IS '联系人，步骤执行过程中的关键联系人信息（TEXT类型）';
COMMENT ON COLUMN emergency_plan_step.planned_start_time IS '计划开始时间，步骤的精确计划开始时间点';
COMMENT ON COLUMN emergency_plan_step.command IS '关联指令ID列表（JSONB数组类型），用于存储多个指令ID';
COMMENT ON COLUMN emergency_plan_step.time_limit IS '执行时限（分钟），建议的执行时限，范围1-1440分钟';
COMMENT ON COLUMN emergency_plan_step.version IS '版本号（乐观锁字段），每次更新时自动递增，用于检测并发修改冲突';






-- 数据库迁移脚本：添加新字段
-- 日期：2024-12-24
-- 说明：根据最新规范添加预案步骤、指令、事件表的新字段

-- 1. 为预案步骤表添加commandId和timeLimit字段
ALTER TABLE emergency_plan_step 
ADD COLUMN IF NOT EXISTS command_id BIGINT,
ADD COLUMN IF NOT EXISTS time_limit INT;

-- 添加字段注释
COMMENT ON COLUMN emergency_plan_step.command_id IS '关联指令ID（可选，关联时必须验证指令ID的有效性）';
COMMENT ON COLUMN emergency_plan_step.time_limit IS '执行时限（分钟，可选，范围1-1440分钟，作为强制要求传递给基于该步骤创建的任务）';

-- 为command_id创建索引
CREATE INDEX IF NOT EXISTS idx_plan_step_command_id ON emergency_plan_step(command_id);

-- 2. 为指令表添加formId字段
ALTER TABLE emergency_command 
ADD COLUMN IF NOT EXISTS form_id BIGINT;

-- 添加字段注释
COMMENT ON COLUMN emergency_command.form_id IS '关联自定义配置表单ID（可选，仅创建时设置，更新时不允许修改）- 用于用户使用自定义表单上报数据。当设置时必须验证表单配置ID的有效性（存在且未删除）';

-- 为form_id创建索引
CREATE INDEX IF NOT EXISTS idx_command_form_id ON emergency_command(form_id);

-- 3. 将事件表的eventType字段类型改为BIGINT（存储分类ID）
-- 注意：如果表中已有数据，需要先进行数据迁移
-- 这里先检查字段类型，如果不是BIGINT则修改
DO $$
DECLARE
    col_is_nullable VARCHAR(10);
    col_data_type VARCHAR(50);
BEGIN
    -- 检查event_type字段是否存在及其属性
    SELECT data_type, is_nullable INTO col_data_type, col_is_nullable
        FROM information_schema.columns 
        WHERE table_name = 'emergency_event' 
    AND column_name = 'event_type';
    
    -- 如果字段不存在，跳过
    IF col_data_type IS NULL THEN
        RAISE NOTICE 'event_type字段不存在，跳过类型转换';
        RETURN;
    END IF;
    
    -- 如果字段类型已经是bigint，跳过
    IF col_data_type = 'bigint' THEN
        RAISE NOTICE 'event_type字段已经是BIGINT类型，无需转换';
        RETURN;
    END IF;
    
    -- 如果有 NOT NULL 约束，先移除（允许字段为空）
    IF col_is_nullable = 'NO' THEN
        RAISE NOTICE '移除event_type字段的NOT NULL约束';
        ALTER TABLE emergency_event 
        ALTER COLUMN event_type DROP NOT NULL;
    END IF;
    
    -- 修改字段类型：将原字段值转换为NULL（因为从字符串类型无法直接转换为分类ID）
    -- 注意：如果需要保留数据，应该先使用 V20241224_002__migrate_event_type_to_category_id.sql 进行数据迁移
    RAISE NOTICE '开始转换event_type字段类型从 % 到 BIGINT', col_data_type;
        ALTER TABLE emergency_event 
        ALTER COLUMN event_type TYPE BIGINT USING NULL;
        
        -- 添加注释说明
        COMMENT ON COLUMN emergency_event.event_type IS '事件分类ID（BIGINT类型，存储分类ID）。关联基础服务的分类管理模块，支持多级分类。创建时可选（允许为空），更新时必须设置并验证有效性（存在且未删除）';
    
    RAISE NOTICE 'event_type字段类型已成功转换为BIGINT';
END $$;

-- 4. 为event_type创建索引（如果不存在）
CREATE INDEX IF NOT EXISTS idx_event_type ON emergency_event(event_type);

-- 5. 标记event_sub_type字段为废弃（添加注释）
COMMENT ON COLUMN emergency_event.event_sub_type IS '细分类型（已废弃，保留字段仅用于兼容历史数据，新数据不应使用此字段）';





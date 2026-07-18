-- 更新 dynamic_entity_field_index 表以符合 BaseDO 规范
-- 添加 creator、updater、deleted 字段（如果不存在）
-- 注意：如果表已经有 create_time/update_time 字段,则跳过重命名

-- 尝试重命名时间字段（如果 created_at 存在）
-- 使用 DO $$ 块来处理列可能不存在的情况
DO $$
BEGIN
    -- 检查 created_at 列是否存在,如果存在则重命名
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'dynamic_entity_field_index' AND column_name = 'created_at') THEN
        ALTER TABLE dynamic_entity_field_index RENAME COLUMN created_at TO create_time;
    END IF;
    
    -- 检查 updated_at 列是否存在,如果存在则重命名
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'dynamic_entity_field_index' AND column_name = 'updated_at') THEN
        ALTER TABLE dynamic_entity_field_index RENAME COLUMN updated_at TO update_time;
    END IF;
END $$;

-- 添加 creator 字段
ALTER TABLE dynamic_entity_field_index ADD COLUMN IF NOT EXISTS creator VARCHAR(64) DEFAULT '';

-- 添加 updater 字段
ALTER TABLE dynamic_entity_field_index ADD COLUMN IF NOT EXISTS updater VARCHAR(64) DEFAULT '';

-- 添加 deleted 字段
ALTER TABLE dynamic_entity_field_index ADD COLUMN IF NOT EXISTS deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- 添加 create_time 字段（如果不存在）
ALTER TABLE dynamic_entity_field_index ADD COLUMN IF NOT EXISTS create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 添加 update_time 字段（如果不存在）
ALTER TABLE dynamic_entity_field_index ADD COLUMN IF NOT EXISTS update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 添加注释
COMMENT ON COLUMN dynamic_entity_field_index.create_time IS '创建时间';
COMMENT ON COLUMN dynamic_entity_field_index.update_time IS '更新时间';
COMMENT ON COLUMN dynamic_entity_field_index.creator IS '创建者';
COMMENT ON COLUMN dynamic_entity_field_index.updater IS '更新者';
COMMENT ON COLUMN dynamic_entity_field_index.deleted IS '是否删除';

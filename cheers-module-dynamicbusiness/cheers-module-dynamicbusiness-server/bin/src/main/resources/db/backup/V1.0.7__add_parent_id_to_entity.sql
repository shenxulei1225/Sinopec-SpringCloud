-- =====================================================
-- 基础服务模块数据库迁移脚本
-- 版本: V1.0.7
-- 日期: 2026-01-04
-- 描述: 为 system_entity 表添加 parent_id 字段,支持实体层级关系
-- =====================================================

-- 检查字段是否存在,如果不存在则添加
DO $$
BEGIN
    -- 检查 parent_id 字段是否存在
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = 'system_entity' 
        AND column_name = 'parent_id'
    ) THEN
        -- 添加 parent_id 字段
        ALTER TABLE system_entity 
        ADD COLUMN parent_id BIGINT;
        
        -- 添加字段注释
        COMMENT ON COLUMN system_entity.parent_id IS '父实体ID（用于支持实体层级关系）,为null或0表示根实体';
        
        -- 创建索引以提高查询性能
        CREATE INDEX IF NOT EXISTS idx_entity_parent_id 
            ON system_entity(parent_id);
        
        -- 创建复合索引（用于查询某个父实体下的所有子实体）
        CREATE INDEX IF NOT EXISTS idx_entity_parent_deleted 
            ON system_entity(parent_id, deleted);
        
        RAISE NOTICE '成功为 system_entity 表添加 parent_id 字段';
    ELSE
        RAISE NOTICE 'system_entity 表的 parent_id 字段已存在,跳过添加';
    END IF;
END $$;



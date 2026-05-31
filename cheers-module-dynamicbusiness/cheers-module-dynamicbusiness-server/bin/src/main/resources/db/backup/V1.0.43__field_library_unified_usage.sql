-- =====================================================
-- 字段库统一使用规范：fieldId 到 fieldCode 迁移
-- 版本: V1.0.43
-- 描述: 将 EntityDO.customFields 从 fieldId 键迁移到 fieldCode 键,实现业务层面的跨环境一致性
-- =====================================================

-- =====================================================
-- 1. 创建迁移函数（简化版本）
-- =====================================================
CREATE OR REPLACE FUNCTION migrate_custom_fields_to_field_code() RETURNS void AS $$
DECLARE
    entity_record RECORD;
    new_custom_fields jsonb;
    field_code text;
BEGIN
    FOR entity_record IN
        SELECT id, custom_fields
        FROM system_entity
        WHERE custom_fields IS NOT NULL
        AND custom_fields != '{}'
        AND deleted = FALSE
    LOOP
        -- 重构JSON对象,将fieldId键替换为fieldCode键
        SELECT jsonb_object_agg(
            COALESCE(
                (SELECT sf.code FROM system_field sf WHERE sf.id = key::bigint AND sf.deleted = FALSE),
                key
            ),
            value
        ) INTO new_custom_fields
        FROM jsonb_each(entity_record.custom_fields) AS t(key, value);

        -- 更新记录
        UPDATE system_entity
        SET custom_fields = new_custom_fields,
            update_time = CURRENT_TIMESTAMP
        WHERE id = entity_record.id;
    END LOOP;

    RAISE NOTICE 'Migration completed successfully';
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- 2. 执行迁移
-- =====================================================
SELECT migrate_custom_fields_to_field_code();

-- =====================================================
-- 4. 验证迁移结果
-- =====================================================
DO $$
DECLARE
    total_entities bigint;
    entities_with_custom_fields bigint;
    migrated_entities bigint;
    partial_migrated_entities bigint;
    fully_migrated_entities bigint;
BEGIN
    SELECT COUNT(*) INTO total_entities FROM system_entity WHERE deleted = FALSE;
    SELECT COUNT(*) INTO entities_with_custom_fields
    FROM system_entity
    WHERE deleted = FALSE AND custom_fields IS NOT NULL AND custom_fields != '{}';

    -- 完全迁移的实体（只包含fieldCode键）
    SELECT COUNT(*) INTO fully_migrated_entities
    FROM system_entity
    WHERE deleted = FALSE
    AND custom_fields IS NOT NULL
    AND custom_fields != '{}'
    AND custom_fields::text LIKE '%"F-%":%'
    AND custom_fields::text NOT LIKE '%\d+":%';

    -- 部分迁移的实体（同时包含fieldId和fieldCode键）
    SELECT COUNT(*) INTO partial_migrated_entities
    FROM system_entity
    WHERE deleted = FALSE
    AND custom_fields IS NOT NULL
    AND custom_fields != '{}'
    AND custom_fields::text LIKE '%"F-%":%'
    AND custom_fields::text LIKE '%\d+":%';

    -- 总迁移实体数
    migrated_entities := fully_migrated_entities + partial_migrated_entities;

    RAISE NOTICE 'Migration Summary:';
    RAISE NOTICE '  Total entities: %', total_entities;
    RAISE NOTICE '  Entities with custom fields: %', entities_with_custom_fields;
    RAISE NOTICE '  Fully migrated entities (fieldCode only): %', fully_migrated_entities;
    RAISE NOTICE '  Partially migrated entities (mixed keys): %', partial_migrated_entities;
    RAISE NOTICE '  Total migrated entities: %', migrated_entities;
    RAISE NOTICE '  Migration coverage: %%%',
        CASE WHEN entities_with_custom_fields > 0
            THEN ROUND(migrated_entities::numeric / entities_with_custom_fields * 100, 2)::text
            ELSE 'N/A'
        END;
END $$;

-- =====================================================
-- 5. 清理临时资源
-- =====================================================
DROP FUNCTION IF EXISTS migrate_entity_custom_fields();
DROP TABLE IF EXISTS temp_field_id_to_code;

-- =====================================================
-- 6. 重新创建索引（可选,用于优化fieldCode查询）
-- 注意：根据实际使用情况,可能需要调整索引策略
-- =====================================================
-- 注意：索引创建移到单独的迁移脚本,避免事务问题
-- CREATE INDEX IF NOT EXISTS idx_entity_custom_fields_field_codes ON system_entity USING GIN (custom_fields);

-- =====================================================
-- 7. 迁移完成标记
-- =====================================================
INSERT INTO system_data_migration_log (
    migration_type,
    migration_version,
    status,
    start_time,
    end_time,
    total_count,
    success_count,
    details,
    creator
) VALUES (
    'field_library_unified_usage',
    'V1.0.43',
    'SUCCESS',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP,
    (SELECT COUNT(*) FROM system_entity WHERE deleted = FALSE AND custom_fields IS NOT NULL AND custom_fields != '{}'),
    (SELECT COUNT(*) FROM system_entity WHERE deleted = FALSE AND custom_fields::text LIKE '%"F-%":%'),
    (SELECT jsonb_build_object(
        'description', '字段库统一使用规范迁移：fieldId到fieldCode',
        'migration_type', 'data_format_migration',
        'affected_table', 'system_entity',
        'affected_column', 'custom_fields',
        'fully_migrated_count', (SELECT COUNT(*) FROM system_entity WHERE deleted = FALSE AND custom_fields IS NOT NULL AND custom_fields != '{}' AND custom_fields::text LIKE '%"F-%":%' AND custom_fields::text NOT LIKE '%\d+":%'),
        'partially_migrated_count', (SELECT COUNT(*) FROM system_entity WHERE deleted = FALSE AND custom_fields IS NOT NULL AND custom_fields != '{}' AND custom_fields::text LIKE '%"F-%":%' AND custom_fields::text LIKE '%\d+":%'),
        'unmigrated_count', (SELECT COUNT(*) FROM system_entity WHERE deleted = FALSE AND custom_fields IS NOT NULL AND custom_fields != '{}' AND custom_fields::text NOT LIKE '%"F-%":%' AND custom_fields::text LIKE '%\d+":%')
    )),
    'system'
);
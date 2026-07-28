-- 将分类节点绑定表的类型列与 relation 表对齐：只保留 entity_type_code 命名
SET search_path TO dynamicbusiness;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'dynamicbusiness'
          AND table_name = 'dynamic_category_entity_link'
          AND column_name = 'storage_entity_type_code'
    ) THEN
        ALTER TABLE dynamic_category_entity_link
            RENAME COLUMN storage_entity_type_code TO entity_type_code;
    END IF;
END $$;

COMMENT ON COLUMN dynamic_category_entity_link.entity_type_code IS
    '实体类型编码（与 dynamic_entity_category_relation.entity_type_code 命名对齐）；由 entity_model_id → dynamic_model.entity_type_code 回填';

ALTER INDEX IF EXISTS idx_dynamic_category_entity_link_storage_entity
    RENAME TO idx_dynamic_category_entity_link_entity_type;

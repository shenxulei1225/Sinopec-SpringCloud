-- 业务类型基础字段：列表查询规则（同步到该类型下全部模型的字段分配）

SET search_path TO dynamicbusiness;

ALTER TABLE dynamic_entity_type_base_field
    ADD COLUMN IF NOT EXISTS is_searchable BOOLEAN DEFAULT true NOT NULL,
    ADD COLUMN IF NOT EXISTS is_filterable BOOLEAN DEFAULT true NOT NULL,
    ADD COLUMN IF NOT EXISTS is_sortable BOOLEAN DEFAULT true NOT NULL;

UPDATE dynamic_entity_type_base_field
SET is_searchable = true,
    is_filterable = true,
    is_sortable = true
WHERE is_searchable IS NULL
   OR is_filterable IS NULL
   OR is_sortable IS NULL;

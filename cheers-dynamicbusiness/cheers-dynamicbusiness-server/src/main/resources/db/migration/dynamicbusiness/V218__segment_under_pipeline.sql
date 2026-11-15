-- 管线段是管线的下级，不单独建目录。
-- 已有管线目录里增加型号「管线段」。名称用城市缩写，上级选择这条管线。
-- 管道、构筑物、场站上的所属管线段改指向管线目录里的记录。
-- 不补示例段，不在打开页面时写数据。
SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.dynamic_entity_type
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE code = 'pipeline_segment' AND deleted = false;

UPDATE dynamicbusiness.dynamic_model
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'pipeline_segment' AND deleted = false;

UPDATE dynamicbusiness.dynamic_category
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE category_type_code = 'pipeline_segment' AND deleted = false;

UPDATE dynamicbusiness.dynamic_category_type
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE category_type_code = 'pipeline_segment' AND deleted = false;

UPDATE dynamicbusiness.dynamic_entity_type_base_field
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'pipeline_segment' AND deleted = false;

UPDATE dynamicbusiness.dynamic_catalog_role_grant
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE registry_code = 'pipeline_segment' AND deleted = false;

UPDATE dynamicbusiness.dynamic_field
SET provider_code = 'dynamic-entity:pipeline',
    description = '管线目录里的那一段。名称是两个城市的缩写，上级是这条管线。',
    updater = 'pipeline-segment',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'pipeline_segment_id' AND deleted = false;

INSERT INTO dynamicbusiness.dynamic_model (
    code, name, entity_type_code, description, status, sort, creator, updater,
    deleted, tenant_id, governance_status
)
SELECT 'MODEL-line-segment', '管线段', 'pipeline',
       '一条管线下面、从一座城市到另一座城市的一段。名称用两个城市的缩写，上级选择这条管线。',
       1, 5, 'pipeline-segment', 'pipeline-segment', false, src.tenant_id, 'COMPANY'
FROM dynamicbusiness.dynamic_model src
WHERE src.deleted = false
  AND src.entity_type_code = 'pipeline'
  AND src.name = '天然气管道线路'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model own
      WHERE own.tenant_id = src.tenant_id
        AND own.code = 'MODEL-line-segment'
        AND own.deleted = false
  );

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
    creator, updater, deleted, tenant_id, model_code, field_code, collection_point,
    create_visible, edit_visible, detail_visible, field_source
)
SELECT
    segment.id, src.field_id,
    CASE WHEN src.field_code = 'parentId' THEN true ELSE src.required END,
    src.is_searchable, src.is_filterable, src.is_sortable, src.sort,
    'pipeline-segment', 'pipeline-segment', false, segment.tenant_id, segment.code, src.field_code, false,
    src.create_visible, src.edit_visible, src.detail_visible, src.field_source
FROM dynamicbusiness.dynamic_model segment
JOIN dynamicbusiness.dynamic_model line
  ON line.tenant_id = segment.tenant_id
 AND line.entity_type_code = 'pipeline'
 AND line.name = '天然气管道线路'
 AND line.deleted = false
JOIN dynamicbusiness.dynamic_model_field_assignment src
  ON src.model_id = line.id AND src.tenant_id = line.tenant_id AND src.deleted = false
WHERE segment.deleted = false
  AND segment.code = 'MODEL-line-segment'
  AND segment.entity_type_code = 'pipeline'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment own
      WHERE own.tenant_id = segment.tenant_id
        AND own.model_id = segment.id
        AND own.field_code = src.field_code
        AND own.deleted = false
  );

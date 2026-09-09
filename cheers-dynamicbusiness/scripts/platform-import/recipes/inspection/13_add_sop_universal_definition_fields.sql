-- ============================================================================
-- recipes/inspection · 13 SOP 通用定义字段（模型字段）
--
-- 目标：按“模型字段 + 结构化数据”承载 SOP 通用定义，不再为对象/内容映射新建专表。
-- 字段编码：
--   - sop_object_category_type_code  对象分类类型码（如 equipment）
--   - sop_content_entity_type_code   内容实体类型码（如 inspection_item）
--   - sop_object_content_pack_json   对象分类 -> 内容集合结构 JSON
-- ============================================================================

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  v_model_id bigint;
BEGIN
  SELECT id INTO v_model_id
  FROM dynamic_model
  WHERE deleted = FALSE
    AND tenant_id = v_tenant
    AND code = 'sop'
  ORDER BY id
  LIMIT 1;

  IF v_model_id IS NULL THEN
    RAISE EXCEPTION '缺少 SOP 型号（dynamic_model.code=sop），请先执行 SOP seed';
  END IF;

  INSERT INTO dynamic_field (
    code, name, type, unit, description, source, status, max_relations,
    index_strategy, options, provider_code, semantic_type, tenant_id, creator
  )
  SELECT
    v.code, v.name, v.data_type, NULL, v.description, 'USER_ADDED', 1, 1,
    'NONE', NULL, NULL, v.code, v_tenant, 'recipe-inspection-seed'
  FROM (
    VALUES
      ('sop_object_category_type_code', 'SOP对象分类类型码', 'TEXT', '该SOP定义使用的对象分类类型码（如 equipment）'),
      ('sop_content_entity_type_code', 'SOP内容实体类型码', 'TEXT', '该SOP定义使用的内容实体类型码（如 inspection_item）'),
      ('sop_object_content_pack_json', 'SOP对象内容定义', 'TEXT', '对象分类 -> 内容集合结构化JSON')
  ) AS v(code, name, data_type, description)
  ON CONFLICT (code, tenant_id) WHERE deleted = false
  DO UPDATE SET
    name = EXCLUDED.name,
    type = EXCLUDED.type,
    description = EXCLUDED.description,
    updater = 'recipe-inspection-seed',
    update_time = CURRENT_TIMESTAMP;

  INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, model_code, field_code,
    required, is_searchable, is_filterable, is_sortable, sort,
    default_value, target_entity_type, field_source, tenant_id, creator
  )
  SELECT
    v_model_id,
    f.id,
    'sop',
    v.field_code,
    FALSE, TRUE, TRUE, FALSE, v.sort_no,
    v.default_value,
    NULL,
    'USER_ADDED',
    v_tenant,
    'recipe-inspection-seed'
  FROM dynamic_field f
  JOIN (
    VALUES
      ('sop_object_category_type_code', 801, 'equipment'),
      ('sop_content_entity_type_code', 802, 'inspection_item'),
      ('sop_object_content_pack_json', 803, '{"version":1,"objectCategoryTypeCode":"equipment","contentEntityTypeCode":"inspection_item","objectMappings":[]}')
  ) AS v(field_code, sort_no, default_value)
    ON f.code = v.field_code
   AND f.tenant_id = v_tenant
   AND f.deleted = FALSE
  ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
  DO UPDATE SET
    model_id = EXCLUDED.model_id,
    field_id = EXCLUDED.field_id,
    required = EXCLUDED.required,
    sort = EXCLUDED.sort,
    default_value = EXCLUDED.default_value,
    field_source = EXCLUDED.field_source,
    updater = 'recipe-inspection-seed',
    update_time = CURRENT_TIMESTAMP;

  RAISE NOTICE 'SOP 通用定义字段已就绪：sop_object_category_type_code / sop_content_entity_type_code / sop_object_content_pack_json';
END $$;

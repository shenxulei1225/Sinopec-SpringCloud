-- ============================================================================
-- action · 15 适用手段：字段库枚举是唯一词表
-- 负责：execution_means 改为 ENUM + 选项；检查项 Tab 改读该字段；
--       巡检设备分配同一字段（多选）；巡检设备管理栏改回浏览分类。
-- 不管：动作内容树 category_type_code=action；应急响应等级；不猜已有设备勾了哪些方式。
-- 禁止：再用执行方式分类当词表；新增选项时不要自动勾到历史动作/设备。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 字段库：适用手段 = 下拉枚举。选项身份是编码，名称可改。
UPDATE dynamic_field
SET
  type = 'ENUM',
  name = '适用手段',
  description = '执行方式词表。动作适用手段、检查项方法 Tab、任务选方式、设备多选都读这里的选项。禁止再用分类树当词表。',
  options = CASE
    WHEN type IS DISTINCT FROM 'ENUM'
      OR options IS NULL
      OR btrim(options) = ''
      OR btrim(options) = '[]'
    THEN '[{"label":"人工","value":"MANUAL"},{"label":"无人机","value":"UAV"},{"label":"机器人","value":"ROBOT"},{"label":"固定摄像机","value":"FIXED_CAMERA"}]'
    ELSE options
  END,
  deleted = false,
  status = 1,
  updater = 'seed-execution-means-enum',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND code = 'execution_means';

-- 动作仍按 JSON 数组存专用列，不改动作基础字段类型。
UPDATE dynamic_entity_type_base_field
SET
  data_type = 'JSON',
  field_name = '适用手段',
  type_config = '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb,
  updater = 'seed-execution-means-enum',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND entity_type_code = 'action'
  AND field_code = 'execution_means';

-- 2) 检查项步骤树包：Tab 读枚举字段，不再读执行方式分类。
UPDATE dynamic_entity_type_base_field bf
SET type_config = jsonb_strip_nulls(
      (
        COALESCE(bf.type_config, '{}'::jsonb)
        - 'packFacetCategoryTypeCode'
        - 'tabCategoryTypeCode'
        - 'packMethods'
      )
      || jsonb_build_object(
           'editorKind', 'step_tree_pack',
           'tabEnumFieldCode', 'execution_means'
         )
    ),
    updater = 'seed-execution-means-enum',
    update_time = CURRENT_TIMESTAMP
WHERE bf.deleted = false
  AND bf.entity_type_code = 'inspection_item'
  AND bf.field_code IN ('step_tree_json', 'action_tree_json');

-- 3) 巡检设备：同一字段，多选。不猜已有设备该勾哪些方式。
INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, is_searchable, is_filterable, is_sortable,
  tenant_id, creator
)
SELECT
  'equipment',
  f.id,
  'execution_means',
  '适用手段',
  'ENUM',
  false,
  NULL,
  '这台设备能用哪些执行方式，可多选。任务第 3 步只列包含当前方式的设备。',
  '{"valueShape":"array","createVisible":true,"editVisible":true,"detailVisible":true}'::jsonb,
  8,
  1,
  false,
  true,
  false,
  1,
  'seed-execution-means-enum'
FROM dynamic_field f
WHERE f.code = 'execution_means'
  AND f.deleted = false
  AND f.tenant_id = 1
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  library_field_id = EXCLUDED.library_field_id,
  field_name = EXCLUDED.field_name,
  data_type = EXCLUDED.data_type,
  required = EXCLUDED.required,
  default_value = EXCLUDED.default_value,
  description = EXCLUDED.description,
  type_config = EXCLUDED.type_config,
  sort_order = EXCLUDED.sort_order,
  status = 1,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  deleted = false,
  updater = 'seed-execution-means-enum',
  update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  field_source, tenant_id, creator
)
SELECT
  m.id,
  m.code,
  f.id,
  'execution_means',
  false,
  false,
  true,
  false,
  8,
  'BASE',
  m.tenant_id,
  'seed-execution-means-enum'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'execution_means'
 AND f.deleted = false
 AND f.tenant_id = 1
WHERE m.entity_type_code = 'equipment'
  AND m.deleted = false
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  field_id = EXCLUDED.field_id,
  model_id = EXCLUDED.model_id,
  required = EXCLUDED.required,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  field_source = EXCLUDED.field_source,
  deleted = false,
  updater = 'seed-execution-means-enum',
  update_time = CURRENT_TIMESTAMP;

-- 4) 巡检设备管理数据页：执行方式分类栏改回浏览分类，不再当词表。
UPDATE dm_data_tab_layout
SET
  column_meta = jsonb_set(
    jsonb_set(COALESCE(column_meta, '{}'::jsonb), '{label}', '"巡检设备管理"'),
    '{categoryTypeCode}',
    '"patrol_equipment"'
  ),
  updater = 'seed-execution-means-enum',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND layout_id = 21
  AND column_kind = 'CATEGORY'
  AND column_meta->>'categoryTypeCode' = 'execution_means';

UPDATE dm_data_tab_column_relation
SET
  from_type_code = 'patrol_equipment',
  updater = 'seed-execution-means-enum',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND layout_id = 21
  AND from_type_code = 'execution_means';

-- 5) 旧分类种类不再当词表。不删行，避免误伤历史挂靠；运行时不再读它。
UPDATE dynamic_category_type
SET
  description = '已停用：执行方式词表改在字段库枚举 execution_means。分类树改名称不会再影响动作库和检查库。',
  updater = 'seed-execution-means-enum',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND category_type_code = 'execution_means';

DELETE FROM model_crud_form_definition
WHERE entity_type_code IN ('inspection_item', 'equipment', 'patrol_equipment');

-- 开发联调 · inspection_item 检查项模板样例
-- 用途：任务 patrol_task 通过 inspection_item_ids 引用

SET search_path TO dynamicbusiness;

INSERT INTO ent_inspection_item (
  id, entity_type_code, model_id, name, code,
  tenant_id, creator, tree_path, sort, status, deleted,
  attrs, custom_fields
)
OVERRIDING SYSTEM VALUE
SELECT
  v.id, 'inspection_item', m.id, v.name, v.code,
  1, 'seed', v.tree_path, v.sort, 1, false,
  '{"is_template": true}'::jsonb,
  v.custom_fields
FROM (
  VALUES
    (
      101, 'INS-ITEM-101', '储罐区外观', '/101/', 1,
      jsonb_build_object(
        'FLD-INS-001', '储罐区外观与标识',
        'FLD-INS-002', '罐体、管线标识清晰完整，无脱落褪色'
      )
    ),
    (
      102, 'INS-ITEM-102', '防火堤完好', '/102/', 2,
      jsonb_build_object(
        'FLD-INS-001', '防火堤完好性',
        'FLD-INS-002', '堤体无开裂渗漏，排水阀可正常操作'
      )
    )
) AS v(id, code, name, tree_path, sort, custom_fields)
JOIN dynamic_model m
  ON m.code = 'inspection_item' AND m.entity_type_code = 'inspection_item'
 AND m.deleted = false AND m.tenant_id = 1
ON CONFLICT (id) DO UPDATE SET
  model_id = EXCLUDED.model_id,
  name = EXCLUDED.name,
  code = EXCLUDED.code,
  attrs = EXCLUDED.attrs,
  custom_fields = EXCLUDED.custom_fields,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

SELECT setval(
  pg_get_serial_sequence('dynamicbusiness.ent_inspection_item', 'id'),
  GREATEST((SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_inspection_item), 102)
);

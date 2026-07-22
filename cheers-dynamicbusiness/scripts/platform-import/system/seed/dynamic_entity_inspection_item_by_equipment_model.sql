-- 型号标准检查项 · 网络摄像机（equipment dynamic_model.id = 5）
-- 正式导入：platform-import / 手工 psql；勿放入 Flyway。
-- 约定：ent_inspection_item.model_id = 被巡检对象型号 id（任务创建按型号拉清单）
-- 可重复执行（按 id 幂等）。

SET search_path TO dynamicbusiness;

INSERT INTO ent_inspection_item (
  id, tenant_id, entity_type_code, model_id, name, code,
  status, parent_id, deleted, sort, attrs, custom_fields,
  creator, updater
)
OVERRIDING SYSTEM VALUE
VALUES
  (201, 1, 'inspection_item', 5, '镜头清洁、无遮挡', 'INS-ITEM-CAM-01', 1, 0, false, 1, '{"is_template": true}'::jsonb, '{}'::jsonb, 'seed', 'seed'),
  (202, 1, 'inspection_item', 5, '视频画面正常、无花屏黑屏', 'INS-ITEM-CAM-02', 1, 0, false, 2, '{"is_template": true}'::jsonb, '{}'::jsonb, 'seed', 'seed'),
  (203, 1, 'inspection_item', 5, '云台 / 变焦动作正常', 'INS-ITEM-CAM-03', 1, 0, false, 3, '{"is_template": true}'::jsonb, '{}'::jsonb, 'seed', 'seed'),
  (204, 1, 'inspection_item', 5, '补光灯 / 红外工作正常', 'INS-ITEM-CAM-04', 1, 0, false, 4, '{"is_template": true}'::jsonb, '{}'::jsonb, 'seed', 'seed'),
  (205, 1, 'inspection_item', 5, '设备外观完好、支架紧固', 'INS-ITEM-CAM-05', 1, 0, false, 5, '{"is_template": true}'::jsonb, '{}'::jsonb, 'seed', 'seed')
ON CONFLICT (id) DO UPDATE SET
  tenant_id = EXCLUDED.tenant_id,
  entity_type_code = EXCLUDED.entity_type_code,
  model_id = EXCLUDED.model_id,
  name = EXCLUDED.name,
  code = EXCLUDED.code,
  status = EXCLUDED.status,
  deleted = false,
  sort = EXCLUDED.sort,
  attrs = EXCLUDED.attrs,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

SELECT setval(
  pg_get_serial_sequence('dynamicbusiness.ent_inspection_item', 'id'),
  GREATEST(COALESCE((SELECT MAX(id) FROM dynamicbusiness.ent_inspection_item), 1), 205)
);

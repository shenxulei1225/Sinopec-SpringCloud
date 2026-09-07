-- V93: 详情跟随语义独立为 detail_follow，停止复用 filter
-- 目的：保持用户连线操作不变（仍点筛选），但内部契约拆分，避免详情跟随与列表筛选混淆

SET search_path TO dynamicbusiness, public;

UPDATE dm_data_tab_column_relation
SET relation_meta = jsonb_set(
  COALESCE(relation_meta, '{}'::jsonb),
  '{edgeAction}',
  '"detail_follow"'::jsonb,
  true
)
WHERE relation_kind IN ('CATEGORY_DETAIL', 'MODEL_DETAIL', 'ENTITY_DETAIL')
  AND COALESCE(relation_meta->>'edgeAction', '') = 'filter'
  AND deleted = FALSE;


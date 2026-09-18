-- ==============================================================================
-- 栏间关系：统一修复为 edgeAction（写路径修复，非读路径推断）
-- 口径：
--   1) 详情边（CATEGORY_DETAIL / ENTITY_DETAIL）固定 detail_follow
--   2) 其余：含写交互 → write；否则 filter
--   3) 清理旧键 edgeRole，避免双键并存
-- ==============================================================================

SET search_path TO dynamicbusiness;

UPDATE dm_data_tab_column_relation
SET relation_meta =
      (
        COALESCE(relation_meta, '{}'::jsonb)
        || jsonb_build_object(
          'edgeAction',
          CASE
            WHEN relation_kind IN ('CATEGORY_DETAIL', 'ENTITY_DETAIL') THEN 'detail_follow'
            WHEN relation_meta->'enabledInteractions' ?| ARRAY[
              'dragAssociate', 'unbindChecked', 'checkboxSet', 'refFieldPick', 'ownershipWrite'
            ] THEN 'write'
            ELSE 'filter'
          END
        )
      ) - 'edgeRole',
  updater = 'patch-edge-action',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (
    relation_meta IS NULL
    OR relation_meta->>'edgeAction' IS NULL
    OR relation_meta->>'edgeAction' NOT IN ('filter', 'write', 'detail_follow')
    OR relation_meta ? 'edgeRole'
  );

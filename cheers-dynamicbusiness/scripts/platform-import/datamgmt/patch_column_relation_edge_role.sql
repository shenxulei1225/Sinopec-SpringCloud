-- ==============================================================================
-- 栏间关系：补齐历史行缺失的 edgeRole（写路径修复，非读路径推断）
-- 口径：仅 browseFilter → filter；含写交互 → write；禁止运行时再猜
-- ==============================================================================

SET search_path TO dynamicbusiness;

UPDATE dm_data_tab_column_relation
SET relation_meta = COALESCE(relation_meta, '{}'::jsonb)
  || jsonb_build_object(
    'edgeRole',
    CASE
      WHEN relation_meta->'enabledInteractions' ?| ARRAY[
        'dragAssociate', 'unbindChecked', 'checkboxSet', 'refFieldPick', 'ownershipWrite'
      ] THEN 'write'
      ELSE 'filter'
    END
  ),
  updater = 'patch-edge-role',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (
    relation_meta IS NULL
    OR relation_meta->>'edgeRole' IS NULL
    OR relation_meta->>'edgeRole' NOT IN ('filter', 'write')
  );

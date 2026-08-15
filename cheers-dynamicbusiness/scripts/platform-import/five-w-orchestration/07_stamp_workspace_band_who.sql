-- ============================================================================
-- five-w-orchestration · 07 型号/实体行补盖 workspaceBand
-- 规则：归属只认创建入口写入的盖章；无盖章的历史 MODEL/ENTITY 一律视为 Who 配置产物（WHO）。
-- 视角型号/实体须在「视角配置」重新保存后才会是 FILTER，禁止运行时按类型猜。
-- ============================================================================

SET search_path TO dynamicbusiness;

UPDATE dm_data_tab_layout
SET category_column = COALESCE(category_column, '{}'::jsonb) || jsonb_build_object('workspaceBand', 'WHO'),
    updater = 'seed-07',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND column_kind IN ('MODEL', 'ENTITY')
  AND (
    category_column IS NULL
    OR NOT (category_column ? 'workspaceBand')
    OR NULLIF(TRIM(category_column->>'workspaceBand'), '') IS NULL
  );

-- ============================================================================
-- catalog-orchestration · 07 型号/实体行补栏所在区域
--
-- 历史若还写着 workspaceBand 或取值 WHO：本脚本一次性改成 columnSection，并删掉旧键。
-- 运行时前端不再读旧键；跑完本脚本后库里不应再依赖 workspaceBand。
-- 无盖章的 MODEL/ENTITY 一律补 OBJECT。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 有旧键 workspaceBand、尚无有效 columnSection：迁成 columnSection（WHO→OBJECT）
UPDATE dm_data_tab_layout
SET column_meta = (
      COALESCE(column_meta, '{}'::jsonb)
      - 'workspaceBand'
    )
    || jsonb_build_object(
      'columnSection',
      CASE
        WHEN UPPER(TRIM(COALESCE(column_meta->>'workspaceBand', ''))) IN ('FILTER', 'OBJECT', 'WHAT')
          THEN UPPER(TRIM(column_meta->>'workspaceBand'))
        WHEN UPPER(TRIM(COALESCE(column_meta->>'workspaceBand', ''))) = 'WHO'
          THEN 'OBJECT'
        ELSE 'OBJECT'
      END
    ),
    updater = 'seed-07',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND column_kind IN ('MODEL', 'ENTITY')
  AND column_meta ? 'workspaceBand'
  AND (
    NOT (column_meta ? 'columnSection')
    OR NULLIF(TRIM(column_meta->>'columnSection'), '') IS NULL
  );

-- 2) 已有 columnSection，但仍残留 workspaceBand：只删旧键
UPDATE dm_data_tab_layout
SET column_meta = column_meta - 'workspaceBand',
    updater = 'seed-07',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND column_kind IN ('MODEL', 'ENTITY')
  AND column_meta ? 'workspaceBand';

-- 3) 完全无盖章：补 OBJECT
UPDATE dm_data_tab_layout
SET column_meta = COALESCE(column_meta, '{}'::jsonb) || jsonb_build_object('columnSection', 'OBJECT'),
    updater = 'seed-07',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND column_kind IN ('MODEL', 'ENTITY')
  AND (
    column_meta IS NULL
    OR NOT (column_meta ? 'columnSection')
    OR NULLIF(TRIM(column_meta->>'columnSection'), '') IS NULL
  );

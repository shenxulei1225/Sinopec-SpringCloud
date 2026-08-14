SET search_path TO platformresource;

-- 移除已废弃的 toolbar 配置键；原 toolbar=true 且 autoCrud 未显式 true 的行，迁移为 autoCrud=true
UPDATE pr_component_props
SET
    props = (
        (props::jsonb - 'toolbar')
        || CASE
            WHEN (props::jsonb->>'toolbar') = 'true'
                 AND COALESCE(props::jsonb->>'autoCrud', 'false') <> 'true'
            THEN '{"autoCrud": true}'::jsonb
            ELSE '{}'::jsonb
           END
    )::text,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND props IS NOT NULL
  AND props <> ''
  AND (props::jsonb ? 'toolbar');

UPDATE pr_component_props
SET
    props_override = (
        (props_override::jsonb - 'toolbar')
        || CASE
            WHEN (props_override::jsonb->>'toolbar') = 'true'
            THEN '{"autoCrud": true}'::jsonb
            ELSE '{}'::jsonb
           END
    )::text,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND props_override IS NOT NULL
  AND props_override <> ''
  AND (props_override::jsonb ? 'toolbar');

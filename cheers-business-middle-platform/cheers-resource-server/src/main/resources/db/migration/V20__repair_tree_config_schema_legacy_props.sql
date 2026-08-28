SET search_path TO platformresource;

-- ConfigSchema 去历史兼容后，将 pr_component_props 中 tree 相关旧形态 props 对齐新契约。
-- 1) defaultExpandAll: boolean → { expandAll, expandLevel }（语义与旧 normalizeDefaultExpandConfig 一致）
-- 2) defaultShortcut: 去掉废弃字段 show
-- 3) props / props_override 顶层扁平快捷字段 → defaultShortcut 块

-- ---------------------------------------------------------------------------
-- props.defaultExpandAll
-- ---------------------------------------------------------------------------
UPDATE pr_component_props
SET
    props = jsonb_set(
        props::jsonb,
        '{defaultExpandAll}',
        CASE
            WHEN props::jsonb->'defaultExpandAll' = 'true'::jsonb
            THEN '{"expandAll": true, "expandLevel": 1}'::jsonb
            ELSE '{"expandAll": false, "expandLevel": 0}'::jsonb
        END,
        true
    )::text,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND component_code = 'tree'
  AND props IS NOT NULL
  AND props <> ''
  AND jsonb_typeof(props::jsonb->'defaultExpandAll') = 'boolean';

-- ---------------------------------------------------------------------------
-- props.defaultShortcut.show（运行时已忽略，落库去掉冗余键）
-- ---------------------------------------------------------------------------
UPDATE pr_component_props
SET
    props = jsonb_set(
        props::jsonb,
        '{defaultShortcut}',
        (props::jsonb->'defaultShortcut') - 'show',
        true
    )::text,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND component_code = 'tree'
  AND props IS NOT NULL
  AND props <> ''
  AND (props::jsonb->'defaultShortcut') ? 'show';

-- ---------------------------------------------------------------------------
-- props 顶层扁平快捷字段 → defaultShortcut
-- ---------------------------------------------------------------------------
UPDATE pr_component_props
SET
    props = (
        (props::jsonb - 'hideDefaultShortcut' - 'showDefaultShortcut' - 'defaultShortcutLabel')
        || jsonb_build_object(
            'defaultShortcut',
            COALESCE(props::jsonb->'defaultShortcut', '{}'::jsonb)
            || jsonb_build_object(
                'showInMultiple',
                COALESCE((props::jsonb->>'showDefaultShortcut')::boolean, false),
                'label',
                COALESCE(NULLIF(BTRIM(props::jsonb->>'defaultShortcutLabel'), ''), '全部')
            )
        )
    )::text,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND component_code = 'tree'
  AND props IS NOT NULL
  AND props <> ''
  AND (
      props::jsonb ? 'hideDefaultShortcut'
      OR props::jsonb ? 'showDefaultShortcut'
      OR props::jsonb ? 'defaultShortcutLabel'
  );

-- ---------------------------------------------------------------------------
-- props_override 顶层扁平快捷字段 → defaultShortcut
-- ---------------------------------------------------------------------------
UPDATE pr_component_props
SET
    props_override = (
        (props_override::jsonb - 'hideDefaultShortcut' - 'showDefaultShortcut' - 'defaultShortcutLabel')
        || jsonb_build_object(
            'defaultShortcut',
            COALESCE(props_override::jsonb->'defaultShortcut', '{}'::jsonb)
            || jsonb_build_object(
                'showInMultiple',
                COALESCE((props_override::jsonb->>'showDefaultShortcut')::boolean, false),
                'label',
                COALESCE(NULLIF(BTRIM(props_override::jsonb->>'defaultShortcutLabel'), ''), '全部')
            )
        )
    )::text,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND component_code = 'tree'
  AND props_override IS NOT NULL
  AND props_override <> ''
  AND (
      props_override::jsonb ? 'hideDefaultShortcut'
      OR props_override::jsonb ? 'showDefaultShortcut'
      OR props_override::jsonb ? 'defaultShortcutLabel'
  );

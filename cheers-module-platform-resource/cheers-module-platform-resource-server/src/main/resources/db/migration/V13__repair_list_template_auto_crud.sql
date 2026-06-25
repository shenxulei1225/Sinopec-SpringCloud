SET search_path TO platformresource;

-- 列表（完整）模板：开启内置 CRUD（与 toolbar=true 一致）
UPDATE pr_component_props
SET
    props = jsonb_set(props, '{autoCrud}', 'true'::jsonb, true),
    updater = 'repair',
    update_time = CURRENT_TIMESTAMP
WHERE id = 1001
  AND component_code = 'list'
  AND is_template = TRUE
  AND deleted = FALSE
  AND (props->>'autoCrud') = 'false';

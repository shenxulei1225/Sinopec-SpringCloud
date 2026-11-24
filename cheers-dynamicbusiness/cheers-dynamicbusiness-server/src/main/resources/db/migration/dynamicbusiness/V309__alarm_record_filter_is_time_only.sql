-- 告警记录页上方已经有紧急、重要、一般。列表筛选项里再画一遍告警级别就是重复。
-- 列表上方只留告警时间，用来选一段时间。只改租户 123 告警记录列表的这份配置。
UPDATE platformresource.pr_component_props
SET props = jsonb_set(
        props::jsonb,
        '{filter,filterSelectedOptions}',
        '["FLD-ALM-004"]'::jsonb,
        true
    )::text,
    updater = 'V309',
    update_time = CURRENT_TIMESTAMP
WHERE id = 17068
  AND deleted = false;

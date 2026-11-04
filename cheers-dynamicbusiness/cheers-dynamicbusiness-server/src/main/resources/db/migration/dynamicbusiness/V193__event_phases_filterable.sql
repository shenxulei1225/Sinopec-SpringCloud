-- 告警联动点选事件主题时，要按「告警或消除」筛主题。
-- 这一列本来就不能进筛选，查询会把条件丢掉，巡检和页面主题就会混进来。
-- 只打开事件主题型号上这一列的可筛选。不改列，不改已有主题行。

SET search_path TO dynamicbusiness, public;

UPDATE dynamic_model_field_assignment a
SET is_filterable = TRUE,
    updater = 'flyway-v193',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE a.deleted = FALSE
  AND a.field_code = 'event_phases'
  AND a.model_id = m.id
  AND m.deleted = FALSE
  AND m.entity_type_code = 'event_topic'
  AND a.is_filterable IS DISTINCT FROM TRUE;

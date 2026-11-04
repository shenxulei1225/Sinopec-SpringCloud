-- 事件主题「告警或消除」给人看的名字。选项是告警、告警消除。
-- 存值仍是 RAISED / CLEARED。不改主题行，不改发布路径。
SET search_path TO dynamicbusiness, public;

UPDATE dynamic_field
SET name = '告警或消除',
    description = '这一条主题是告警，还是告警消除。',
    options = '[{"label":"告警","value":"RAISED"},{"label":"告警消除","value":"CLEARED"}]',
    updater = 'flyway-v191',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND code = 'event_phases';

UPDATE dynamic_field
SET name = '告警事件',
    description = '告警出现时发布的事件主题。',
    updater = 'flyway-v191',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND code = 'alarm_raised_event_topic_ref';

UPDATE dynamic_field
SET name = '告警消除事件',
    description = '可空。告警消除时发布的另一条事件主题。',
    updater = 'flyway-v191',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND code = 'alarm_cleared_event_topic_ref';

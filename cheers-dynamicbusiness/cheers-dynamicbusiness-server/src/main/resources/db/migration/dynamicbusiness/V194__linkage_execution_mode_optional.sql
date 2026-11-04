-- 联动「执行方式」给人看的选项：自动执行、人工确认后执行。可空。
-- 不改已保存的 AUTO / CONFIRM。空着按自动执行。
SET search_path TO dynamicbusiness, public;

UPDATE dynamic_field
SET name = '执行方式',
    description = '事件到了是立刻执行，还是等人确认后再执行。可空。空着按自动执行。',
    options = '[{"label":"自动执行","value":"AUTO"},{"label":"人工确认后执行","value":"CONFIRM"}]',
    updater = 'flyway-v194',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND code = 'F-6fb90a47274748a3b708ad32a7dc4ce0';

UPDATE dynamic_entity_type_base_field
SET required = FALSE,
    updater = 'flyway-v194',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND field_code = 'F-6fb90a47274748a3b708ad32a7dc4ce0'
  AND required IS DISTINCT FROM FALSE;

UPDATE dynamic_model_field_assignment
SET required = FALSE,
    updater = 'flyway-v194',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND field_code = 'F-6fb90a47274748a3b708ad32a7dc4ce0'
  AND required IS DISTINCT FROM FALSE;

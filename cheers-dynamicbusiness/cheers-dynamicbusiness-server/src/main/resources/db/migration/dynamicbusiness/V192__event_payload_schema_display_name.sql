-- 事件主题这一列给人看的名字是「主题内容」，不再叫「字段说明」。
-- 不改列编码，不改用途，不改已写下的 JSON。
SET search_path TO dynamicbusiness, public;

UPDATE dynamic_field
SET name = '主题内容',
    description = '这一条主题发出时带哪些内容。',
    updater = 'flyway-v192',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND code = 'event_payload_schema';

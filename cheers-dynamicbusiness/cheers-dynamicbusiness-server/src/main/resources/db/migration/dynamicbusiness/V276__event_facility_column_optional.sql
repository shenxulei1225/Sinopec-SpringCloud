-- 事件和动作上的 facility_id 列不再是归属的权威。
-- 归属写在事件载荷里，按目录声明抄过来的引用。
-- 这一列只在抄过来的字段编码正好是 facility_id 时有值，其余空着。

SET search_path TO dynamicbusiness, public;

ALTER TABLE dynamicbusiness.platform_event_outbox ALTER COLUMN facility_id DROP NOT NULL;
ALTER TABLE dynamicbusiness.dynamic_action_execution ALTER COLUMN facility_id DROP NOT NULL;

COMMENT ON COLUMN dynamicbusiness.platform_event_outbox.facility_id IS
    '仅当事件载荷里的归属字段编码是 facility_id 时写入。归属以载荷为准。';
COMMENT ON COLUMN dynamicbusiness.dynamic_action_execution.facility_id IS
    '仅当事件载荷里的归属字段编码是 facility_id 时写入。归属以载荷为准。';

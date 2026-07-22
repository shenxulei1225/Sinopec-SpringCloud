-- 告警转事件：只存告警关联标识（跨模块 id），不存告警实体快照
ALTER TABLE emergency_event
    ADD COLUMN IF NOT EXISTS source_alert_type VARCHAR(64),
    ADD COLUMN IF NOT EXISTS source_alert_id VARCHAR(64);

COMMENT ON COLUMN emergency_event.source_alert_type IS
    '告警来源类型，如 iot_alert_record';
COMMENT ON COLUMN emergency_event.source_alert_id IS
    '来源告警业务 id（字符串化），与 source_alert_type 组成关联键';

CREATE UNIQUE INDEX IF NOT EXISTS uk_emergency_event_source_alert
    ON emergency_event (tenant_id, source_alert_type, source_alert_id)
    WHERE deleted = false AND source_alert_id IS NOT NULL;

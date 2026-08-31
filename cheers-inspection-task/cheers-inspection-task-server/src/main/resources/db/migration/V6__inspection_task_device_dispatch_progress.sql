-- 开跑下发身份与停靠进度（任务会话权威；上行按此精确定位，不猜台账）
ALTER TABLE inspection_task
    ADD COLUMN IF NOT EXISTS device_dispatch_task_id VARCHAR(64);

ALTER TABLE inspection_task
    ADD COLUMN IF NOT EXISTS device_dispatch_template_id VARCHAR(128);

ALTER TABLE inspection_task
    ADD COLUMN IF NOT EXISTS device_stop_progress TEXT;

COMMENT ON COLUMN inspection_task.device_dispatch_task_id IS
    'Wire taskId sent in 500201; uplink match key';
COMMENT ON COLUMN inspection_task.device_dispatch_template_id IS
    'Wire templateId for command package; uplink secondary match key';
COMMENT ON COLUMN inspection_task.device_stop_progress IS
    'JSON array of stop progress: pointId/sequence/status PENDING|DONE|FAILED';

CREATE INDEX IF NOT EXISTS idx_inspection_task_device_dispatch_task_id
    ON inspection_task (device_dispatch_task_id)
    WHERE device_dispatch_task_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_inspection_task_device_dispatch_template_id
    ON inspection_task (device_dispatch_template_id)
    WHERE device_dispatch_template_id IS NOT NULL;

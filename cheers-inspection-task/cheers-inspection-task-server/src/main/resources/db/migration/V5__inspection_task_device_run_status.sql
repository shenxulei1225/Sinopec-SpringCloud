-- 任务会话上的设备侧运行态（开跑下发 / 上行回写）；不存瞬时 WebSocket。
ALTER TABLE inspection_task
    ADD COLUMN IF NOT EXISTS device_run_status VARCHAR(32);

ALTER TABLE inspection_task
    ADD COLUMN IF NOT EXISTS device_last_uplink_at BIGINT;

ALTER TABLE inspection_task
    ADD COLUMN IF NOT EXISTS device_last_uplink_opcode INTEGER;

COMMENT ON COLUMN inspection_task.device_run_status IS
    'Device run status on task session: IDLE/DISPATCHED/RUNNING/COMPLETED/FAULT';
COMMENT ON COLUMN inspection_task.device_last_uplink_at IS
    'Last device uplink epoch millis';
COMMENT ON COLUMN inspection_task.device_last_uplink_opcode IS
    'Last device uplink transport opcode';

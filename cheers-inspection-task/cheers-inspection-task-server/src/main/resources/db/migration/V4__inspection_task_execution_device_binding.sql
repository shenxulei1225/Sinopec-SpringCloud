-- 任务会话上的执行设备绑定（JSON）：设备业务 id + 对接协议编码 + 逻辑设备标识。
-- 开跑只读本列，不查设备台账；通讯连接变化不改写本列。
ALTER TABLE inspection_task
    ADD COLUMN IF NOT EXISTS execution_device_binding TEXT;

COMMENT ON COLUMN inspection_task.execution_device_binding IS
    'Execution device binding JSON: equipmentId, protocolCode, logicalDeviceId';

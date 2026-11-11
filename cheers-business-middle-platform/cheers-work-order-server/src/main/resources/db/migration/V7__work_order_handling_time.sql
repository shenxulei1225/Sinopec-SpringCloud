SET search_path TO work_order;

-- 消缺工单的处理时刻。告诉提出方何时去处理这台设备。与计划开始、计划结束不是同一件事。
ALTER TABLE wo_work_order ADD COLUMN IF NOT EXISTS handling_time TIMESTAMP;

COMMENT ON COLUMN wo_work_order.handling_time IS '处理时刻。报修派工写入。空着表示这张单不约定何时去处理。';
COMMENT ON COLUMN wo_work_order.planned_start_time IS '计划开始。只和计划结束一起出现，表示这张工单占维修班的一段档期。';
COMMENT ON COLUMN wo_work_order.planned_finish_time IS '计划结束。只和计划开始一起出现。';

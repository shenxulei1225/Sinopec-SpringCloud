SET search_path TO platform;

-- L4 占窗定稿：platform_schedule_slot → platform_resource_reservation
ALTER TABLE platform_schedule_slot RENAME TO platform_resource_reservation;

ALTER TABLE platform_resource_reservation
    RENAME COLUMN planned_start TO planned_start;

ALTER TABLE platform_resource_reservation
    RENAME COLUMN planned_end TO planned_end;

ALTER TABLE platform_resource_reservation
    RENAME COLUMN actual_start TO actual_start;

ALTER TABLE platform_resource_reservation
    RENAME COLUMN actual_end TO actual_end;

ALTER TABLE platform_resource_reservation
    RENAME COLUMN slot_status TO candidate_status;

ALTER TABLE platform_resource_reservation
    ADD COLUMN IF NOT EXISTS candidate_type VARCHAR(32) NOT NULL DEFAULT 'TASK_EXECUTION';

ALTER TABLE platform_resource_reservation
    ADD COLUMN IF NOT EXISTS candidate_start TIMESTAMPTZ;

ALTER TABLE platform_resource_reservation
    ADD COLUMN IF NOT EXISTS candidate_end TIMESTAMPTZ;

UPDATE platform_resource_reservation
SET candidate_start = planned_start,
    candidate_end   = planned_end
WHERE candidate_start IS NULL;

ALTER TABLE platform_resource_reservation
    ALTER COLUMN candidate_start SET NOT NULL;

ALTER TABLE platform_resource_reservation
    ALTER COLUMN candidate_end SET NOT NULL;

COMMENT ON TABLE platform_resource_reservation IS
    '资源占窗（Resource Reservation）：编排 SOLVE 落库；TASK_EXECUTION 含计划/实际执行起止';

COMMENT ON COLUMN platform_resource_reservation.candidate_type IS
    'TASK_EXECUTION | BATTERY_CHARGING | INTER_TASK_GAP';

COMMENT ON COLUMN platform_resource_reservation.planned_start IS
    '计划执行开始（用户/看板/协议认的时刻）';

COMMENT ON COLUMN platform_resource_reservation.planned_end IS
    '计划执行结束（计划开始 + 任务时长）';

COMMENT ON COLUMN platform_resource_reservation.candidate_start IS
    '资源占窗开始（冲突检测）；TASK_EXECUTION 与 planned_start 同值';

COMMENT ON COLUMN platform_resource_reservation.candidate_end IS
    '资源占窗结束（冲突检测）；TASK_EXECUTION 与 planned_end 同值';

ALTER INDEX IF EXISTS idx_platform_schedule_slot_job
    RENAME TO idx_platform_resource_reservation_job;

ALTER INDEX IF EXISTS idx_platform_schedule_slot_business_type
    RENAME TO idx_platform_resource_reservation_entity_type;

ALTER INDEX IF EXISTS idx_platform_schedule_slot_planned_start
    RENAME TO idx_platform_resource_reservation_planned_start;

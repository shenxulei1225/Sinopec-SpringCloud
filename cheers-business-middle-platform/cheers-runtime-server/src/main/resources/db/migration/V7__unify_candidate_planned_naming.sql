SET search_path TO platform;

-- 统一占窗/计划/实测命名：candidate*（试排占窗）、planned*（启用排程定稿）、actual*（实测）
-- 本库 V6 实际落盘列为 reservation_* / planned_execution_* / actual_execution_*，此处对齐 DO 与契约字段名。

ALTER TABLE platform_resource_reservation
    RENAME COLUMN reservation_type TO candidate_type;

ALTER TABLE platform_resource_reservation
    RENAME COLUMN reservation_status TO candidate_status;

ALTER TABLE platform_resource_reservation
    RENAME COLUMN reservation_start TO candidate_start;

ALTER TABLE platform_resource_reservation
    RENAME COLUMN reservation_end TO candidate_end;

ALTER TABLE platform_resource_reservation
    RENAME COLUMN planned_execution_start TO planned_start;

ALTER TABLE platform_resource_reservation
    RENAME COLUMN planned_execution_end TO planned_end;

ALTER TABLE platform_resource_reservation
    RENAME COLUMN actual_execution_start TO actual_start;

ALTER TABLE platform_resource_reservation
    RENAME COLUMN actual_execution_end TO actual_end;

COMMENT ON COLUMN platform_resource_reservation.candidate_type IS
    '占窗类型：TASK_EXECUTION | BATTERY_CHARGING | INTER_TASK_GAP';

COMMENT ON COLUMN platform_resource_reservation.candidate_status IS
    '候选占窗状态（试排 SOLVE 落库）';

COMMENT ON COLUMN platform_resource_reservation.candidate_start IS
    '排期候选占窗开始（智能编排试排/冲突检测）';

COMMENT ON COLUMN platform_resource_reservation.candidate_end IS
    '排期候选占窗结束（智能编排试排/冲突检测）';

COMMENT ON COLUMN platform_resource_reservation.planned_start IS
    '执行计划开始时刻（enableSchedule 定稿）';

COMMENT ON COLUMN platform_resource_reservation.planned_end IS
    '执行计划结束时刻（enableSchedule 定稿）';

COMMENT ON COLUMN platform_resource_reservation.actual_start IS
    '实测开始时刻（开跑/完成回写）';

COMMENT ON COLUMN platform_resource_reservation.actual_end IS
    '实测结束时刻（开跑/完成回写）';

ALTER INDEX IF EXISTS idx_platform_resource_reservation_planned_execution_start
    RENAME TO idx_platform_resource_reservation_planned_start;

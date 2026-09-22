SET search_path TO platform;

-- 试排（arrangeSchedule）只写 candidate_*；planned_* 在 enableSchedule 定稿时由 finalizePlannedSchedule 写入。
-- V1 遗留 NOT NULL 会导致 persist 插入 candidate 窗时 planned_start/planned_end 为空而失败（前端表现为「系统异常」）。

ALTER TABLE platform_resource_reservation
    ALTER COLUMN planned_start DROP NOT NULL;

ALTER TABLE platform_resource_reservation
    ALTER COLUMN planned_end DROP NOT NULL;

COMMENT ON COLUMN platform_resource_reservation.planned_start IS
    '执行计划开始（enableSchedule 定稿前为 NULL；定稿时从 candidate_start 复制）';

COMMENT ON COLUMN platform_resource_reservation.planned_end IS
    '执行计划结束（enableSchedule 定稿前为 NULL；定稿时从 candidate_end 复制）';

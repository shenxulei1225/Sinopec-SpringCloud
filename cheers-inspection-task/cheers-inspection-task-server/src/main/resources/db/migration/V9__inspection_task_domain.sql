-- V9: 任务业务域 domain（编排驱动 / 任务功能点分池）
-- 权威：docs/动态业务/编排驱动业务/编排驱动业务权威说明.md §2.4
-- 例：巡检任务 domain='巡检'；与功能库 taskDomain 对齐。禁止读路径猜默认域。

ALTER TABLE inspection_task
    ADD COLUMN IF NOT EXISTS domain VARCHAR(64);

COMMENT ON COLUMN inspection_task.domain IS
    'Task business domain for pooling (e.g. 巡检). Authority written on create/update; no read-path default.';

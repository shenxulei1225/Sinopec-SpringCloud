-- Retract wire-protocol mirror columns on inspection_task (authority moves to execution record).
ALTER TABLE inspection_task DROP COLUMN IF EXISTS device_dispatch_task_id;
ALTER TABLE inspection_task DROP COLUMN IF EXISTS device_dispatch_template_id;
ALTER TABLE inspection_task DROP COLUMN IF EXISTS device_stop_progress;

DROP INDEX IF EXISTS idx_inspection_task_device_dispatch_task_id;
DROP INDEX IF EXISTS idx_inspection_task_device_dispatch_template_id;

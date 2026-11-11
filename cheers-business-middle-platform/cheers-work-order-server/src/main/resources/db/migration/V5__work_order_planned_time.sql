-- 派工时写入预计开始、预计完工。空着表示这一格还没填，不编默认时刻。
SET search_path TO work_order;

ALTER TABLE wo_work_order ADD COLUMN IF NOT EXISTS planned_start_time TIMESTAMP;
ALTER TABLE wo_work_order ADD COLUMN IF NOT EXISTS planned_finish_time TIMESTAMP;

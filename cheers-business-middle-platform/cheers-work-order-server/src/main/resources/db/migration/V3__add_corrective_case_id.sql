SET search_path TO work_order;
ALTER TABLE wo_work_order ADD COLUMN IF NOT EXISTS corrective_case_id BIGINT;

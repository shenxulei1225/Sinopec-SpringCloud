ALTER TABLE inspection_task
    ADD COLUMN IF NOT EXISTS runtime_job_id VARCHAR(64);

COMMENT ON COLUMN inspection_task.runtime_job_id IS 'Active patrol runtime job id after schedule reserve';

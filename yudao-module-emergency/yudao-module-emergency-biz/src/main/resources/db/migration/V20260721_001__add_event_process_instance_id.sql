-- B2-A: bind emergency event to Flowable process instance
ALTER TABLE emergency_event
    ADD COLUMN IF NOT EXISTS process_instance_id VARCHAR(64);

COMMENT ON COLUMN emergency_event.process_instance_id IS
    'Flowable process instance id; set when command process starts';

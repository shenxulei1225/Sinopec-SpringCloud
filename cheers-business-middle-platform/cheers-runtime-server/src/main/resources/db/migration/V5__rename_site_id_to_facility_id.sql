SET search_path TO platform;

ALTER TABLE platform_runtime_job
    RENAME COLUMN site_id TO facility_id;

ALTER TABLE platform_schedule_slot
    RENAME COLUMN site_id TO facility_id;

ALTER TABLE platform_process_timeline_action
    RENAME COLUMN site_id TO facility_id;

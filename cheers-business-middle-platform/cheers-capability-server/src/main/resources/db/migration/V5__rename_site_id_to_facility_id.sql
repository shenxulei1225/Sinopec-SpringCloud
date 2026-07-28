SET search_path TO platform;

ALTER TABLE platform_process_capability_binding
    RENAME COLUMN site_id TO facility_id;

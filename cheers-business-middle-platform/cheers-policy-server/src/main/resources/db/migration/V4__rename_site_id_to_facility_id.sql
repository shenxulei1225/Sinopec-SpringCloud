SET search_path TO platform;

ALTER TABLE platform_policy_set
    RENAME COLUMN site_id TO facility_id;

ALTER TABLE platform_policy_snapshot
    RENAME COLUMN site_id TO facility_id;

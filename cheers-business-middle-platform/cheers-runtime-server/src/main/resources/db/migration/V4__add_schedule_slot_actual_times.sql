SET search_path TO platform;

ALTER TABLE platform_schedule_slot
    ADD COLUMN IF NOT EXISTS actual_start TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS actual_end   TIMESTAMPTZ;

SET search_path TO platform;

-- 路网多实例元数据：名称、说明、适用设备类型（定稿 2026-07-20）
ALTER TABLE platform_path_network
    ADD COLUMN IF NOT EXISTS display_name VARCHAR(128),
    ADD COLUMN IF NOT EXISTS description VARCHAR(512),
    ADD COLUMN IF NOT EXISTS applicable_equipment_types JSONB NOT NULL DEFAULT '[]';

UPDATE platform_path_network
SET display_name = COALESCE(NULLIF(TRIM(display_name), ''), '默认路网')
WHERE deleted = FALSE
  AND status = 'DRAFT'
  AND (display_name IS NULL OR TRIM(display_name) = '');

CREATE INDEX IF NOT EXISTS idx_path_network_facility_status
    ON platform_path_network (facility_id, status)
    WHERE deleted = FALSE;

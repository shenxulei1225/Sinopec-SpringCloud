-- Phase 1: 场景可挂多设施；成员表补设施编码（导入导出）
-- Spec: docs/superpowers/specs/2026-07-22-scene-facility-identity-design.md

ALTER TABLE scene_platform.facility_scene_binding
    DROP CONSTRAINT IF EXISTS uk_facility_scene_binding_scene_code;

ALTER TABLE scene_platform.facility_scene_binding
    ADD COLUMN IF NOT EXISTS facility_code VARCHAR(64);

COMMENT ON COLUMN scene_platform.facility_scene_binding.facility_code IS '设施编码（对外/导入导出）；对内关联仍用 facility_id';

CREATE INDEX IF NOT EXISTS idx_facility_scene_binding_scene_code
    ON scene_platform.facility_scene_binding (scene_code)
    WHERE deleted = FALSE;

CREATE UNIQUE INDEX IF NOT EXISTS uk_facility_scene_binding_facility_code
    ON scene_platform.facility_scene_binding (facility_code)
    WHERE deleted = FALSE AND facility_code IS NOT NULL;

UPDATE scene_platform.facility_scene_binding
SET facility_code = 'FAC-JINQIAO',
    update_time = CURRENT_TIMESTAMP,
    updater = 'system'
WHERE facility_id = 44
  AND deleted = FALSE
  AND (facility_code IS NULL OR facility_code = '');

UPDATE scene_platform.facility_scene_binding
SET facility_code = 'FAC-LUOYANG-SHENGRUI',
    update_time = CURRENT_TIMESTAMP,
    updater = 'system'
WHERE facility_id = 45
  AND deleted = FALSE
  AND (facility_code IS NULL OR facility_code = '');

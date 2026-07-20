-- 站点原点高程来源 + 实例 GPS 高程（地形采样确认写回）

ALTER TABLE scene_platform.coordinate_reference
    ADD COLUMN IF NOT EXISTS origin_height_source VARCHAR(32);

COMMENT ON COLUMN scene_platform.coordinate_reference.origin_height_source IS
    '原点高程来源：TERRAIN_SAMPLE=地形采样；MANUAL=手填；空=历史未标注';

ALTER TABLE scene_platform.actor_instance
    ADD COLUMN IF NOT EXISTS gps_lng NUMERIC(18, 8),
    ADD COLUMN IF NOT EXISTS gps_lat NUMERIC(18, 8),
    ADD COLUMN IF NOT EXISTS gps_height NUMERIC(18, 8),
    ADD COLUMN IF NOT EXISTS gps_height_source VARCHAR(32);

COMMENT ON COLUMN scene_platform.actor_instance.gps_lng IS '实例 GPS 经度（WGS84），可由站心+局部换算';
COMMENT ON COLUMN scene_platform.actor_instance.gps_lat IS '实例 GPS 纬度（WGS84），可由站心+局部换算';
COMMENT ON COLUMN scene_platform.actor_instance.gps_height IS '实例 GPS 椭球高（米），权威来源为地形采样';
COMMENT ON COLUMN scene_platform.actor_instance.gps_height_source IS
    '实例 GPS 高程来源：TERRAIN_SAMPLE=地形采样；MANUAL=手填；空=历史未标注';

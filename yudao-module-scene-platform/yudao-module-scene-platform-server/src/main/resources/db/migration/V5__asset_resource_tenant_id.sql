-- asset_resource 无 tenant_id，但租户插件会对已注册 Mapper 表注入条件，导致列表 500。
ALTER TABLE scene_platform.asset_resource
    ADD COLUMN IF NOT EXISTS tenant_id bigint NOT NULL DEFAULT 1;

UPDATE scene_platform.asset_resource
SET tenant_id = 1
WHERE tenant_id IS NULL OR tenant_id = 0;

-- 演示资产类型对齐 scene_asset 叶子 code（原先 MODEL 非法）
UPDATE scene_platform.asset_resource
SET asset_type = 'TANK', update_time = CURRENT_TIMESTAMP
WHERE id = 11001 AND asset_type = 'MODEL';

UPDATE scene_platform.asset_resource
SET asset_type = 'EQUIPMENT_MESH', update_time = CURRENT_TIMESTAMP
WHERE id = 11002 AND asset_type = 'MODEL';

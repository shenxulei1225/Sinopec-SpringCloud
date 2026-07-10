-- 开发联调 · facility 业务实例样例
-- 用途：数据管理 / scene-3d 工作台（?facilityId=1&facilityName=东营油库）
-- 幂等：按 code 存在则更新 name/model_id/位置字段，不重复插入
-- 说明：REF_REGION 挂作业区层 ent_region.id（东营作业区 id=100101）

SET search_path TO dynamicbusiness;

INSERT INTO ent_facility (
    id,
    entity_type_code,
    model_id,
    name,
    code,
    tenant_id,
    creator,
    tree_path,
    sort,
    status,
    deleted,
    region_id,
    address,
    longitude,
    latitude,
    facility_type,
    custom_fields
)
OVERRIDING SYSTEM VALUE
SELECT
    v.id,
    'facility',
    m.id,
    v.name,
    v.code,
    1,
    'seed',
    v.tree_path,
    v.sort,
    1,
    false,
    v.region_id,
    v.address,
    v.longitude,
    v.latitude,
    v.facility_type,
    '{}'::text
FROM (
    VALUES
        (
            1,
            'FAC-DEPOT-001',
            '东营油库',
            '/1/',
            1,
            'MODEL-FACILITY-DEPOT',
            100101,
            '山东省东营市东营区',
            118.58200000::numeric(12,8),
            37.44800000::numeric(12,8),
            'depot'
        ),
        (
            2,
            'FAC-ST-001',
            '示范站场',
            '/2/',
            2,
            'MODEL-FACILITY-STATION',
            100101,
            '山东省东营市',
            118.67000000::numeric(12,8),
            37.43000000::numeric(12,8),
            'station'
        )
) AS v(
    id, code, name, tree_path, sort, model_code, region_id,
    address, longitude, latitude, facility_type
)
JOIN dynamic_model m
  ON m.code = v.model_code
 AND m.entity_type_code = 'facility'
 AND m.deleted = false
ON CONFLICT (id) DO UPDATE SET
    model_id = EXCLUDED.model_id,
    name = EXCLUDED.name,
    code = EXCLUDED.code,
    tenant_id = EXCLUDED.tenant_id,
    tree_path = EXCLUDED.tree_path,
    sort = EXCLUDED.sort,
    status = EXCLUDED.status,
    deleted = false,
    region_id = EXCLUDED.region_id,
    address = EXCLUDED.address,
    longitude = EXCLUDED.longitude,
    latitude = EXCLUDED.latitude,
    facility_type = EXCLUDED.facility_type,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP;

SELECT setval(
    pg_get_serial_sequence('dynamicbusiness.ent_facility', 'id'),
    GREATEST(
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_facility),
        2
    )
);

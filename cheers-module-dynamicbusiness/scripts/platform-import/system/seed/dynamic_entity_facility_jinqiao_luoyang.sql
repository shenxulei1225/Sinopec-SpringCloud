-- ============================================================================
-- 金桥 / 洛阳圣瑞 · facility 样例（新 facilityId，不用旧 site_id）
-- 业务编码：FAC-JINQIAO / FAC-LUOYANG-SHENGRUI
-- 旧 site_id 121212 / 121240 仅作导入映射源，见 legacy-site-import design。
-- 幂等：按 code upsert。
-- ============================================================================

SET search_path TO dynamicbusiness;

SELECT setval(
  'dynamicbusiness.ent_facility_id_seq',
  GREATEST(COALESCE((SELECT MAX(id) FROM dynamicbusiness.ent_facility), 1), 1)
);

INSERT INTO ent_facility (
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
SELECT
    'facility',
    m.id,
    v.name,
    v.code,
    1,
    'seed',
    '/',
    v.sort,
    1,
    false,
    v.region_id,
    v.address,
    v.longitude,
    v.latitude,
    'station',
    '{}'::jsonb
FROM (
    VALUES
        (
            'FAC-JINQIAO',
            '金桥厂区',
            10,
            -- 若无对应作业区可留空；福建样例常用 100024，此处不强绑
            NULL::bigint,
            '江苏省徐州市',
            117.23805600::numeric(12,8),
            34.30305600::numeric(12,8)
        ),
        (
            'FAC-LUOYANG-SHENGRUI',
            '洛阳圣瑞',
            20,
            NULL::bigint,
            '河南省洛阳市',
            112.45390000::numeric(12,8),
            34.61970000::numeric(12,8)
        )
) AS v(code, name, sort, region_id, address, longitude, latitude)
JOIN dynamic_model m
  ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-FACILITY-STATION'
WHERE NOT EXISTS (
    SELECT 1 FROM ent_facility e
    WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = v.code
);

UPDATE ent_facility e
SET
    name = v.name,
    address = v.address,
    longitude = v.longitude,
    latitude = v.latitude,
    facility_type = 'station',
    model_id = m.id,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
    VALUES
        ('FAC-JINQIAO', '金桥厂区', '江苏省徐州市', 117.23805600::numeric(12,8), 34.30305600::numeric(12,8)),
        ('FAC-LUOYANG-SHENGRUI', '洛阳圣瑞', '河南省洛阳市', 112.45390000::numeric(12,8), 34.61970000::numeric(12,8))
) AS v(code, name, address, longitude, latitude)
JOIN dynamic_model m
  ON m.deleted = false AND m.tenant_id = 1 AND m.code = 'MODEL-FACILITY-STATION'
WHERE e.deleted = false AND e.tenant_id = 1 AND e.code = v.code;

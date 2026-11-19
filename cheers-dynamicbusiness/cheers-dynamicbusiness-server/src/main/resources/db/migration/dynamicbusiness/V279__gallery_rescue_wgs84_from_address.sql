-- 三维地球使用 WGS84。
-- 本次坐标来自 OpenStreetMap 地名检索，返回值已是 WGS84，不再做火星坐标转换。
-- 只写入检索结果的道路能对上名录地址的单位。对不上或没有检索结果的，经纬度保持为空。

SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.ent_external_rescue_resource_t123 AS e
SET longitude = v.lon, latitude = v.lat, updater = 'V279', update_time = now()
FROM (
    VALUES
    ('ex-baoxie-police', 114.5421, 30.4622),
    ('ex-hosp-001', 114.2896, 30.5837),
    ('ex-hosp-002', 114.2851, 30.6026),
    ('ex-hosp-004', 114.2819, 30.6090),
    ('ex-hosp-005', 114.2948, 30.6284),
    ('ex-hosp-011', 114.2749, 30.5923),
    ('ex-hosp-012', 114.2923, 30.5908),
    ('ex-hosp-021', 114.2677, 30.6165),
    ('ex-hosp-026', 114.2381, 30.6205),
    ('ex-hosp-028', 114.2589, 30.5991),
    ('ex-hosp-036', 114.2770, 30.5855),
    ('ex-hosp-073', 114.3468, 30.5557),
    ('ex-hosp-074', 114.2972, 30.5432),
    ('ex-hosp-075', 114.3360, 30.6048),
    ('ex-hosp-078', 114.3264, 30.5444),
    ('ex-hosp-079', 114.3522, 30.5315),
    ('ex-hosp-099', 114.3748, 30.6261),
    ('ex-hosp-101', 114.4573, 30.5771),
    ('ex-hosp-107', 114.3468, 30.5305),
    ('ex-hosp-111', 114.3609, 30.5244),
    ('ex-hosp-113', 114.3615, 30.5235),
    ('ex-hosp-115', 114.3592, 30.5023),
    ('ex-hosp-125', 114.2655, 30.4676),
    ('ex-hosp-127', 114.2797, 30.6689),
    ('ex-hosp-181', 114.3933, 30.5370)
) AS v(code, lon, lat)
WHERE e.tenant_id = 123 AND e.deleted = false AND e.code = v.code
  AND e.longitude IS NULL;

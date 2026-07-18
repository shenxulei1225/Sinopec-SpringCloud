-- 试点站心：与 dynamicbusiness 设施种子经纬度一致（金桥 / 洛阳圣瑞）
-- 权威：区域总览用设施 lon/lat；局部系桥接用本表 origin_*

INSERT INTO scene_platform.coordinate_reference (
    id, scene_id,
    origin_lng, origin_lat, origin_height,
    geographic_crs_code, planet_shape, reference_frame_type, local_frame_type, engine_frame_type,
    axis_order, handedness, linear_unit, angular_unit,
    tenant_id, create_time, update_time, creator, updater, deleted
)
VALUES
    (
        16001, 10002,
        117.238056, 34.303056, 0,
        'EPSG:4326', 'WGS84', 'GEODETIC', 'ENU', 'Y_UP',
        'ENU', 'RIGHT', 'METER', 'DEGREE',
        1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE
    ),
    (
        16002, 10003,
        112.453900, 34.619700, 0,
        'EPSG:4326', 'WGS84', 'GEODETIC', 'ENU', 'Y_UP',
        'ENU', 'RIGHT', 'METER', 'DEGREE',
        1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system', FALSE
    )
ON CONFLICT (scene_id) DO UPDATE SET
    origin_lng = EXCLUDED.origin_lng,
    origin_lat = EXCLUDED.origin_lat,
    origin_height = EXCLUDED.origin_height,
    geographic_crs_code = EXCLUDED.geographic_crs_code,
    planet_shape = EXCLUDED.planet_shape,
    reference_frame_type = EXCLUDED.reference_frame_type,
    local_frame_type = EXCLUDED.local_frame_type,
    engine_frame_type = EXCLUDED.engine_frame_type,
    axis_order = EXCLUDED.axis_order,
    handedness = EXCLUDED.handedness,
    linear_unit = EXCLUDED.linear_unit,
    angular_unit = EXCLUDED.angular_unit,
    update_time = CURRENT_TIMESTAMP,
    updater = EXCLUDED.updater,
    deleted = FALSE;

SET search_path TO platform;

INSERT INTO platform_mobility_profile (
    id,
    display_name,
    allowed_network_kinds,
    layer,
    respect_doors,
    allow_portal_hop,
    tenant_id,
    creator,
    deleted
) VALUES
    ('person_walk', '人员步行', '["SITE"]'::jsonb, 'GROUND', TRUE, FALSE, 0, 'system', FALSE),
    ('ground_vehicle', '地面车辆', '["ROAD","SITE"]'::jsonb, 'GROUND', TRUE, TRUE, 0, 'system', FALSE),
    ('ground_robot', '地面机器人', '["SITE"]'::jsonb, 'GROUND', TRUE, FALSE, 0, 'system', FALSE),
    ('uav_low', '低空UAV', '["SITE","PERIMETER","PIPELINE"]'::jsonb, 'AIR', FALSE, TRUE, 0, 'system', FALSE)
ON CONFLICT (id) DO NOTHING;

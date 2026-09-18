-- 存量：把路网节点 payload.longitude / latitude 写到路网点位「坐标」。
-- 不改节点 JSON 键名；没有经纬度的节点不写假点。
-- 幂等。依赖 platform.platform_path_network 与 dynamicbusiness.ent_point_t1。

SET search_path TO dynamicbusiness;

WITH latest_node AS (
  SELECT DISTINCT ON (n->>'nodeId')
    n->>'nodeId' AS node_id,
    n->'payload'->>'longitude' AS longitude,
    n->'payload'->>'latitude' AS latitude,
    n->'payload'->>'height' AS height
  FROM platform.platform_path_network net
  CROSS JOIN LATERAL jsonb_array_elements(COALESCE(net.nodes, '[]'::jsonb)) n
  WHERE net.deleted = false
    AND NULLIF(n->'payload'->>'longitude', '') IS NOT NULL
    AND NULLIF(n->'payload'->>'latitude', '') IS NOT NULL
  ORDER BY n->>'nodeId', net.update_time DESC NULLS LAST
)
UPDATE dynamicbusiness.ent_point_t1 p
SET custom_fields = jsonb_set(
      COALESCE(p.custom_fields, '{}'::jsonb),
      '{FLD-LOC-014}',
      CASE
        WHEN NULLIF(src.height, '') IS NULL THEN
          jsonb_build_object(
            'longitude', src.longitude::float8,
            'latitude', src.latitude::float8
          )
        ELSE
          jsonb_build_object(
            'longitude', src.longitude::float8,
            'latitude', src.latitude::float8,
            'height', src.height::float8
          )
      END
    ),
    updater = 'repair-route-point-coordinate',
    update_time = CURRENT_TIMESTAMP
FROM latest_node src
WHERE p.deleted = false
  AND p.domain = 'route_network'
  AND p.custom_fields->>'FLD-PNT-002' = src.node_id;

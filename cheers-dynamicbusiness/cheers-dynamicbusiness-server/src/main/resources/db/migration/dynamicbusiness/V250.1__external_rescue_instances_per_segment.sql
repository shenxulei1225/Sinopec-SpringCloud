-- 有站的管线段各补三条外部救援记录：属地医院、属地消防救援站、属地公安。
-- 金桥段上已经有的不重复插。床位、人数、电话不编。站上没有坐标的，记录只留在名单上。

INSERT INTO dynamicbusiness.ent_external_rescue_resource_t1 (
    tenant_id, entity_type_code, model_id, name, code, status,
    pipeline_segment_id, longitude, latitude, creator, deleted
)
SELECT 1,
       'external_rescue_resource',
       m.id,
       m.name,
       'ext-' || s.segment_id || '-' || m.code,
       1,
       s.segment_id,
       CASE
           WHEN s.lon IS NULL THEN NULL
           WHEN m.code = 'ex-hospital' THEN s.lon + 0.020
           WHEN m.code = 'ex-fire' THEN s.lon + 0.002
           ELSE s.lon - 0.015
       END,
       CASE
           WHEN s.lat IS NULL THEN NULL
           WHEN m.code = 'ex-hospital' THEN s.lat + 0.003
           WHEN m.code = 'ex-fire' THEN s.lat + 0.016
           ELSE s.lat + 0.004
       END,
       'external-rescue-instances',
       false
FROM (
    SELECT r.target_entity_id AS segment_id,
           (array_agg(f.longitude ORDER BY f.id) FILTER (WHERE f.longitude IS NOT NULL))[1] AS lon,
           (array_agg(f.latitude ORDER BY f.id) FILTER (WHERE f.latitude IS NOT NULL))[1] AS lat
    FROM dynamicbusiness.dynamic_entity_relation_t1 r
    JOIN dynamicbusiness.ent_facility_t1 f
      ON f.id = r.source_entity_id AND f.deleted = false
    JOIN dynamicbusiness.ent_pipeline_t1 p
      ON p.id = r.target_entity_id AND p.deleted = false AND coalesce(p.parent_id, 0) > 0
    WHERE r.deleted = false
      AND r.field_code = 'pipeline_segment_ids'
      AND r.source_entity_type_code = 'facility'
    GROUP BY r.target_entity_id
) s
JOIN dynamicbusiness.dynamic_model m
  ON m.tenant_id = 1
 AND m.deleted = false
 AND m.entity_type_code = 'external_rescue_resource'
 AND m.code IN ('ex-hospital', 'ex-fire', 'ex-police')
WHERE NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.ent_external_rescue_resource_t1 x
    WHERE x.tenant_id = 1
      AND x.deleted = false
      AND x.pipeline_segment_id = s.segment_id
      AND x.name = m.name
);

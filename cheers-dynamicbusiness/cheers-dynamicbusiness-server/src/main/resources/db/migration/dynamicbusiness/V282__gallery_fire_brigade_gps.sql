-- 消防大队里，政务文件写了门牌、并且反查道路能对上的，写入 WGS84。
-- 硚口区政府行政执法公告：建一路18号。
-- 武昌区消防救援大队部门预算：巡司河街25号。
-- 洪山区珞桂路60号反查落到珞喻路，不写坐标。

SET search_path TO dynamicbusiness, public;

INSERT INTO dynamicbusiness.ent_external_rescue_resource_t123 (
    tenant_id, entity_type_code, model_id, name, code, status, facility_id,
    current_location, contact_phone, longitude, latitude, creator, updater, deleted
)
SELECT 123, 'external_rescue_resource', m.id, v.name, v.code, 1, 1,
       v.place, v.phone, v.lon, v.lat, 'V282', 'V282', false
FROM (
    VALUES
        ('ex-fire-qk-brigade', '武汉市硚口区消防救援大队', '武汉市硚口区建一路18号', '027-83770119', 114.2246::numeric, 30.5796::numeric),
        ('ex-fire-wc-brigade', '武汉市武昌区消防救援大队', '武汉市武昌区巡司河街25号', '027-88773600', 114.3055::numeric, 30.5215::numeric)
) AS v(code, name, place, phone, lon, lat)
JOIN dynamicbusiness.dynamic_model m
  ON m.tenant_id = 123 AND m.deleted = false AND m.code = 'ex-fire'
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.ent_external_rescue_resource_t123 e
    WHERE e.tenant_id = 123 AND e.deleted = false AND e.code = v.code
);

INSERT INTO dynamicbusiness.dynamic_category_entity_link_t123 (
    category_id, entity_id, entity_model_id, creator, tenant_id, entity_type_code
)
SELECT c.id, e.id, e.model_id, 'V282', 123, 'external_rescue_resource'
FROM dynamicbusiness.ent_external_rescue_resource_t123 e
JOIN dynamicbusiness.dynamic_category c
  ON c.tenant_id = 123 AND c.deleted = false AND c.code = 'ex-fire'
WHERE e.tenant_id = 123 AND e.deleted = false AND e.creator = 'V282'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_category_entity_link_t123 l
      WHERE l.tenant_id = 123 AND l.deleted = false
        AND l.entity_id = e.id AND l.category_id = c.id
        AND l.entity_type_code = 'external_rescue_resource'
  );

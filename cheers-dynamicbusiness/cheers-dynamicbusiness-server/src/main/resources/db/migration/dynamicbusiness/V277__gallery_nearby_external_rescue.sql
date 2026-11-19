-- 智慧管廊没有管线段。外部救援目录上的管线段归属会把整本目录判成受限，页面打不开。
-- 本脚本只撤掉这条归属，并写入政务网、医院官网已公开的单位。没有公开坐标的单位不填经纬度。

SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.dynamic_catalog_ownership
SET deleted = true, updater = 'V277', update_time = now()
WHERE tenant_id = 123
  AND deleted = false
  AND registry_code = 'external_rescue_resource'
  AND field_code = 'pipeline_segment_id';

UPDATE dynamicbusiness.dynamic_entity_type
SET dedicated_table_name = 'ent_emergency_t123', updater = 'V277', update_time = now()
WHERE tenant_id = 123
  AND deleted = false
  AND code = 'emergency'
  AND (dedicated_table_name IS NULL OR btrim(dedicated_table_name) = '');

INSERT INTO dynamicbusiness.dynamic_model_category_relation (
    model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
)
SELECT m.id, c.id, 'emergency', 0, 'V277', 123, m.code, c.code
FROM dynamicbusiness.dynamic_model m
JOIN dynamicbusiness.dynamic_category c
  ON c.tenant_id = 123 AND c.deleted = false AND c.code = 'emergency_root'
WHERE m.tenant_id = 123
  AND m.deleted = false
  AND m.id = 2607
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation r
      WHERE r.tenant_id = 123 AND r.deleted = false
        AND r.model_id = m.id AND r.category_id = c.id
  );

INSERT INTO dynamicbusiness.dynamic_model_category_relation_t123 (
    model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
)
SELECT model_id, category_id, entity_type_code, sort, creator, tenant_id, model_code, category_code
FROM dynamicbusiness.dynamic_model_category_relation
WHERE tenant_id = 123 AND deleted = false AND creator = 'V277' AND entity_type_code = 'emergency'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation_t123 t
      WHERE t.tenant_id = 123 AND t.deleted = false
        AND t.model_id = dynamic_model_category_relation.model_id
        AND t.category_id = dynamic_model_category_relation.category_id
  );

INSERT INTO dynamicbusiness.ent_external_rescue_resource_t123 (
    tenant_id, entity_type_code, model_id, name, code, status, facility_id,
    current_location, contact_phone, longitude, latitude,
    creator, updater, deleted
)
SELECT 123, 'external_rescue_resource', m.id, v.name, v.code, 1, 1,
       v.place, v.phone, v.lng, v.lat,
       'V277', 'V277', false
FROM (
    VALUES
        ('ex-hospital', 'ex-wh3-optics', '武汉市第三医院光谷院区',
         '武汉市洪山区关山大道216号', '027-65399492', 114.411085::numeric, 30.498964::numeric),
        ('ex-fire', 'ex-donghu-fire', '武汉东湖新技术开发区消防救援大队',
         '武汉市高新大道777号', NULL::varchar, NULL::numeric, NULL::numeric),
        ('ex-police', 'ex-guandong-police', '关东派出所',
         '武汉东湖新技术开发区纺织路8号', NULL::varchar, NULL::numeric, NULL::numeric),
        ('ex-police', 'ex-baoxie-police', '豹澥派出所',
         '武汉东湖新技术开发区高新五路（靠近三眼桥社区南门）', NULL::varchar, NULL::numeric, NULL::numeric)
) AS v(model_code, code, name, place, phone, lng, lat)
JOIN dynamicbusiness.dynamic_model m
  ON m.tenant_id = 123 AND m.deleted = false AND m.code = v.model_code
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.ent_external_rescue_resource_t123 e
    WHERE e.tenant_id = 123 AND e.deleted = false AND e.code = v.code
);

INSERT INTO dynamicbusiness.dynamic_category_entity_link_t123 (
    category_id, entity_id, entity_model_id, creator, tenant_id, entity_type_code
)
SELECT c.id, e.id, e.model_id, 'V277', 123, 'external_rescue_resource'
FROM dynamicbusiness.ent_external_rescue_resource_t123 e
JOIN dynamicbusiness.dynamic_category c
  ON c.tenant_id = 123 AND c.deleted = false
 AND c.code = CASE e.code
        WHEN 'ex-wh3-optics' THEN 'ex-hospital'
        WHEN 'ex-donghu-fire' THEN 'ex-fire'
        WHEN 'ex-guandong-police' THEN 'ex-police'
        WHEN 'ex-baoxie-police' THEN 'ex-police'
     END
WHERE e.tenant_id = 123 AND e.deleted = false AND e.creator = 'V277'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_category_entity_link_t123 l
      WHERE l.tenant_id = 123 AND l.deleted = false
        AND l.entity_id = e.id AND l.category_id = c.id
        AND l.entity_type_code = 'external_rescue_resource'
  );

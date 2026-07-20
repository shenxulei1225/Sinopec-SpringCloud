-- One-time: collapse net_*_vN into single net_*_site_published per facility SITE
INSERT INTO platform.platform_path_network (
  id, network_kind, facility_id, scope_id, status, version,
  display_name, description, applicable_equipment_types, nodes, edges,
  tenant_id, creator, create_time, updater, update_time, deleted
)
SELECT
  'net_' || facility_id || '_site_published',
  network_kind, facility_id, scope_id, 'PUBLISHED', version,
  COALESCE(NULLIF(btrim(display_name), ''), 'site'),
  description, applicable_equipment_types, nodes, edges,
  tenant_id, creator, create_time, updater, NOW(), false
FROM platform.platform_path_network p
WHERE p.status = 'PUBLISHED'
  AND p.deleted = false
  AND p.id ~ '_v[0-9]+$'
  AND p.version = (
    SELECT MAX(p2.version) FROM platform.platform_path_network p2
    WHERE p2.facility_id = p.facility_id
      AND p2.network_kind = p.network_kind
      AND p2.status = 'PUBLISHED'
      AND p2.deleted = false
      AND p2.id ~ '_v[0-9]+$'
  )
ON CONFLICT (id) DO UPDATE SET
  nodes = EXCLUDED.nodes,
  edges = EXCLUDED.edges,
  display_name = EXCLUDED.display_name,
  description = EXCLUDED.description,
  applicable_equipment_types = EXCLUDED.applicable_equipment_types,
  version = EXCLUDED.version,
  update_time = NOW();

DELETE FROM platform.platform_path_network
WHERE status = 'PUBLISHED'
  AND deleted = false
  AND id ~ '_v[0-9]+$';

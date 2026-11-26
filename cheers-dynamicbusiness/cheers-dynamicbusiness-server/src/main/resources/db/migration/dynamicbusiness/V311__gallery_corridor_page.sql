-- 场站管理（这一租户顶栏上也可能叫管廊管理）里增加「管廊数据」。
-- 已开通管廊或场站的管理包补上这一页。能看分区或三维场景的角色同时能看这一页。
SET search_path TO dynamicbusiness;

UPDATE tenant_portal_menu AS sibling
SET sort = sibling.sort + 1,
    updater = 'V311',
    update_time = CURRENT_TIMESTAMP
FROM tenant_portal_menu AS zone
WHERE zone.deleted = false
  AND zone.page_code = 'gallery-zone'
  AND sibling.deleted = false
  AND sibling.tenant_id = zone.tenant_id
  AND sibling.parent_id IS NOT DISTINCT FROM zone.parent_id
  AND sibling.sort > zone.sort
  AND NOT EXISTS (
      SELECT 1 FROM tenant_portal_menu existing
      WHERE existing.tenant_id = zone.tenant_id
        AND existing.page_code = 'gallery-corridor'
        AND existing.deleted = false
  );

INSERT INTO tenant_portal_menu (tenant_id, parent_id, title, page_code, sort, creator, updater)
SELECT zone.tenant_id, zone.parent_id, '管廊数据', 'gallery-corridor',
       COALESCE(zone.sort, 0) + 1, 'V311', 'V311'
FROM tenant_portal_menu zone
WHERE zone.deleted = false
  AND zone.page_code = 'gallery-zone'
  AND NOT EXISTS (
      SELECT 1 FROM tenant_portal_menu existing
      WHERE existing.tenant_id = zone.tenant_id
        AND existing.page_code = 'gallery-corridor'
        AND existing.deleted = false
  );

-- 挂到顶栏上的场站管理。这一租户如果把这一组改名为管廊管理，就挂在管廊管理下面。
INSERT INTO tenant_portal_menu (tenant_id, parent_id, title, page_code, sort, creator, updater)
SELECT station.tenant_id, station.id, '管廊数据', 'gallery-corridor',
       COALESCE((
           SELECT MAX(child.sort) FROM tenant_portal_menu child
           WHERE child.tenant_id = station.tenant_id
             AND child.parent_id = station.id
             AND child.deleted = false
       ), -1) + 1,
       'V311', 'V311'
FROM tenant_portal_menu station
WHERE station.deleted = false
  AND station.parent_id IS NULL
  AND station.title IN ('场站管理', '管廊管理')
  AND NOT EXISTS (
      SELECT 1 FROM tenant_portal_menu existing
      WHERE existing.tenant_id = station.tenant_id
        AND existing.page_code = 'gallery-corridor'
        AND existing.deleted = false
  )
  AND NOT EXISTS (
      SELECT 1 FROM tenant_portal_menu preferred
      WHERE preferred.tenant_id = station.tenant_id
        AND preferred.deleted = false
        AND preferred.parent_id IS NULL
        AND preferred.title = '场站管理'
        AND station.title <> '场站管理'
  );

UPDATE tenant_portal_menu AS item
SET parent_id = station.id,
    sort = COALESCE((
        SELECT MAX(child.sort) FROM tenant_portal_menu child
        WHERE child.tenant_id = station.tenant_id
          AND child.parent_id = station.id
          AND child.deleted = false
          AND child.id <> item.id
    ), -1) + 1,
    updater = 'V311',
    update_time = CURRENT_TIMESTAMP
FROM tenant_portal_menu AS station
WHERE item.deleted = false
  AND item.page_code = 'gallery-corridor'
  AND station.deleted = false
  AND station.tenant_id = item.tenant_id
  AND station.parent_id IS NULL
  AND station.title IN ('场站管理', '管廊管理')
  AND item.parent_id IS DISTINCT FROM station.id
  AND NOT EXISTS (
      SELECT 1 FROM tenant_portal_menu preferred
      WHERE preferred.tenant_id = station.tenant_id
        AND preferred.deleted = false
        AND preferred.parent_id IS NULL
        AND preferred.title = '场站管理'
        AND station.title <> '场站管理'
  );

UPDATE tenant_capability_pack
SET opened_page_codes = opened_page_codes || ',gallery-corridor',
    updater = 'V311',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND pack_code IN ('pipe-gallery', 'station')
  AND opened_page_codes IS NOT NULL
  AND opened_page_codes <> ''
  AND position('gallery-corridor' IN opened_page_codes) = 0
  AND tenant_id IN (
      SELECT menu.tenant_id FROM tenant_portal_menu menu
      WHERE menu.deleted = false
        AND menu.parent_id IS NULL
        AND menu.title IN ('场站管理', '管廊管理')
  );

INSERT INTO dynamic_page_role_grant (role_id, page_code, action, creator, updater, tenant_id)
SELECT DISTINCT source.role_id, 'gallery-corridor', 'view', 'V311', 'V311', source.tenant_id
FROM dynamic_page_role_grant source
WHERE source.deleted = false
  AND source.page_code IN ('gallery-zone', 'station-scene')
  AND source.action = 'view'
  AND source.tenant_id IN (
      SELECT menu.tenant_id FROM tenant_portal_menu menu
      WHERE menu.deleted = false
        AND menu.parent_id IS NULL
        AND menu.title IN ('场站管理', '管廊管理')
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_page_role_grant existing
      WHERE existing.role_id = source.role_id
        AND existing.page_code = 'gallery-corridor'
        AND existing.action = 'view'
        AND existing.tenant_id = source.tenant_id
        AND existing.deleted = false
  );

-- 顶栏「应急」增加「应急资源」，打开已有的三维地图页。
-- 已保存菜单的租户补这一项。已经勾过页面明细的应急包，把新页面补进开通清单。
-- 已有应急事件列表查看权的角色，补上这一页的查看。
SET search_path TO dynamicbusiness;

UPDATE tenant_capability_pack
SET opened_page_codes = opened_page_codes || ',emergency-resource-map',
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND pack_code = 'emergency'
  AND opened_page_codes IS NOT NULL
  AND opened_page_codes <> ''
  AND position('emergency-resource-map' IN opened_page_codes) = 0;

INSERT INTO tenant_portal_menu (
    tenant_id, parent_id, title, page_code, sort, creator, updater, deleted
)
SELECT p.tenant_id,
       p.id,
       '应急资源',
       'emergency-resource-map',
       COALESCE((
           SELECT MAX(c.sort) + 1
           FROM tenant_portal_menu c
           WHERE c.deleted = false
             AND c.parent_id = p.id
       ), 0),
       '1',
       '1',
       false
FROM tenant_portal_menu p
WHERE p.deleted = false
  AND p.title = '应急'
  AND (p.page_code IS NULL OR p.page_code = '')
  AND NOT EXISTS (
      SELECT 1
      FROM tenant_portal_menu x
      WHERE x.deleted = false
        AND x.tenant_id = p.tenant_id
        AND x.page_code = 'emergency-resource-map'
  );

INSERT INTO dynamic_page_role_grant (
    role_id, page_code, action, creator, updater, tenant_id
)
SELECT DISTINCT g.role_id, 'emergency-resource-map', 'view', '1', '1', g.tenant_id
FROM dynamic_page_role_grant g
WHERE g.deleted = false
  AND g.page_code = 'emergency-event'
  AND g.action = 'view'
  AND NOT EXISTS (
      SELECT 1
      FROM dynamic_page_role_grant x
      WHERE x.deleted = false
        AND x.role_id = g.role_id
        AND x.page_code = 'emergency-resource-map'
        AND x.action = 'view'
        AND x.tenant_id = g.tenant_id
  );

-- 应急顶栏收口后：角色页用预案/自定义表单编码，去掉旧物资只读、保障、指令模板授权行。
-- 已有「应急事件列表」查看权的角色，补上预案与自定义表单的查看和维护。
SET search_path TO dynamicbusiness;

INSERT INTO dynamic_page_role_grant (
    role_id, page_code, action, creator, updater, tenant_id
)
SELECT DISTINCT g.role_id, act.page_code, act.action, '1', '1', g.tenant_id
FROM dynamic_page_role_grant g
CROSS JOIN (
    VALUES
        ('emergency-plan', 'view'),
        ('emergency-plan', 'maintain'),
        ('emergency-custom-form', 'view'),
        ('emergency-custom-form', 'maintain')
) AS act(page_code, action)
WHERE g.deleted = false
  AND g.page_code = 'emergency-event'
  AND g.action = 'view'
  AND NOT EXISTS (
      SELECT 1
      FROM dynamic_page_role_grant x
      WHERE x.deleted = false
        AND x.role_id = g.role_id
        AND x.page_code = act.page_code
        AND x.action = act.action
        AND x.tenant_id = g.tenant_id
  );

UPDATE dynamic_page_role_grant
SET deleted = true,
    updater = '1',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND page_code IN (
      'emergency-resource',
      'emergency-guarantee',
      'emergency-command-template'
  );

-- 作业管理从应急分组拆成顶栏自己的一组。
-- 已开通且还没勾页面的租户，写上四页。已经保存过菜单的租户，补上这一组。
-- 租户管理员获得这四页的查看和维护，以及四本目录的查看和维护。
SET search_path TO dynamicbusiness;

UPDATE dynamicbusiness.dynamic_entity_type
SET group_name = '作业管理', updater = 'V313', update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code IN ('person_certificate', 'external_worker', 'work_permit', 'repair_field_record');

UPDATE dynamicbusiness.tenant_capability_pack
SET opened_page_codes = 'work-person-certificate,work-external-worker,work-permit,work-field-record',
    updater = 'V313',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND pack_code = 'work-management'
  AND (opened_page_codes IS NULL OR btrim(opened_page_codes) = '');

INSERT INTO dynamicbusiness.dynamic_page_role_grant (
    role_id, page_code, action, tenant_id, creator, updater, deleted
)
SELECT r.id, p.page_code, p.action, r.tenant_id, 'V313', 'V313', false
FROM public.system_role r
JOIN dynamicbusiness.tenant_capability_pack pack
  ON pack.tenant_id = r.tenant_id AND pack.pack_code = 'work-management' AND pack.deleted = false
CROSS JOIN (
    VALUES
        ('work-person-certificate', 'view'),
        ('work-person-certificate', 'maintain'),
        ('work-external-worker', 'view'),
        ('work-external-worker', 'maintain'),
        ('work-permit', 'view'),
        ('work-permit', 'maintain'),
        ('work-field-record', 'view'),
        ('work-field-record', 'maintain')
) AS p(page_code, action)
WHERE r.deleted = false
  AND r.code = 'tenant_admin'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_page_role_grant own
      WHERE own.role_id = r.id AND own.tenant_id = r.tenant_id
        AND own.page_code = p.page_code AND own.action = p.action AND own.deleted = false
  );

INSERT INTO dynamicbusiness.dynamic_catalog_role_grant (
    role_id, registry_code, operation, creator, updater, tenant_id, subject, deleted
)
SELECT r.id, c.registry_code, op.operation, 'V313', 'V313', r.tenant_id, sub.subject, false
FROM public.system_role r
JOIN dynamicbusiness.tenant_capability_pack pack
  ON pack.tenant_id = r.tenant_id AND pack.pack_code = 'work-management' AND pack.deleted = false
CROSS JOIN (
    VALUES ('person_certificate'), ('external_worker'), ('work_permit'), ('repair_field_record')
) AS c(registry_code)
CROSS JOIN (
    VALUES ('category'), ('model'), ('entity'), ('detail')
) AS sub(subject)
CROSS JOIN (
    VALUES ('query'), ('create'), ('update'), ('delete')
) AS op(operation)
WHERE r.deleted = false
  AND r.code = 'tenant_admin'
  AND EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_type e
      WHERE e.tenant_id = r.tenant_id AND e.code = c.registry_code AND e.deleted = false
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_catalog_role_grant own
      WHERE own.role_id = r.id AND own.tenant_id = r.tenant_id
        AND own.registry_code = c.registry_code AND own.subject = sub.subject
        AND own.operation = op.operation AND own.deleted = false
  );

INSERT INTO dynamicbusiness.tenant_portal_menu (
    tenant_id, parent_id, title, page_code, sort, creator, updater, deleted
)
SELECT pack.tenant_id, NULL, '作业管理', NULL,
       COALESCE(emer.sort, fallback.max_sort, 0) + 1,
       'V313', 'V313', false
FROM dynamicbusiness.tenant_capability_pack pack
LEFT JOIN LATERAL (
    SELECT m.sort
    FROM dynamicbusiness.tenant_portal_menu m
    WHERE m.tenant_id = pack.tenant_id AND m.deleted = false
      AND m.parent_id IS NULL AND m.page_code IS NULL AND m.title = '应急'
    ORDER BY m.id LIMIT 1
) emer ON TRUE
LEFT JOIN LATERAL (
    SELECT MAX(m.sort) AS max_sort
    FROM dynamicbusiness.tenant_portal_menu m
    WHERE m.tenant_id = pack.tenant_id AND m.deleted = false AND m.parent_id IS NULL
) fallback ON TRUE
WHERE pack.deleted = false
  AND pack.pack_code = 'work-management'
  AND EXISTS (
      SELECT 1 FROM dynamicbusiness.tenant_portal_menu m
      WHERE m.tenant_id = pack.tenant_id AND m.deleted = false
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.tenant_portal_menu own
      WHERE own.tenant_id = pack.tenant_id AND own.deleted = false
        AND own.parent_id IS NULL AND own.page_code IS NULL AND own.title = '作业管理'
  );

INSERT INTO dynamicbusiness.tenant_portal_menu (
    tenant_id, parent_id, title, page_code, sort, creator, updater, deleted
)
SELECT parent.tenant_id, parent.id, item.title, item.page_code, item.sort,
       'V313', 'V313', false
FROM dynamicbusiness.tenant_portal_menu parent
CROSS JOIN (
    VALUES
        ('人员资质', 'work-person-certificate', 1),
        ('外部施工人员', 'work-external-worker', 2),
        ('作业许可', 'work-permit', 3),
        ('现场作业记录', 'work-field-record', 4)
) AS item(title, page_code, sort)
WHERE parent.deleted = false
  AND parent.parent_id IS NULL
  AND parent.page_code IS NULL
  AND parent.title = '作业管理'
  AND parent.creator = 'V313'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.tenant_portal_menu own
      WHERE own.tenant_id = parent.tenant_id AND own.deleted = false
        AND own.page_code = item.page_code
  );

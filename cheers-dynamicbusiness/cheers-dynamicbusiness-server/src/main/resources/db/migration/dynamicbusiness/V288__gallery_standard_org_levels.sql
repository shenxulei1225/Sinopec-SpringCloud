-- 管廊组织改用产品标准层级：集团、省公司、分公司、作业区、实验基地。
-- 用户自己的节点是：中交集团 → 现有这家公司（分公司）。
-- 武汉管廊直接挂在这家公司上。不另建作业区。部门仍挂在管廊上。工单和报修归到这座管廊。
-- 不复制别的租户的人员。不改智慧巡检。

SET search_path TO dynamicbusiness;

INSERT INTO dynamic_model (
    code, name, entity_type_code, description, status, sort,
    creator, create_time, updater, update_time, deleted, tenant_id, governance_status)
SELECT src.code, src.name, src.entity_type_code, src.description, src.status, src.sort,
       'gallery-org-standard', CURRENT_TIMESTAMP, 'gallery-org-standard', CURRENT_TIMESTAMP, false, 123, src.governance_status
FROM dynamic_model src
WHERE src.tenant_id = 1
  AND src.deleted = false
  AND src.entity_type_code = 'organization'
  AND src.name IN ('集团', '省公司', '分公司', '作业区', '实验基地')
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_model own
      WHERE own.tenant_id = 123 AND own.code = src.code AND own.deleted = false);

INSERT INTO ent_organization_t123 (
    tenant_id, entity_type_code, model_id, name, code, parent_id, tree_path, sort, status,
    creator, updater, deleted)
SELECT 123, 'organization', m.id, '中交集团', 'org-cccc-group', 0, '/0/', 1, 1,
       'gallery-org-standard', 'gallery-org-standard', false
FROM dynamic_model m
WHERE m.tenant_id = 123 AND m.deleted = false AND m.entity_type_code = 'organization' AND m.name = '集团'
  AND NOT EXISTS (
      SELECT 1 FROM ent_organization_t123 o
      WHERE o.tenant_id = 123 AND o.deleted = false AND o.code = 'org-cccc-group');

UPDATE ent_organization_t123
SET tree_path = '/' || id || '/',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND code = 'org-cccc-group'
  AND tree_path IS DISTINCT FROM '/' || id || '/';

UPDATE ent_organization_t123 company
SET model_id = branch.id,
    parent_id = grp.id,
    tree_path = '/' || grp.id || '/' || company.id || '/',
    updater = 'gallery-org-standard',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model branch, ent_organization_t123 grp
WHERE company.tenant_id = 123
  AND company.deleted = false
  AND company.id = 1
  AND branch.tenant_id = 123 AND branch.deleted = false AND branch.name = '分公司'
  AND grp.tenant_id = 123 AND grp.deleted = false AND grp.code = 'org-cccc-group'
  AND (company.model_id IS DISTINCT FROM branch.id
       OR company.parent_id IS DISTINCT FROM grp.id
       OR company.tree_path IS DISTINCT FROM '/' || grp.id || '/' || company.id || '/');

-- 武汉管廊直接挂在这家公司上。先前多出来的武汉作业区去掉，避免下次启动再挂回去。
UPDATE ent_facility_t123 fac
SET organization_id = company.id,
    updater = 'gallery-org-standard',
    update_time = CURRENT_TIMESTAMP
FROM ent_organization_t123 company
WHERE fac.tenant_id = 123 AND fac.deleted = false AND fac.id = 1
  AND company.tenant_id = 123 AND company.deleted = false AND company.id = 1
  AND fac.organization_id IS DISTINCT FROM company.id;

UPDATE ent_organization_t123
SET deleted = true,
    updater = 'gallery-org-standard',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND code = 'org-wuhan-work-area';

UPDATE dynamic_management_unit_catalog
SET deleted = true,
    updater = 'gallery-org-standard',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false
  AND management_unit_model_id IN (
      SELECT id FROM dynamic_model
      WHERE tenant_id = 123 AND deleted = false AND entity_type_code = 'organization' AND name = '项目公司');

UPDATE dynamic_model
SET deleted = true,
    updater = 'gallery-org-standard',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 123 AND deleted = false AND entity_type_code = 'organization' AND name = '项目公司'
  AND NOT EXISTS (
      SELECT 1 FROM ent_organization_t123 o
      WHERE o.tenant_id = 123 AND o.deleted = false AND o.model_id = dynamic_model.id);

INSERT INTO dynamic_management_unit_catalog (
    management_unit_model_id, registry_code,
    creator, updater, deleted, tenant_id)
SELECT m.id, catalog.code, 'gallery-org-standard', 'gallery-org-standard', false, 123
FROM dynamic_model m
CROSS JOIN (VALUES ('organization'), ('facility'), ('department'), ('work_order'), ('maintanance')) AS catalog(code)
WHERE m.tenant_id = 123 AND m.deleted = false AND m.entity_type_code = 'organization'
  AND m.name IN ('集团', '省公司', '分公司', '作业区', '实验基地')
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_management_unit_catalog own
      WHERE own.tenant_id = 123 AND own.deleted = false
        AND own.management_unit_model_id = m.id AND own.registry_code = catalog.code);

INSERT INTO dynamic_management_method (
    registry_code, sort, creator, updater, deleted, tenant_id)
SELECT 'facility', 2, 'gallery-org-standard', 'gallery-org-standard', false, 123
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_management_method
    WHERE tenant_id = 123 AND deleted = false AND registry_code = 'facility');

INSERT INTO dynamic_catalog_ownership (
    registry_code, field_code, target_registry_code, required, multi_valued,
    creator, updater, deleted, tenant_id)
SELECT src.registry_code, 'facility_id', 'facility', true, false,
       'gallery-org-standard', 'gallery-org-standard', false, 123
FROM (VALUES ('work_order'), ('maintanance')) AS src(registry_code)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_catalog_ownership own
    WHERE own.tenant_id = 123 AND own.deleted = false
      AND own.registry_code = src.registry_code AND own.field_code = 'facility_id');

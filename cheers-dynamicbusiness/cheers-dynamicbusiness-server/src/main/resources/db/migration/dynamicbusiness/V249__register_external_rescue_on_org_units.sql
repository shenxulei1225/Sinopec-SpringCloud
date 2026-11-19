-- 外部救援资源已经声明按管线段归属，但没有登记到组织类型的可管目录。
-- 受约束的目录只认任职：组织类型没登记这本目录时，分类和对象都返回没有该操作权限。
-- 和站内应急资源登记在同一批组织类型上（集团、省公司、分公司、作业区、实验基地）。

INSERT INTO dynamicbusiness.dynamic_management_unit_catalog (
    management_unit_model_id, registry_code, creator, tenant_id, deleted
)
SELECT c.management_unit_model_id, 'external_rescue_resource', 'external-rescue-manageable', c.tenant_id, false
FROM dynamicbusiness.dynamic_management_unit_catalog c
WHERE c.deleted = false
  AND c.registry_code = 'emergency_resource'
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_management_unit_catalog x
      WHERE x.deleted = false
        AND x.tenant_id = c.tenant_id
        AND x.management_unit_model_id = c.management_unit_model_id
        AND x.registry_code = 'external_rescue_resource'
  );

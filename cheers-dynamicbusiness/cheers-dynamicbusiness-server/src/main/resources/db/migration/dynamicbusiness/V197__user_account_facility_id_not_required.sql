-- 用户目录上的所属场站若仍挂着（历史从站场级误挂），不得再当必填。
-- 平台租户管理创建登录账号不写所属场站；租户内组织若要用场站，另在本租户组织能力里处理。

UPDATE dynamicbusiness.dynamic_entity_type_base_field
SET required = false,
    updater = 'V197',
    update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'user_account'
  AND field_code = 'facility_id'
  AND deleted = false
  AND required = true;

UPDATE dynamicbusiness.dynamic_model_field_assignment a
SET required = false,
    updater = 'V197',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.dynamic_model m
WHERE a.model_id = m.id
  AND m.entity_type_code = 'user_account'
  AND m.deleted = false
  AND a.field_code = 'facility_id'
  AND a.deleted = false
  AND a.required = true;

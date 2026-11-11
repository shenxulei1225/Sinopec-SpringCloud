-- 用户目录是租户级登录身份，不是站场级业务数据。
-- 平台租户管理按租户读写登录账号，不依赖所属场站。
-- 所属场站只用于租户内组织等站场级目录（如 department）。
-- V165 原文即为 NETWORK；历史被改成 FACILITY 后把站场闸门错误套到登录账号上。

UPDATE dynamicbusiness.dynamic_entity_type
SET work_scope = 'NETWORK',
    updater = 'V196',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'user_account'
  AND deleted = false
  AND COALESCE(work_scope, '') <> 'NETWORK';

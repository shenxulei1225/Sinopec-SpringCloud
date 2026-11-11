-- V196 之后用户目录又被改回 FACILITY，租户管理拉账号触发「站场级列表须指定所属场站」，管理员列表被拖空。
-- 登录账号目录只认 NETWORK；开通底座时也会再校正一次。

UPDATE dynamicbusiness.dynamic_entity_type
SET work_scope = 'NETWORK',
    updater = 'V198',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'user_account'
  AND deleted = false
  AND COALESCE(work_scope, '') <> 'NETWORK';

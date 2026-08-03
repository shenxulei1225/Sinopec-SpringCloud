-- 样例全网目录：运营区域 / 管线站场等标为 NETWORK，避免全网管理模式下误出门禁选站
UPDATE dynamicbusiness.dynamic_entity_type
SET work_scope = 'NETWORK'
WHERE deleted = false
  AND code IN (
    'region',
    'facility'
  )
  AND COALESCE(work_scope, 'FACILITY') <> 'NETWORK';

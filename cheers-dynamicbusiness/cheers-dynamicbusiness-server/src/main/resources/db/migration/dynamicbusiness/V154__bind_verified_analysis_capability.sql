SET search_path TO dynamicbusiness;

-- 只有这一条动作能与工作区 detection 官方资料中的 LIQUID_LEAKAGE 明确对应。
-- 其余存量智能分析动作继续暴露“未绑定能力”，禁止按名称猜厂商算法编号。
UPDATE ent_action_t1 action
SET analysis_capability_id = capability.id,
    updater = 'flyway-v154',
    update_time = CURRENT_TIMESTAMP
FROM intelligent_analysis_capability capability
WHERE action.tenant_id = 1
  AND action.deleted = FALSE
  AND action.code = 'action-fa4e3366ba554a73a7cd50234aaf78d8'
  AND capability.tenant_id = action.tenant_id
  AND capability.code = 'LIQUID_LEAKAGE'
  AND capability.deleted = FALSE;

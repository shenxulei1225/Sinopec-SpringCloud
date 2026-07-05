SET search_path TO platform;

INSERT INTO platform_capability_pack (
    id, display_name, domain, version, required_engines,
    orchestration_ref, default_policy_template_id, manifest, enabled, tenant_id
)
VALUES (
    'cap.patrol.periodic.v1',
    '巡检 · 周期排程 v1',
    'patrol',
    '1.0.0',
    '["scheduling"]'::jsonb,
    'orch.standard_expand_solve_persist_v1',
    'tpl_policy_sched_periodic_v1',
    '{"capabilityPackId":"cap.patrol.periodic.v1","displayName":"巡检 · 周期排程 v1","domain":"patrol","version":"1.0.0","orchestrationRef":"orch.standard_expand_solve_persist_v1","defaultPolicyTemplateId":"tpl_policy_sched_periodic_v1","requiredEngines":["scheduling"],"handlerIds":["expand.standard_v1"],"suggestedEntityRoles":["task","checkpoint"]}'::jsonb,
    TRUE,
    0
)
ON CONFLICT (id) DO NOTHING;

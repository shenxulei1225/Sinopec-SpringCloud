SET search_path TO platform;

INSERT INTO platform_policy_template (id, display_name, domain, version, default_spec, enabled, tenant_id)
VALUES (
    'tpl_policy_sched_periodic_v1',
    '周期排程策略模板 v1',
    'scheduling',
    '1.0.0',
    '{"schedulingSpec":{"mode":"weekly","horizonStart":null,"horizonEnd":null,"conflictStrategy":"defer_slot"}}'::jsonb,
    TRUE,
    0
)
ON CONFLICT (id) DO NOTHING;

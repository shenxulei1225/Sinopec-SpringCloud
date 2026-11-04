-- ============================================================================
-- action · 21 巡检可控制阀门的共用动作
-- 负责：智慧巡检租户的动作库里写入打开阀门、关闭阀门。
-- 阀门和 DN400 阀门共用这两条。不按每一台设备复制。
-- 不负责：不写执行手段，不写指令对照，不写节点标识。
-- 巡检还没有点位对照，写点先留空。对上写点之前，下发拒绝。
-- 阀室、阀组区是场所，不建动作。
-- ============================================================================

SET search_path TO dynamicbusiness;

SELECT to_regclass('dynamicbusiness.ent_action_t1') IS NOT NULL AS inspection_action_ready \gset

\if :inspection_action_ready

INSERT INTO ent_action_t1 (
  tenant_id, entity_type_code, model_id, name, code, status,
  execution_means, param_slots_json, child_action_ids_json, is_composite,
  action_domain, creator, deleted
)
SELECT
  1,
  'action',
  model.id,
  v.name,
  v.code,
  1,
  '[]'::jsonb,
  '{"version":2,"methods":[]}'::jsonb,
  '[]'::jsonb,
  false,
  'DEVICE',
  'seed',
  false
FROM (
  VALUES
    ('act-valve-on', '打开阀门'),
    ('act-valve-off', '关闭阀门')
) AS v(code, name)
JOIN dynamic_model model
  ON model.deleted = false
 AND model.tenant_id = 1
 AND model.code = 'action_device'
WHERE NOT EXISTS (
  SELECT 1 FROM ent_action_t1 action
  WHERE action.deleted = false
    AND action.tenant_id = 1
    AND action.code = v.code
);

UPDATE ent_action_t1 AS action
SET attrs = COALESCE(action.attrs, '{}'::jsonb) || jsonb_build_object(
      'industrialEquipment',
      jsonb_build_object(
        'equipmentModelCodes', jsonb_build_array(valve.code, dn400.code),
        'writes', '[]'::jsonb
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model AS valve,
     dynamic_model AS dn400
WHERE action.tenant_id = 1
  AND action.deleted = false
  AND action.code IN ('act-valve-on', 'act-valve-off')
  AND action.action_domain = 'DEVICE'
  AND valve.deleted = false
  AND valve.tenant_id = 1
  AND valve.name = '阀门'
  AND dn400.deleted = false
  AND dn400.tenant_id = 1
  AND dn400.name = 'DN400 阀门';

DO $$
DECLARE
  linked_count integer;
BEGIN
  SELECT count(*) INTO linked_count
  FROM ent_action_t1
  WHERE tenant_id = 1
    AND deleted = false
    AND code IN ('act-valve-on', 'act-valve-off')
    AND action_domain = 'DEVICE'
    AND execution_means = '[]'::jsonb
    AND jsonb_array_length(attrs->'industrialEquipment'->'equipmentModelCodes') = 2
    AND jsonb_array_length(attrs->'industrialEquipment'->'writes') = 0;
  IF linked_count <> 2 THEN
    RAISE EXCEPTION '巡检阀门动作未按约定写入，当前 % 条', linked_count;
  END IF;
END $$;

\endif

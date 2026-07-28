-- ============================================================================
-- 管廊 tenant：构筑物细类只保留 structure 模型；zone 只保留根/段/防火区/舱室
-- psql -v corridor_tenant_id=2 -f dynamic_corridor_zone_structure_split.sql
-- ============================================================================

SET search_path TO dynamicbusiness;

\if :{?corridor_tenant_id}
\else
\set corridor_tenant_id 2
\endif

-- 口部 / 风亭 / 坑等：不得作为 zone 模型出现在分区能力中
UPDATE dynamic_model
SET deleted = true, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = :corridor_tenant_id
  AND entity_type_code = 'zone'
  AND deleted = false
  AND code IN (
    'MODEL-ZONE-UT-INTERSECTION',
    'MODEL-ZONE-UT-PERSONNEL-ACCESS',
    'MODEL-ZONE-UT-INVERTED-SIPHON',
    'MODEL-ZONE-UT-SUBSTATION',
    'MODEL-ZONE-UT-HOIST-PORT',
    'MODEL-ZONE-UT-LEAD-OUT-DOOR',
    'MODEL-ZONE-UT-MECH-EXHAUST',
    'MODEL-ZONE-UT-MECH-AIR-INLET',
    'MODEL-ZONE-UT-END-SHAFT',
    'MODEL-ZONE-UT-NATURAL-AIR-INLET',
    'MODEL-ZONE-UT-SUMP-PIT',
    'MODEL-ZONE-UT-DRAIN-PIT'
  );

-- 段 / 防火区 / 舱室：不得作为 structure 模型（仅 zone 或仅 structure 二选一）
UPDATE dynamic_model
SET deleted = true, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = :corridor_tenant_id
  AND entity_type_code = 'structure'
  AND deleted = false
  AND (
    code IN (
      'MODEL-STRUCTURE-UT-TUNNEL-SEGMENT',
      'MODEL-STRUCTURE-UT-FIRE-COMPARTMENT'
    )
    OR code LIKE 'MODEL-STRUCTURE-UT-%-CABIN'
    OR code = 'MODEL-STRUCTURE-UT-POWER-INFO-CABIN'
    OR code = 'MODEL-STRUCTURE-UT-INTEGRATED-PIPE-CABIN'
  );

-- 若历史误导入：软删挂在「构筑物 zone 模型」上的分区实例
UPDATE ent_zone z
SET deleted = true, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
FROM dynamic_model dm
WHERE z.model_id = dm.id
  AND z.tenant_id = :corridor_tenant_id
  AND z.deleted = false
  AND dm.tenant_id = :corridor_tenant_id
  AND dm.deleted = true
  AND dm.entity_type_code = 'zone';

UPDATE dynamic_model_field_assignment a
SET deleted = true, updater = 'corridor-seed', update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE a.model_id = m.id
  AND a.tenant_id = :corridor_tenant_id
  AND a.deleted = false
  AND m.tenant_id = :corridor_tenant_id
  AND m.deleted = true;

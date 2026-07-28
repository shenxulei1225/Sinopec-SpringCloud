-- ============================================================================
-- 管廊 tenant · 字段库：自 tenant 1 全量复制（字段 + FIELD 分组 + 分组关系）
-- 排除项由实施方在界面自行删除；本脚本不做业务裁剪。
-- psql -v corridor_tenant_id=2 -f dynamic_corridor_field_library.sql
-- ============================================================================

SET search_path TO dynamicbusiness;

\if :{?corridor_tenant_id}
\else
\set corridor_tenant_id 2
\endif

-- ---------- 字段（tenant 1 → 管廊 tenant；FLD-UT-* 等本租户独有 code 保留） ----------

INSERT INTO dynamic_field (
  code, name, type, unit, description, source, status, max_relations,
  index_strategy, options, provider_code, semantic_type, tenant_id, creator
)
SELECT
  f.code, f.name, f.type, f.unit, f.description, f.source, f.status, f.max_relations,
  f.index_strategy, f.options, f.provider_code, f.semantic_type,
  :corridor_tenant_id, 'corridor-seed'
FROM dynamic_field f
WHERE f.deleted = false
  AND f.tenant_id = 1
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  unit = EXCLUDED.unit,
  description = EXCLUDED.description,
  source = EXCLUDED.source,
  status = EXCLUDED.status,
  max_relations = EXCLUDED.max_relations,
  index_strategy = EXCLUDED.index_strategy,
  options = EXCLUDED.options,
  provider_code = EXCLUDED.provider_code,
  semantic_type = EXCLUDED.semantic_type,
  deleted = false,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- 历史软删残留行：若同 code 已有启用行，删掉 deleted=true 的重复行
DELETE FROM dynamic_field d
WHERE d.tenant_id = :corridor_tenant_id
  AND d.deleted = true
  AND EXISTS (
    SELECT 1 FROM dynamic_field a
    WHERE a.tenant_id = d.tenant_id
      AND a.code = d.code
      AND a.deleted = false
  );

-- 若仅有软删行、无启用行，从 tenant 1 恢复该 code
UPDATE dynamic_field tgt
SET
  deleted = false,
  name = src.name,
  type = src.type,
  unit = src.unit,
  description = src.description,
  source = src.source,
  status = src.status,
  max_relations = src.max_relations,
  index_strategy = src.index_strategy,
  options = src.options,
  provider_code = src.provider_code,
  semantic_type = src.semantic_type,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_field src
WHERE src.tenant_id = 1
  AND src.deleted = false
  AND tgt.tenant_id = :corridor_tenant_id
  AND tgt.code = src.code
  AND tgt.deleted = true
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_field a
    WHERE a.tenant_id = :corridor_tenant_id
      AND a.code = src.code
      AND a.deleted = false
      AND a.id <> tgt.id
  );

-- ---------- 字段分组（FIELD） ----------

INSERT INTO dynamic_group (
  group_type, code, name, description, parent_id, path, level, sort, status, tenant_id, creator
)
SELECT
  g.group_type, g.code, g.name, g.description, NULL, g.path, g.level, g.sort, g.status,
  :corridor_tenant_id, 'corridor-seed'
FROM dynamic_group g
WHERE g.deleted = false
  AND g.tenant_id = 1
  AND g.group_type = 'FIELD'
ON CONFLICT (tenant_id, group_type, code) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  description = EXCLUDED.description,
  sort = EXCLUDED.sort,
  status = EXCLUDED.status,
  level = EXCLUDED.level,
  path = EXCLUDED.path,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- ---------- 分组 ↔ 字段 ----------

INSERT INTO dynamic_group_relation (
  group_type, group_code, target_code, group_id, target_id, sort, tenant_id, creator
)
SELECT
  r.group_type,
  r.group_code,
  r.target_code,
  g2.id,
  f2.id,
  r.sort,
  :corridor_tenant_id,
  'corridor-seed'
FROM dynamic_group_relation r
INNER JOIN dynamic_group g1
  ON g1.id = r.group_id
 AND g1.tenant_id = 1
 AND g1.deleted = false
INNER JOIN dynamic_group g2
  ON g2.code = g1.code
 AND g2.tenant_id = :corridor_tenant_id
 AND g2.group_type = r.group_type
 AND g2.deleted = false
INNER JOIN dynamic_field f2
  ON f2.code = r.target_code
 AND f2.tenant_id = :corridor_tenant_id
 AND f2.deleted = false
WHERE r.deleted = false
  AND r.tenant_id = 1
  AND r.group_type = 'FIELD'
ON CONFLICT (group_type, group_code, target_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  group_id = EXCLUDED.group_id,
  target_id = EXCLUDED.target_id,
  sort = EXCLUDED.sort,
  deleted = false,
  updater = 'corridor-seed',
  update_time = CURRENT_TIMESTAMP;

-- 恢复曾被软删的分组关系（与 tenant1 仍存在的绑定对齐）
UPDATE dynamic_group_relation tgt
SET deleted = false,
    group_id = src_map.group_id,
    target_id = src_map.target_id,
    sort = src_map.sort,
    updater = 'corridor-seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT
    r.group_type,
    r.group_code,
    r.target_code,
    g2.id AS group_id,
    f2.id AS target_id,
    r.sort
  FROM dynamic_group_relation r
  INNER JOIN dynamic_group g2
    ON g2.code = r.group_code
   AND g2.tenant_id = :corridor_tenant_id
   AND g2.group_type = r.group_type
   AND g2.deleted = false
  INNER JOIN dynamic_field f2
    ON f2.code = r.target_code
   AND f2.tenant_id = :corridor_tenant_id
   AND f2.deleted = false
  WHERE r.deleted = false
    AND r.tenant_id = 1
    AND r.group_type = 'FIELD'
) src_map
WHERE tgt.tenant_id = :corridor_tenant_id
  AND tgt.group_type = src_map.group_type
  AND tgt.group_code = src_map.group_code
  AND tgt.target_code = src_map.target_code
  AND tgt.deleted = true;

-- ---------- 基础字段 · library_field_id 指向本租户字段库 ----------

UPDATE dynamic_entity_type_base_field b
SET library_field_id = f.id,
    updater = 'corridor-seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE b.tenant_id = :corridor_tenant_id
  AND b.deleted = false
  AND f.tenant_id = :corridor_tenant_id
  AND f.deleted = false
  AND (
    b.field_code = f.code
    OR (
      b.field_code LIKE 'FLD-BASE-structure-%'
      AND f.code = REPLACE(b.field_code, 'FLD-BASE-structure-', 'FLD-BASE-zone-')
    )
  );

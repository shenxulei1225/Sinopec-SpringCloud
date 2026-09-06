-- 按已有场站实体补「分类即实体」节点。
-- 管什么：设施实体已在 ent_facility_t*，但 dynamic_category 没有对应 facility 节点时，
--         Who 分类树画不出来（金桥厂区 / 洛阳圣瑞 / 东营油库）。
-- 不管什么：不新建实体；不改查数；不在读树时兜底。
-- 禁止：再 createCategory(isEntity) 造第二条实体。
--
-- 用法：psql 对本库执行（search_path=dynamicbusiness）。可重复执行。

SET search_path TO dynamicbusiness;

-- 福建 4 站已有节点，只补 link 上的类型编码。
UPDATE dynamic_category_entity_link_t1
SET entity_type_code = 'facility'
WHERE deleted = false
  AND entity_id IN (46, 47, 48, 49)
  AND (entity_type_code IS NULL OR btrim(entity_type_code) = '');

INSERT INTO dynamic_category (
    parent_id, name, code, category_type_code, tree_path, level, sort, status,
    description, creator, create_time, updater, update_time, deleted, tenant_id, parent_code
)
SELECT
    root.id,
    need.entity_name,
    need.cat_code,
    'facility',
    '/3265/0/',
    2,
    need.sort,
    1,
    NULL,
    'backfill-facility-category',
    NOW(),
    'backfill-facility-category',
    NOW(),
    false,
    1,
    'facility_root'
FROM (VALUES
    (44::bigint, '金桥厂区'::varchar, 'FAC-CAT-JINQIAO'::varchar, 10),
    (45, '洛阳圣瑞', 'FAC-CAT-LUOYANG-SHENGRUI', 20),
    (1, '东营油库', 'FAC-CAT-DEPOT-001', 30)
) AS need(entity_id, entity_name, cat_code, sort)
CROSS JOIN (
    SELECT id FROM dynamic_category
    WHERE deleted = false AND category_type_code = 'facility' AND code = 'facility_root'
    LIMIT 1
) AS root
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_category c
    WHERE c.deleted = false AND c.category_type_code = 'facility' AND c.code = need.cat_code
)
AND NOT EXISTS (
    SELECT 1 FROM dynamic_category_entity_link_t1 l
    WHERE l.deleted = false AND l.entity_id = need.entity_id
      AND (l.entity_type_code = 'facility' OR l.entity_type_code IS NULL OR btrim(l.entity_type_code) = '')
);

UPDATE dynamic_category c
SET tree_path = '/3265/' || c.id || '/',
    parent_id = (SELECT id FROM dynamic_category WHERE code = 'facility_root' AND deleted = false LIMIT 1),
    parent_code = 'facility_root',
    level = 2
WHERE c.deleted = false
  AND c.category_type_code = 'facility'
  AND c.code IN ('FAC-CAT-JINQIAO', 'FAC-CAT-LUOYANG-SHENGRUI', 'FAC-CAT-DEPOT-001');

INSERT INTO dynamic_category_entity_link_t1 (
    category_id, entity_id, entity_model_id, creator, create_time, updater, update_time,
    deleted, tenant_id, entity_type_code, domain
)
SELECT
    c.id,
    f.id,
    f.model_id,
    'backfill-facility-category',
    NOW(),
    'backfill-facility-category',
    NOW(),
    false,
    1,
    'facility',
    NULL
FROM dynamic_category c
JOIN ent_facility_t1 f ON f.deleted = false AND (
    (c.code = 'FAC-CAT-JINQIAO' AND f.id = 44) OR
    (c.code = 'FAC-CAT-LUOYANG-SHENGRUI' AND f.id = 45) OR
    (c.code = 'FAC-CAT-DEPOT-001' AND f.id = 1)
)
WHERE c.deleted = false AND c.category_type_code = 'facility'
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_category_entity_link_t1 l
      WHERE l.deleted = false AND l.category_id = c.id
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_category_entity_link_t1 l
      WHERE l.deleted = false AND l.entity_id = f.id
        AND (l.entity_type_code = 'facility' OR l.entity_type_code IS NULL OR btrim(l.entity_type_code) = '')
  );

-- 作业区 → 场站：筛选连线裁树认分类—分类，不认实体 id
INSERT INTO dynamic_category_category_relation_t1 (
    host_category_id, host_category_type_code,
    member_category_id, member_category_type_code,
    sort, creator, create_time, updater, update_time, deleted, tenant_id
)
SELECT
    r.id, 'region',
    c.id, 'facility',
    1,
    'backfill-facility-category',
    NOW(),
    'backfill-facility-category',
    NOW(),
    false,
    1
FROM (VALUES
    ('REG-CAT-OP-EAST-JQ', 'FAC-CAT-JINQIAO'),
    ('REG-CAT-OP-EAST-LY', 'FAC-CAT-LUOYANG-SHENGRUI'),
    ('REG-CAT-OP-SD-DY', 'FAC-CAT-DEPOT-001')
) AS m(region_code, fac_code)
JOIN dynamic_category r ON r.deleted = false AND r.category_type_code = 'region' AND r.code = m.region_code
JOIN dynamic_category c ON c.deleted = false AND c.category_type_code = 'facility' AND c.code = m.fac_code
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_category_category_relation_t1 rel
    WHERE rel.deleted = false
      AND rel.host_category_id = r.id
      AND rel.member_category_id = c.id
);

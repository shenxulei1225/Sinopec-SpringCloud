-- ============================================================================
-- zhgl_import_temp：修正「3#防火区(3668)」误挂在「2#防火区(3667)」下的分类树
--
-- 问题：system_category.region 中 entity_id=3668 的 parent 指向 3667（防火区），
--       应为管廊段「光谷五路北」(3658)，与 1#/2#/4# 一致。
--
-- 用法：
--   psql -h 127.0.0.1 -U postgres -d zhgl_import_temp -v ON_ERROR_STOP=1 \
--     -f repair_zhgl_misnested_fire_category.sql
--
-- 修完后请重新运行 export + dynamic_corridor_spatial_entities.generated.sql
-- （export 脚本另有 normalize 兜底，源库修正后可与之一致、便于其它消费方读 zhgl）
-- ============================================================================

\echo '>> 修复前：3668 及其子节点'
SELECT c.id AS category_id, c.entity_id, c.name, c.parent_id, p.entity_id AS parent_entity_id,
       p_br.name AS parent_entity_name, c.tree_path, c.level
FROM system_category c
LEFT JOIN system_category p ON p.id = c.parent_id AND p.deleted = false
LEFT JOIN biz_region p_br ON p_br.id = p.entity_id
WHERE c.deleted = false
  AND c.category_type_code = 'region'
  AND (c.entity_id = 3668 OR c.parent_id = (
    SELECT id FROM system_category
    WHERE deleted = false AND category_type_code = 'region' AND entity_id = 3668
    LIMIT 1
  ))
ORDER BY c.level, c.id;

BEGIN;

-- 1) 仅 3# 防火区分类行：parent 改为「光谷五路北」(entity 3658 → category 3772)
UPDATE system_category fire3
SET parent_id = seg_cat.id,
    tree_path = '光谷五路北/3#防火区',
    level = 2,
    updater = 'corridor-fix-3668',
    update_time = CURRENT_TIMESTAMP
FROM system_category seg_cat
WHERE fire3.deleted = false
  AND fire3.category_type_code = 'region'
  AND fire3.entity_id = 3668
  AND seg_cat.deleted = false
  AND seg_cat.category_type_code = 'region'
  AND seg_cat.entity_id = 3658;

-- 2) 3# 下舱室/集水坑等：只修正 tree_path、level，parent 仍指向 3# 分类行
UPDATE system_category child
SET tree_path = REPLACE(
      child.tree_path,
      '光谷五路北/2#防火区/3#防火区/',
      '光谷五路北/3#防火区/'
    ),
    level = GREATEST(child.level - 1, 1),
    updater = 'corridor-fix-3668',
    update_time = CURRENT_TIMESTAMP
WHERE child.deleted = false
  AND child.category_type_code = 'region'
  AND child.tree_path LIKE '光谷五路北/2#防火区/3#防火区/%';

-- 3) 3# 下子节点 parent 须为 3# 分类行（3782 / entity 3668），不得挂在段(3772)上
UPDATE system_category child
SET parent_id = fire3.id,
    updater = 'corridor-fix-3668',
    update_time = CURRENT_TIMESTAMP
FROM system_category fire3
WHERE child.deleted = false
  AND child.category_type_code = 'region'
  AND child.tree_path LIKE '光谷五路北/3#防火区/%'
  AND child.entity_id <> 3668
  AND fire3.deleted = false
  AND fire3.category_type_code = 'region'
  AND fire3.entity_id = 3668
  AND child.parent_id IS DISTINCT FROM fire3.id;

COMMIT;

\echo '>> 修复后：光谷五路北下 1#/2#/3#/4# 防火区 parent 应为段 3658'
SELECT c.entity_id, br.name, p.entity_id AS parent_entity_id, p_br.name AS parent_name,
       p_br.model_id AS parent_model, c.tree_path, c.level
FROM system_category c
JOIN biz_region br ON br.id = c.entity_id
JOIN system_category p ON p.id = c.parent_id AND p.deleted = false
LEFT JOIN biz_region p_br ON p_br.id = p.entity_id
WHERE c.deleted = false
  AND c.category_type_code = 'region'
  AND br.model_id = 74
  AND p.entity_id = 3658
  AND br.name IN ('1#防火区', '2#防火区', '3#防火区', '4#防火区')
ORDER BY br.sort, br.id;

\echo '>> 不应再存在「防火区 parent 仍是防火区」'
SELECT c.entity_id, br.name, p.entity_id AS parent_entity_id, p_br.name AS parent_name
FROM system_category c
JOIN biz_region br ON br.id = c.entity_id AND br.model_id = 74
JOIN system_category p ON p.id = c.parent_id
JOIN biz_region p_br ON p_br.id = p.entity_id AND p_br.model_id = 74
WHERE c.deleted = false AND c.category_type_code = 'region';

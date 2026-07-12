-- 按 parent_id 链重建 dynamic_category.tree_path（id 路径，格式 /1/677/282/）
-- 用途：纠正历史「按名称拼接」或改名后 tree_path 与子节点前缀不一致的问题
-- 可重复执行（幂等）

SET search_path TO dynamicbusiness;

WITH RECURSIVE chain AS (
    SELECT
        id,
        parent_id,
        '/' || id::text || '/' AS expected_path
    FROM dynamic_category
    WHERE deleted = false
      AND (parent_id IS NULL OR parent_id = 0)
    UNION ALL
    SELECT
        c.id,
        c.parent_id,
        p.expected_path || c.id::text || '/'
    FROM dynamic_category c
    JOIN chain p ON c.parent_id = p.id
    WHERE c.deleted = false
)
UPDATE dynamic_category c
SET
    tree_path = e.expected_path,
    updater = 'seed-repair-tree-path',
    update_time = CURRENT_TIMESTAMP
FROM chain e
WHERE c.id = e.id
  AND c.deleted = false
  AND coalesce(c.tree_path, '') <> e.expected_path;

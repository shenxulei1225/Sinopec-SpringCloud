-- 父业务不存在时 parent_id 视为无效，提升为根业务（与 list-tree 孤儿归并逻辑一致）

SET search_path TO dynamicbusiness;

UPDATE dynamicbusiness.dynamic_business AS b
SET parent_id = NULL,
    updater = 'flyway',
    update_time = CURRENT_TIMESTAMP
WHERE b.deleted = false
  AND b.parent_id IS NOT NULL
  AND b.parent_id <> 0
  AND NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dynamic_business AS p
    WHERE p.id = b.parent_id
      AND p.deleted = false
  );

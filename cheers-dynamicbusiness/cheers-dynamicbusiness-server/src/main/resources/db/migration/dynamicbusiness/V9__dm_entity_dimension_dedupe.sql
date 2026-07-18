-- 清理 dm_entity_dimension 同 scope 下重复的有效行（保留 id 最大的一条）

SET search_path TO dynamicbusiness;

WITH ranked AS (
    SELECT id,
           ROW_NUMBER() OVER (
               PARTITION BY tenant_id, entity_type_code, dimension_kind, COALESCE(perspective_id, '')
               ORDER BY id DESC
           ) AS rn
    FROM dynamicbusiness.dm_entity_dimension
    WHERE deleted = false
)
UPDATE dynamicbusiness.dm_entity_dimension AS d
SET deleted = true,
    update_time = CURRENT_TIMESTAMP
FROM ranked AS r
WHERE d.id = r.id
  AND r.rn > 1;

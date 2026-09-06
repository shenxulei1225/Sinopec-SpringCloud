-- V12 只清了 parent 指向另一条 entity_type 的行。
-- 应急资源 / 应急队伍的 parent_id 误指 dynamic_group.id（分组表「应急管理」），
-- 侧栏把它当成上级目录，分组名也没写上。
-- 本版：凡 parent_id 落在分组表、且不是另一条数据类型的，清 parent、补 group_name。

SET search_path TO dynamicbusiness;

UPDATE dynamicbusiness.dynamic_entity_type AS et
SET group_name = COALESCE(NULLIF(trim(et.group_name), ''), g.name),
    parent_id = NULL
FROM dynamicbusiness.dynamic_group AS g
WHERE et.parent_id = g.id
  AND et.deleted = false
  AND COALESCE(g.deleted, false) = false
  AND NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dynamic_entity_type AS p
    WHERE p.id = et.parent_id
      AND p.deleted = false
  );

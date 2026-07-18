-- 数据类型由 parent_id 层级改为 group_name 分组；历史子级迁移为同级分组项
ALTER TABLE dynamicbusiness.dynamic_entity_type
    ADD COLUMN IF NOT EXISTS group_name character varying(100);

COMMENT ON COLUMN dynamicbusiness.dynamic_entity_type.group_name IS '数据类型分组名（仅用于导航归类，无继承语义）';

UPDATE dynamicbusiness.dynamic_entity_type AS child
SET group_name = parent.name,
    parent_id = NULL
FROM dynamicbusiness.dynamic_entity_type AS parent
WHERE child.parent_id = parent.id
  AND child.deleted = false
  AND parent.deleted = false;

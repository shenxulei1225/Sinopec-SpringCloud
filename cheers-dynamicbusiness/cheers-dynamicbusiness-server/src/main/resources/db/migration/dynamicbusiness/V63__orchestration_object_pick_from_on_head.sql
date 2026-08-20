-- V63: 编排规则收到编排头 object_pick_from；删掉与布局重复的筛选槽/谁槽表。
-- 开哪些栏、展示配置、工作区盖章仍只认 dm_data_tab_layout。

SET search_path TO dynamicbusiness, public;

ALTER TABLE dynamicbusiness.dm_five_w_orchestration
  ADD COLUMN IF NOT EXISTS object_pick_from VARCHAR(32);

UPDATE dynamicbusiness.dm_five_w_orchestration o
SET object_pick_from = 'CATEGORY_NODE'
FROM dynamicbusiness.dm_five_w_filter_layout f
WHERE f.entity_type_code = o.entity_type_code
  AND f.tenant_id = o.tenant_id
  AND COALESCE(f.deleted, false) = false
  AND COALESCE(o.deleted, false) = false
  AND f.object_pick_from = 'CATEGORY_NODE';

UPDATE dynamicbusiness.dm_five_w_orchestration
SET object_pick_from = 'LIST_ROW'
WHERE object_pick_from IS NULL
   OR object_pick_from = '';

COMMENT ON COLUMN dynamicbusiness.dm_five_w_orchestration.object_pick_from IS
  '当前对象从哪来：LIST_ROW=点列表这一行；CATEGORY_NODE=点树上这个节点（分类即对象）';

ALTER TABLE dynamicbusiness.dm_five_w_orchestration
  ALTER COLUMN object_pick_from SET DEFAULT 'LIST_ROW';
ALTER TABLE dynamicbusiness.dm_five_w_orchestration
  ALTER COLUMN object_pick_from SET NOT NULL;

DROP TABLE IF EXISTS dynamicbusiness.dm_five_w_filter_layout;
DROP TABLE IF EXISTS dynamicbusiness.dm_five_w_who_layout;

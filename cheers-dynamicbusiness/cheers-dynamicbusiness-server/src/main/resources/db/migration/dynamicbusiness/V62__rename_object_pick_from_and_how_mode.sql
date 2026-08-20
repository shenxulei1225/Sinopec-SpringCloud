-- V62: 编排槽「当前对象从哪来」列改名；取值与如何栏模式改人话名
-- 已执行的 V51/V57 不可改，本版只 Rename + UPDATE 存量值。

SET search_path TO dynamicbusiness, public;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = 'dm_five_w_who_layout'
      AND column_name = 'entity_id_rule'
  ) THEN
    ALTER TABLE dynamicbusiness.dm_five_w_who_layout
      RENAME COLUMN entity_id_rule TO object_pick_from;
  END IF;

  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = 'dm_five_w_filter_layout'
      AND column_name = 'entity_id_rule'
  ) THEN
    ALTER TABLE dynamicbusiness.dm_five_w_filter_layout
      RENAME COLUMN entity_id_rule TO object_pick_from;
  END IF;
END $$;

UPDATE dynamicbusiness.dm_five_w_who_layout
SET object_pick_from = 'LIST_ROW'
WHERE object_pick_from = 'rowSelection';

UPDATE dynamicbusiness.dm_five_w_filter_layout
SET object_pick_from = 'CATEGORY_NODE'
WHERE object_pick_from = 'categoryLinkedEntity';

UPDATE dynamicbusiness.dm_five_w_who_layout
SET object_pick_from = 'CATEGORY_NODE'
WHERE object_pick_from = 'categoryLinkedEntity';

UPDATE dynamicbusiness.dm_five_w_filter_layout
SET object_pick_from = 'LIST_ROW'
WHERE object_pick_from = 'rowSelection';

UPDATE dynamicbusiness.dm_five_w_orchestration
SET how_mode = 'FOLLOW_WHAT'
WHERE how_mode = 'AFTER_WHAT_ITEM';

COMMENT ON COLUMN dynamicbusiness.dm_five_w_who_layout.object_pick_from IS
  '当前对象从哪来：LIST_ROW=点列表这一行；空=不算选中对象';
COMMENT ON COLUMN dynamicbusiness.dm_five_w_filter_layout.object_pick_from IS
  '当前对象从哪来：CATEGORY_NODE=点树上这个节点（分类即对象）；空=只筛选';

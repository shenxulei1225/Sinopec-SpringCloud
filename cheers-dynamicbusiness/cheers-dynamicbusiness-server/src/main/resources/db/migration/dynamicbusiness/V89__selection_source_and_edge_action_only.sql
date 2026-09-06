-- V89: P2 清理旧协议键。编排头统一 selection_source；关系扩展统一 edgeAction。

SET search_path TO dynamicbusiness, public;

DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = 'dm_catalog_orchestration'
      AND column_name = 'object_pick_from'
  ) THEN
    ALTER TABLE dynamicbusiness.dm_catalog_orchestration
      RENAME COLUMN object_pick_from TO selection_source;
  END IF;
END $$;

UPDATE dynamicbusiness.dm_catalog_orchestration
SET selection_source = 'LIST_ROW'
WHERE selection_source IS NULL
   OR btrim(selection_source) = '';

ALTER TABLE dynamicbusiness.dm_catalog_orchestration
  ALTER COLUMN selection_source SET DEFAULT 'LIST_ROW';
ALTER TABLE dynamicbusiness.dm_catalog_orchestration
  ALTER COLUMN selection_source SET NOT NULL;

COMMENT ON COLUMN dynamicbusiness.dm_catalog_orchestration.selection_source IS
  '当前记录来源：LIST_ROW=点列表这一行；CATEGORY_NODE=点树上这个节点';

-- 旧 key edgeRole → edgeAction（仅在 edgeAction 缺失时拷贝）
UPDATE dynamicbusiness.dm_data_tab_column_relation
SET relation_meta = jsonb_set(
      COALESCE(relation_meta, '{}'::jsonb),
      '{edgeAction}',
      to_jsonb(relation_meta ->> 'edgeRole'),
      true
    )
WHERE relation_meta IS NOT NULL
  AND NOT (relation_meta ? 'edgeAction')
  AND relation_meta ? 'edgeRole'
  AND btrim(COALESCE(relation_meta ->> 'edgeRole', '')) <> '';

-- 统一只保留 edgeAction
UPDATE dynamicbusiness.dm_data_tab_column_relation
SET relation_meta = relation_meta - 'edgeRole'
WHERE relation_meta ? 'edgeRole';

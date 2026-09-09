-- 栏间关系 relation_meta 字段名收敛：linkKeys -> refFieldCodes
-- 目标：移除历史旧键，统一到 refFieldCodes，避免读路径继续兼容两套命名。

UPDATE dynamicbusiness.dm_data_tab_column_relation
SET relation_meta = (
    jsonb_set(
        COALESCE(relation_meta, '{}'::jsonb),
        '{refFieldCodes}',
        COALESCE(relation_meta -> 'refFieldCodes', relation_meta -> 'linkKeys')
    ) - 'linkKeys'
)
WHERE relation_meta IS NOT NULL
  AND relation_meta ? 'linkKeys';

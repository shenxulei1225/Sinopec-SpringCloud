-- 与创建目录实例化布局相同：分类/型号盖本份清单第 1 块，实体/详情盖第 2 块。
-- 只写栏上还没有区域编号的行。清单不够两块的不写，缺口继续暴露，读路径不猜。

UPDATE dynamicbusiness.dm_data_tab_layout AS layout_row
SET column_meta = jsonb_set(
        COALESCE(layout_row.column_meta, '{}'::jsonb),
        '{columnSection}',
        to_jsonb(
            CASE
                WHEN layout_row.column_kind IN ('CATEGORY', 'MODEL')
                    THEN NULLIF(btrim(workbench.settings_json -> 'sections' -> 0 ->> 'id'), '')
                WHEN layout_row.column_kind IN ('ENTITY', 'DETAIL')
                    THEN NULLIF(btrim(workbench.settings_json -> 'sections' -> 1 ->> 'id'), '')
            END
        ),
        true
    )
FROM dynamicbusiness.dm_workbench_layout AS workbench
WHERE layout_row.layout_id = workbench.id
  AND COALESCE(layout_row.deleted, false) = false
  AND NULLIF(btrim(COALESCE(layout_row.column_meta ->> 'columnSection', '')), '') IS NULL
  AND CASE
        WHEN layout_row.column_kind IN ('CATEGORY', 'MODEL')
            THEN NULLIF(btrim(workbench.settings_json -> 'sections' -> 0 ->> 'id'), '')
        WHEN layout_row.column_kind IN ('ENTITY', 'DETAIL')
            THEN NULLIF(btrim(workbench.settings_json -> 'sections' -> 1 ->> 'id'), '')
      END IS NOT NULL;

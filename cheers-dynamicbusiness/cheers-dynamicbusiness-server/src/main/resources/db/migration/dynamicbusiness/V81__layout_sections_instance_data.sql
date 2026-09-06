-- 区域清单改为某一数据目录的页面布局上的实例数据。
-- 常用台账模板写入 filter / who / what；栏上 columnSection 改为这些区域编号。
-- 只改库一次。读路径不得把旧 FILTER/OBJECT/WHAT 映射成新区。

UPDATE dynamicbusiness.dm_data_tab_layout
SET column_meta = jsonb_set(
        column_meta,
        '{columnSection}',
        to_jsonb(
            CASE column_meta->>'columnSection'
                WHEN 'FILTER' THEN 'filter'
                WHEN 'OBJECT' THEN 'who'
                WHEN 'WHAT' THEN 'what'
                ELSE column_meta->>'columnSection'
            END
        )
    )
WHERE column_meta IS NOT NULL
  AND column_meta ? 'columnSection'
  AND column_meta->>'columnSection' IN ('FILTER', 'OBJECT', 'WHAT');

UPDATE dynamicbusiness.dm_workbench_layout
SET settings_json = (
    COALESCE(settings_json, '{}'::jsonb)
    || jsonb_build_object(
        'sections', jsonb_build_array(
            jsonb_build_object('id', 'filter', 'name', 'filter'),
            jsonb_build_object('id', 'who', 'name', 'who'),
            jsonb_build_object('id', 'what', 'name', 'what')
        )
    )
    || jsonb_build_object(
        'sectionHidden',
        jsonb_strip_nulls(jsonb_build_object(
            'filter', CASE
                WHEN COALESCE(settings_json->'sectionHidden'->>'FILTER', '') IN ('true', 't')
                    THEN 'true'::jsonb
            END,
            'who', CASE
                WHEN COALESCE(settings_json->'sectionHidden'->>'OBJECT', '') IN ('true', 't')
                    THEN 'true'::jsonb
            END,
            'what', CASE
                WHEN COALESCE(settings_json->'sectionHidden'->>'WHAT', '') IN ('true', 't')
                    THEN 'true'::jsonb
            END
        ))
    )
);

COMMENT ON COLUMN dynamicbusiness.dm_workbench_layout.settings_json IS
    '布局头设置：sections=本份布局的区域清单；sectionHidden=按区域编号隐藏';

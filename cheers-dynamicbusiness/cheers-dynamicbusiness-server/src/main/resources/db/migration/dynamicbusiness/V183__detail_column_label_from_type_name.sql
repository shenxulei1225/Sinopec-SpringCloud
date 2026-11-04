-- 创建数据目录时，详情栏名称曾沿用模版占位「区段C栏」或「区段栏」。
-- 所属类型已经写在这一栏上。这里只把仍是这两个占位名的详情栏改成所属类型的中文名。
-- 手改过的名称不在此列。模版行没有所属类型，也不会被改。可重复执行。

UPDATE dynamicbusiness.dm_data_tab_layout AS layout_row
SET column_meta = jsonb_set(
        layout_row.column_meta,
        '{label}',
        to_jsonb(
            COALESCE(
                (
                    SELECT NULLIF(btrim(entity_type.name), '')
                    FROM dynamicbusiness.dynamic_entity_type AS entity_type
                    WHERE entity_type.deleted = false
                      AND entity_type.tenant_id = layout_row.tenant_id
                      AND entity_type.code = btrim(layout_row.column_meta ->> 'entityEntityTypeCode')
                    LIMIT 1
                ),
                btrim(layout_row.column_meta ->> 'entityEntityTypeCode')
            )
        ),
        true),
    updater = 'detail-label-from-type',
    update_time = CURRENT_TIMESTAMP
WHERE layout_row.deleted = false
  AND layout_row.column_kind = 'DETAIL'
  AND btrim(COALESCE(layout_row.column_meta ->> 'label', '')) IN ('区段C栏', '区段栏')
  AND NULLIF(btrim(COALESCE(layout_row.column_meta ->> 'entityEntityTypeCode', '')), '') IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dm_workbench_layout AS layout_head
      WHERE layout_head.id = layout_row.layout_id
        AND layout_head.is_template = true
  );

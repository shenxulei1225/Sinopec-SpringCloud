-- V108: 清理栏间关系历史孤儿边（端点不在当前布局身份集合）
-- 背景：旧流程曾出现布局栏已删除/改身份，但关系端点仍残留，导致「图上看不到、保存时报端点不在集合」。
-- 目标：一次性删除历史无效边；后续在线由 service 在读/写时自动清理。

SET search_path TO dynamicbusiness, public;

WITH live_layout_identity AS (
    SELECT
        l.tenant_id,
        l.layout_id,
        CASE
            WHEN upper(btrim(coalesce(l.column_kind, ''))) = 'CATEGORY' THEN
                CASE
                    WHEN btrim(coalesce(l.tab_id, '')) = '' THEN NULL
                    WHEN lower(btrim(l.tab_id)) = 'default' THEN NULL
                    WHEN lower(btrim(l.tab_id)) LIKE '%-default' THEN NULL
                    WHEN btrim(coalesce(l.column_meta ->> 'columnKey', '')) = '' THEN NULL
                    WHEN lower(btrim(l.column_meta ->> 'columnKey')) = 'default' THEN NULL
                    WHEN lower(btrim(l.column_meta ->> 'columnKey')) LIKE '%-default' THEN NULL
                    ELSE 'CATEGORY:' || btrim(l.column_meta ->> 'columnKey') || ':' || btrim(l.tab_id)
                END
            WHEN upper(btrim(coalesce(l.column_kind, ''))) IN ('MODEL', 'ENTITY', 'DETAIL') THEN
                CASE
                    WHEN btrim(coalesce(l.tab_id, '')) = '' THEN NULL
                    WHEN lower(btrim(l.tab_id)) = 'default' THEN NULL
                    WHEN lower(btrim(l.tab_id)) LIKE '%-default' THEN NULL
                    ELSE upper(btrim(l.column_kind)) || ':' || btrim(l.tab_id)
                END
            ELSE NULL
        END AS identity
    FROM dynamicbusiness.dm_data_tab_layout l
    WHERE l.deleted = FALSE
),
orphan_relation AS (
    SELECT r.id
    FROM dynamicbusiness.dm_data_tab_column_relation r
    WHERE r.deleted = FALSE
      AND (
        NOT EXISTS (
            SELECT 1
            FROM live_layout_identity v
            WHERE v.tenant_id = r.tenant_id
              AND v.layout_id = r.layout_id
              AND v.identity IS NOT NULL
              AND v.identity = btrim(coalesce(r.from_column_identity, ''))
        )
        OR NOT EXISTS (
            SELECT 1
            FROM live_layout_identity v
            WHERE v.tenant_id = r.tenant_id
              AND v.layout_id = r.layout_id
              AND v.identity IS NOT NULL
              AND v.identity = btrim(coalesce(r.to_column_identity, ''))
        )
      )
)
DELETE FROM dynamicbusiness.dm_data_tab_column_relation t
WHERE t.id IN (SELECT id FROM orphan_relation);

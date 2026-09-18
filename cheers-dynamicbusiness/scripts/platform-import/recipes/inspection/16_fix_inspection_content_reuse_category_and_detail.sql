-- 修复 inspection-content（REUSE）历史残留分类与详情跟随缺口
-- 目标：
-- 1) 清理历史 inspection_content 自有分类残留（REUSE 应复用 inspection_item 分类）
-- 2) 为 inspection-content 补齐默认详情跟随：ENTITY -> DETAIL（edgeAction=detail_follow）
--
-- 适用范围：tenant_id = 1，entity_type_code = inspection-content
-- 执行建议：先在测试库验证，再在目标环境执行

SET search_path TO dynamicbusiness;

BEGIN;

-- A. 软删 inspection_content 历史残留分类根（避免 REUSE 看起来有自有分类）
UPDATE dynamic_category
SET deleted = TRUE,
    updater = 'script:16_fix_inspection_content_reuse_category_and_detail',
    update_time = NOW()
WHERE tenant_id = 1
  AND category_type_code = 'inspection_content'
  AND deleted = FALSE;

-- B1. 若 data layout(59) 缺 DETAIL 栏，补一个标准 detail 栏
INSERT INTO dm_data_tab_layout (
    tenant_id,
    entity_type_code,
    column_kind,
    tab_id,
    props_id,
    enabled,
    column_meta,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    layout_id
)
SELECT
    1,
    'inspection-content',
    'DETAIL',
    'detail',
    NULL,
    TRUE,
    '{"label":"详情","columnSection":"what"}'::jsonb,
    'script:16_fix_inspection_content_reuse_category_and_detail',
    NOW(),
    'script:16_fix_inspection_content_reuse_category_and_detail',
    NOW(),
    FALSE,
    59
WHERE NOT EXISTS (
    SELECT 1
    FROM dm_data_tab_layout x
    WHERE x.tenant_id = 1
      AND x.layout_id = 59
      AND x.column_kind = 'DETAIL'
      AND x.tab_id = 'detail'
      AND x.deleted = FALSE
);

-- B2. data layout(59) 补 ENTITY:inspection_item -> DETAIL:detail 的 detail_follow
INSERT INTO dm_data_tab_column_relation (
    entity_type_code,
    edge_id,
    from_column_identity,
    to_column_identity,
    relation_kind,
    from_type_code,
    to_type_code,
    relation_meta,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    tenant_id,
    layout_id
)
SELECT
    'inspection-content',
    'ed-filter-59-507-detail',
    'ENTITY:inspection_item',
    'DETAIL:detail',
    'ENTITY_DETAIL',
    'inspection_item',
    'inspection_item',
    '{"edgeAction":"detail_follow","enabledInteractions":[]}'::jsonb,
    'script:16_fix_inspection_content_reuse_category_and_detail',
    NOW(),
    'script:16_fix_inspection_content_reuse_category_and_detail',
    NOW(),
    FALSE,
    1,
    59
WHERE EXISTS (
    SELECT 1
    FROM dm_data_tab_layout e
    WHERE e.tenant_id = 1
      AND e.layout_id = 59
      AND e.column_kind = 'ENTITY'
      AND e.tab_id = 'inspection_item'
      AND e.deleted = FALSE
)
AND EXISTS (
    SELECT 1
    FROM dm_data_tab_layout d
    WHERE d.tenant_id = 1
      AND d.layout_id = 59
      AND d.column_kind = 'DETAIL'
      AND d.tab_id = 'detail'
      AND d.deleted = FALSE
)
AND NOT EXISTS (
    SELECT 1
    FROM dm_data_tab_column_relation r
    WHERE r.tenant_id = 1
      AND r.layout_id = 59
      AND r.entity_type_code = 'inspection-content'
      AND r.from_column_identity = 'ENTITY:inspection_item'
      AND r.to_column_identity = 'DETAIL:detail'
      AND r.relation_kind = 'ENTITY_DETAIL'
      AND r.deleted = FALSE
);

-- B3. model layout(69) 补 ENTITY:inspection_item -> DETAIL:detail 的 detail_follow
INSERT INTO dm_data_tab_column_relation (
    entity_type_code,
    edge_id,
    from_column_identity,
    to_column_identity,
    relation_kind,
    from_type_code,
    to_type_code,
    relation_meta,
    creator,
    create_time,
    updater,
    update_time,
    deleted,
    tenant_id,
    layout_id
)
SELECT
    'inspection-content',
    'ed-filter-69-632-631',
    'ENTITY:inspection_item',
    'DETAIL:detail',
    'ENTITY_DETAIL',
    'inspection_item',
    'inspection_item',
    '{"edgeAction":"detail_follow","enabledInteractions":[]}'::jsonb,
    'script:16_fix_inspection_content_reuse_category_and_detail',
    NOW(),
    'script:16_fix_inspection_content_reuse_category_and_detail',
    NOW(),
    FALSE,
    1,
    69
WHERE EXISTS (
    SELECT 1
    FROM dm_data_tab_layout e
    WHERE e.tenant_id = 1
      AND e.layout_id = 69
      AND e.column_kind = 'ENTITY'
      AND e.tab_id = 'inspection_item'
      AND e.deleted = FALSE
)
AND EXISTS (
    SELECT 1
    FROM dm_data_tab_layout d
    WHERE d.tenant_id = 1
      AND d.layout_id = 69
      AND d.column_kind = 'DETAIL'
      AND d.tab_id = 'detail'
      AND d.deleted = FALSE
)
AND NOT EXISTS (
    SELECT 1
    FROM dm_data_tab_column_relation r
    WHERE r.tenant_id = 1
      AND r.layout_id = 69
      AND r.entity_type_code = 'inspection-content'
      AND r.from_column_identity = 'ENTITY:inspection_item'
      AND r.to_column_identity = 'DETAIL:detail'
      AND r.relation_kind = 'ENTITY_DETAIL'
      AND r.deleted = FALSE
);

COMMIT;

-- 执行后建议核对：
-- 1) inspection_content 分类是否已无 active 行
-- 2) layout 59/69 是否均存在 ENTITY:inspection_item -> DETAIL:detail (detail_follow)

-- 详情收成可连线的栏：补编号、迁到第三块、写默认「实体→详情」筛选线。
-- 只改库一次。读路径不得用整页选中冒充跟随。

SET search_path TO dynamicbusiness;

UPDATE dynamicbusiness.dm_data_tab_layout
SET tab_id = 'detail',
    updater = 'flyway-v86',
    update_time = CURRENT_TIMESTAMP
WHERE COALESCE(deleted, false) = false
  AND upper(trim(column_kind)) = 'DETAIL'
  AND (
    tab_id IS NULL
    OR trim(tab_id) = ''
    OR lower(trim(tab_id)) = 'default'
  );

DO $$
DECLARE
  rec record;
  sections jsonb;
  what_id text;
  meta jsonb;
BEGIN
  FOR rec IN
    SELECT l.id AS layout_id, l.settings_json, t.id AS row_id, t.column_meta, t.enabled
    FROM dynamicbusiness.dm_workbench_layout l
    JOIN dynamicbusiness.dm_data_tab_layout t
      ON t.layout_id = l.id
     AND COALESCE(t.deleted, false) = false
     AND upper(trim(t.column_kind)) = 'DETAIL'
    WHERE COALESCE(l.deleted, false) = false
  LOOP
    sections := rec.settings_json->'sections';
    what_id := NULL;
    IF jsonb_typeof(sections) = 'array' THEN
      SELECT trim(value->>'id')
      INTO what_id
      FROM jsonb_array_elements(sections) AS value
      WHERE trim(COALESCE(value->>'id', '')) = 'what'
      LIMIT 1;
    END IF;
    IF what_id IS NULL OR what_id = '' THEN
      CONTINUE;
    END IF;
    meta := COALESCE(rec.column_meta, '{}'::jsonb);
    meta := jsonb_set(meta, '{columnSection}', to_jsonb(what_id), true);
    IF coalesce(trim(meta->>'label'), '') = '' THEN
      meta := jsonb_set(meta, '{label}', '"详情"', true);
    END IF;
    UPDATE dynamicbusiness.dm_data_tab_layout
    SET column_meta = meta,
        enabled = true,
        updater = 'flyway-v86',
        update_time = CURRENT_TIMESTAMP
    WHERE id = rec.row_id;
  END LOOP;
END $$;

INSERT INTO dynamicbusiness.dm_data_tab_column_relation (
    tenant_id,
    layout_id,
    entity_type_code,
    edge_id,
    from_column_identity,
    to_column_identity,
    relation_kind,
    from_type_code,
    to_type_code,
    relation_meta,
    creator,
    deleted
)
SELECT
    e.tenant_id,
    e.layout_id,
    COALESCE(NULLIF(trim(e.entity_type_code), ''), NULLIF(trim(d.entity_type_code), ''), 'unknown'),
    'ed-filter-' || e.layout_id || '-' || e.id || '-' || d.id,
    'ENTITY:' || trim(e.tab_id),
    'DETAIL:' || trim(d.tab_id),
    'ENTITY_DETAIL',
    COALESCE(
      NULLIF(trim(e.column_meta->>'entityEntityTypeCode'), ''),
      trim(e.tab_id)
    ),
    COALESCE(
      NULLIF(trim(d.column_meta->>'entityEntityTypeCode'), ''),
      NULLIF(trim(e.column_meta->>'entityEntityTypeCode'), ''),
      trim(e.tab_id)
    ),
    jsonb_build_object('edgeRole', 'filter', 'enabledInteractions', '[]'::jsonb),
    'V86',
    FALSE
FROM dynamicbusiness.dm_data_tab_layout e
JOIN dynamicbusiness.dm_data_tab_layout d
  ON d.layout_id = e.layout_id
 AND COALESCE(d.deleted, false) = false
 AND upper(trim(d.column_kind)) = 'DETAIL'
 AND d.enabled IS DISTINCT FROM false
 AND d.tab_id IS NOT NULL
 AND trim(d.tab_id) <> ''
 AND lower(trim(d.tab_id)) <> 'default'
WHERE COALESCE(e.deleted, false) = false
  AND upper(trim(e.column_kind)) = 'ENTITY'
  AND e.enabled IS DISTINCT FROM false
  AND e.tab_id IS NOT NULL
  AND trim(e.tab_id) <> ''
  AND lower(trim(e.tab_id)) <> 'default'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dm_data_tab_column_relation r
    WHERE r.deleted = FALSE
      AND r.layout_id = e.layout_id
      AND r.to_column_identity = 'DETAIL:' || trim(d.tab_id)
      AND r.relation_kind IN ('ENTITY_DETAIL', 'CATEGORY_DETAIL', 'MODEL_DETAIL')
      AND COALESCE(r.relation_meta->>'edgeRole', 'filter') = 'filter'
  );

INSERT INTO dynamicbusiness.dm_data_tab_column_relation (
    tenant_id,
    layout_id,
    entity_type_code,
    edge_id,
    from_column_identity,
    to_column_identity,
    relation_kind,
    from_type_code,
    to_type_code,
    relation_meta,
    creator,
    deleted
)
SELECT
    c.tenant_id,
    c.layout_id,
    COALESCE(NULLIF(trim(c.entity_type_code), ''), NULLIF(trim(d.entity_type_code), ''), 'unknown'),
    'cd-filter-' || c.layout_id || '-' || c.id || '-' || d.id,
    'CATEGORY:' || trim(COALESCE(c.column_meta->>'columnKey', '')) || ':' || trim(c.tab_id),
    'DETAIL:' || trim(d.tab_id),
    'CATEGORY_DETAIL',
    COALESCE(NULLIF(trim(c.column_meta->>'categoryTypeCode'), ''), trim(c.tab_id)),
    COALESCE(
      NULLIF(trim(d.column_meta->>'entityEntityTypeCode'), ''),
      NULLIF(trim(c.column_meta->>'categoryTypeCode'), ''),
      trim(c.tab_id)
    ),
    jsonb_build_object('edgeRole', 'filter', 'enabledInteractions', '[]'::jsonb),
    'V86',
    FALSE
FROM dynamicbusiness.dm_data_tab_layout c
JOIN dynamicbusiness.dm_data_tab_layout d
  ON d.layout_id = c.layout_id
 AND COALESCE(d.deleted, false) = false
 AND upper(trim(d.column_kind)) = 'DETAIL'
 AND d.enabled IS DISTINCT FROM false
 AND d.tab_id IS NOT NULL
 AND trim(d.tab_id) <> ''
WHERE COALESCE(c.deleted, false) = false
  AND upper(trim(c.column_kind)) = 'CATEGORY'
  AND c.enabled IS DISTINCT FROM false
  AND c.tab_id IS NOT NULL
  AND trim(c.tab_id) <> ''
  AND coalesce(trim(c.column_meta->>'columnKey'), '') <> ''
  AND NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dm_data_tab_layout e
    WHERE e.layout_id = c.layout_id
      AND COALESCE(e.deleted, false) = false
      AND upper(trim(e.column_kind)) = 'ENTITY'
      AND e.enabled IS DISTINCT FROM false
  )
  AND NOT EXISTS (
    SELECT 1
    FROM dynamicbusiness.dm_data_tab_column_relation r
    WHERE r.deleted = FALSE
      AND r.layout_id = c.layout_id
      AND r.to_column_identity = 'DETAIL:' || trim(d.tab_id)
      AND r.relation_kind IN ('ENTITY_DETAIL', 'CATEGORY_DETAIL', 'MODEL_DETAIL')
      AND COALESCE(r.relation_meta->>'edgeRole', 'filter') = 'filter'
  );

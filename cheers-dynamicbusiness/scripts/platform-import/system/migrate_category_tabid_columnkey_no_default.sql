-- ============================================================================
-- 分类栏：废除 tab_id / columnKey 字面 default
--
-- 定稿：columnKey = categoryTypeCode（冲突加 -2）；tabId = {type}-1（冲突加 -2）
-- 边端点 CATEGORY:default:default → CATEGORY:{newKey}:{newTab}（只改名，不删边）
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  r record;
  v_type text;
  v_old_key text;
  v_old_tab text;
  v_new_key text;
  v_new_tab text;
  v_old_identity text;
  v_new_identity text;
  v_used_keys text[];
  v_used_tabs text[];
  v_n int;
  v_rows int := 0;
  v_edges int := 0;
BEGIN
  FOR r IN
    SELECT id, layout_id, tab_id, column_meta
    FROM dm_data_tab_layout
    WHERE deleted = false
      AND column_kind = 'CATEGORY'
      AND (
        lower(trim(COALESCE(tab_id, ''))) = 'default'
        OR lower(trim(COALESCE(column_meta->>'columnKey', ''))) = 'default'
        OR NULLIF(trim(COALESCE(tab_id, '')), '') IS NULL
        OR NULLIF(trim(COALESCE(column_meta->>'columnKey', '')), '') IS NULL
      )
    ORDER BY layout_id, id
  LOOP
    v_type := NULLIF(trim(COALESCE(r.column_meta->>'categoryTypeCode', '')), '');
    IF v_type IS NULL OR lower(v_type) = 'default' THEN
      RAISE NOTICE 'skip CATEGORY id=%: 缺 categoryTypeCode', r.id;
      CONTINUE;
    END IF;

    SELECT COALESCE(array_agg(trim(column_meta->>'columnKey')), ARRAY[]::text[])
    INTO v_used_keys
    FROM dm_data_tab_layout
    WHERE layout_id = r.layout_id AND deleted = false AND column_kind = 'CATEGORY'
      AND id <> r.id
      AND NULLIF(trim(COALESCE(column_meta->>'columnKey', '')), '') IS NOT NULL
      AND lower(trim(column_meta->>'columnKey')) <> 'default';

    v_new_key := v_type;
    IF v_used_keys IS NOT NULL AND v_new_key = ANY (v_used_keys) THEN
      v_n := 2;
      WHILE (v_type || '-' || v_n) = ANY (v_used_keys) LOOP
        v_n := v_n + 1;
      END LOOP;
      v_new_key := v_type || '-' || v_n;
    END IF;

    SELECT COALESCE(array_agg(trim(tab_id)), ARRAY[]::text[])
    INTO v_used_tabs
    FROM dm_data_tab_layout
    WHERE layout_id = r.layout_id AND deleted = false AND column_kind = 'CATEGORY'
      AND id <> r.id
      AND NULLIF(trim(COALESCE(tab_id, '')), '') IS NOT NULL
      AND lower(trim(tab_id)) <> 'default';

    v_new_tab := v_type || '-1';
    IF v_used_tabs IS NOT NULL AND v_new_tab = ANY (v_used_tabs) THEN
      v_n := 2;
      WHILE (v_type || '-' || v_n) = ANY (v_used_tabs) LOOP
        v_n := v_n + 1;
      END LOOP;
      v_new_tab := v_type || '-' || v_n;
    END IF;

    v_old_key := COALESCE(NULLIF(trim(COALESCE(r.column_meta->>'columnKey', '')), ''), 'default');
    v_old_tab := COALESCE(NULLIF(trim(COALESCE(r.tab_id, '')), ''), 'default');
    v_old_identity := 'CATEGORY:' || v_old_key || ':' || v_old_tab;
    v_new_identity := 'CATEGORY:' || v_new_key || ':' || v_new_tab;

    UPDATE dm_data_tab_layout
    SET tab_id = v_new_tab,
        column_meta = jsonb_set(
          COALESCE(column_meta, '{}'::jsonb),
          '{columnKey}',
          to_jsonb(v_new_key),
          true
        ),
        updater = 'migrate-category-no-default',
        update_time = CURRENT_TIMESTAMP
    WHERE id = r.id;
    v_rows := v_rows + 1;

    IF v_old_identity IS DISTINCT FROM v_new_identity THEN
      UPDATE dm_data_tab_column_relation
      SET from_column_identity = v_new_identity,
          updater = 'migrate-category-no-default',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = r.layout_id AND deleted = false
        AND from_column_identity = v_old_identity;
      GET DIAGNOSTICS v_n = ROW_COUNT;
      v_edges := v_edges + v_n;

      UPDATE dm_data_tab_column_relation
      SET to_column_identity = v_new_identity,
          updater = 'migrate-category-no-default',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = r.layout_id AND deleted = false
        AND to_column_identity = v_old_identity;
      GET DIAGNOSTICS v_n = ROW_COUNT;
      v_edges := v_edges + v_n;
    END IF;
  END LOOP;

  RAISE NOTICE 'migrate-category-no-default: rows=% edge_endpoints=%', v_rows, v_edges;
END $$;

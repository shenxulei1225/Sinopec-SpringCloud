-- ============================================================================
-- 布局页编号收口为稳定 tab-xxxxxx；边上的栏身份跟着改名（不删边）
--
-- 人话：
--   每一页自己有编号，自己有类型。编号不再等于类型码。
--   已是 tab-* 且不是 default 的行不改。
--   空 / default / 类型码旧编号一次性改成 tab- + 行 id 派生后缀。
--   关系图端点按「旧身份 → 新身份」改名，不删边。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  r record;
  v_old_tab text;
  v_new_tab text;
  v_old_identity text;
  v_new_identity text;
  v_column_key text;
  v_rows int := 0;
  v_edges int := 0;
  v_n int;
BEGIN
  FOR r IN
    SELECT
      l.id,
      l.layout_id,
      l.column_kind,
      trim(COALESCE(l.tab_id, '')) AS tab_id,
      NULLIF(trim(COALESCE(l.column_meta->>'columnKey', '')), '') AS column_key
    FROM dm_data_tab_layout l
    WHERE l.deleted = false
      AND (
        NULLIF(trim(COALESCE(l.tab_id, '')), '') IS NULL
        OR lower(trim(l.tab_id)) = 'default'
        OR lower(trim(l.tab_id)) LIKE '%-default'
        OR trim(l.tab_id) NOT LIKE 'tab-%'
      )
  LOOP
    v_old_tab := r.tab_id;
    v_new_tab := 'tab-' || substr(md5(r.id::text), 1, 8);
    v_column_key := r.column_key;

    v_old_identity := CASE r.column_kind
      WHEN 'CATEGORY' THEN
        CASE
          WHEN v_column_key IS NULL OR v_old_tab = '' THEN NULL
          ELSE 'CATEGORY:' || v_column_key || ':' || v_old_tab
        END
      WHEN 'MODEL' THEN
        CASE WHEN v_old_tab = '' THEN NULL ELSE 'MODEL:' || v_old_tab END
      WHEN 'ENTITY' THEN
        CASE WHEN v_old_tab = '' THEN NULL ELSE 'ENTITY:' || v_old_tab END
      WHEN 'DETAIL' THEN
        CASE WHEN v_old_tab = '' THEN NULL ELSE 'DETAIL:' || v_old_tab END
      ELSE NULL
    END;
    v_new_identity := CASE r.column_kind
      WHEN 'CATEGORY' THEN
        CASE
          WHEN v_column_key IS NULL THEN NULL
          ELSE 'CATEGORY:' || v_column_key || ':' || v_new_tab
        END
      WHEN 'MODEL' THEN 'MODEL:' || v_new_tab
      WHEN 'ENTITY' THEN 'ENTITY:' || v_new_tab
      WHEN 'DETAIL' THEN 'DETAIL:' || v_new_tab
      ELSE NULL
    END;

    UPDATE dm_data_tab_layout
    SET tab_id = v_new_tab,
        updater = 'migrate-tab-stable',
        update_time = CURRENT_TIMESTAMP
    WHERE id = r.id;
    v_rows := v_rows + 1;

    IF v_old_identity IS NOT NULL AND v_new_identity IS NOT NULL AND v_old_identity <> v_new_identity THEN
      UPDATE dm_data_tab_column_relation
      SET from_column_identity = v_new_identity,
          updater = 'migrate-tab-stable',
          update_time = CURRENT_TIMESTAMP
      WHERE deleted = false
        AND layout_id = r.layout_id
        AND from_column_identity = v_old_identity;
      GET DIAGNOSTICS v_n = ROW_COUNT;
      v_edges := v_edges + v_n;

      UPDATE dm_data_tab_column_relation
      SET to_column_identity = v_new_identity,
          updater = 'migrate-tab-stable',
          update_time = CURRENT_TIMESTAMP
      WHERE deleted = false
        AND layout_id = r.layout_id
        AND to_column_identity = v_old_identity;
      GET DIAGNOSTICS v_n = ROW_COUNT;
      v_edges := v_edges + v_n;
    END IF;
  END LOOP;

  RAISE NOTICE 'V129 layout tab-stable: rows=% edge-renames=%', v_rows, v_edges;
END $$;

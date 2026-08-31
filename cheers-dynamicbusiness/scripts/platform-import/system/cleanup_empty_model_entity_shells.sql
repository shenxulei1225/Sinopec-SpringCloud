-- ============================================================================
-- 清理：无类型码的型号/实体空壳 + 指向 MODEL:default / ENTITY:default 的死边
--
-- 人话：
--   · 空壳：layout 行是 MODEL/ENTITY，但 meta 里没有底座类型编码，且 tab 空或 default
--     → 软删布局行（禁止 invent 类型码）。
--   · 死边：from/to 仍是 MODEL:default 或 ENTITY:default（新协议下不存在该身份）
--     → 若本页恰好一栏合法型号/实体，则改名对齐；否则软删该边。
--   · 不动 CATEGORY:…:default（分类允许字面 default 作 tabId）。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_shells int := 0;
  v_renamed int := 0;
  v_edges_deleted int := 0;
  r record;
  v_model_id text;
  v_entity_id text;
  v_model_n int;
  v_entity_n int;
  v_n int;
BEGIN
  -- 1) 软删无类型码空壳
  UPDATE dm_data_tab_layout l
  SET deleted = true,
      updater = 'cleanup-empty-me-shells',
      update_time = CURRENT_TIMESTAMP
  WHERE l.deleted = false
    AND l.column_kind IN ('MODEL', 'ENTITY')
    AND (
      l.tab_id IS NULL
      OR trim(l.tab_id) = ''
      OR lower(trim(l.tab_id)) = 'default'
    )
    AND (
      (l.column_kind = 'MODEL'
        AND NULLIF(trim(COALESCE(l.column_meta->>'modelEntityTypeCode', '')), '') IS NULL)
      OR (l.column_kind = 'ENTITY'
        AND NULLIF(trim(COALESCE(l.column_meta->>'entityEntityTypeCode', '')), '') IS NULL)
    );
  GET DIAGNOSTICS v_shells = ROW_COUNT;

  -- 2) 按页：能对上唯一合法身份则改名 MODEL/ENTITY:default 端点
  FOR r IN
    SELECT DISTINCT layout_id
    FROM dm_data_tab_column_relation
    WHERE deleted = false
      AND (
        from_column_identity IN ('MODEL:default', 'ENTITY:default')
        OR to_column_identity IN ('MODEL:default', 'ENTITY:default')
      )
  LOOP
    SELECT
      count(*) FILTER (
        WHERE column_kind = 'MODEL'
          AND NULLIF(trim(COALESCE(tab_id, '')), '') IS NOT NULL
          AND lower(trim(tab_id)) <> 'default'
      ),
      count(*) FILTER (
        WHERE column_kind = 'ENTITY'
          AND NULLIF(trim(COALESCE(tab_id, '')), '') IS NOT NULL
          AND lower(trim(tab_id)) <> 'default'
      ),
      max(CASE
        WHEN column_kind = 'MODEL'
          AND NULLIF(trim(COALESCE(tab_id, '')), '') IS NOT NULL
          AND lower(trim(tab_id)) <> 'default'
        THEN 'MODEL:' || trim(tab_id)
      END),
      max(CASE
        WHEN column_kind = 'ENTITY'
          AND NULLIF(trim(COALESCE(tab_id, '')), '') IS NOT NULL
          AND lower(trim(tab_id)) <> 'default'
        THEN 'ENTITY:' || trim(tab_id)
      END)
    INTO v_model_n, v_entity_n, v_model_id, v_entity_id
    FROM dm_data_tab_layout
    WHERE layout_id = r.layout_id AND deleted = false;

    IF v_model_n = 1 AND v_model_id IS NOT NULL THEN
      UPDATE dm_data_tab_column_relation
      SET from_column_identity = v_model_id,
          updater = 'cleanup-empty-me-shells',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = r.layout_id AND deleted = false
        AND from_column_identity = 'MODEL:default';
      GET DIAGNOSTICS v_n = ROW_COUNT;
      v_renamed := v_renamed + v_n;

      UPDATE dm_data_tab_column_relation
      SET to_column_identity = v_model_id,
          updater = 'cleanup-empty-me-shells',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = r.layout_id AND deleted = false
        AND to_column_identity = 'MODEL:default';
      GET DIAGNOSTICS v_n = ROW_COUNT;
      v_renamed := v_renamed + v_n;
    END IF;

    IF v_entity_n = 1 AND v_entity_id IS NOT NULL THEN
      UPDATE dm_data_tab_column_relation
      SET from_column_identity = v_entity_id,
          updater = 'cleanup-empty-me-shells',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = r.layout_id AND deleted = false
        AND from_column_identity = 'ENTITY:default';
      GET DIAGNOSTICS v_n = ROW_COUNT;
      v_renamed := v_renamed + v_n;

      UPDATE dm_data_tab_column_relation
      SET to_column_identity = v_entity_id,
          updater = 'cleanup-empty-me-shells',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = r.layout_id AND deleted = false
        AND to_column_identity = 'ENTITY:default';
      GET DIAGNOSTICS v_n = ROW_COUNT;
      v_renamed := v_renamed + v_n;
    END IF;
  END LOOP;

  -- 3) 仍指向 MODEL:default / ENTITY:default 的边：软删（死边）
  UPDATE dm_data_tab_column_relation
  SET deleted = true,
      updater = 'cleanup-empty-me-shells',
      update_time = CURRENT_TIMESTAMP
  WHERE deleted = false
    AND (
      from_column_identity IN ('MODEL:default', 'ENTITY:default')
      OR to_column_identity IN ('MODEL:default', 'ENTITY:default')
    );
  GET DIAGNOSTICS v_edges_deleted = ROW_COUNT;

  RAISE NOTICE 'cleanup-empty-me-shells: shells=% renamed_endpoints=% dead_edges=%',
    v_shells, v_renamed, v_edges_deleted;
END $$;

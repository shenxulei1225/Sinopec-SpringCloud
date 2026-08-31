-- ============================================================================
-- 数据页签布局 · 栏身份与边端点对齐 + 缺「型号→实体」筛选边则补上（幂等）
--
-- 人话：
--   · 边上写的栏名字必须和布局里那一栏算出来的名字一致；不一致就「改边的名字」。
--   · 同类型、只有一栏型号+一栏实体、且已经有「分类→型号」筛选时，若缺「型号→实体」就补一条。
--   · 绝对不自动删边（禁止 deleted=true）。看不准的边原样留下，人工处理。
--
-- 型号/实体 tabId = 底座类型编码（禁止 default）。
-- 存量先跑 migrate_model_entity_tabid_to_typecode.sql；本脚本不再把栏收成 default。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  r record;
  v_model_id text;
  v_entity_id text;
  v_model_n int;
  v_entity_n int;
  v_model_type text;
  v_entity_type text;
  v_has_cm boolean;
  v_has_me boolean;
  v_edge_id text;
  v_scope_pages int := 0;
  v_retarget int := 0;
  v_me_pages int := 0;
  v_n int;
BEGIN
  -- -------------------------------------------------------------------------
  -- A. （已移除）SCOPE→default。请先跑 migrate_model_entity_tabid_to_typecode.sql
  -- -------------------------------------------------------------------------
  v_scope_pages := 0;

  -- -------------------------------------------------------------------------
  -- B. 全库：边写的型号/实体名字对不上布局时，若该种类恰好只有一栏 → 只改边的名字
  --    没有栏、或多栏对不上：不删边，只 NOTICE
  -- -------------------------------------------------------------------------
  FOR r IN
    SELECT DISTINCT layout_id
    FROM dm_data_tab_column_relation
    WHERE deleted = false
      AND (
        from_column_identity LIKE 'MODEL:%'
        OR from_column_identity LIKE 'ENTITY:%'
        OR to_column_identity LIKE 'MODEL:%'
        OR to_column_identity LIKE 'ENTITY:%'
      )
  LOOP
    SELECT
      count(*) FILTER (WHERE column_kind = 'MODEL'),
      count(*) FILTER (WHERE column_kind = 'ENTITY'),
      max(CASE WHEN column_kind = 'MODEL'
        THEN CASE WHEN NULLIF(trim(COALESCE(tab_id,'')),'') IS NULL OR lower(trim(tab_id))='default' THEN NULL ELSE 'MODEL:' || trim(tab_id) END END),
      max(CASE WHEN column_kind = 'ENTITY'
        THEN CASE WHEN NULLIF(trim(COALESCE(tab_id,'')),'') IS NULL OR lower(trim(tab_id))='default' THEN NULL ELSE 'ENTITY:' || trim(tab_id) END END)
    INTO v_model_n, v_entity_n, v_model_id, v_entity_id
    FROM dm_data_tab_layout
    WHERE layout_id = r.layout_id AND deleted = false
      AND column_kind IN ('MODEL', 'ENTITY');

    IF v_model_n = 1 AND v_model_id IS NOT NULL THEN
      UPDATE dm_data_tab_column_relation x
      SET from_column_identity = v_model_id,
          updater = 'repair-layout-identity',
          update_time = CURRENT_TIMESTAMP
      WHERE x.layout_id = r.layout_id
        AND x.deleted = false
        AND x.from_column_identity LIKE 'MODEL:%'
        AND x.from_column_identity <> v_model_id
        AND NOT EXISTS (
          SELECT 1 FROM dm_data_tab_layout l
          WHERE l.layout_id = r.layout_id AND l.deleted = false AND l.column_kind = 'MODEL'
            AND (CASE WHEN NULLIF(trim(COALESCE(l.tab_id,'')),'') IS NULL OR lower(trim(l.tab_id))='default' THEN NULL ELSE 'MODEL:' || trim(l.tab_id) END)
                = x.from_column_identity
        );
      GET DIAGNOSTICS v_n = ROW_COUNT;
      v_retarget := v_retarget + v_n;

      UPDATE dm_data_tab_column_relation x
      SET to_column_identity = v_model_id,
          updater = 'repair-layout-identity',
          update_time = CURRENT_TIMESTAMP
      WHERE x.layout_id = r.layout_id
        AND x.deleted = false
        AND x.to_column_identity LIKE 'MODEL:%'
        AND x.to_column_identity <> v_model_id
        AND NOT EXISTS (
          SELECT 1 FROM dm_data_tab_layout l
          WHERE l.layout_id = r.layout_id AND l.deleted = false AND l.column_kind = 'MODEL'
            AND (CASE WHEN NULLIF(trim(COALESCE(l.tab_id,'')),'') IS NULL OR lower(trim(l.tab_id))='default' THEN NULL ELSE 'MODEL:' || trim(l.tab_id) END)
                = x.to_column_identity
        );
      GET DIAGNOSTICS v_n = ROW_COUNT;
      v_retarget := v_retarget + v_n;
    ELSIF v_model_n = 0 THEN
      IF EXISTS (
        SELECT 1 FROM dm_data_tab_column_relation
        WHERE layout_id = r.layout_id AND deleted = false
          AND (from_column_identity LIKE 'MODEL:%' OR to_column_identity LIKE 'MODEL:%')
      ) THEN
        RAISE NOTICE 'layout_id=% 边上有型号端点但布局无型号栏：保留边不删，请人工核对', r.layout_id;
      END IF;
    END IF;

    IF v_entity_n = 1 AND v_entity_id IS NOT NULL THEN
      UPDATE dm_data_tab_column_relation x
      SET from_column_identity = v_entity_id,
          updater = 'repair-layout-identity',
          update_time = CURRENT_TIMESTAMP
      WHERE x.layout_id = r.layout_id
        AND x.deleted = false
        AND x.from_column_identity LIKE 'ENTITY:%'
        AND x.from_column_identity <> v_entity_id
        AND NOT EXISTS (
          SELECT 1 FROM dm_data_tab_layout l
          WHERE l.layout_id = r.layout_id AND l.deleted = false AND l.column_kind = 'ENTITY'
            AND (CASE WHEN NULLIF(trim(COALESCE(l.tab_id,'')),'') IS NULL OR lower(trim(l.tab_id))='default' THEN NULL ELSE 'ENTITY:' || trim(l.tab_id) END)
                = x.from_column_identity
        );
      GET DIAGNOSTICS v_n = ROW_COUNT;
      v_retarget := v_retarget + v_n;

      UPDATE dm_data_tab_column_relation x
      SET to_column_identity = v_entity_id,
          updater = 'repair-layout-identity',
          update_time = CURRENT_TIMESTAMP
      WHERE x.layout_id = r.layout_id
        AND x.deleted = false
        AND x.to_column_identity LIKE 'ENTITY:%'
        AND x.to_column_identity <> v_entity_id
        AND NOT EXISTS (
          SELECT 1 FROM dm_data_tab_layout l
          WHERE l.layout_id = r.layout_id AND l.deleted = false AND l.column_kind = 'ENTITY'
            AND (CASE WHEN NULLIF(trim(COALESCE(l.tab_id,'')),'') IS NULL OR lower(trim(l.tab_id))='default' THEN NULL ELSE 'ENTITY:' || trim(l.tab_id) END)
                = x.to_column_identity
        );
      GET DIAGNOSTICS v_n = ROW_COUNT;
      v_retarget := v_retarget + v_n;
    ELSIF v_entity_n = 0 THEN
      IF EXISTS (
        SELECT 1 FROM dm_data_tab_column_relation
        WHERE layout_id = r.layout_id AND deleted = false
          AND (from_column_identity LIKE 'ENTITY:%' OR to_column_identity LIKE 'ENTITY:%')
      ) THEN
        RAISE NOTICE 'layout_id=% 边上有实体端点但布局无实体栏：保留边不删，请人工核对', r.layout_id;
      END IF;
    END IF;
  END LOOP;

  -- -------------------------------------------------------------------------
  -- C. 缺「型号→实体」筛选边则补上（不删已有边）
  --    条件：挂了目录；恰好一型号一实体；两边类型码相同且非空；已有分类→型号筛选
  -- -------------------------------------------------------------------------
  FOR r IN
    SELECT et.code, et.data_layout_id AS layout_id
    FROM dynamic_entity_type et
    WHERE et.deleted = false
      AND et.tenant_id = v_tenant
      AND et.data_layout_id IS NOT NULL
  LOOP
    SELECT
      count(*) FILTER (WHERE column_kind = 'MODEL'),
      count(*) FILTER (WHERE column_kind = 'ENTITY'),
      max(CASE WHEN column_kind = 'MODEL'
        THEN CASE WHEN NULLIF(trim(COALESCE(tab_id,'')),'') IS NULL OR lower(trim(tab_id))='default' THEN NULL ELSE 'MODEL:' || trim(tab_id) END END),
      max(CASE WHEN column_kind = 'ENTITY'
        THEN CASE WHEN NULLIF(trim(COALESCE(tab_id,'')),'') IS NULL OR lower(trim(tab_id))='default' THEN NULL ELSE 'ENTITY:' || trim(tab_id) END END),
      max(CASE WHEN column_kind = 'MODEL'
        THEN NULLIF(trim(column_meta->>'modelEntityTypeCode'), '') END),
      max(CASE WHEN column_kind = 'ENTITY'
        THEN NULLIF(trim(column_meta->>'entityEntityTypeCode'), '') END)
    INTO v_model_n, v_entity_n, v_model_id, v_entity_id, v_model_type, v_entity_type
    FROM dm_data_tab_layout
    WHERE layout_id = r.layout_id AND deleted = false
      AND column_kind IN ('MODEL', 'ENTITY');

    IF v_model_n <> 1 OR v_entity_n <> 1 THEN
      CONTINUE;
    END IF;
    IF v_model_type IS NULL OR v_entity_type IS NULL OR v_model_type <> v_entity_type THEN
      CONTINUE;
    END IF;

    SELECT EXISTS (
      SELECT 1 FROM dm_data_tab_column_relation
      WHERE layout_id = r.layout_id AND deleted = false
        AND relation_kind = 'CATEGORY_MODEL'
        AND COALESCE(relation_meta->>'edgeRole', 'filter') = 'filter'
    ) INTO v_has_cm;
    IF NOT v_has_cm THEN
      CONTINUE;
    END IF;

    SELECT EXISTS (
      SELECT 1 FROM dm_data_tab_column_relation
      WHERE layout_id = r.layout_id AND deleted = false
        AND relation_kind = 'MODEL_ENTITY'
        AND COALESCE(relation_meta->>'edgeRole', 'filter') = 'filter'
        AND from_column_identity = v_model_id
        AND to_column_identity = v_entity_id
    ) INTO v_has_me;
    IF v_has_me THEN
      CONTINUE;
    END IF;

    v_edge_id := 'edge-repair-me-' || r.layout_id::text;

    IF EXISTS (
      SELECT 1 FROM dm_data_tab_column_relation
      WHERE layout_id = r.layout_id AND edge_id = v_edge_id
    ) THEN
      -- 已有同 edge_id：只改端点与元数据，禁止靠 deleted 清掉别的边
      UPDATE dm_data_tab_column_relation
      SET deleted = false,
          from_column_identity = v_model_id,
          to_column_identity = v_entity_id,
          relation_kind = 'MODEL_ENTITY',
          from_type_code = v_model_type,
          to_type_code = v_entity_type,
          relation_meta = jsonb_build_object(
            'edgeRole', 'filter',
            'enabledInteractions', '[]'::jsonb
          ),
          entity_type_code = r.code,
          updater = 'repair-layout-identity',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = r.layout_id
        AND edge_id = v_edge_id;
    ELSE
      INSERT INTO dm_data_tab_column_relation (
        tenant_id, layout_id, entity_type_code, edge_id,
        from_column_identity, to_column_identity, relation_kind,
        from_type_code, to_type_code, relation_meta, creator, deleted
      ) VALUES (
        v_tenant, r.layout_id, r.code, v_edge_id,
        v_model_id, v_entity_id, 'MODEL_ENTITY',
        v_model_type, v_entity_type,
        jsonb_build_object('edgeRole', 'filter', 'enabledInteractions', '[]'::jsonb),
        'repair-layout-identity', false
      );
    END IF;

    v_me_pages := v_me_pages + 1;
  END LOOP;

  RAISE NOTICE
    'repair_dm_layout_column_identities: SCOPE页=% 边改名次数=% 补ME页=%（无删边）',
    v_scope_pages, v_retarget, v_me_pages;
END $$;

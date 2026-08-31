-- ============================================================================
-- 型号/实体栏身份：废除空与字面 default，改用底座类型编码（方案 A）
--
-- 定稿口径：
--   · 布局行 tab_id = column_meta 里的底座类型编码（modelEntityTypeCode / entityEntityTypeCode）
--   · 同页同种类多栏且类型码冲突时，后者加 -2、-3（本脚本对「每页每种栏」按 id 顺序处理）
--   · 栏间关系边 from/to 同步改名；禁止再出现 MODEL:default / ENTITY:default
--
-- 幂等：可重复执行；已是类型编码且边已对齐则无变更。
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  r record;
  v_type text;
  v_old_identity text;
  v_new_tab text;
  v_new_identity text;
  v_used text[];
  v_n int;
BEGIN
  -- -------- MODEL --------
  FOR r IN
    SELECT l.id, l.layout_id, l.tab_id, l.column_meta
    FROM dm_data_tab_layout l
    WHERE l.deleted = false
      AND l.column_kind = 'MODEL'
    ORDER BY l.layout_id, l.id
  LOOP
    v_type := NULLIF(trim(COALESCE(r.column_meta ->> 'modelEntityTypeCode', '')), '');
    IF v_type IS NULL OR lower(v_type) = 'default' THEN
      RAISE NOTICE 'skip MODEL id=% layout=%: 缺 modelEntityTypeCode', r.id, r.layout_id;
      CONTINUE;
    END IF;

    SELECT COALESCE(array_agg(trim(tab_id)), ARRAY[]::text[])
    INTO v_used
    FROM dm_data_tab_layout
    WHERE layout_id = r.layout_id
      AND column_kind = 'MODEL'
      AND deleted = false
      AND id <> r.id
      AND tab_id IS NOT NULL
      AND trim(tab_id) <> ''
      AND lower(trim(tab_id)) <> 'default';

    v_new_tab := v_type;
    IF v_used IS NOT NULL AND v_new_tab = ANY (v_used) THEN
      v_n := 2;
      WHILE (v_type || '-' || v_n) = ANY (v_used) LOOP
        v_n := v_n + 1;
      END LOOP;
      v_new_tab := v_type || '-' || v_n;
    END IF;

    v_old_identity := 'MODEL:' || COALESCE(NULLIF(trim(COALESCE(r.tab_id, '')), ''), 'default');
    v_new_identity := 'MODEL:' || v_new_tab;

    IF COALESCE(NULLIF(trim(COALESCE(r.tab_id, '')), ''), '') IS DISTINCT FROM v_new_tab THEN
      UPDATE dm_data_tab_layout
      SET tab_id = v_new_tab,
          updater = 'migrate-typecode-tabid',
          update_time = CURRENT_TIMESTAMP
      WHERE id = r.id;
    END IF;

    IF v_old_identity IS DISTINCT FROM v_new_identity THEN
      UPDATE dm_data_tab_column_relation
      SET from_column_identity = v_new_identity,
          updater = 'migrate-typecode-tabid',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = r.layout_id
        AND deleted = false
        AND from_column_identity = v_old_identity;

      UPDATE dm_data_tab_column_relation
      SET to_column_identity = v_new_identity,
          updater = 'migrate-typecode-tabid',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = r.layout_id
        AND deleted = false
        AND to_column_identity = v_old_identity;
    END IF;
  END LOOP;

  -- -------- ENTITY --------
  FOR r IN
    SELECT l.id, l.layout_id, l.tab_id, l.column_meta
    FROM dm_data_tab_layout l
    WHERE l.deleted = false
      AND l.column_kind = 'ENTITY'
    ORDER BY l.layout_id, l.id
  LOOP
    v_type := NULLIF(trim(COALESCE(r.column_meta ->> 'entityEntityTypeCode', '')), '');
    IF v_type IS NULL OR lower(v_type) = 'default' THEN
      RAISE NOTICE 'skip ENTITY id=% layout=%: 缺 entityEntityTypeCode', r.id, r.layout_id;
      CONTINUE;
    END IF;

    SELECT COALESCE(array_agg(trim(tab_id)), ARRAY[]::text[])
    INTO v_used
    FROM dm_data_tab_layout
    WHERE layout_id = r.layout_id
      AND column_kind = 'ENTITY'
      AND deleted = false
      AND id <> r.id
      AND tab_id IS NOT NULL
      AND trim(tab_id) <> ''
      AND lower(trim(tab_id)) <> 'default';

    v_new_tab := v_type;
    IF v_used IS NOT NULL AND v_new_tab = ANY (v_used) THEN
      v_n := 2;
      WHILE (v_type || '-' || v_n) = ANY (v_used) LOOP
        v_n := v_n + 1;
      END LOOP;
      v_new_tab := v_type || '-' || v_n;
    END IF;

    v_old_identity := 'ENTITY:' || COALESCE(NULLIF(trim(COALESCE(r.tab_id, '')), ''), 'default');
    v_new_identity := 'ENTITY:' || v_new_tab;

    IF COALESCE(NULLIF(trim(COALESCE(r.tab_id, '')), ''), '') IS DISTINCT FROM v_new_tab THEN
      UPDATE dm_data_tab_layout
      SET tab_id = v_new_tab,
          updater = 'migrate-typecode-tabid',
          update_time = CURRENT_TIMESTAMP
      WHERE id = r.id;
    END IF;

    IF v_old_identity IS DISTINCT FROM v_new_identity THEN
      UPDATE dm_data_tab_column_relation
      SET from_column_identity = v_new_identity,
          updater = 'migrate-typecode-tabid',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = r.layout_id
        AND deleted = false
        AND from_column_identity = v_old_identity;

      UPDATE dm_data_tab_column_relation
      SET to_column_identity = v_new_identity,
          updater = 'migrate-typecode-tabid',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = r.layout_id
        AND deleted = false
        AND to_column_identity = v_old_identity;
    END IF;
  END LOOP;

  RAISE NOTICE 'migrate_model_entity_tabid_to_typecode: done';
END $$;

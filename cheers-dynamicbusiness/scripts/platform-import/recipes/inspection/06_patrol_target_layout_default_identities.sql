-- ============================================================================
-- 巡检目标管理（patrol_target）· 型号/实体栏身份改为底座类型编码
--
-- 定稿口径（方案 A）：tab_id = meta 底座类型编码；边端点同步改名。
-- 全库通用脚本：../../system/migrate_model_entity_tabid_to_typecode.sql
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  v_tenant CONSTANT bigint := 1;
  v_layout_id bigint;
  r record;
  v_type text;
  v_old_identity text;
  v_new_tab text;
  v_new_identity text;
BEGIN
  SELECT data_layout_id INTO v_layout_id
  FROM dynamic_entity_type
  WHERE deleted = false AND tenant_id = v_tenant AND code = 'patrol_target'
  LIMIT 1;

  IF v_layout_id IS NULL THEN
    RAISE NOTICE 'skip 06: 无 patrol_target 或未挂 dataLayoutId';
    RETURN;
  END IF;

  FOR r IN
    SELECT id, tab_id, column_meta, column_kind
    FROM dm_data_tab_layout
    WHERE layout_id = v_layout_id AND deleted = false
      AND column_kind IN ('MODEL', 'ENTITY')
    ORDER BY id
  LOOP
    IF r.column_kind = 'MODEL' THEN
      v_type := NULLIF(trim(COALESCE(r.column_meta ->> 'modelEntityTypeCode', '')), '');
    ELSE
      v_type := NULLIF(trim(COALESCE(r.column_meta ->> 'entityEntityTypeCode', '')), '');
    END IF;
    IF v_type IS NULL OR lower(v_type) = 'default' THEN
      RAISE NOTICE 'skip layout row id=%: 缺类型码', r.id;
      CONTINUE;
    END IF;

    v_new_tab := v_type;
    v_old_identity := r.column_kind || ':' || COALESCE(NULLIF(trim(COALESCE(r.tab_id, '')), ''), 'default');
    v_new_identity := r.column_kind || ':' || v_new_tab;

    UPDATE dm_data_tab_layout
    SET tab_id = v_new_tab,
        updater = '06-patrol-target-typecode',
        update_time = CURRENT_TIMESTAMP
    WHERE id = r.id
      AND COALESCE(NULLIF(trim(COALESCE(tab_id, '')), ''), '') IS DISTINCT FROM v_new_tab;

    IF v_old_identity IS DISTINCT FROM v_new_identity THEN
      UPDATE dm_data_tab_column_relation
      SET from_column_identity = v_new_identity,
          updater = '06-patrol-target-typecode',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = v_layout_id AND deleted = false
        AND from_column_identity = v_old_identity;

      UPDATE dm_data_tab_column_relation
      SET to_column_identity = v_new_identity,
          updater = '06-patrol-target-typecode',
          update_time = CURRENT_TIMESTAMP
      WHERE layout_id = v_layout_id AND deleted = false
        AND to_column_identity = v_old_identity;
    END IF;
  END LOOP;

  RAISE NOTICE '06_patrol_target_layout typecode identities: layout_id=%', v_layout_id;
END $$;

-- ============================================================================
-- recipes/inspection · 23 重键：宿主参数包 targetType sop → inspection_item（破坏性，手工执行）
--
-- 规则（P2b 定稿）：
--   当 subjectType = inspection_item 且 subjectId 有效、且 targetType = sop 时：
--     targetType ← 'inspection_item'
--     targetId   ← subjectId
--   旧 sop id 丢弃（不再反查）。
--
-- 本机实测同设备+subject+dimension 无多 sop 目标冲突，故不做合并；若别处撞键需另案。
-- 明确不做：不改非 inspection_item 的 subject；不跑 11。
-- ============================================================================

SET search_path TO dynamicbusiness, public;

CREATE OR REPLACE FUNCTION dynamicbusiness._p2b_rekey_host_pack(p_pack jsonb)
RETURNS jsonb
LANGUAGE plpgsql
AS $$
DECLARE
  v_entries jsonb := '[]'::jsonb;
  v_e jsonb;
  v_subject_type text;
  v_subject_id bigint;
  v_target_type text;
  v_target_id bigint;
  v_new jsonb;
BEGIN
  IF p_pack IS NULL OR jsonb_typeof(p_pack) <> 'object' THEN
    RETURN p_pack;
  END IF;
  IF jsonb_typeof(p_pack -> 'entries') <> 'array' THEN
    RETURN p_pack;
  END IF;

  FOR v_e IN SELECT value FROM jsonb_array_elements(p_pack -> 'entries')
  LOOP
    v_subject_type := COALESCE(v_e ->> 'subjectType', '');
    BEGIN
      v_subject_id := NULLIF(v_e ->> 'subjectId', '')::bigint;
    EXCEPTION WHEN others THEN
      v_subject_id := NULL;
    END;
    v_target_type := COALESCE(v_e ->> 'targetType', '');
    BEGIN
      v_target_id := NULLIF(v_e ->> 'targetId', '')::bigint;
    EXCEPTION WHEN others THEN
      v_target_id := NULL;
    END;

    IF v_subject_type = 'inspection_item'
       AND v_subject_id IS NOT NULL
       AND v_subject_id > 0
       AND v_target_type = 'sop' THEN
      v_target_type := 'inspection_item';
      v_target_id := v_subject_id;
    END IF;

    v_new := v_e
      || jsonb_build_object(
           'targetType', v_target_type,
           'targetId', v_target_id
         );
    v_entries := v_entries || jsonb_build_array(v_new);
  END LOOP;

  RETURN jsonb_set(p_pack, '{entries}', v_entries, true);
END;
$$;

DO $$
DECLARE
  r record;
  v_rows bigint;
  v_total bigint := 0;
BEGIN
  FOR r IN
    SELECT c.relname AS tbl
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'r'
      AND c.relname ~ '^ent_equipment(_t[0-9]+)?$'
    ORDER BY c.relname
  LOOP
    EXECUTE format($q$
      UPDATE dynamicbusiness.%I eq
      SET host_sop_param_pack = dynamicbusiness._p2b_rekey_host_pack(eq.host_sop_param_pack)
      WHERE eq.deleted = FALSE
        AND eq.host_sop_param_pack IS NOT NULL
        AND EXISTS (
          SELECT 1
          FROM jsonb_array_elements(
            CASE
              WHEN jsonb_typeof(eq.host_sop_param_pack -> 'entries') = 'array'
                THEN eq.host_sop_param_pack -> 'entries'
              ELSE '[]'::jsonb
            END
          ) e
          WHERE e ->> 'targetType' = 'sop'
            AND e ->> 'subjectType' = 'inspection_item'
        )
    $q$, r.tbl);
    GET DIAGNOSTICS v_rows = ROW_COUNT;
    v_total := v_total + v_rows;
    RAISE NOTICE 'rekeyed host pack: table=% equipment_rows=%', r.tbl, v_rows;
  END LOOP;

  RAISE NOTICE 'P2b host pack rekey done. equipment_rows_updated=%', v_total;
END $$;

DROP FUNCTION IF EXISTS dynamicbusiness._p2b_rekey_host_pack(jsonb);

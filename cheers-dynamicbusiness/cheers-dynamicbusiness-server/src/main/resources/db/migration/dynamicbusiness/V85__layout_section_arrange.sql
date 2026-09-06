-- 区域清单补写摆法 arrange，并把种子写成编号的名字改成中文。
-- 有分类 / 型号 / 实体 / 详情栏盖在该区 → 水平并排；否则 → 自由摆放。
-- 只改库一次。读路径不得按有没有栏猜摆法。

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  rec record;
  sections jsonb;
  new_sections jsonb;
  elem jsonb;
  sid text;
  sname text;
  arrange text;
  has_cols boolean;
  display_name text;
BEGIN
  FOR rec IN
    SELECT id, settings_json
    FROM dynamicbusiness.dm_workbench_layout
    WHERE COALESCE(deleted, false) = false
  LOOP
    IF rec.settings_json IS NULL OR rec.settings_json->'sections' IS NULL THEN
      CONTINUE;
    END IF;
    sections := rec.settings_json->'sections';
    IF jsonb_typeof(sections) <> 'array' THEN
      CONTINUE;
    END IF;

    new_sections := '[]'::jsonb;
    FOR elem IN SELECT value FROM jsonb_array_elements(sections)
    LOOP
      sid := trim(COALESCE(elem->>'id', ''));
      IF sid = '' THEN
        CONTINUE;
      END IF;

      sname := trim(COALESCE(elem->>'name', ''));
      arrange := lower(trim(COALESCE(elem->>'arrange', '')));
      IF arrange NOT IN ('horizontal', 'free') THEN
        SELECT EXISTS (
          SELECT 1
          FROM dynamicbusiness.dm_data_tab_layout t
          WHERE t.layout_id = rec.id
            AND COALESCE(t.deleted, false) = false
            AND t.column_kind IN ('CATEGORY', 'MODEL', 'ENTITY', 'DETAIL')
            AND t.column_meta->>'columnSection' = sid
        ) INTO has_cols;
        arrange := CASE WHEN has_cols THEN 'horizontal' ELSE 'free' END;
      END IF;

      display_name := sname;
      IF display_name = '' OR display_name = sid THEN
        display_name := CASE sid
          WHEN 'filter' THEN '筛选'
          WHEN 'who' THEN '对象'
          WHEN 'what' THEN '详情'
          ELSE COALESCE(NULLIF(sname, ''), sid)
        END;
      END IF;

      new_sections := new_sections || jsonb_build_array(
        jsonb_build_object(
          'id', sid,
          'name', display_name,
          'arrange', arrange
        )
      );
    END LOOP;

    UPDATE dynamicbusiness.dm_workbench_layout
    SET settings_json = jsonb_set(
      COALESCE(rec.settings_json, '{}'::jsonb),
      '{sections}',
      new_sections
    )
    WHERE id = rec.id;
  END LOOP;
END $$;

COMMENT ON COLUMN dynamicbusiness.dm_workbench_layout.settings_json IS
    '布局头设置：sections=本份布局的区域清单（id/name/arrange）；sectionHidden=按区域编号隐藏';

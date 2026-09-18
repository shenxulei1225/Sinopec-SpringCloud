-- V124: 去掉型号分组名「标题后」魔术。
-- 名称后跟哪些字段改由该数据目录详情栏配置勾选；分组只负责正文分块。
-- 本组字段回到未分组，不在读路径从组名反推。

SET search_path TO dynamicbusiness, public;

UPDATE dynamic_model
SET
  field_groups_config = jsonb_set(
    COALESCE(field_groups_config::jsonb, '{}'::jsonb),
    '{groups}',
    COALESCE((
      SELECT jsonb_agg(g)
      FROM jsonb_array_elements(COALESCE(field_groups_config::jsonb->'groups', '[]'::jsonb)) g
      WHERE btrim(COALESCE(g->>'name', '')) IS DISTINCT FROM '标题后'
    ), '[]'::jsonb)
  )::text,
  updater = 'flyway-v124',
  update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND EXISTS (
    SELECT 1
    FROM jsonb_array_elements(COALESCE(field_groups_config::jsonb->'groups', '[]'::jsonb)) g
    WHERE btrim(COALESCE(g->>'name', '')) = '标题后'
  );

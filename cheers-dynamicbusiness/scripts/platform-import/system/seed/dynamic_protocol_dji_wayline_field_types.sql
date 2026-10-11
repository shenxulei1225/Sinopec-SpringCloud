-- 航线字段说明的类型和枚举取值改跟字段库一致。
-- 只改已有字段说明上的 type、options，不重写空包，不改必填。
-- 字段库没有这条字段时，类型保持原样。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

UPDATE ent_data_protocol_t1 e
SET custom_fields = jsonb_set(
      e.custom_fields,
      '{field_description_json,outbound}',
      COALESCE((
        SELECT jsonb_agg(
          jsonb_strip_nulls(
            item || jsonb_build_object(
              'type', COALESCE(NULLIF(btrim(f.type), ''), item->>'type'),
              'options', CASE
                WHEN f.options IS NULL OR btrim(f.options) = '' THEN NULL
                ELSE f.options::jsonb
              END
            )
          )
          ORDER BY ord
        )
        FROM jsonb_array_elements(e.custom_fields->'field_description_json'->'outbound') WITH ORDINALITY AS src(item, ord)
        LEFT JOIN dynamic_field f
          ON f.deleted = false AND f.tenant_id = e.tenant_id AND f.code = item->>'code'
      ), '[]'::jsonb)
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false
  AND e.communication_protocol = 'wayline'
  AND jsonb_typeof(e.custom_fields->'field_description_json'->'outbound') = 'array';

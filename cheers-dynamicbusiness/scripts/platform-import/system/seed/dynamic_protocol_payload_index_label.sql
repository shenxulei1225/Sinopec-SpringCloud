-- 相机的负载索引编号：改界面名称，并把仅写在物理列上的字段说明补进型号扩展字段。
-- 负责：payload_index 的中文名；两条只有物理列字段表的拍照指令。
-- 不负责：把已有扩展字段倒灌进物理列；不把 39-0-7 写成默认值。
SET search_path TO dynamicbusiness;

BEGIN;

UPDATE dynamic_field
SET name = '相机的负载索引编号',
    description = '类型-子类型-云台位置。来自所选执行设备上报的负载索引，不是相机类型。39-0-7 只是官网举例，不是默认值。',
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND code = 'payload_index';

UPDATE ent_data_protocol_t1
SET custom_fields = jsonb_set(
      COALESCE(custom_fields, '{}'::jsonb),
      '{field_description_json}',
      field_description_json,
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND jsonb_typeof(field_description_json->'outbound') = 'array'
  AND jsonb_typeof(custom_fields->'field_description_json'->'outbound') IS DISTINCT FROM 'array';

CREATE OR REPLACE FUNCTION pg_temp.rename_payload_index_label(doc jsonb)
RETURNS jsonb
LANGUAGE plpgsql
AS $$
DECLARE
  outbound jsonb;
  rebuilt jsonb := '[]'::jsonb;
  elem jsonb;
  label text;
  descr text;
BEGIN
  IF doc IS NULL OR jsonb_typeof(doc) <> 'object' THEN
    RETURN doc;
  END IF;
  outbound := doc->'outbound';
  IF jsonb_typeof(outbound) <> 'array' THEN
    RETURN doc;
  END IF;
  FOR elem IN SELECT value FROM jsonb_array_elements(outbound)
  LOOP
    label := elem->>'label';
    descr := COALESCE(elem->>'description', '');
    IF label = '相机枚举' THEN
      elem := jsonb_set(elem, '{label}', to_jsonb('相机的负载索引编号'::text), true);
    END IF;
    IF descr = '相机枚举值' THEN
      elem := jsonb_set(
        elem,
        '{description}',
        to_jsonb('类型-子类型-云台位置。来自所选执行设备上报的负载索引，不是相机类型。'::text),
        true
      );
    ELSIF descr LIKE '%相机枚举%' THEN
      elem := jsonb_set(
        elem,
        '{description}',
        to_jsonb(replace(descr, '相机枚举', '相机的负载索引编号')),
        true
      );
    END IF;
    rebuilt := rebuilt || jsonb_build_array(elem);
  END LOOP;
  RETURN jsonb_set(doc, '{outbound}', rebuilt, true);
END;
$$;

UPDATE ent_data_protocol_t1
SET custom_fields = jsonb_set(
      custom_fields,
      '{field_description_json}',
      pg_temp.rename_payload_index_label(custom_fields->'field_description_json'),
      true
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND (custom_fields->'field_description_json')::text LIKE '%相机枚举%';

UPDATE ent_data_protocol_t1
SET field_description_json = pg_temp.rename_payload_index_label(field_description_json),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND tenant_id = 1
  AND field_description_json::text LIKE '%相机枚举%';

UPDATE dynamic_field
SET description = replace(description, '相机枚举', '相机的负载索引编号'),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND deleted = false
  AND description LIKE '%相机枚举%'
  AND code <> 'payload_index';

COMMIT;

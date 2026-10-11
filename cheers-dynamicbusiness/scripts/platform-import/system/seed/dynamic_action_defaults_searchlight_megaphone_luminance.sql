-- 探照灯亮度：官网取值 0 到 100，取中间值 50。
-- 环境亮度：定点拍照上这一格是必填。按曝光值填写，16 表示 0EV。
-- 喊话文件：这一格是必填。音频按喊话器输入格式生成，单声道、16 kHz、16 bit、PCM，时长 1 秒。
--   来源：https://developer.dji.com/doc/cloud-api-tutorial/en/api-reference/dock-to-cloud/mqtt/dock/dock3/psdk.html
--   官网 file.url 是机场下载音频的链接。这里用系统文件存储返回的访问地址，不用工作区路径。
--   文件已写入主存储，访问地址见下面的 megaphone_file_path。具体喊话时再换成当时要播的那一份。
-- 气体上报频率：从大疆飞机型号的参数里去掉。东古和机器人保留。
-- 已有默认值不覆盖。
SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness, public;

BEGIN;

CREATE TEMP TABLE fill_default (
  field_code text PRIMARY KEY,
  default_value text NOT NULL
) ON COMMIT DROP;

INSERT INTO fill_default (field_code, default_value) VALUES
  ('searchlightBrightness', '50'),
  ('orientedCameraLuminance', '16'),
  ('megaphone_file_name', 'megaphone-default_1791236112923.pcm'),
  ('megaphone_file_path', 'http://127.0.0.1:15080/admin-api/infra/file/29/get/megaphone/20261006/megaphone-default_1791236112923.pcm'),
  ('megaphone_file_md5', '22054b0f4ee3aba7e05490fd09d5799d');

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(
          jsonb_set(
            m.value,
            '{fields}',
            (
              SELECT COALESCE(
                jsonb_agg(
                  CASE
                    WHEN COALESCE(f.value->>'defaultValue', '') = ''
                     AND fill.default_value IS NOT NULL THEN
                      f.value || jsonb_build_object('defaultValue', fill.default_value)
                    ELSE f.value
                  END
                  ORDER BY f.ord
                ),
                '[]'::jsonb
              )
              FROM jsonb_array_elements(COALESCE(m.value->'fields', '[]'::jsonb))
                WITH ORDINALITY AS f(value, ord)
              LEFT JOIN fill_default fill ON fill.field_code = f.value->>'fieldCode'
            )
          )
          ORDER BY m.ord
        ), '[]'::jsonb)
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS m(value, ord)
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND jsonb_typeof(a.param_slots_json->'methods') = 'array'
  AND (
    a.param_slots_json::text LIKE '%searchlightBrightness%'
    OR a.param_slots_json::text LIKE '%orientedCameraLuminance%'
    OR a.param_slots_json::text LIKE '%megaphone_file_name%'
  );

UPDATE ent_action_t1 a
SET param_slots_json = jsonb_set(
      a.param_slots_json,
      '{methods}',
      (
        SELECT COALESCE(jsonb_agg(
          CASE
            WHEN EXISTS (
              SELECT 1
              FROM dynamic_model host
              WHERE host.deleted = false
                AND host.tenant_id = 1
                AND host.id::text = m.value->>'equipmentModelId'
                AND host.code LIKE 'dji-m%'
            ) THEN
              jsonb_set(
                m.value,
                '{fields}',
                (
                  SELECT COALESCE(
                    jsonb_agg(f.value ORDER BY f.ord),
                    '[]'::jsonb
                  )
                  FROM jsonb_array_elements(COALESCE(m.value->'fields', '[]'::jsonb))
                    WITH ORDINALITY AS f(value, ord)
                  WHERE f.value->>'fieldCode' IS DISTINCT FROM 'gas_frequency'
                )
              )
            ELSE m.value
          END
          ORDER BY m.ord
        ), '[]'::jsonb)
        FROM jsonb_array_elements(a.param_slots_json->'methods') WITH ORDINALITY AS m(value, ord)
      )
    ),
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND a.tenant_id = 1
  AND a.param_slots_json::text LIKE '%gas_frequency%';

DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN dynamic_model host ON host.code = 'dji-m4d' AND host.deleted = false AND host.tenant_id = 1
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
    WHERE a.code = 'searchlightOn'
      AND m->>'equipmentModelId' = host.id::text
      AND f->>'fieldCode' = 'searchlightBrightness'
      AND f->>'defaultValue' IS DISTINCT FROM '50'
  ) THEN
    RAISE EXCEPTION '探照灯亮度不是 50';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN dynamic_model host ON host.code = 'dji-m30' AND host.deleted = false AND host.tenant_id = 1
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
    WHERE a.code = 'orientedShoot'
      AND m->>'equipmentModelId' = host.id::text
      AND f->>'fieldCode' = 'orientedCameraLuminance'
      AND f->>'defaultValue' IS DISTINCT FROM '16'
  ) THEN
    RAISE EXCEPTION '环境亮度不是曝光值 16';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN dynamic_model host ON host.code LIKE 'dji-m%' AND host.deleted = false AND host.tenant_id = 1
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
    WHERE m->>'equipmentModelId' = host.id::text
      AND f->>'fieldCode' = 'gas_frequency'
  ) THEN
    RAISE EXCEPTION '大疆型号上还留着气体上报频率';
  END IF;

  IF NOT EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN dynamic_model host ON host.code = 'donggu-uav-small' AND host.deleted = false
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
    WHERE m->>'equipmentModelId' = host.id::text
      AND f->>'fieldCode' = 'gas_frequency'
  ) THEN
    RAISE EXCEPTION '东古小型无人机上的气体上报频率被删了';
  END IF;

  IF EXISTS (
    SELECT 1
    FROM ent_action_t1 a
    JOIN dynamic_model host ON host.code = 'dji-m4td' AND host.deleted = false
    JOIN LATERAL jsonb_array_elements(a.param_slots_json->'methods') m ON true
    JOIN LATERAL jsonb_array_elements(m->'fields') f ON true
    WHERE a.code = 'megaphone'
      AND m->>'equipmentModelId' = host.id::text
      AND f->>'fieldCode' = 'megaphone_file_md5'
      AND f->>'defaultValue' IS DISTINCT FROM '22054b0f4ee3aba7e05490fd09d5799d'
  ) THEN
    RAISE EXCEPTION '喊话文件校验没有写上';
  END IF;
END $$;

COMMIT;

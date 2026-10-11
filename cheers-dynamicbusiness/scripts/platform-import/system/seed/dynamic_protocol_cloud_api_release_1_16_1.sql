-- 大疆机场、大疆 Pilot 的协议版本码加上上云 API 发布号 1.16.1。
-- 两本仍分开。指令编号（proto-dji-dock-…）不改。机器人、地面站无人机不加这个号。
-- 只改等于 dji-dock / dji-pilot 的版本值，不改型号编码和指令编号。

SET search_path TO dynamicbusiness, public;

UPDATE dynamic_field
SET
  options = '[{"label":"机器人对接协议","value":"robot-ws"},{"label":"无人机对接协议","value":"uav-ws"},{"label":"大疆机场 1.16.1","value":"dji-dock-1.16.1"},{"label":"大疆 Pilot 1.16.1","value":"dji-pilot-1.16.1"}]',
  description = CASE code
    WHEN 'platform_protocol' THEN '这台设备默认按哪一种平台对接协议下发。大疆机场和大疆 Pilot 的选项带上云 API 发布号 1.16.1。不是支持清单。任务里可临时改，不改就用这条。'
    WHEN 'protocol_versions' THEN '这台设备支持哪些平台对接协议。双击多选。大疆机场和大疆 Pilot 的选项带上云 API 发布号 1.16.1。不是当前正在用的那一条。'
    ELSE description
  END,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE code IN ('platform_protocol', 'protocol_versions', 'mapped_protocol_version', 'protocol_version')
  AND tenant_id = 1
  AND deleted = false;

CREATE OR REPLACE FUNCTION pg_temp.map_cloud_release(value text)
RETURNS text
LANGUAGE sql
IMMUTABLE
AS $$
  SELECT CASE value
    WHEN 'dji-dock' THEN 'dji-dock-1.16.1'
    WHEN 'dji-pilot' THEN 'dji-pilot-1.16.1'
    ELSE value
  END
$$;

CREATE OR REPLACE FUNCTION pg_temp.map_cloud_release_json(doc jsonb)
RETURNS jsonb
LANGUAGE plpgsql
IMMUTABLE
AS $$
DECLARE
  key text;
  element jsonb;
  rebuilt jsonb;
BEGIN
  IF doc IS NULL THEN
    RETURN NULL;
  END IF;
  IF jsonb_typeof(doc) = 'string' THEN
    RETURN to_jsonb(pg_temp.map_cloud_release(doc #>> '{}'));
  ELSIF jsonb_typeof(doc) = 'array' THEN
    rebuilt := '[]'::jsonb;
    FOR element IN SELECT value FROM jsonb_array_elements(doc) LOOP
      rebuilt := rebuilt || jsonb_build_array(pg_temp.map_cloud_release_json(element));
    END LOOP;
    RETURN rebuilt;
  ELSIF jsonb_typeof(doc) = 'object' THEN
    rebuilt := '{}'::jsonb;
    FOR key, element IN SELECT e.key, e.value FROM jsonb_each(doc) AS e LOOP
      rebuilt := rebuilt || jsonb_build_object(key, pg_temp.map_cloud_release_json(element));
    END LOOP;
    RETURN rebuilt;
  END IF;
  RETURN doc;
END
$$;

DO $$
DECLARE
  rec record;
BEGIN
  FOR rec IN
    SELECT table_schema, table_name, column_name, data_type
    FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND (
        data_type = 'jsonb'
        OR (data_type IN ('text', 'character varying')
            AND column_name IN (
              'platform_protocol', 'protocol_version', 'mapped_protocol_version', 'value_string'
            ))
      )
  LOOP
    IF rec.data_type = 'jsonb' THEN
      EXECUTE format(
        'UPDATE %I.%I SET %I = pg_temp.map_cloud_release_json(%I) WHERE %I::text LIKE %L OR %I::text LIKE %L',
        rec.table_schema, rec.table_name, rec.column_name, rec.column_name,
        rec.column_name, '%"dji-dock"%',
        rec.column_name, '%"dji-pilot"%'
      );
    ELSE
      EXECUTE format(
        'UPDATE %I.%I SET %I = pg_temp.map_cloud_release(%I) WHERE %I IN (%L, %L)',
        rec.table_schema, rec.table_name, rec.column_name, rec.column_name,
        rec.column_name, 'dji-dock', 'dji-pilot'
      );
    END IF;
  END LOOP;
END $$;

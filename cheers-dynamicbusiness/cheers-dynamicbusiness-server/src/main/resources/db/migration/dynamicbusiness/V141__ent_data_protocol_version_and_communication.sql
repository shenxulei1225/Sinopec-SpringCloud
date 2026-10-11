-- V141: 数据协议「协议版本」「通讯方式」升成类型基础字段物理列
--
-- 对象栏按分类看时，大疆机场和 Pilot、MQTT 和航线文件会排在一起，
-- 这两列每一行并不相同，和方向一样要展示、要筛选。
-- 协议版本沿用扩展袋里的数组（多选），列类型 JSONB。
-- 通讯方式是单一枚举，列类型 VARCHAR。
-- 值从 custom_fields 搬进列后，从扩展袋去掉，避免两处各写一份。
-- 不负责：指令 JSON、字段说明、样例仍是型号扩展字段；通道和主题只挂 MQTT。

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_data_protocol(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS protocol_version JSONB',
      tbl);
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.protocol_version IS %L',
      tbl, '对接协议版本，多选数组，如 ["dji-dock"]');
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS communication_protocol VARCHAR(32)',
      tbl);
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.communication_protocol IS %L',
      tbl, '这条指令的通讯方式：mqtt / https / websocket / jsbridge / wayline');
    EXECUTE format(
      'CREATE INDEX IF NOT EXISTS %I ON dynamicbusiness.%I (communication_protocol) WHERE deleted = false',
      'idx_' || tbl || '_communication_protocol',
      tbl);

    EXECUTE format(
      $sql$
      UPDATE dynamicbusiness.%I
      SET
        protocol_version = CASE
          WHEN jsonb_typeof(custom_fields->'protocol_version') = 'array'
            THEN custom_fields->'protocol_version'
          WHEN NULLIF(btrim(custom_fields->>'protocol_version'), '') IS NOT NULL
            THEN jsonb_build_array(btrim(custom_fields->>'protocol_version'))
          ELSE protocol_version
        END,
        communication_protocol = COALESCE(
          NULLIF(btrim(custom_fields->>'communication_protocol'), ''),
          communication_protocol
        ),
        custom_fields = custom_fields - 'protocol_version' - 'communication_protocol',
        updater = CASE WHEN updater IS NULL OR updater = '' THEN 'v141' ELSE updater END,
        update_time = CURRENT_TIMESTAMP
      WHERE deleted = false
        AND custom_fields ?| ARRAY['protocol_version', 'communication_protocol']
      $sql$,
      tbl);
  END LOOP;
END $$;

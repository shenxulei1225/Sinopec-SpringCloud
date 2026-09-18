-- V128: 设备台账「协议版本」数组升成类型基础字段物理列
--
-- 权威：设备存其支持的对接协议版本，执行取第一种。
-- 列名 = 字段编码 protocol_versions。工业设备可空数组，不在读路径猜 robot-ws。
-- 不负责：开跑按步骤树组动作。

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
      AND t.table_name ~ '^ent_equipment(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS protocol_versions JSONB NOT NULL DEFAULT ''[]''::jsonb',
      tbl);
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.protocol_versions IS %L',
      tbl, '设备支持的对接协议版本（如 robot-ws / uav-ws）；执行取第一种');
  END LOOP;
END $$;

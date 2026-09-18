-- V133: 设备台账「当前使用的平台对接协议」单值列
--
-- 权威：开跑只认这一条；不是软件版本，也不是支持清单。
-- 支持的协议只展示，不另开可写字段。
-- 不负责：协议指令说明书、任务页选手动协议。

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
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS platform_protocol VARCHAR(32)',
      tbl);
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.platform_protocol IS %L',
      tbl, '当前使用的平台对接协议（robot-ws / uav-ws）；空表示尚未登记，不得猜默认');
  END LOOP;
END $$;

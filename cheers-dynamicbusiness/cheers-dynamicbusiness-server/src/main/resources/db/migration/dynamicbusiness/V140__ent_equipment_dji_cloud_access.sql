-- V140: 大疆上云执行设备的接入方式、网关序列号、飞机序列号
--
-- 权威：协议仍是设备上的「当前使用的协议」。大疆上云与东古 WebSocket 不是同一套。
-- 机场和 Pilot 只是接入方式，不另开协议、不另开设备台账。
-- 不负责：填写真实序列号；上云登录和按动作下发。

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
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS cloud_access_mode VARCHAR(16)',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS gateway_sn VARCHAR(64)',
      tbl);
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS aircraft_sn VARCHAR(64)',
      tbl);
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.cloud_access_mode IS %L',
      tbl, '大疆上云接入方式：dock 机场，pilot 为 Pilot。其它协议留空，不得猜');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.gateway_sn IS %L',
      tbl, '上云网关序列号：机场填机场序列号，Pilot 填遥控器序列号');
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.aircraft_sn IS %L',
      tbl, '这台网关下面实际飞行的飞机序列号');
  END LOOP;
END $$;

-- V127: 数据协议「方向」升成类型基础字段物理列
--
-- 对象栏展示列/筛选只认系统字段 + 类型基础字段。方向原先在 custom_fields，
-- 列表筛不了。缺列走加基础字段再 ALTER，不在读路径猜。
-- 不负责：交互方式、协议版本仍按型号扩展字段。

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
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS command_direction VARCHAR(32)',
      tbl);
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.command_direction IS %L',
      tbl, '指令方向：downlink=下发，uplink=上报，ack=回执');
    EXECUTE format(
      'CREATE INDEX IF NOT EXISTS %I ON dynamicbusiness.%I (command_direction) WHERE deleted = false',
      'idx_' || tbl || '_command_direction',
      tbl);
    EXECUTE format(
      $sql$
      UPDATE dynamicbusiness.%I
      SET
        command_direction = NULLIF(btrim(custom_fields->>'command_direction'), ''),
        custom_fields = custom_fields - 'command_direction',
        updater = CASE WHEN updater IS NULL OR updater = '' THEN 'v127' ELSE updater END,
        update_time = CURRENT_TIMESTAMP
      WHERE deleted = false
        AND custom_fields ? 'command_direction'
        AND (
          command_direction IS NULL
          OR command_direction IS DISTINCT FROM NULLIF(btrim(custom_fields->>'command_direction'), '')
        )
      $sql$,
      tbl);
  END LOOP;
END $$;

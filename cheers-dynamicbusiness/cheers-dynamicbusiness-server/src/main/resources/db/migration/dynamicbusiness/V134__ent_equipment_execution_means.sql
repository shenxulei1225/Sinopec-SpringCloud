-- V134: 设备台账「适用手段」多选升成类型基础字段物理列
--
-- 现象：seed 15 把 execution_means 挂成 equipment BASE 后，查数 SELECT 该列，
--       专用表还没有列，打开任务选目标 / 拉设备详情报系统异常。
-- 权威：类型基础字段必须对应专用表真实物理列；缺列走 ALTER，禁止读路径改查询。
-- 不负责：给已有设备猜该勾哪些手段。

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
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS execution_means JSONB NOT NULL DEFAULT ''[]''::jsonb',
      tbl);
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.execution_means IS %L',
      tbl, '设备适用手段编码数组（MANUAL/UAV/ROBOT/FIXED_CAMERA）；可多选，不猜历史设备');
  END LOOP;
END $$;

-- V91: 宿主 SOP 参数包列（设备专用表）+ 动作参数定义列注释
-- 定稿：docs/动态业务/宿主SOP参数包与动作参数定稿.md
-- 元数据 seed：scripts/platform-import/action/（param_slots_json 显示名）
--             scripts/platform-import/system/seed/dynamic_equipment_host_sop_param_pack.sql

SET search_path TO dynamicbusiness, public;

-- ---------------------------------------------------------------------------
-- 1) ent_equipment / ent_equipment_t*：宿主 SOP 参数包物理列
--    字段编码 = 列名 host_sop_param_pack；检查配方界面显示名可为「设备检查参数」
--    JSON 外形：{ "version": 1, "entries": [ { subjectType, subjectId, sopTemplateId,
--                 dimensionKey, dimensionValue, paramsByNode } ] }
-- ---------------------------------------------------------------------------
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
      'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS host_sop_param_pack JSONB NOT NULL DEFAULT ''{}''::jsonb',
      tbl);
    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.host_sop_param_pack IS %L',
      tbl,
      '宿主 SOP 参数包（平台语义）；检查配方显示名可用「设备检查参数」。'
        || '外形 {version,entries[{subjectType,subjectId,sopTemplateId,dimensionKey,dimensionValue,paramsByNode}]}');
  END LOOP;
END $$;

-- ---------------------------------------------------------------------------
-- 2) ent_action / ent_action_t*：param_slots_json 注释改为「动作参数定义」
--    外形 { "version": 1, "fields": [ { fieldCode, required, defaultValue } ] }
--    兼容旧数据：若存的是 JSON 字符串数组，则视为 fieldCode 列表
-- ---------------------------------------------------------------------------
DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_action(_t[0-9]+)?$'
  LOOP
    IF EXISTS (
      SELECT 1
      FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness'
        AND c.table_name = tbl
        AND c.column_name = 'param_slots_json'
    ) THEN
      EXECUTE format(
        'COMMENT ON COLUMN dynamicbusiness.%I.param_slots_json IS %L',
        tbl,
        '动作参数定义（复用列 param_slots_json）。'
          || '外形 {version,fields[{fieldCode,required,defaultValue}]}；'
          || '兼容旧数据：JSON 字符串数组视为 fieldCode 列表');
    END IF;
  END LOOP;
END $$;

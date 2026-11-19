-- 应急事件按发生的场站归属。
-- 权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md 第 2.2 节（归属声明）。
--
-- 背景：应急事件是全网目录，只声明了可选的「所属管线段」，现有 11 条都没有管线段，
-- 受限用户一条都看不到。事件发生的场站一直写在自定义字段 custom_fields.facility_id 里，权限核对读不到。
-- 「所属场站」基础字段由数据目录基本信息按正常流程添加（专用列 facility_id）。
--
-- 本迁移做的事（租户 1，一次性）：
--   1. 把 custom_fields.facility_id 搬进专用列 facility_id，并从自定义字段里删掉这个键，只留一份。
--   2. 写归属声明「应急事件 · 所属场站 → 场站」，选填。与已有的「所属管线段 → 管线」并存，命中任一条即可见。
-- 专用列不存在（还没加所属场站基础字段的环境）时只记日志，不建列、不写声明。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    moved integer;
BEGIN
    IF to_regclass('dynamicbusiness.ent_emergency_t1') IS NULL THEN
        RAISE NOTICE 'V225: 租户 1 没有应急事件表，跳过';
        RETURN;
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = 'dynamicbusiness' AND table_name = 'ent_emergency_t1' AND column_name = 'facility_id'
    ) THEN
        RAISE NOTICE 'V225: 应急事件还没有所属场站基础字段，请先在数据目录基本信息里添加，再补这条迁移的数据';
        RETURN;
    END IF;

    UPDATE ent_emergency_t1
    SET facility_id = (custom_fields ->> 'facility_id')::bigint,
        updater = 'V225', update_time = CURRENT_TIMESTAMP
    WHERE deleted = false AND facility_id IS NULL
      AND (custom_fields ->> 'facility_id') ~ '^[0-9]+$';
    GET DIAGNOSTICS moved = ROW_COUNT;

    UPDATE ent_emergency_t1
    SET custom_fields = custom_fields - 'facility_id'
    WHERE custom_fields ? 'facility_id';

    INSERT INTO dynamic_catalog_ownership (registry_code, field_code, target_registry_code, required,
                                           multi_valued, creator, updater, tenant_id)
    SELECT 'emergency', 'facility_id', 'facility', false, false, 'V225', 'V225', 1
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamic_catalog_ownership
        WHERE tenant_id = 1 AND deleted = false AND registry_code = 'emergency' AND field_code = 'facility_id'
    );

    RAISE NOTICE 'V225: 应急事件所属场站搬入专用列 % 条', moved;
END $$;

-- ============================================================================
-- 平台导入完整性校验（布局 + 数据关系图）
--
-- 目的：
-- 1) 防止“只导入了 entity_type，没生成 layout/props/关系用途”；
-- 2) 把缺口在导入阶段直接报错，不允许读路径补丁兜底。
-- ============================================================================

SET search_path TO dynamicbusiness;

DROP TABLE IF EXISTS tmp_import_integrity_violations;
CREATE TEMP TABLE tmp_import_integrity_violations (
    id BIGSERIAL PRIMARY KEY,
    rule_code TEXT NOT NULL,
    tenant_id BIGINT,
    entity_type_code TEXT,
    layout_id BIGINT,
    detail TEXT NOT NULL
);

-- R001：每个有效目录都必须挂 data_layout_id
INSERT INTO tmp_import_integrity_violations (rule_code, tenant_id, entity_type_code, layout_id, detail)
SELECT
    'R001',
    et.tenant_id,
    et.code,
    NULL,
    '目录缺 data_layout_id，导入绕过了创建 bootstrap（应由创建流程写入）'
FROM dynamic_entity_type et
WHERE et.deleted = false
  AND et.data_layout_id IS NULL;

-- R002：非 SCOPE 目录必须挂 model_layout_id
INSERT INTO tmp_import_integrity_violations (rule_code, tenant_id, entity_type_code, layout_id, detail)
SELECT
    'R002',
    et.tenant_id,
    et.code,
    NULL,
    '非 SCOPE 目录缺 model_layout_id，模型管理页无法稳定加载独立布局'
FROM dynamic_entity_type et
WHERE et.deleted = false
  AND COALESCE(et.entry_kind, 'NATIVE') <> 'SCOPE'
  AND et.model_layout_id IS NULL;

-- R003：data_layout_id 指向的布局行必须存在，且 entity_type_code 要对得上
INSERT INTO tmp_import_integrity_violations (rule_code, tenant_id, entity_type_code, layout_id, detail)
SELECT
    'R003',
    et.tenant_id,
    et.code,
    et.data_layout_id,
    'data_layout_id 无有效布局行，或布局行 entity_type_code 与目录码不一致'
FROM dynamic_entity_type et
WHERE et.deleted = false
  AND et.data_layout_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM dm_data_tab_layout l
    WHERE l.deleted = false
      AND l.layout_id = et.data_layout_id
      AND l.entity_type_code = et.code
  );

-- R004：model_layout_id 指向的布局行必须存在，且 entity_type_code 要对得上
INSERT INTO tmp_import_integrity_violations (rule_code, tenant_id, entity_type_code, layout_id, detail)
SELECT
    'R004',
    et.tenant_id,
    et.code,
    et.model_layout_id,
    'model_layout_id 无有效布局行，或布局行 entity_type_code 与目录码不一致'
FROM dynamic_entity_type et
WHERE et.deleted = false
  AND COALESCE(et.entry_kind, 'NATIVE') <> 'SCOPE'
  AND et.model_layout_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM dm_data_tab_layout l
    WHERE l.deleted = false
      AND l.layout_id = et.model_layout_id
      AND l.entity_type_code = et.code
  );

-- R005：被目录引用的布局头必须有 sections（区域清单）
WITH referenced_layouts AS (
    SELECT et.tenant_id, et.code, et.data_layout_id AS layout_id
    FROM dynamic_entity_type et
    WHERE et.deleted = false AND et.data_layout_id IS NOT NULL
    UNION ALL
    SELECT et.tenant_id, et.code, et.model_layout_id AS layout_id
    FROM dynamic_entity_type et
    WHERE et.deleted = false
      AND COALESCE(et.entry_kind, 'NATIVE') <> 'SCOPE'
      AND et.model_layout_id IS NOT NULL
)
INSERT INTO tmp_import_integrity_violations (rule_code, tenant_id, entity_type_code, layout_id, detail)
SELECT
    'R005',
    r.tenant_id,
    r.code,
    r.layout_id,
    '布局头缺 sections 区域清单，关系图无法按区域稳定渲染'
FROM referenced_layouts r
LEFT JOIN dm_workbench_layout w ON w.id = r.layout_id AND w.deleted = false
WHERE w.id IS NULL
   OR jsonb_typeof(w.settings_json -> 'sections') <> 'array'
   OR jsonb_array_length(w.settings_json -> 'sections') = 0;

-- R006：启用中的 CATEGORY/MODEL/ENTITY 行必须有类型码
INSERT INTO tmp_import_integrity_violations (rule_code, tenant_id, entity_type_code, layout_id, detail)
SELECT
    'R006',
    et.tenant_id,
    l.entity_type_code,
    l.layout_id,
    CASE l.column_kind
        WHEN 'CATEGORY' THEN '启用分类栏缺 categoryTypeCode'
        WHEN 'MODEL' THEN '启用模型栏缺 modelEntityTypeCode'
        WHEN 'ENTITY' THEN '启用实体栏缺 entityEntityTypeCode'
        ELSE '启用栏缺类型码'
    END
FROM dm_data_tab_layout l
JOIN dynamic_entity_type et
  ON et.code = l.entity_type_code
 AND et.tenant_id = l.tenant_id
 AND et.deleted = false
WHERE l.deleted = false
  AND COALESCE(l.enabled, true) = true
  AND (
      (l.column_kind = 'CATEGORY' AND COALESCE(BTRIM(l.column_meta ->> 'categoryTypeCode'), '') = '')
      OR
      (l.column_kind = 'MODEL' AND COALESCE(BTRIM(l.column_meta ->> 'modelEntityTypeCode'), '') = '')
      OR
      (l.column_kind = 'ENTITY' AND COALESCE(BTRIM(l.column_meta ->> 'entityEntityTypeCode'), '') = '')
  );

-- R007：启用中的 CATEGORY/MODEL/ENTITY 行必须有 props_id
INSERT INTO tmp_import_integrity_violations (rule_code, tenant_id, entity_type_code, layout_id, detail)
SELECT
    'R007',
    et.tenant_id,
    l.entity_type_code,
    l.layout_id,
    '启用栏缺 props_id（展示配置），页面会出现“栏已开启但无法展示”'
FROM dm_data_tab_layout l
JOIN dynamic_entity_type et
  ON et.code = l.entity_type_code
 AND et.tenant_id = l.tenant_id
 AND et.deleted = false
WHERE l.deleted = false
  AND COALESCE(l.enabled, true) = true
  AND l.column_kind IN ('CATEGORY', 'MODEL', 'ENTITY')
  AND (l.props_id IS NULL OR l.props_id <= 0);

-- R008：栏间关系必须显式 edgeAction（filter/write/detail_follow）
INSERT INTO tmp_import_integrity_violations (rule_code, tenant_id, entity_type_code, layout_id, detail)
SELECT
    'R008',
    r.tenant_id,
    r.entity_type_code,
    r.layout_id,
    '栏间关系缺 edgeAction 或值非法（禁止运行时反推用途）'
FROM dm_data_tab_column_relation r
WHERE r.deleted = false
  AND COALESCE(BTRIM(r.relation_meta ->> 'edgeAction'), '') NOT IN ('filter', 'write', 'detail_follow');

-- R009：栏间关系端点不得为空
INSERT INTO tmp_import_integrity_violations (rule_code, tenant_id, entity_type_code, layout_id, detail)
SELECT
    'R009',
    r.tenant_id,
    r.entity_type_code,
    r.layout_id,
    '栏间关系 from/to 端点为空'
FROM dm_data_tab_column_relation r
WHERE r.deleted = false
  AND (
      COALESCE(BTRIM(r.from_column_identity), '') = ''
      OR COALESCE(BTRIM(r.to_column_identity), '') = ''
  );

DO $$
DECLARE
    v_count BIGINT;
    v_sample TEXT;
BEGIN
    SELECT COUNT(*) INTO v_count FROM tmp_import_integrity_violations;
    IF v_count > 0 THEN
        SELECT string_agg(
                   format(
                       '[%s] tenant=%s type=%s layout=%s detail=%s',
                       rule_code,
                       COALESCE(tenant_id::TEXT, '-'),
                       COALESCE(entity_type_code, '-'),
                       COALESCE(layout_id::TEXT, '-'),
                       detail
                   ),
                   E'\n'
               )
        INTO v_sample
        FROM (
            SELECT rule_code, tenant_id, entity_type_code, layout_id, detail
            FROM tmp_import_integrity_violations
            ORDER BY id
            LIMIT 20
        ) t;

        RAISE EXCEPTION E'导入完整性校验失败：共 % 条。\n示例（最多20条）：\n%',
            v_count, COALESCE(v_sample, '(no sample)');
    END IF;
END $$;

SELECT 'PASS: 布局与数据关系图导入完整性校验通过' AS integrity_check_result;


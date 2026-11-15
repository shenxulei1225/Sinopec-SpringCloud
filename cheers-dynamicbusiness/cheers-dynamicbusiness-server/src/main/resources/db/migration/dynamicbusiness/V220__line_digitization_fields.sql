-- 按线路数字化恢复规格书，补管段、管道和构筑物上还缺的型号和字段。
-- 管段仍用管线目录里已有的天然气管道线路、成品油管道线路。不另建管线段目录。
-- 不写入示例管段、焊缝或桩。打开页面不补这些字段。
SET search_path TO dynamicbusiness, public;

INSERT INTO dynamicbusiness.dynamic_field (
    code, name, type, description, source, status, provider_code, tenant_id,
    creator, updater, deleted, governance_status
)
SELECT v.code, v.name, v.type, v.description, 'CUSTOM', 1, v.provider_code, tenant.tenant_id,
       'line-digit', 'line-digit', false, 'COMPANY'
FROM (
    SELECT DISTINCT tenant_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code IN ('pipeline', 'pipeline_pipe', 'pipeline_structure', 'facility')
      AND deleted = false
) tenant
CROSS JOIN (VALUES
    ('design_temperature', '设计温度', 'NUMBER', '设计温度，单位℃。', NULL),
    ('operating_pressure', '运行压力', 'NUMBER', '运行压力，单位 MPa。', NULL),
    ('section_length', '长度', 'NUMBER', '这一段的长度，单位米。', NULL),
    ('pipe_material', '管道材质', 'TEXT', '管道材质。', NULL),
    ('cp_method', '阴保方式', 'TEXT', '阴极保护方式。', NULL),
    ('maop', '最大允许操作压力', 'NUMBER', '最大允许操作压力，单位 MPa。', NULL),
    ('start_facility_id', '起点站场阀室', 'ENTITY_REF', '这一段从哪一座站场或阀室开始。', 'dynamic-entity:facility'),
    ('end_facility_id', '终点站场阀室', 'ENTITY_REF', '这一段到哪一座站场或阀室结束。', 'dynamic-entity:facility'),
    ('stake_offset', '相对桩距离', 'NUMBER', '相对中线桩的距离，单位米。', NULL),
    ('inlet_diameter', '入口外径', 'NUMBER', '入口外径，单位毫米。', NULL),
    ('inlet_wall', '入口壁厚', 'NUMBER', '入口壁厚，单位毫米。', NULL),
    ('outlet_diameter', '出口外径', 'NUMBER', '出口外径，单位毫米。', NULL),
    ('outlet_wall', '出口壁厚', 'NUMBER', '出口壁厚，单位毫米。', NULL),
    ('branch_diameter', '支线外径', 'NUMBER', '三通支线外径，单位毫米。', NULL),
    ('branch_wall', '支线壁厚', 'NUMBER', '三通支线壁厚，单位毫米。', NULL),
    ('pressure_class', '压力等级', 'TEXT', '压力等级。', NULL),
    ('tee_type', '三通类型', 'TEXT', '三通类型。', NULL),
    ('weld_type', '焊缝类型', 'TEXT', '焊缝类型。', NULL),
    ('front_pipe_no', '前钢管编号', 'TEXT', '焊缝前一根钢管的编号。', NULL),
    ('front_pipe_spec', '前钢管规格', 'TEXT', '焊缝前一根钢管的规格。', NULL),
    ('back_pipe_no', '后钢管编号', 'TEXT', '焊缝后一根钢管的编号。', NULL),
    ('back_pipe_spec', '后钢管规格', 'TEXT', '焊缝后一根钢管的规格。', NULL),
    ('weld_date', '施焊日期', 'DATE', '施焊日期。', NULL),
    ('welder_name', '焊工姓名', 'TEXT', '建设期焊缝上的焊工姓名。', NULL),
    ('pile_type', '桩类型', 'TEXT', '里程桩、测试桩或标志桩。', NULL),
    ('burial_depth', '埋深', 'NUMBER', '管顶到地面的埋深，单位米。', NULL),
    ('hca_type', '高后果区类型', 'TEXT', '人员密集、环境敏感、交通设施或易燃易爆场所。', NULL),
    ('end_mileage', '结束里程', 'NUMBER', '这一段结束的绝对里程，单位米。', NULL),
    ('segment_seq', '顺序编号', 'NUMBER', '站场或阀室在这一段上的顺序。', NULL)
) AS v(code, name, type, description, provider_code)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_field own
    WHERE own.tenant_id = tenant.tenant_id AND own.code = v.code AND own.deleted = false
);

INSERT INTO dynamicbusiness.dynamic_model (
    code, name, entity_type_code, description, status, sort, creator, updater,
    deleted, tenant_id, governance_status
)
SELECT v.model_code, v.model_name, v.entity_code, v.model_name, 1, v.sort,
       'line-digit', 'line-digit', false, catalog.tenant_id, 'COMPANY'
FROM dynamicbusiness.dynamic_entity_type catalog
JOIN (VALUES
    ('MODEL-pipe-reducer', '变径管', 'pipeline_pipe', 3),
    ('MODEL-pipe-tee', '三通', 'pipeline_pipe', 4),
    ('MODEL-structure-test-stake', '测试桩', 'pipeline_structure', 4),
    ('MODEL-structure-hydraulic', '水工保护', 'pipeline_structure', 5),
    ('MODEL-structure-slope', '挡土墙护坡', 'pipeline_structure', 6),
    ('MODEL-structure-insulation', '绝缘装置', 'pipeline_structure', 7),
    ('MODEL-structure-drainage', '排流装置', 'pipeline_structure', 8),
    ('MODEL-structure-anode', '牺牲阳极', 'pipeline_structure', 9)
) AS v(model_code, model_name, entity_code, sort)
  ON catalog.code = v.entity_code AND catalog.deleted = false
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_model own
    WHERE own.tenant_id = catalog.tenant_id AND own.code = v.model_code AND own.deleted = false
);

DROP TABLE IF EXISTS line_digit_field;
CREATE TEMP TABLE line_digit_field (
    entity_type_code text NOT NULL,
    model_code text,
    field_code text NOT NULL,
    required boolean NOT NULL,
    sort_order integer NOT NULL
);

INSERT INTO line_digit_field (entity_type_code, model_code, field_code, required, sort_order) VALUES
    ('pipeline', 'LINE', 'medium_type', false, 20),
    ('pipeline', 'LINE', 'pipe_material', false, 21),
    ('pipeline', 'LINE', 'design_pressure', false, 22),
    ('pipeline', 'LINE', 'design_temperature', false, 23),
    ('pipeline', 'LINE', 'operating_pressure', false, 24),
    ('pipeline', 'LINE', 'maop', false, 25),
    ('pipeline', 'LINE', 'section_length', false, 26),
    ('pipeline', 'LINE', 'cp_method', false, 27),
    ('pipeline', 'LINE', 'commission_date', false, 28),
    ('pipeline', 'LINE', 'start_facility_id', false, 29),
    ('pipeline', 'LINE', 'end_facility_id', false, 30),
    ('pipeline_pipe', 'MODEL-pipe-elbow', 'stake_number', false, 20),
    ('pipeline_pipe', 'MODEL-pipe-elbow', 'stake_offset', false, 21),
    ('pipeline_pipe', 'MODEL-pipe-reducer', 'pipeline_segment_id', true, 10),
    ('pipeline_pipe', 'MODEL-pipe-reducer', 'inlet_diameter', false, 11),
    ('pipeline_pipe', 'MODEL-pipe-reducer', 'inlet_wall', false, 12),
    ('pipeline_pipe', 'MODEL-pipe-reducer', 'outlet_diameter', false, 13),
    ('pipeline_pipe', 'MODEL-pipe-reducer', 'outlet_wall', false, 14),
    ('pipeline_pipe', 'MODEL-pipe-reducer', 'pipe_grade', false, 15),
    ('pipeline_pipe', 'MODEL-pipe-reducer', 'pressure_class', false, 16),
    ('pipeline_pipe', 'MODEL-pipe-reducer', 'line_mileage', false, 17),
    ('pipeline_pipe', 'MODEL-pipe-tee', 'pipeline_segment_id', true, 10),
    ('pipeline_pipe', 'MODEL-pipe-tee', 'tee_type', false, 11),
    ('pipeline_pipe', 'MODEL-pipe-tee', 'inlet_diameter', false, 12),
    ('pipeline_pipe', 'MODEL-pipe-tee', 'inlet_wall', false, 13),
    ('pipeline_pipe', 'MODEL-pipe-tee', 'outlet_diameter', false, 14),
    ('pipeline_pipe', 'MODEL-pipe-tee', 'outlet_wall', false, 15),
    ('pipeline_pipe', 'MODEL-pipe-tee', 'branch_diameter', false, 16),
    ('pipeline_pipe', 'MODEL-pipe-tee', 'branch_wall', false, 17),
    ('pipeline_pipe', 'MODEL-pipe-tee', 'pipe_grade', false, 18),
    ('pipeline_pipe', 'MODEL-pipe-tee', 'pressure_class', false, 19),
    ('pipeline_pipe', 'MODEL-pipe-tee', 'line_mileage', false, 20),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'line_mileage', false, 20),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'weld_type', false, 21),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'front_pipe_no', false, 22),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'front_pipe_spec', false, 23),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'back_pipe_no', false, 24),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'back_pipe_spec', false, 25),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'weld_date', false, 26),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'welder_name', false, 27),
    ('pipeline_structure', 'MODEL-structure-stake', 'pile_type', false, 20),
    ('pipeline_structure', 'MODEL-structure-stake', 'burial_depth', false, 21),
    ('pipeline_structure', 'MODEL-structure-hca', 'hca_type', false, 20),
    ('pipeline_structure', 'MODEL-structure-hca', 'end_mileage', false, 21),
    ('facility', NULL, 'line_mileage', false, 81),
    ('facility', NULL, 'segment_seq', false, 82);

INSERT INTO line_digit_field (entity_type_code, model_code, field_code, required, sort_order)
SELECT 'pipeline_structure', v.model_code, loc.field_code, loc.required, loc.sort_order
FROM (VALUES
    ('MODEL-structure-test-stake'),
    ('MODEL-structure-hydraulic'),
    ('MODEL-structure-slope'),
    ('MODEL-structure-insulation'),
    ('MODEL-structure-drainage'),
    ('MODEL-structure-anode')
) AS v(model_code)
CROSS JOIN (VALUES
    ('pipeline_segment_id', true, 10),
    ('stake_number', false, 11),
    ('line_mileage', false, 12),
    ('longitude', false, 13),
    ('latitude', false, 14)
) AS loc(field_code, required, sort_order);

INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
    entity_type_code, library_field_id, field_code, field_name, data_type, required,
    description, sort_order, status, tenant_id, creator, updater, deleted,
    is_searchable, is_filterable, is_sortable
)
SELECT
    spec.entity_type_code, field.id, field.code, field.name,
    CASE field.type
        WHEN 'ENTITY_REF' THEN 'REF'
        WHEN 'NUMBER' THEN 'NUMBER'
        WHEN 'DATE' THEN 'DATE'
        WHEN 'DATETIME' THEN 'DATETIME'
        ELSE 'TEXT'
    END,
    bool_or(spec.required), field.description, min(spec.sort_order), 1, catalog.tenant_id,
    'line-digit', 'line-digit', false, false, true, false
FROM line_digit_field spec
JOIN dynamicbusiness.dynamic_entity_type catalog
  ON catalog.code = spec.entity_type_code AND catalog.deleted = false
 AND (spec.entity_type_code NOT IN ('pipeline', 'facility') OR catalog.tenant_id = 1)
JOIN dynamicbusiness.dynamic_field field
  ON field.tenant_id = catalog.tenant_id AND field.code = spec.field_code AND field.deleted = false
GROUP BY spec.entity_type_code, field.id, field.code, field.name, field.type, field.description, catalog.tenant_id
HAVING NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field base
    WHERE base.tenant_id = catalog.tenant_id
      AND base.entity_type_code = spec.entity_type_code
      AND base.field_code = field.code
      AND base.deleted = false
);

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
    creator, updater, deleted, tenant_id, model_code, field_code, collection_point,
    create_visible, edit_visible, detail_visible, field_source
)
SELECT
    model.id, field.id, spec.required, false, true, false, spec.sort_order,
    'line-digit', 'line-digit', false, model.tenant_id, model.code, field.code, false,
    true, true, true, 'BASE'
FROM line_digit_field spec
JOIN dynamicbusiness.dynamic_model model
  ON model.deleted = false
 AND model.entity_type_code = spec.entity_type_code
 AND (
      (spec.model_code = 'LINE' AND model.name IN ('天然气管道线路', '成品油管道线路'))
      OR (spec.model_code IS NULL AND spec.entity_type_code = 'facility' AND model.tenant_id = 1)
      OR (spec.model_code IS NOT NULL AND spec.model_code <> 'LINE' AND model.code = spec.model_code)
 )
JOIN dynamicbusiness.dynamic_field field
  ON field.tenant_id = model.tenant_id AND field.code = spec.field_code AND field.deleted = false
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment own
    WHERE own.tenant_id = model.tenant_id
      AND own.model_id = model.id
      AND own.field_code = field.code
      AND own.deleted = false
);

DO $$
DECLARE
    rec record;
    col record;
BEGIN
    FOR rec IN
        SELECT code, dedicated_table_name
        FROM dynamicbusiness.dynamic_entity_type
        WHERE deleted = false
          AND dedicated_table_name IS NOT NULL
          AND btrim(dedicated_table_name) <> ''
          AND code IN ('pipeline', 'pipeline_pipe', 'pipeline_structure', 'facility')
    LOOP
        FOR col IN
            SELECT DISTINCT field.code AS name,
                CASE field.type
                    WHEN 'ENTITY_REF' THEN 'bigint'
                    WHEN 'NUMBER' THEN 'numeric'
                    WHEN 'DATE' THEN 'date'
                    WHEN 'DATETIME' THEN 'timestamp'
                    ELSE 'text'
                END AS kind
            FROM line_digit_field spec
            JOIN dynamicbusiness.dynamic_field field
              ON field.code = spec.field_code AND field.deleted = false
            WHERE spec.entity_type_code = rec.code
        LOOP
            EXECUTE format(
                'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS %I %s',
                rec.dedicated_table_name, col.name, col.kind);
        END LOOP;
    END LOOP;
END $$;

INSERT INTO dynamicbusiness.dynamic_model_category_relation (
    model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id,
    model_code, category_code
)
SELECT model.id, category.id, model.entity_type_code, model.sort,
       'line-digit', 'line-digit', false, model.tenant_id, model.code, category.code
FROM dynamicbusiness.dynamic_model model
JOIN dynamicbusiness.dynamic_category category
  ON category.tenant_id = model.tenant_id
 AND category.category_type_code = model.entity_type_code
 AND category.parent_id IS NULL
 AND category.deleted = false
WHERE model.deleted = false
  AND model.code IN (
      'MODEL-pipe-reducer', 'MODEL-pipe-tee',
      'MODEL-structure-test-stake', 'MODEL-structure-hydraulic', 'MODEL-structure-slope',
      'MODEL-structure-insulation', 'MODEL-structure-drainage', 'MODEL-structure-anode'
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_category_relation own
      WHERE own.tenant_id = model.tenant_id
        AND own.model_code = model.code
        AND own.category_code = category.code
        AND own.entity_type_code = model.entity_type_code
        AND own.deleted = false
  );

DO $$
DECLARE
    rec record;
    rel text;
BEGIN
    FOR rec IN
        SELECT DISTINCT tenant_id
        FROM dynamicbusiness.dynamic_entity_type
        WHERE deleted = false AND code IN ('pipeline_pipe', 'pipeline_structure')
    LOOP
        rel := 'dynamic_model_category_relation_t' || rec.tenant_id::text;
        IF to_regclass('dynamicbusiness.' || rel) IS NULL THEN
            CONTINUE;
        END IF;
        EXECUTE format($c$
            INSERT INTO dynamicbusiness.%I (
                model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id,
                model_code, category_code
            )
            SELECT model.id, category.id, model.entity_type_code, model.sort,
                   'line-digit', 'line-digit', false, model.tenant_id, model.code, category.code
            FROM dynamicbusiness.dynamic_model model
            JOIN dynamicbusiness.dynamic_category category
              ON category.tenant_id = model.tenant_id
             AND category.category_type_code = model.entity_type_code
             AND category.parent_id IS NULL
             AND category.deleted = false
            WHERE model.deleted = false
              AND model.tenant_id = %s
              AND model.code IN (
                  'MODEL-pipe-reducer', 'MODEL-pipe-tee',
                  'MODEL-structure-test-stake', 'MODEL-structure-hydraulic', 'MODEL-structure-slope',
                  'MODEL-structure-insulation', 'MODEL-structure-drainage', 'MODEL-structure-anode'
              )
              AND NOT EXISTS (
                  SELECT 1 FROM dynamicbusiness.%I own
                  WHERE own.tenant_id = model.tenant_id
                    AND own.model_code = model.code
                    AND own.category_code = category.code
                    AND own.entity_type_code = model.entity_type_code
                    AND own.deleted = false
              )
        $c$, rel, rec.tenant_id::text, rel);
    END LOOP;
END $$;

DROP TABLE IF EXISTS line_digit_field;

-- 管线下面补管线段、管道、管线构筑物。
-- 管线仍用已有目录。管线段用城市缩写命名，挂在一条管线上。
-- 场站只增加「位于哪一个管线段」。管道和构筑物都只记所属管线段。
-- 上次的管道线路目录撤下，不往里录数据。不在这里编数据页布局。
SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.dynamic_entity_type
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE code = 'pipeline_line' AND deleted = false;

UPDATE dynamicbusiness.dynamic_model
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'pipeline_line' AND deleted = false;

UPDATE dynamicbusiness.dynamic_category
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE category_type_code = 'pipeline_line' AND deleted = false;

UPDATE dynamicbusiness.dynamic_category_type
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE category_type_code = 'pipeline_line' AND deleted = false;

UPDATE dynamicbusiness.dynamic_entity_type_base_field
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE field_code = 'pipeline_line_id' AND deleted = false
  AND entity_type_code IN ('emergency', 'repair_field_record', 'pipeline_line');

UPDATE dynamicbusiness.dynamic_model_field_assignment
SET deleted = true, updater = 'pipeline-segment', update_time = CURRENT_TIMESTAMP
WHERE field_code = 'pipeline_line_id' AND deleted = false;

INSERT INTO dynamicbusiness.dynamic_field (
    code, name, type, description, source, status, provider_code, tenant_id,
    creator, updater, deleted, governance_status
)
SELECT v.code, v.name, v.type, v.description, 'CUSTOM', 1, v.provider_code, tenant.tenant_id,
       'pipeline-segment', 'pipeline-segment', false, 'COMPANY'
FROM (
    SELECT DISTINCT tenant_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code = 'pipeline' AND deleted = false
) tenant
CROSS JOIN (VALUES
    ('pipeline_id', '所属管线', 'ENTITY_REF', '这一段属于哪一条管线。', 'dynamic-entity:pipeline'),
    ('pipe_diameter', '管径', 'NUMBER', '管径。', NULL),
    ('wall_thickness', '壁厚', 'NUMBER', '壁厚。', NULL),
    ('pipe_grade', '钢级', 'TEXT', '钢管钢级。', NULL),
    ('stake_number', '桩号', 'TEXT', '桩号。', NULL),
    ('line_mileage', '里程', 'NUMBER', '里程。', NULL),
    ('longitude', '经度', 'NUMBER', '经度。', NULL),
    ('latitude', '纬度', 'NUMBER', '纬度。', NULL),
    ('start_city', '起点城市', 'TEXT', '这一段从哪一座城市开始。', NULL),
    ('end_city', '终点城市', 'TEXT', '这一段到哪一座城市结束。', NULL),
    ('pipeline_segment_id', '所属管线段', 'ENTITY_REF', '这条记录在哪一个管线段上。', 'dynamic-entity:pipeline_segment'),
    ('pipeline_pipe_id', '所属管道', 'ENTITY_REF', '现场记录引用的管子、弯头或焊缝。', 'dynamic-entity:pipeline_pipe'),
    ('weld_no', '焊缝编号', 'TEXT', '这条焊缝的编号。', NULL)
) AS v(code, name, type, description, provider_code)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_field own
    WHERE own.tenant_id = tenant.tenant_id AND own.code = v.code AND own.deleted = false
);

DO $$
DECLARE
    rec record;
    spec record;
    tbl text;
BEGIN
    FOR rec IN
        SELECT DISTINCT tenant_id
        FROM dynamicbusiness.dynamic_entity_type
        WHERE code = 'pipeline' AND deleted = false
    LOOP
        FOR spec IN
            SELECT *
            FROM (VALUES
                ('pipeline_segment', '管线段', '一条管线从一座城市到另一座城市的一段，用两个城市的名字缩写命名', 'NETWORK'),
                ('pipeline_pipe', '管道', '用来输送的管子、弯头或焊缝。管径、壁厚、钢级写在这里', 'NETWORK'),
                ('pipeline_structure', '管线构筑物', '挂在管线段上的桩、穿跨越、防腐设施或高后果区', 'NETWORK')
            ) AS t(code, name, description, work_scope)
        LOOP
            tbl := 'ent_' || spec.code || '_t' || rec.tenant_id::text;
            EXECUTE format('CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.%I', tbl || '_id_seq');
            EXECUTE format($c$
                CREATE TABLE IF NOT EXISTS dynamicbusiness.%I (
                    id bigint PRIMARY KEY DEFAULT nextval(%L),
                    tenant_id bigint NOT NULL DEFAULT %s,
                    entity_type_code varchar(64) NOT NULL DEFAULT %L,
                    model_id bigint NOT NULL,
                    domain varchar(128),
                    name varchar(255) NOT NULL,
                    code varchar(255),
                    parent_id bigint DEFAULT 0,
                    tree_path varchar(1024),
                    sort integer DEFAULT 0,
                    status integer DEFAULT 1,
                    custom_fields jsonb DEFAULT '{}'::jsonb,
                    creator varchar(64),
                    create_time timestamp DEFAULT CURRENT_TIMESTAMP,
                    updater varchar(64),
                    update_time timestamp DEFAULT CURRENT_TIMESTAMP,
                    deleted boolean DEFAULT false
                )$c$, tbl, 'dynamicbusiness.' || tbl || '_id_seq', rec.tenant_id::text, spec.code);

            INSERT INTO dynamicbusiness.dynamic_entity_type (
                code, name, description, alias, sort, status, type_level, association_fields,
                storage_type, dedicated_table_name, enable_rule_engine, creator, updater, deleted,
                tenant_id, group_name, entry_kind, model_workbench_mode, work_scope
            )
            SELECT
                spec.code, spec.name, spec.description, spec.name,
                COALESCE((SELECT MAX(sort) FROM dynamicbusiness.dynamic_entity_type WHERE tenant_id = rec.tenant_id AND deleted = false), 0) + 1,
                'active', 'USER', '{}'::jsonb, 'DEDICATED', tbl, false,
                'pipeline-segment', 'pipeline-segment', false, rec.tenant_id, '智慧管网', 'NATIVE', 'MULTI', spec.work_scope
            WHERE NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_entity_type
                WHERE tenant_id = rec.tenant_id AND code = spec.code AND deleted = false
            );
        END LOOP;

        INSERT INTO dynamicbusiness.dynamic_model (
            code, name, entity_type_code, description, status, sort, creator, updater,
            deleted, tenant_id, governance_status
        )
        SELECT v.model_code, v.model_name, v.entity_code, v.model_name, 1, v.sort,
               'pipeline-segment', 'pipeline-segment', false, rec.tenant_id, 'COMPANY'
        FROM (VALUES
            ('MODEL-pipeline-segment', '管线段', 'pipeline_segment', 0),
            ('MODEL-pipe-tube', '管子', 'pipeline_pipe', 0),
            ('MODEL-pipe-elbow', '弯头', 'pipeline_pipe', 1),
            ('MODEL-pipe-weld', '焊缝', 'pipeline_pipe', 2),
            ('MODEL-structure-stake', '桩', 'pipeline_structure', 0),
            ('MODEL-structure-crossing', '穿跨越', 'pipeline_structure', 1),
            ('MODEL-structure-coating', '防腐设施', 'pipeline_structure', 2),
            ('MODEL-structure-hca', '高后果区', 'pipeline_structure', 3)
        ) AS v(model_code, model_name, entity_code, sort)
        WHERE NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_model own
            WHERE own.tenant_id = rec.tenant_id AND own.code = v.model_code AND own.deleted = false
        );
    END LOOP;
END $$;

DROP TABLE IF EXISTS pipeline_segment_field;
CREATE TEMP TABLE pipeline_segment_field (
    entity_type_code text NOT NULL,
    model_code text,
    field_code text NOT NULL,
    required boolean NOT NULL,
    sort_order integer NOT NULL
);

INSERT INTO pipeline_segment_field (entity_type_code, model_code, field_code, required, sort_order) VALUES
    ('pipeline_segment', 'MODEL-pipeline-segment', 'pipeline_id', true, 10),
    ('pipeline_segment', 'MODEL-pipeline-segment', 'start_city', false, 11),
    ('pipeline_segment', 'MODEL-pipeline-segment', 'end_city', false, 12),
    ('pipeline_pipe', 'MODEL-pipe-tube', 'pipeline_segment_id', true, 10),
    ('pipeline_pipe', 'MODEL-pipe-tube', 'pipe_diameter', false, 11),
    ('pipeline_pipe', 'MODEL-pipe-tube', 'wall_thickness', false, 12),
    ('pipeline_pipe', 'MODEL-pipe-tube', 'pipe_grade', false, 13),
    ('pipeline_pipe', 'MODEL-pipe-elbow', 'pipeline_segment_id', true, 10),
    ('pipeline_pipe', 'MODEL-pipe-elbow', 'pipe_diameter', false, 11),
    ('pipeline_pipe', 'MODEL-pipe-elbow', 'wall_thickness', false, 12),
    ('pipeline_pipe', 'MODEL-pipe-elbow', 'pipe_grade', false, 13),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'pipeline_segment_id', true, 10),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'weld_no', true, 11),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'pipe_diameter', false, 12),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'wall_thickness', false, 13),
    ('pipeline_pipe', 'MODEL-pipe-weld', 'pipe_grade', false, 14),
    ('pipeline_structure', 'MODEL-structure-stake', 'pipeline_segment_id', true, 10),
    ('pipeline_structure', 'MODEL-structure-stake', 'stake_number', false, 11),
    ('pipeline_structure', 'MODEL-structure-stake', 'line_mileage', false, 12),
    ('pipeline_structure', 'MODEL-structure-stake', 'longitude', false, 13),
    ('pipeline_structure', 'MODEL-structure-stake', 'latitude', false, 14),
    ('pipeline_structure', 'MODEL-structure-crossing', 'pipeline_segment_id', true, 10),
    ('pipeline_structure', 'MODEL-structure-crossing', 'stake_number', false, 11),
    ('pipeline_structure', 'MODEL-structure-crossing', 'line_mileage', false, 12),
    ('pipeline_structure', 'MODEL-structure-crossing', 'longitude', false, 13),
    ('pipeline_structure', 'MODEL-structure-crossing', 'latitude', false, 14),
    ('pipeline_structure', 'MODEL-structure-coating', 'pipeline_segment_id', true, 10),
    ('pipeline_structure', 'MODEL-structure-coating', 'stake_number', false, 11),
    ('pipeline_structure', 'MODEL-structure-coating', 'line_mileage', false, 12),
    ('pipeline_structure', 'MODEL-structure-coating', 'longitude', false, 13),
    ('pipeline_structure', 'MODEL-structure-coating', 'latitude', false, 14),
    ('pipeline_structure', 'MODEL-structure-hca', 'pipeline_segment_id', true, 10),
    ('pipeline_structure', 'MODEL-structure-hca', 'stake_number', false, 11),
    ('pipeline_structure', 'MODEL-structure-hca', 'line_mileage', false, 12),
    ('pipeline_structure', 'MODEL-structure-hca', 'longitude', false, 13),
    ('pipeline_structure', 'MODEL-structure-hca', 'latitude', false, 14),
    ('facility', NULL, 'pipeline_segment_id', false, 80),
    ('emergency', NULL, 'pipeline_segment_id', false, 48),
    ('repair_field_record', NULL, 'pipeline_pipe_id', false, 12);

INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
    entity_type_code, library_field_id, field_code, field_name, data_type, required,
    description, sort_order, status, tenant_id, creator, updater, deleted,
    is_searchable, is_filterable, is_sortable
)
SELECT
    spec.entity_type_code,
    field.id,
    field.code,
    field.name,
    CASE field.type
        WHEN 'ENTITY_REF' THEN 'REF'
        WHEN 'NUMBER' THEN 'NUMBER'
        ELSE 'TEXT'
    END,
    bool_or(spec.required),
    field.description,
    min(spec.sort_order),
    1,
    catalog.tenant_id,
    'pipeline-segment',
    'pipeline-segment',
    false,
    false,
    true,
    false
FROM pipeline_segment_field spec
JOIN dynamicbusiness.dynamic_entity_type catalog
  ON catalog.code = spec.entity_type_code AND catalog.deleted = false
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
    'pipeline-segment', 'pipeline-segment', false, model.tenant_id, model.code, field.code, false,
    true, true, true, 'BASE'
FROM pipeline_segment_field spec
JOIN dynamicbusiness.dynamic_model model
  ON model.deleted = false
 AND model.entity_type_code = spec.entity_type_code
 AND (spec.model_code IS NULL OR model.code = spec.model_code)
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
          AND code IN (
              'pipeline_segment', 'pipeline_pipe', 'pipeline_structure',
              'facility', 'emergency', 'repair_field_record'
          )
    LOOP
        FOR col IN
            SELECT name, kind
            FROM (VALUES
                ('pipeline_segment', 'pipeline_id', 'bigint'),
                ('pipeline_segment', 'start_city', 'text'),
                ('pipeline_segment', 'end_city', 'text'),
                ('pipeline_pipe', 'pipeline_segment_id', 'bigint'),
                ('pipeline_pipe', 'pipe_diameter', 'numeric'),
                ('pipeline_pipe', 'wall_thickness', 'numeric'),
                ('pipeline_pipe', 'pipe_grade', 'text'),
                ('pipeline_pipe', 'weld_no', 'text'),
                ('pipeline_structure', 'pipeline_segment_id', 'bigint'),
                ('pipeline_structure', 'stake_number', 'text'),
                ('pipeline_structure', 'line_mileage', 'numeric'),
                ('pipeline_structure', 'longitude', 'numeric'),
                ('pipeline_structure', 'latitude', 'numeric'),
                ('facility', 'pipeline_segment_id', 'bigint'),
                ('emergency', 'pipeline_segment_id', 'bigint'),
                ('repair_field_record', 'pipeline_pipe_id', 'bigint')
            ) AS v(catalog, name, kind)
            WHERE v.catalog = rec.code
        LOOP
            EXECUTE format(
                'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS %I %s',
                rec.dedicated_table_name, col.name, col.kind);
        END LOOP;
    END LOOP;
END $$;

INSERT INTO dynamicbusiness.dynamic_category_type (
    category_type_code, name, description, status, tenant_id, creator, updater, deleted,
    category_mode, entity_association_mode
)
SELECT catalog.code, catalog.name,
       '数据类型「' || catalog.name || '」默认分类',
       1, catalog.tenant_id, 'pipeline-segment', 'pipeline-segment', false, 'SIMPLE', 'MULTI'
FROM dynamicbusiness.dynamic_entity_type catalog
WHERE catalog.deleted = false
  AND catalog.code IN ('pipeline_segment', 'pipeline_pipe', 'pipeline_structure')
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_category_type own
      WHERE own.tenant_id = catalog.tenant_id
        AND own.category_type_code = catalog.code
        AND own.deleted = false
  );

INSERT INTO dynamicbusiness.dynamic_category (
    parent_id, name, code, category_type_code, level, sort, status, description,
    creator, updater, deleted, tenant_id, parent_code
)
SELECT NULL, v.name, v.code, v.type_code, 1, 1, 1, v.name,
       'pipeline-segment', 'pipeline-segment', false, catalog.tenant_id, NULL
FROM dynamicbusiness.dynamic_entity_type catalog
CROSS JOIN (VALUES
    ('pipeline_segment', 'pipeline_segment_root', '管线段'),
    ('pipeline_pipe', 'pipeline_pipe_root', '管道'),
    ('pipeline_structure', 'pipeline_structure_root', '管线构筑物')
) AS v(type_code, code, name)
WHERE catalog.deleted = false
  AND catalog.code = v.type_code
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_category own
      WHERE own.tenant_id = catalog.tenant_id AND own.code = v.code AND own.deleted = false
  );

UPDATE dynamicbusiness.dynamic_category c
SET tree_path = '/' || c.id::text || '/',
    updater = 'pipeline-segment',
    update_time = CURRENT_TIMESTAMP
WHERE c.deleted = false
  AND c.parent_id IS NULL
  AND c.code IN ('pipeline_segment_root', 'pipeline_pipe_root', 'pipeline_structure_root')
  AND (c.tree_path IS NULL OR btrim(c.tree_path) = '');

UPDATE dynamicbusiness.dynamic_category_type t
SET top_level_category_id = c.id,
    updater = 'pipeline-segment',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.dynamic_category c
WHERE t.deleted = false
  AND c.deleted = false
  AND t.tenant_id = c.tenant_id
  AND t.category_type_code = c.category_type_code
  AND c.code = t.category_type_code || '_root'
  AND t.category_type_code IN ('pipeline_segment', 'pipeline_pipe', 'pipeline_structure')
  AND t.top_level_category_id IS NULL;

INSERT INTO dynamicbusiness.dynamic_model_category_relation (
    model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id,
    model_code, category_code
)
SELECT model.id, category.id, model.entity_type_code, model.sort,
       'pipeline-segment', 'pipeline-segment', false, model.tenant_id, model.code, category.code
FROM dynamicbusiness.dynamic_model model
JOIN dynamicbusiness.dynamic_category category
  ON category.tenant_id = model.tenant_id
 AND category.category_type_code = model.entity_type_code
 AND category.parent_id IS NULL
 AND category.deleted = false
WHERE model.deleted = false
  AND model.entity_type_code IN ('pipeline_segment', 'pipeline_pipe', 'pipeline_structure')
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
        WHERE deleted = false AND code = 'pipeline_segment'
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
                   'pipeline-segment', 'pipeline-segment', false, model.tenant_id, model.code, category.code
            FROM dynamicbusiness.dynamic_model model
            JOIN dynamicbusiness.dynamic_category category
              ON category.tenant_id = model.tenant_id
             AND category.category_type_code = model.entity_type_code
             AND category.parent_id IS NULL
             AND category.deleted = false
            WHERE model.deleted = false
              AND model.tenant_id = %s
              AND model.entity_type_code IN ('pipeline_segment', 'pipeline_pipe', 'pipeline_structure')
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

INSERT INTO dynamicbusiness.dynamic_catalog_role_grant (
    role_id, registry_code, subject, operation, tenant_id, creator, updater, deleted
)
SELECT src.role_id, target.code, src.subject, src.operation, src.tenant_id,
       'pipeline-segment', 'pipeline-segment', false
FROM dynamicbusiness.dynamic_catalog_role_grant src
CROSS JOIN (VALUES
    ('pipeline_segment'),
    ('pipeline_pipe'),
    ('pipeline_structure')
) AS target(code)
WHERE src.registry_code = 'pipeline'
  AND src.deleted = false
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_catalog_role_grant own
      WHERE own.role_id = src.role_id
        AND own.registry_code = target.code
        AND own.subject = src.subject
        AND own.operation = src.operation
        AND own.tenant_id = src.tenant_id
        AND own.deleted = false
  );

DROP TABLE IF EXISTS pipeline_segment_field;

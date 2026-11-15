-- 维抢修基础数据。只给已经有应急事件目录的租户写。
-- 五本新目录：管道线路、人员资质、抢修方案、作业许可、现场作业记录。
-- 应急事件、应急预案、应急资源只增加字段。不另建事件账。
-- 不在这里编数据页布局。打开数据页时按创建目录的同一条挂上。
-- 目录已经在时，本脚本不改它的身份，只补还没有的字段、型号和分类。
SET search_path TO dynamicbusiness, public;

INSERT INTO dynamicbusiness.dynamic_field (
    code, name, type, description, source, status, provider_code, tenant_id,
    creator, updater, deleted, governance_status
)
SELECT v.code, v.name, v.type, v.description, 'CUSTOM', 1, v.provider_code, tenant.tenant_id,
       'repair-base', 'repair-base', false, 'COMPANY'
FROM (
    SELECT DISTINCT tenant_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE code = 'emergency' AND deleted = false
) tenant
CROSS JOIN (VALUES
    ('pipeline_code', '管线编码', 'TEXT', '这条线路的编码。', NULL),
    ('medium_type', '介质', 'TEXT', '这条管道输送的介质。', NULL),
    ('design_pressure', '设计压力', 'NUMBER', '设计压力。', NULL),
    ('pipe_diameter', '管径', 'NUMBER', '管径。', NULL),
    ('wall_thickness', '壁厚', 'NUMBER', '壁厚。', NULL),
    ('pipe_grade', '钢级', 'TEXT', '钢管钢级。', NULL),
    ('total_length', '长度', 'NUMBER', '线路长度，或这一段的长度。', NULL),
    ('management_unit', '管理单位', 'TEXT', '这条线路的管理单位。', NULL),
    ('line_mileage', '里程', 'NUMBER', '里程。', NULL),
    ('stake_number', '桩号', 'TEXT', '桩号。', NULL),
    ('burial_depth', '埋深', 'NUMBER', '埋深。', NULL),
    ('longitude', '经度', 'NUMBER', '经度。', NULL),
    ('latitude', '纬度', 'NUMBER', '纬度。', NULL),
    ('pipeline_line_id', '管道线路', 'ENTITY_REF', '引用的线路或设施。', 'dynamic-entity:pipeline_line'),
    ('person_id', '人员', 'ENTITY_REF', '持证的人。只记人员编号。', 'dynamic-entity:user_account'),
    ('certificate_no', '证书编号', 'TEXT', '证书编号。', NULL),
    ('certificate_issuer', '发证机关', 'TEXT', '发证机关。', NULL),
    ('certificate_valid_until', '证书到期日', 'DATE', '证书到期日。', NULL),
    ('certificate_work_range', '作业范围', 'TEXT', '这张证书准许的作业范围。', NULL),
    ('certificate_attachment', '证书附件', 'TEXT', '证书附件。', NULL),
    ('response_id', '应急响应', 'ENTITY_REF', '这条记录属于哪一次响应。', 'dynamic-entity:emergency_response'),
    ('scheme_status', '方案状态', 'TEXT', '草稿、审批中或已批准。', NULL),
    ('scheme_version', '方案版本', 'NUMBER', '这一份方案的版本。', NULL),
    ('scheme_overview', '方案概述', 'TEXT', '这一次抢修怎么做。', NULL),
    ('scheme_risk', '风险评估', 'TEXT', '这一次抢修的风险。', NULL),
    ('scheme_steps', '处置步骤', 'TEXT', '处置步骤。', NULL),
    ('scheme_resources', '资源需求', 'TEXT', '需要哪些人和物资。', NULL),
    ('scheme_safety', '安全措施', 'TEXT', '安全措施。', NULL),
    ('scheme_id', '抢修方案', 'ENTITY_REF', '这张许可属于哪一份方案。', 'dynamic-entity:repair_scheme'),
    ('permit_status', '许可状态', 'TEXT', '草稿、审批中、已许可、已失效或已关闭。', NULL),
    ('permit_valid_until', '许可有效期', 'DATETIME', '许可到什么时候为止。', NULL),
    ('permit_started_at', '开工时间', 'DATETIME', '现场确认开工的时间。', NULL),
    ('permit_process_check', '工艺条件', 'TEXT', '工艺条件是否已确认。', NULL),
    ('permit_environment_check', '环境条件', 'TEXT', '环境条件是否已确认。', NULL),
    ('permit_qualification_check', '人员资质核对', 'TEXT', '人员资质是否已确认。', NULL),
    ('permit_material_check', '物资设备核对', 'TEXT', '物资设备是否已确认。', NULL),
    ('permit_safety_check', '安全措施核对', 'TEXT', '安全措施是否已确认。', NULL),
    ('record_kind', '记录种类', 'TEXT', '环境检测、运行参数、开挖、焊接、焊缝检测、应力、回填恢复、复产评估或后期处置。', NULL),
    ('measure_name', '测量项', 'TEXT', '这一条记录测的是什么。', NULL),
    ('measure_value', '测量值', 'TEXT', '测到的值。', NULL),
    ('measure_unit', '单位', 'TEXT', '测量值的单位。', NULL),
    ('recorded_at', '记录时间', 'DATETIME', '这一条是什么时候记下的。', NULL),
    ('record_conclusion', '结论', 'TEXT', '这一条记录的结论。', NULL),
    ('record_attachment', '记录附件', 'TEXT', '照片或检测附件。', NULL),
    ('command_source', '接报来源', 'TEXT', '监视中心、人工接警或现场上报。', NULL),
    ('external_command_id', '外部命令编号', 'TEXT', '监视中心命令或告警编号。', NULL),
    ('occurred_at', '发生时间', 'DATETIME', '事故发生时间。', NULL),
    ('location_text', '地点描述', 'TEXT', '事故地点的文字描述。', NULL),
    ('needs_field_repair', '需要现场抢修', 'TEXT', '这份预案要不要做现场抢修。是或否。', NULL),
    ('safety_stock', '安全库存', 'NUMBER', '低于这个数时，出库同时写库存预警。', NULL),
    ('plate_no', '车牌', 'TEXT', '车辆的车牌。', NULL),
    ('vehicle_load', '载重', 'TEXT', '车辆载重。', NULL),
    ('license_valid_until', '证照到期日', 'DATE', '车辆证照到期日。', NULL),
    ('resource_category', '资源分类', 'TEXT', '物资、车辆、机具或外部单位。', NULL)
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
        WHERE code = 'emergency' AND deleted = false
    LOOP
        FOR spec IN
            SELECT *
            FROM (VALUES
                ('pipeline_line', '管道线路', '一条管道及其管子、焊缝、桩和检测资料', 'NETWORK', 'MODEL-pipeline-line', '管道线路'),
                ('person_certificate', '人员资质', '组织内人员持有的证书和到期日', 'NETWORK', 'MODEL-person-certificate', '证书'),
                ('repair_scheme', '抢修方案', '这一次事故的抢修技术文件', 'FACILITY', 'MODEL-repair-scheme', '抢修方案'),
                ('work_permit', '作业许可', '这一次抢修开工前的许可', 'FACILITY', 'MODEL-work-permit', '作业许可'),
                ('repair_field_record', '现场作业记录', '这一次抢修现场写下的检测和作业', 'FACILITY', 'MODEL-repair-field-record', '现场作业记录')
            ) AS t(code, name, description, work_scope, model_code, model_name)
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
                'repair-base', 'repair-base', false, rec.tenant_id, '应急管理', 'NATIVE', 'MULTI', spec.work_scope
            WHERE NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_entity_type
                WHERE tenant_id = rec.tenant_id AND code = spec.code AND deleted = false
            );

            INSERT INTO dynamicbusiness.dynamic_model (
                code, name, entity_type_code, description, status, sort, creator, updater,
                deleted, tenant_id, governance_status
            )
            SELECT spec.model_code, spec.model_name, spec.code, spec.model_name, 1, 0,
                   'repair-base', 'repair-base', false, rec.tenant_id, 'COMPANY'
            WHERE NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_model
                WHERE tenant_id = rec.tenant_id AND entity_type_code = spec.code AND deleted = false
            );
        END LOOP;

        INSERT INTO dynamicbusiness.dynamic_model (
            code, name, entity_type_code, description, status, sort, creator, updater,
            deleted, tenant_id, governance_status
        )
        SELECT 'MODEL-emergency-resource', '应急资源', 'emergency_resource', '应急资源', 1, 0,
               'repair-base', 'repair-base', false, rec.tenant_id, 'COMPANY'
        WHERE EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_entity_type
            WHERE tenant_id = rec.tenant_id AND code = 'emergency_resource' AND deleted = false
        )
          AND NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_model
            WHERE tenant_id = rec.tenant_id AND entity_type_code = 'emergency_resource' AND deleted = false
        );
    END LOOP;
END $$;

DROP TABLE IF EXISTS repair_base_field;
CREATE TEMP TABLE repair_base_field (
    entity_type_code text NOT NULL,
    field_code text NOT NULL,
    required boolean NOT NULL,
    sort_order integer NOT NULL
);

INSERT INTO repair_base_field (entity_type_code, field_code, required, sort_order) VALUES
    ('pipeline_line', 'pipeline_code', true, 10),
    ('pipeline_line', 'medium_type', false, 11),
    ('pipeline_line', 'design_pressure', false, 12),
    ('pipeline_line', 'pipe_diameter', false, 13),
    ('pipeline_line', 'wall_thickness', false, 14),
    ('pipeline_line', 'pipe_grade', false, 15),
    ('pipeline_line', 'total_length', false, 16),
    ('pipeline_line', 'management_unit', false, 17),
    ('pipeline_line', 'line_mileage', false, 18),
    ('pipeline_line', 'stake_number', false, 19),
    ('pipeline_line', 'longitude', false, 20),
    ('pipeline_line', 'latitude', false, 21),
    ('pipeline_line', 'burial_depth', false, 22),
    ('person_certificate', 'person_id', true, 10),
    ('person_certificate', 'certificate_no', false, 11),
    ('person_certificate', 'certificate_issuer', false, 12),
    ('person_certificate', 'certificate_valid_until', false, 13),
    ('person_certificate', 'certificate_work_range', false, 14),
    ('person_certificate', 'certificate_attachment', false, 15),
    ('repair_scheme', 'response_id', true, 10),
    ('repair_scheme', 'scheme_status', true, 11),
    ('repair_scheme', 'scheme_version', false, 12),
    ('repair_scheme', 'scheme_overview', false, 13),
    ('repair_scheme', 'scheme_risk', false, 14),
    ('repair_scheme', 'scheme_steps', false, 15),
    ('repair_scheme', 'scheme_resources', false, 16),
    ('repair_scheme', 'scheme_safety', false, 17),
    ('work_permit', 'scheme_id', true, 10),
    ('work_permit', 'permit_status', true, 11),
    ('work_permit', 'permit_valid_until', false, 12),
    ('work_permit', 'permit_started_at', false, 13),
    ('work_permit', 'permit_process_check', false, 14),
    ('work_permit', 'permit_environment_check', false, 15),
    ('work_permit', 'permit_qualification_check', false, 16),
    ('work_permit', 'permit_material_check', false, 17),
    ('work_permit', 'permit_safety_check', false, 18),
    ('repair_field_record', 'response_id', true, 10),
    ('repair_field_record', 'record_kind', true, 11),
    ('repair_field_record', 'pipeline_line_id', false, 12),
    ('repair_field_record', 'measure_name', false, 13),
    ('repair_field_record', 'measure_value', false, 14),
    ('repair_field_record', 'measure_unit', false, 15),
    ('repair_field_record', 'recorded_at', false, 16),
    ('repair_field_record', 'record_conclusion', false, 17),
    ('repair_field_record', 'record_attachment', false, 18),
    ('emergency', 'command_source', false, 40),
    ('emergency', 'external_command_id', false, 41),
    ('emergency', 'occurred_at', false, 42),
    ('emergency', 'location_text', false, 43),
    ('emergency', 'longitude', false, 44),
    ('emergency', 'latitude', false, 45),
    ('emergency', 'stake_number', false, 46),
    ('emergency', 'line_mileage', false, 47),
    ('emergency', 'pipeline_line_id', false, 48),
    ('emergentcy_plan', 'needs_field_repair', false, 40),
    ('emergency_resource', 'resource_category', false, 40),
    ('emergency_resource', 'safety_stock', false, 41),
    ('emergency_resource', 'plate_no', false, 42),
    ('emergency_resource', 'vehicle_load', false, 43),
    ('emergency_resource', 'license_valid_until', false, 44);

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
        WHEN 'DATETIME' THEN 'DATETIME'
        WHEN 'DATE' THEN 'DATE'
        WHEN 'NUMBER' THEN 'NUMBER'
        ELSE 'TEXT'
    END,
    spec.required,
    field.description,
    spec.sort_order,
    1,
    catalog.tenant_id,
    'repair-base',
    'repair-base',
    false,
    false,
    true,
    false
FROM repair_base_field spec
JOIN dynamicbusiness.dynamic_entity_type catalog
  ON catalog.code = spec.entity_type_code AND catalog.deleted = false
JOIN dynamicbusiness.dynamic_field field
  ON field.tenant_id = catalog.tenant_id AND field.code = spec.field_code AND field.deleted = false
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field base
    WHERE base.tenant_id = catalog.tenant_id
      AND base.entity_type_code = spec.entity_type_code
      AND base.field_code = spec.field_code
      AND base.deleted = false
);

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
    creator, updater, deleted, tenant_id, model_code, field_code, collection_point,
    create_visible, edit_visible, detail_visible, field_source
)
SELECT
    model.id, field.id, base.required, false, true, false, base.sort_order,
    'repair-base', 'repair-base', false, model.tenant_id, model.code, field.code, false,
    true, true, true, 'BASE'
FROM repair_base_field spec
JOIN dynamicbusiness.dynamic_model model
  ON model.entity_type_code = spec.entity_type_code AND model.deleted = false
JOIN dynamicbusiness.dynamic_entity_type_base_field base
  ON base.tenant_id = model.tenant_id
 AND base.entity_type_code = spec.entity_type_code
 AND base.field_code = spec.field_code
 AND base.deleted = false
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
              'pipeline_line', 'person_certificate', 'repair_scheme', 'work_permit',
              'repair_field_record', 'emergency', 'emergency_resource'
          )
    LOOP
        FOR col IN
            SELECT name, kind
            FROM (VALUES
                ('pipeline_line', 'pipeline_code', 'text'),
                ('pipeline_line', 'medium_type', 'text'),
                ('pipeline_line', 'design_pressure', 'numeric'),
                ('pipeline_line', 'pipe_diameter', 'numeric'),
                ('pipeline_line', 'wall_thickness', 'numeric'),
                ('pipeline_line', 'pipe_grade', 'text'),
                ('pipeline_line', 'total_length', 'numeric'),
                ('pipeline_line', 'management_unit', 'text'),
                ('pipeline_line', 'line_mileage', 'numeric'),
                ('pipeline_line', 'stake_number', 'text'),
                ('pipeline_line', 'longitude', 'numeric'),
                ('pipeline_line', 'latitude', 'numeric'),
                ('pipeline_line', 'burial_depth', 'numeric'),
                ('person_certificate', 'person_id', 'bigint'),
                ('person_certificate', 'certificate_no', 'text'),
                ('person_certificate', 'certificate_issuer', 'text'),
                ('person_certificate', 'certificate_valid_until', 'date'),
                ('person_certificate', 'certificate_work_range', 'text'),
                ('person_certificate', 'certificate_attachment', 'text'),
                ('repair_scheme', 'response_id', 'bigint'),
                ('repair_scheme', 'scheme_status', 'text'),
                ('repair_scheme', 'scheme_version', 'numeric'),
                ('repair_scheme', 'scheme_overview', 'text'),
                ('repair_scheme', 'scheme_risk', 'text'),
                ('repair_scheme', 'scheme_steps', 'text'),
                ('repair_scheme', 'scheme_resources', 'text'),
                ('repair_scheme', 'scheme_safety', 'text'),
                ('work_permit', 'scheme_id', 'bigint'),
                ('work_permit', 'permit_status', 'text'),
                ('work_permit', 'permit_valid_until', 'timestamp'),
                ('work_permit', 'permit_started_at', 'timestamp'),
                ('work_permit', 'permit_process_check', 'text'),
                ('work_permit', 'permit_environment_check', 'text'),
                ('work_permit', 'permit_qualification_check', 'text'),
                ('work_permit', 'permit_material_check', 'text'),
                ('work_permit', 'permit_safety_check', 'text'),
                ('repair_field_record', 'response_id', 'bigint'),
                ('repair_field_record', 'record_kind', 'text'),
                ('repair_field_record', 'pipeline_line_id', 'bigint'),
                ('repair_field_record', 'measure_name', 'text'),
                ('repair_field_record', 'measure_value', 'text'),
                ('repair_field_record', 'measure_unit', 'text'),
                ('repair_field_record', 'recorded_at', 'timestamp'),
                ('repair_field_record', 'record_conclusion', 'text'),
                ('repair_field_record', 'record_attachment', 'text'),
                ('emergency', 'command_source', 'text'),
                ('emergency', 'external_command_id', 'text'),
                ('emergency', 'occurred_at', 'timestamp'),
                ('emergency', 'location_text', 'text'),
                ('emergency', 'longitude', 'numeric'),
                ('emergency', 'latitude', 'numeric'),
                ('emergency', 'stake_number', 'text'),
                ('emergency', 'line_mileage', 'numeric'),
                ('emergency', 'pipeline_line_id', 'bigint'),
                ('emergency_resource', 'resource_category', 'text'),
                ('emergency_resource', 'safety_stock', 'numeric'),
                ('emergency_resource', 'plate_no', 'text'),
                ('emergency_resource', 'vehicle_load', 'text'),
                ('emergency_resource', 'license_valid_until', 'date')
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
       1, catalog.tenant_id, 'repair-base', 'repair-base', false, 'SIMPLE', 'MULTI'
FROM dynamicbusiness.dynamic_entity_type catalog
WHERE catalog.deleted = false
  AND catalog.code IN (
      'pipeline_line', 'person_certificate', 'repair_scheme', 'work_permit', 'repair_field_record'
  )
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
SELECT NULL, v.name, v.code, v.type_code, 1, v.sort, 1, v.name,
       'repair-base', 'repair-base', false, catalog.tenant_id, NULL
FROM dynamicbusiness.dynamic_entity_type catalog
CROSS JOIN (VALUES
    ('pipeline_line', 'pipeline_line_root', '线路', 1),
    ('person_certificate', 'person_certificate_root', '人员资质', 1),
    ('repair_scheme', 'repair_scheme_root', '抢修方案', 1),
    ('work_permit', 'work_permit_root', '作业许可', 1),
    ('repair_field_record', 'repair_field_record_root', '现场作业记录', 1)
) AS v(type_code, code, name, sort)
WHERE catalog.deleted = false
  AND catalog.code = v.type_code
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_category own
      WHERE own.tenant_id = catalog.tenant_id AND own.code = v.code AND own.deleted = false
  );

INSERT INTO dynamicbusiness.dynamic_category (
    parent_id, name, code, category_type_code, level, sort, status, description,
    creator, updater, deleted, tenant_id, parent_code
)
SELECT root.id, v.name, v.code, 'pipeline_line', 2, v.sort, 1, v.name,
       'repair-base', 'repair-base', false, root.tenant_id, 'pipeline_line_root'
FROM dynamicbusiness.dynamic_category root
CROSS JOIN (VALUES
    ('pipe_tube', '管子', 10),
    ('pipe_elbow', '弯头', 11),
    ('pipe_weld', '焊缝', 12),
    ('pipe_valve_room', '阀室', 13),
    ('pipe_stake', '桩', 14),
    ('pipe_crossing', '穿跨越', 15),
    ('pipe_coating', '防腐设施', 16),
    ('pipe_hca', '高后果区', 17),
    ('pipe_inspection_doc', '检测资料', 18)
) AS v(code, name, sort)
WHERE root.deleted = false
  AND root.code = 'pipeline_line_root'
  AND root.category_type_code = 'pipeline_line'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_category own
      WHERE own.tenant_id = root.tenant_id AND own.code = v.code AND own.deleted = false
  );

UPDATE dynamicbusiness.dynamic_category c
SET tree_path = '/' || c.id::text || '/',
    updater = 'repair-base',
    update_time = CURRENT_TIMESTAMP
WHERE c.deleted = false
  AND c.parent_id IS NULL
  AND c.code IN (
      'pipeline_line_root', 'person_certificate_root', 'repair_scheme_root',
      'work_permit_root', 'repair_field_record_root'
  )
  AND (c.tree_path IS NULL OR btrim(c.tree_path) = '');

UPDATE dynamicbusiness.dynamic_category c
SET tree_path = p.tree_path || c.id::text || '/',
    updater = 'repair-base',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.dynamic_category p
WHERE c.deleted = false
  AND p.deleted = false
  AND c.parent_id = p.id
  AND c.category_type_code = 'pipeline_line'
  AND c.code IN (
      'pipe_tube', 'pipe_elbow', 'pipe_weld', 'pipe_valve_room', 'pipe_stake',
      'pipe_crossing', 'pipe_coating', 'pipe_hca', 'pipe_inspection_doc'
  )
  AND (c.tree_path IS NULL OR btrim(c.tree_path) = '');

UPDATE dynamicbusiness.dynamic_category_type t
SET top_level_category_id = c.id,
    updater = 'repair-base',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.dynamic_category c
WHERE t.deleted = false
  AND c.deleted = false
  AND t.tenant_id = c.tenant_id
  AND t.category_type_code = c.category_type_code
  AND c.code = t.category_type_code || '_root'
  AND t.category_type_code IN (
      'pipeline_line', 'person_certificate', 'repair_scheme', 'work_permit', 'repair_field_record'
  )
  AND t.top_level_category_id IS NULL;

INSERT INTO dynamicbusiness.dynamic_model_category_relation (
    model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id,
    model_code, category_code
)
SELECT model.id, category.id, model.entity_type_code, category.sort,
       'repair-base', 'repair-base', false, model.tenant_id, model.code, category.code
FROM dynamicbusiness.dynamic_model model
JOIN dynamicbusiness.dynamic_category category
  ON category.tenant_id = model.tenant_id
 AND category.category_type_code = model.entity_type_code
 AND category.deleted = false
WHERE model.deleted = false
  AND model.entity_type_code IN (
      'pipeline_line', 'person_certificate', 'repair_scheme', 'work_permit', 'repair_field_record'
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
        WHERE deleted = false
          AND code = 'pipeline_line'
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
            SELECT model.id, category.id, model.entity_type_code, category.sort,
                   'repair-base', 'repair-base', false, model.tenant_id, model.code, category.code
            FROM dynamicbusiness.dynamic_model model
            JOIN dynamicbusiness.dynamic_category category
              ON category.tenant_id = model.tenant_id
             AND category.category_type_code = model.entity_type_code
             AND category.deleted = false
            WHERE model.deleted = false
              AND model.tenant_id = %s
              AND model.entity_type_code IN (
                  'pipeline_line', 'person_certificate', 'repair_scheme', 'work_permit', 'repair_field_record'
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

INSERT INTO dynamicbusiness.dynamic_catalog_role_grant (
    role_id, registry_code, subject, operation, tenant_id, creator, updater, deleted
)
SELECT src.role_id, target.code, src.subject, src.operation, src.tenant_id,
       'repair-base', 'repair-base', false
FROM dynamicbusiness.dynamic_catalog_role_grant src
CROSS JOIN (VALUES
    ('pipeline_line'),
    ('person_certificate'),
    ('repair_scheme'),
    ('work_permit'),
    ('repair_field_record')
) AS target(code)
WHERE src.registry_code = 'emergency'
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

DROP TABLE IF EXISTS repair_base_field;

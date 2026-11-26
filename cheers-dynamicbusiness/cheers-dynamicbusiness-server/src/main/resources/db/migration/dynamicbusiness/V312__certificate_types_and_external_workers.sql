-- 人员资质按证书类型建型号。外部施工人员单独一本目录。
-- 已有证书的持证人记为内部人员，编号仍是原来的组织用户编号。不复制第二张证书。
SET search_path TO dynamicbusiness;

CREATE OR REPLACE FUNCTION dynamicbusiness.v312_field(
    p_tenant bigint,
    p_code text,
    p_name text,
    p_type text,
    p_provider text,
    p_options text
) RETURNS bigint
LANGUAGE plpgsql
AS $$
DECLARE
    v_id bigint;
    v_src bigint;
BEGIN
    SELECT id INTO v_id
    FROM dynamicbusiness.dynamic_field
    WHERE tenant_id = p_tenant AND code = p_code AND deleted = false
    ORDER BY id LIMIT 1;
    IF v_id IS NOT NULL THEN
        UPDATE dynamicbusiness.dynamic_field
        SET name = p_name,
            type = p_type,
            description = p_name,
            provider_code = p_provider,
            options = p_options,
            updater = 'V312',
            update_time = CURRENT_TIMESTAMP
        WHERE id = v_id;
        RETURN v_id;
    END IF;

    SELECT id INTO v_src
    FROM dynamicbusiness.dynamic_field
    WHERE tenant_id = p_tenant AND deleted = false
    ORDER BY id LIMIT 1;
    IF v_src IS NULL THEN
        RETURN NULL;
    END IF;

    CREATE TEMP TABLE v312_field ON COMMIT DROP AS
        SELECT * FROM dynamicbusiness.dynamic_field WHERE id = v_src;
    UPDATE v312_field SET
        id = nextval('dynamicbusiness.dynamic_field_id_seq'),
        code = p_code,
        name = p_name,
        type = p_type,
        description = p_name,
        provider_code = p_provider,
        options = p_options,
        creator = 'V312',
        create_time = CURRENT_TIMESTAMP,
        updater = 'V312',
        update_time = CURRENT_TIMESTAMP,
        deleted = false,
        tenant_id = p_tenant;
    INSERT INTO dynamicbusiness.dynamic_field SELECT * FROM v312_field;
    SELECT id INTO v_id FROM v312_field;
    DROP TABLE v312_field;
    RETURN v_id;
END;
$$;

DO $$
DECLARE
    rec record;
    spec record;
    v_field bigint;
    v_table text;
    v_group text;
    kind_options text := '[{"label":"内部人员","value":"内部人员"},{"label":"外部施工人员","value":"外部施工人员"}]';
BEGIN
    FOR rec IN
        SELECT tenant_id
        FROM dynamicbusiness.dynamic_entity_type
        WHERE deleted = false AND code = 'person_certificate'
    LOOP
        PERFORM dynamicbusiness.v312_field(rec.tenant_id, 'holder_kind', '持证人种类', 'ENUM', NULL, kind_options);
        PERFORM dynamicbusiness.v312_field(rec.tenant_id, 'internal_user_id', '内部人员', 'ENTITY_REF', 'dynamic-entity:user_account', NULL);
        PERFORM dynamicbusiness.v312_field(rec.tenant_id, 'external_worker_id', '外部施工人员', 'ENTITY_REF', 'dynamic-entity:external_worker', NULL);
        PERFORM dynamicbusiness.v312_field(rec.tenant_id, 'welder_holder_kind', '焊工种类', 'ENUM', NULL, kind_options);
        PERFORM dynamicbusiness.v312_field(rec.tenant_id, 'welder_internal_user_id', '内部焊工', 'ENTITY_REF', 'dynamic-entity:user_account', NULL);
        PERFORM dynamicbusiness.v312_field(rec.tenant_id, 'welder_external_worker_id', '外部焊工', 'ENTITY_REF', 'dynamic-entity:external_worker', NULL);
        PERFORM dynamicbusiness.v312_field(rec.tenant_id, 'id_document_no', '证件号', 'TEXT', NULL, NULL);
        PERFORM dynamicbusiness.v312_field(rec.tenant_id, 'contractor_name', '所属施工单位', 'TEXT', NULL, NULL);
        PERFORM dynamicbusiness.v312_field(rec.tenant_id, 'trade', '工种', 'TEXT', NULL, NULL);
        PERFORM dynamicbusiness.v312_field(rec.tenant_id, 'contact_phone', '联系电话', 'TEXT', NULL, NULL);

        FOR spec IN
            SELECT * FROM (VALUES
                ('person_certificate', 'holder_kind', '持证人种类', 'TEXT', false, 30),
                ('person_certificate', 'internal_user_id', '内部人员', 'REF', false, 31),
                ('person_certificate', 'external_worker_id', '外部施工人员', 'REF', false, 32),
                ('repair_field_record', 'welder_holder_kind', '焊工种类', 'TEXT', false, 30),
                ('repair_field_record', 'welder_internal_user_id', '内部焊工', 'REF', false, 31),
                ('repair_field_record', 'welder_external_worker_id', '外部焊工', 'REF', false, 32)
            ) AS t(entity_type_code, field_code, field_name, data_type, required, sort_order)
        LOOP
            SELECT id INTO v_field
            FROM dynamicbusiness.dynamic_field
            WHERE tenant_id = rec.tenant_id AND code = spec.field_code AND deleted = false
            ORDER BY id LIMIT 1;
            IF v_field IS NULL THEN
                CONTINUE;
            END IF;
            IF NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field
                WHERE tenant_id = rec.tenant_id AND entity_type_code = spec.entity_type_code
                  AND field_code = spec.field_code AND deleted = false
            ) THEN
                INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
                    entity_type_code, field_code, field_name, data_type, required, description,
                    sort_order, status, creator, updater, tenant_id, library_field_id,
                    is_searchable, is_filterable, is_sortable
                ) VALUES (
                    spec.entity_type_code, spec.field_code, spec.field_name, spec.data_type, spec.required,
                    spec.field_name, spec.sort_order, 1, 'V312', 'V312', rec.tenant_id, v_field,
                    false, true, false
                );
            END IF;
        END LOOP;

        UPDATE dynamicbusiness.dynamic_entity_type_base_field
        SET required = false, updater = 'V312', update_time = CURRENT_TIMESTAMP
        WHERE tenant_id = rec.tenant_id AND entity_type_code = 'person_certificate'
          AND field_code = 'person_id' AND deleted = false;

        v_table := 'ent_person_certificate_t' || rec.tenant_id::text;
        IF to_regclass('dynamicbusiness.' || v_table) IS NOT NULL THEN
            EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS holder_kind varchar(64)', v_table);
            EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS internal_user_id bigint', v_table);
            EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS external_worker_id bigint', v_table);
            EXECUTE format(
                'UPDATE dynamicbusiness.%I SET holder_kind = %L, internal_user_id = person_id, updater = %L, update_time = CURRENT_TIMESTAMP WHERE deleted = false AND person_id IS NOT NULL AND internal_user_id IS NULL',
                v_table, '内部人员', 'V312');
        END IF;

        v_table := 'ent_repair_field_record_t' || rec.tenant_id::text;
        IF to_regclass('dynamicbusiness.' || v_table) IS NOT NULL THEN
            EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS welder_holder_kind varchar(64)', v_table);
            EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS welder_internal_user_id bigint', v_table);
            EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS welder_external_worker_id bigint', v_table);
        END IF;

        FOR spec IN
            SELECT * FROM (VALUES
                ('cert-welder', '焊工证', '焊接', 1),
                ('cert-hot-work', '动火作业证', '动火', 2),
                ('cert-height', '高处作业证', '高处', 3),
                ('cert-confined', '受限空间作业证', '受限空间', 4),
                ('cert-lifting', '吊装作业证', '吊装', 5)
            ) AS t(code, name, allowed_work, sort)
        LOOP
            IF NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_model
                WHERE tenant_id = rec.tenant_id AND code = spec.code AND deleted = false
            ) THEN
                INSERT INTO dynamicbusiness.dynamic_model (
                    code, name, entity_type_code, description, status, sort,
                    creator, updater, deleted, tenant_id, governance_status
                ) VALUES (
                    spec.code, spec.name, 'person_certificate', spec.name, 1, spec.sort,
                    'V312', 'V312', false, rec.tenant_id, 'COMPANY'
                );
            END IF;

            INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
                model_id, field_id, required, sort, creator, updater, deleted, tenant_id,
                model_code, field_code, create_visible, edit_visible, detail_visible, default_value
            )
            SELECT newer.id, src.field_id, src.required, src.sort, 'V312', 'V312', false, rec.tenant_id,
                   spec.code, src.field_code, src.create_visible, src.edit_visible, src.detail_visible, src.default_value
            FROM dynamicbusiness.dynamic_model newer
            JOIN dynamicbusiness.dynamic_model_field_assignment src
              ON src.tenant_id = rec.tenant_id AND src.model_code = 'MODEL-person-certificate' AND src.deleted = false
            WHERE newer.tenant_id = rec.tenant_id AND newer.code = spec.code AND newer.deleted = false
              AND NOT EXISTS (
                  SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment own
                  WHERE own.tenant_id = rec.tenant_id AND own.model_id = newer.id
                    AND own.field_code = src.field_code AND own.deleted = false
              );

            UPDATE dynamicbusiness.dynamic_model_field_assignment
            SET default_value = spec.allowed_work,
                required = false,
                create_visible = false,
                edit_visible = false,
                detail_visible = true,
                updater = 'V312',
                update_time = CURRENT_TIMESTAMP
            WHERE tenant_id = rec.tenant_id AND model_code = spec.code
              AND field_code = 'certificate_work_range' AND deleted = false;
        END LOOP;

        INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
            model_id, field_id, required, sort, creator, updater, deleted, tenant_id,
            model_code, field_code, create_visible, edit_visible, detail_visible
        )
            SELECT model.id, field.id,
               field.code = 'holder_kind' AND model.code <> 'MODEL-person-certificate',
               CASE field.code
                   WHEN 'holder_kind' THEN 30
                   WHEN 'internal_user_id' THEN 31
                   ELSE 32
               END,
               'V312', 'V312', false, rec.tenant_id, model.code, field.code, true, true, true
        FROM dynamicbusiness.dynamic_model model
        JOIN dynamicbusiness.dynamic_field field
          ON field.tenant_id = rec.tenant_id AND field.deleted = false
         AND field.code IN ('holder_kind', 'internal_user_id', 'external_worker_id')
        WHERE model.tenant_id = rec.tenant_id AND model.entity_type_code = 'person_certificate'
          AND model.deleted = false
          AND NOT EXISTS (
              SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment own
              WHERE own.tenant_id = rec.tenant_id AND own.model_id = model.id
                AND own.field_code = field.code AND own.deleted = false
          );

        UPDATE dynamicbusiness.dynamic_model_field_assignment
        SET required = false, create_visible = false, edit_visible = false, detail_visible = false,
            updater = 'V312', update_time = CURRENT_TIMESTAMP
        WHERE tenant_id = rec.tenant_id AND field_code = 'person_id' AND deleted = false
          AND model_code IN (
              'MODEL-person-certificate', 'cert-welder', 'cert-hot-work', 'cert-height', 'cert-confined', 'cert-lifting'
          );

        INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
            model_id, field_id, required, sort, creator, updater, deleted, tenant_id,
            model_code, field_code, create_visible, edit_visible, detail_visible
        )
        SELECT model.id, field.id, false,
               CASE field.code
                   WHEN 'welder_holder_kind' THEN 30
                   WHEN 'welder_internal_user_id' THEN 31
                   ELSE 32
               END,
               'V312', 'V312', false, rec.tenant_id, model.code, field.code, true, true, true
        FROM dynamicbusiness.dynamic_model model
        JOIN dynamicbusiness.dynamic_field field
          ON field.tenant_id = rec.tenant_id AND field.deleted = false
         AND field.code IN ('welder_holder_kind', 'welder_internal_user_id', 'welder_external_worker_id')
        WHERE model.tenant_id = rec.tenant_id AND model.entity_type_code = 'repair_field_record'
          AND model.deleted = false
          AND NOT EXISTS (
              SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment own
              WHERE own.tenant_id = rec.tenant_id AND own.model_id = model.id
                AND own.field_code = field.code AND own.deleted = false
          );

        SELECT group_name INTO v_group
        FROM dynamicbusiness.dynamic_entity_type
        WHERE tenant_id = rec.tenant_id AND code = 'person_certificate' AND deleted = false
        ORDER BY id LIMIT 1;

        v_table := 'ent_external_worker_t' || rec.tenant_id::text;
        EXECUTE format('CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.%I', v_table || '_id_seq');
        EXECUTE format($c$
            CREATE TABLE IF NOT EXISTS dynamicbusiness.%I (
                id bigint PRIMARY KEY DEFAULT nextval(%L),
                tenant_id bigint NOT NULL DEFAULT %s,
                entity_type_code varchar(64) NOT NULL DEFAULT 'external_worker',
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
                deleted boolean DEFAULT false,
                id_document_no varchar(255),
                contractor_name varchar(255),
                trade varchar(255),
                contact_phone varchar(255)
            )$c$, v_table, 'dynamicbusiness.' || v_table || '_id_seq', rec.tenant_id::text);
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS id_document_no varchar(255)', v_table);
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS contractor_name varchar(255)', v_table);
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS trade varchar(255)', v_table);
        EXECUTE format('ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS contact_phone varchar(255)', v_table);

        IF NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_entity_type
            WHERE tenant_id = rec.tenant_id AND code = 'external_worker' AND deleted = false
        ) THEN
            INSERT INTO dynamicbusiness.dynamic_entity_type (
                code, name, description, alias, sort, status, type_level, association_fields,
                storage_type, dedicated_table_name, enable_rule_engine, creator, updater, deleted,
                tenant_id, group_name, entry_kind, model_workbench_mode
            )
            SELECT 'external_worker', '外部施工人员', '没有系统账号、到场施工的人', '外部施工人员',
                   COALESCE((SELECT MAX(sort) FROM dynamicbusiness.dynamic_entity_type
                             WHERE tenant_id = rec.tenant_id AND deleted = false), 0) + 1,
                   'active', 'USER', '{}'::jsonb, 'DEDICATED', v_table, false,
                   'V312', 'V312', false, rec.tenant_id, v_group, 'NATIVE', 'MULTI';
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_model
            WHERE tenant_id = rec.tenant_id AND code = 'MODEL-external-worker' AND deleted = false
        ) THEN
            INSERT INTO dynamicbusiness.dynamic_model (
                code, name, entity_type_code, description, status, sort,
                creator, updater, deleted, tenant_id, governance_status
            ) VALUES (
                'MODEL-external-worker', '外部施工人员', 'external_worker', '外部施工人员', 1, 0,
                'V312', 'V312', false, rec.tenant_id, 'COMPANY'
            );
        END IF;

        FOR spec IN
            SELECT * FROM (VALUES
                ('id_document_no', '证件号', 'TEXT', true, 1),
                ('contractor_name', '所属施工单位', 'TEXT', false, 2),
                ('trade', '工种', 'TEXT', false, 3),
                ('contact_phone', '联系电话', 'TEXT', false, 4)
            ) AS t(field_code, field_name, data_type, required, sort_order)
        LOOP
            SELECT id INTO v_field
            FROM dynamicbusiness.dynamic_field
            WHERE tenant_id = rec.tenant_id AND code = spec.field_code AND deleted = false
            ORDER BY id LIMIT 1;
            IF v_field IS NULL THEN
                CONTINUE;
            END IF;
            IF NOT EXISTS (
                SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field
                WHERE tenant_id = rec.tenant_id AND entity_type_code = 'external_worker'
                  AND field_code = spec.field_code AND deleted = false
            ) THEN
                INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
                    entity_type_code, field_code, field_name, data_type, required, description,
                    sort_order, status, creator, updater, tenant_id, library_field_id,
                    is_searchable, is_filterable, is_sortable
                ) VALUES (
                    'external_worker', spec.field_code, spec.field_name, spec.data_type, spec.required,
                    spec.field_name, spec.sort_order, 1, 'V312', 'V312', rec.tenant_id, v_field,
                    spec.field_code = 'id_document_no', true, false
                );
            END IF;
            INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
                model_id, field_id, required, sort, creator, updater, deleted, tenant_id,
                model_code, field_code, create_visible, edit_visible, detail_visible
            )
            SELECT model.id, v_field, spec.required, spec.sort_order, 'V312', 'V312', false, rec.tenant_id,
                   'MODEL-external-worker', spec.field_code, true, true, true
            FROM dynamicbusiness.dynamic_model model
            WHERE model.tenant_id = rec.tenant_id AND model.code = 'MODEL-external-worker' AND model.deleted = false
              AND NOT EXISTS (
                  SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment own
                  WHERE own.tenant_id = rec.tenant_id AND own.model_id = model.id
                    AND own.field_code = spec.field_code AND own.deleted = false
              );
        END LOOP;

        IF NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_category_type
            WHERE tenant_id = rec.tenant_id AND category_type_code = 'external_worker' AND deleted = false
        ) THEN
            INSERT INTO dynamicbusiness.dynamic_category_type (
                category_type_code, name, description, status, tenant_id, creator, updater, deleted,
                category_mode, entity_association_mode
            ) VALUES (
                'external_worker', '外部施工人员', '数据类型「外部施工人员」默认分类',
                1, rec.tenant_id, 'V312', 'V312', false, 'SIMPLE', 'MULTI'
            );
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_category
            WHERE tenant_id = rec.tenant_id AND code = 'external_worker_root' AND deleted = false
        ) THEN
            INSERT INTO dynamicbusiness.dynamic_category (
                parent_id, name, code, category_type_code, level, sort, status, description,
                creator, updater, deleted, tenant_id, parent_code
            ) VALUES (
                NULL, '外部施工人员', 'external_worker_root', 'external_worker', 1, 1, 1, '外部施工人员',
                'V312', 'V312', false, rec.tenant_id, NULL
            );
        END IF;

        UPDATE dynamicbusiness.dynamic_category
        SET tree_path = '/' || id::text || '/', updater = 'V312', update_time = CURRENT_TIMESTAMP
        WHERE tenant_id = rec.tenant_id AND code = 'external_worker_root' AND deleted = false
          AND (tree_path IS NULL OR btrim(tree_path) = '');

        UPDATE dynamicbusiness.dynamic_category_type t
        SET top_level_category_id = c.id, updater = 'V312', update_time = CURRENT_TIMESTAMP
        FROM dynamicbusiness.dynamic_category c
        WHERE t.tenant_id = rec.tenant_id AND c.tenant_id = rec.tenant_id
          AND t.category_type_code = 'external_worker' AND c.code = 'external_worker_root'
          AND t.deleted = false AND c.deleted = false
          AND t.top_level_category_id IS NULL;
    END LOOP;
END $$;

INSERT INTO dynamicbusiness.dynamic_model_category_relation (
    model_id, category_id, entity_type_code, sort, creator, updater, deleted, tenant_id,
    model_code, category_code
)
SELECT model.id, category.id, model.entity_type_code, category.sort,
       'V312', 'V312', false, model.tenant_id, model.code, category.code
FROM dynamicbusiness.dynamic_model model
JOIN dynamicbusiness.dynamic_category category
  ON category.tenant_id = model.tenant_id
 AND category.category_type_code = model.entity_type_code
 AND category.deleted = false
WHERE model.deleted = false
  AND (
      model.code IN ('cert-welder', 'cert-hot-work', 'cert-height', 'cert-confined', 'cert-lifting', 'MODEL-external-worker')
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
        WHERE deleted = false AND code = 'person_certificate'
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
                   'V312', 'V312', false, model.tenant_id, model.code, category.code
            FROM dynamicbusiness.dynamic_model model
            JOIN dynamicbusiness.dynamic_category category
              ON category.tenant_id = model.tenant_id
             AND category.category_type_code = model.entity_type_code
             AND category.deleted = false
            WHERE model.deleted = false
              AND model.tenant_id = %s
              AND model.code IN (
                  'cert-welder', 'cert-hot-work', 'cert-height', 'cert-confined', 'cert-lifting', 'MODEL-external-worker'
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

DROP FUNCTION dynamicbusiness.v312_field(bigint, text, text, text, text, text);

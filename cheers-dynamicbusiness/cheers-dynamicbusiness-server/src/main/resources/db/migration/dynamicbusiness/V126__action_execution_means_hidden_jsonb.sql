-- V126: 动作「适用手段」回到隐藏基础字段 + jsonb 专用列
--
-- 现象：详情勾选适用手段后，补丁把值塞进 custom_fields；专用列仍是 varchar 旧单值。
-- 定稿：与 param_slots_json 同一条写路径——类型基础字段（详情隐藏）+ 专用列 jsonb。
-- 不负责：把执行手段再投影进新建弹窗。

SET search_path TO dynamicbusiness, public;

-- 1) varchar → jsonb：优先用 custom_fields.execution_means 数组，其次收旧单值
DO $$
DECLARE
  tbl text;
  col_udt text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_action(_t[0-9]+)?$'
  LOOP
    SELECT c.udt_name
      INTO col_udt
    FROM information_schema.columns c
    WHERE c.table_schema = 'dynamicbusiness'
      AND c.table_name = tbl
      AND c.column_name = 'execution_means';

    IF col_udt IS NULL THEN
      EXECUTE format(
        'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS execution_means JSONB',
        tbl
      );
    ELSIF col_udt <> 'jsonb' THEN
      EXECUTE format(
        $sql$
        ALTER TABLE dynamicbusiness.%I
          ALTER COLUMN execution_means TYPE jsonb
          USING (
            CASE
              WHEN custom_fields ? 'execution_means'
                THEN COALESCE(custom_fields -> 'execution_means', '[]'::jsonb)
              WHEN execution_means IS NULL OR btrim(execution_means::text) = ''
                THEN '[]'::jsonb
              WHEN left(btrim(execution_means::text), 1) = '['
                THEN btrim(execution_means::text)::jsonb
              ELSE to_jsonb(ARRAY[btrim(execution_means::text)])
            END
          )
        $sql$,
        tbl
      );
    END IF;

    EXECUTE format(
      'COMMENT ON COLUMN dynamicbusiness.%I.execution_means IS %L',
      tbl,
      '动作适用手段编码数组（详情勾选；新建表单隐藏）'
    );

    EXECUTE format(
      $sql$
      UPDATE dynamicbusiness.%I
      SET custom_fields = custom_fields - 'execution_means',
          updater = COALESCE(updater, 'flyway-v126'),
          update_time = CURRENT_TIMESTAMP
      WHERE deleted = FALSE
        AND custom_fields ? 'execution_means'
      $sql$,
      tbl
    );
  END LOOP;
END $$;

-- 2) 字段库 / 基础字段 / BASE 分配恢复为有效隐藏字段
UPDATE dynamic_field
SET
  deleted = false,
  status = 1,
  type = 'JSON',
  name = '适用手段',
  description = '动作适用的执行手段编码数组。详情勾选；新建/编辑表单不展示。',
  semantic_type = COALESCE(NULLIF(btrim(semantic_type), ''), 'execution_means'),
  updater = 'flyway-v126',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND code = 'execution_means';

UPDATE dynamic_entity_type_base_field
SET
  deleted = false,
  status = 1,
  data_type = 'JSON',
  field_name = '适用手段',
  required = false,
  default_value = '[]',
  type_config = '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb,
  updater = 'flyway-v126',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND entity_type_code = 'action'
  AND field_code = 'execution_means';

INSERT INTO dynamic_entity_type_base_field (
  entity_type_code, library_field_id, field_code, field_name, data_type, required, default_value,
  description, type_config, sort_order, status, tenant_id, creator
)
SELECT
  'action',
  f.id,
  'execution_means',
  '适用手段',
  'JSON',
  false,
  '[]',
  '动作适用的执行手段编码数组。详情勾选；新建/编辑表单不展示。',
  '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb,
  10,
  1,
  1,
  'flyway-v126'
FROM dynamic_field f
WHERE f.code = 'execution_means'
  AND f.deleted = false
  AND f.tenant_id = 1
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_entity_type_base_field bf
    WHERE bf.entity_type_code = 'action'
      AND bf.field_code = 'execution_means'
      AND bf.tenant_id = 1
  );

UPDATE dynamic_model_field_assignment
SET
  deleted = false,
  field_source = 'BASE',
  updater = 'flyway-v126',
  update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND model_code = 'action'
  AND field_code = 'execution_means';

INSERT INTO dynamic_model_field_assignment (
  model_id, field_id, required, is_searchable, is_filterable, is_sortable,
  sort, field_source, creator, tenant_id, model_code, field_code
)
SELECT
  m.id,
  f.id,
  false,
  false,
  false,
  false,
  10,
  'BASE',
  'flyway-v126',
  1,
  'action',
  'execution_means'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.code = 'execution_means' AND f.deleted = false AND f.tenant_id = 1
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND m.entity_type_code = 'action'
  AND m.code = 'action'
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment a
    WHERE a.tenant_id = 1
      AND a.model_code = 'action'
      AND a.field_code = 'execution_means'
  );

-- 3) 清 CRUD 缓存，下次请求按型号重算分桶（须含隐藏的 execution_means）
DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'action';

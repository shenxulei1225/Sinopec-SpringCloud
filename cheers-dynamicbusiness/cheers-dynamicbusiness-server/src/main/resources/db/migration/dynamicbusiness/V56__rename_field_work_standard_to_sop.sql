-- V56: SOP 真源编码统一为 sop（标准作业流程SOP）
-- 废弃并列入口 field_work_standard：表改名、元数据改码、软删空壳 sop 类型后再占用 code=sop

SET search_path TO dynamicbusiness, public;

-- 1) 软删空壳类型 sop（用户误建，无业务实体）；释放 code=sop
UPDATE dynamic_entity_type
SET deleted = true, status = 'inactive', updater = 'flyway', update_time = CURRENT_TIMESTAMP
WHERE code = 'sop' AND deleted = false;

UPDATE dynamic_entity_type_config
SET deleted = true, updater = 'flyway', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'sop' AND deleted = false;

UPDATE dynamic_model
SET deleted = true, updater = 'flyway', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'sop' AND deleted = false;

UPDATE dynamic_entity_type_base_field
SET deleted = true, updater = 'flyway', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'sop' AND deleted = false;

UPDATE dm_data_tab_layout
SET deleted = true, updater = 'flyway', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'sop' AND deleted = false;

UPDATE dm_five_w_orchestration
SET deleted = true, updater = 'flyway', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'sop' AND deleted = false;

-- 2) 丢掉空壳物理表 ent_sop_t*（无数据），以便把真表改名为 ent_sop*
DO $$
DECLARE
  r record;
BEGIN
  FOR r IN
    SELECT c.relname AS seqname
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'S'
      AND c.relname LIKE 'ent_sop%_id_seq'
  LOOP
    EXECUTE format('DROP SEQUENCE IF EXISTS dynamicbusiness.%I CASCADE', r.seqname);
  END LOOP;

  FOR r IN
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness'
      AND table_name LIKE 'ent_sop%'
  LOOP
    EXECUTE format('DROP TABLE IF EXISTS dynamicbusiness.%I CASCADE', r.table_name);
  END LOOP;
END $$;

-- 3) 真表/序列改名：ent_field_work_standard* → ent_sop*
DO $$
DECLARE
  r record;
  new_name text;
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = 'ent_field_work_standard'
  ) THEN
    ALTER TABLE dynamicbusiness.ent_field_work_standard RENAME TO ent_sop;
  END IF;

  IF EXISTS (
    SELECT 1 FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness' AND c.relkind = 'S' AND c.relname = 'ent_field_work_standard_id_seq'
  ) THEN
    ALTER SEQUENCE dynamicbusiness.ent_field_work_standard_id_seq RENAME TO ent_sop_id_seq;
  END IF;

  FOR r IN
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness'
      AND table_name ~ '^ent_field_work_standard_t[0-9]+$'
  LOOP
    new_name := replace(r.table_name, 'ent_field_work_standard_', 'ent_sop_');
    EXECUTE format('ALTER TABLE dynamicbusiness.%I RENAME TO %I', r.table_name, new_name);
  END LOOP;

  FOR r IN
    SELECT c.relname AS seqname
    FROM pg_class c
    JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE n.nspname = 'dynamicbusiness'
      AND c.relkind = 'S'
      AND c.relname ~ '^ent_field_work_standard_t[0-9]+_id_seq$'
  LOOP
    new_name := replace(r.seqname, 'ent_field_work_standard_', 'ent_sop_');
    EXECUTE format('ALTER SEQUENCE dynamicbusiness.%I RENAME TO %I', r.seqname, new_name);
  END LOOP;
END $$;

-- 默认值与注释对齐新表名
ALTER TABLE IF EXISTS dynamicbusiness.ent_sop
  ALTER COLUMN entity_type_code SET DEFAULT 'sop';
COMMENT ON TABLE dynamicbusiness.ent_sop IS '标准作业流程SOP；entityTypeCode=sop';

DO $$
DECLARE
  r record;
BEGIN
  FOR r IN
    SELECT table_name
    FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness'
      AND table_name ~ '^ent_sop(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'ALTER TABLE dynamicbusiness.%I ALTER COLUMN entity_type_code SET DEFAULT %L',
      r.table_name, 'sop');
    EXECUTE format(
      'UPDATE dynamicbusiness.%I SET entity_type_code = %L WHERE entity_type_code = %L OR entity_type_code IS NULL',
      r.table_name, 'sop', 'field_work_standard');
  END LOOP;
END $$;

-- 4) 类型元数据：field_work_standard → sop
UPDATE dynamic_entity_type
SET
  code = 'sop',
  name = '标准作业流程SOP',
  alias = 'SOP',
  description = '全网标准作业流程（SOP）；有序步骤与参数槽；可版本发布',
  group_name = '知识库',
  work_scope = 'NETWORK',
  model_workbench_mode = 'SINGLE',
  dedicated_table_name = CASE
    WHEN dedicated_table_name LIKE 'ent_field_work_standard%' THEN replace(dedicated_table_name, 'ent_field_work_standard', 'ent_sop')
    WHEN dedicated_table_name LIKE 'ent_sop%' THEN dedicated_table_name
    ELSE 'ent_sop_t' || COALESCE(tenant_id, 1)::text
  END,
  updater = 'flyway',
  update_time = CURRENT_TIMESTAMP
WHERE code = 'field_work_standard' AND deleted = false;

UPDATE dynamic_entity_type_config
SET
  entity_type_code = 'sop',
  name = '标准作业流程SOP',
  description = 'SOP 专用表；固定列 version_no / publish_status / steps_json',
  dedicated_table_name = CASE
    WHEN dedicated_table_name LIKE 'ent_field_work_standard%' THEN replace(dedicated_table_name, 'ent_field_work_standard', 'ent_sop')
    WHEN dedicated_table_name LIKE 'ent_sop%' THEN dedicated_table_name
    ELSE 'ent_sop_t' || COALESCE(tenant_id, 1)::text
  END,
  updater = 'flyway',
  update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'field_work_standard' AND deleted = false;

UPDATE dynamic_entity_type_base_field
SET entity_type_code = 'sop', updater = 'flyway', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'field_work_standard' AND deleted = false;

UPDATE dynamic_model
SET
  code = CASE WHEN code = 'field_work_standard' THEN 'sop' ELSE code END,
  entity_type_code = 'sop',
  name = CASE WHEN code = 'field_work_standard' OR name = '现场作业标准' THEN '标准作业流程SOP' ELSE name END,
  description = COALESCE(NULLIF(btrim(description), ''), 'SOP 规范型号（SINGLE）；具体流程建为实体实例'),
  updater = 'flyway',
  update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'field_work_standard' AND deleted = false;

UPDATE dynamic_model_field_assignment
SET
  model_code = CASE WHEN model_code = 'field_work_standard' THEN 'sop' ELSE model_code END,
  updater = 'flyway',
  update_time = CURRENT_TIMESTAMP
WHERE model_code = 'field_work_standard' AND deleted = false;

UPDATE dm_data_tab_layout
SET entity_type_code = 'sop', updater = 'flyway', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'field_work_standard' AND deleted = false;

UPDATE dm_five_w_orchestration
SET entity_type_code = 'sop', updater = 'flyway', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'field_work_standard' AND deleted = false;

-- Who 槽位若按 orchestration 挂接，orchestration 已改码即可；若有直存 type code 再补
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.columns
    WHERE table_schema = 'dynamicbusiness'
      AND table_name = 'dm_five_w_who_layout'
      AND column_name = 'entity_type_code'
  ) THEN
    EXECUTE $q$
      UPDATE dm_five_w_who_layout
      SET entity_type_code = 'sop', updater = 'flyway', update_time = CURRENT_TIMESTAMP
      WHERE entity_type_code = 'field_work_standard' AND deleted = false
    $q$;
  END IF;
END $$;

-- V123: 规范标准附件列从 source_ref 改名为 attachments
-- source_ref 是「引用出处」，不是附件。本页管的是这条规范自己的文件（介绍、预览、下载）。
-- 字段编码 = 物理列名。不保留 source_ref 双键。

SET search_path TO dynamicbusiness, public;

-- 1) 台账列改名
DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^ent_standard(_t[0-9]+)?$'
  LOOP
    IF EXISTS (
      SELECT 1
      FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness'
        AND c.table_name = tbl
        AND c.column_name = 'source_ref'
    ) AND NOT EXISTS (
      SELECT 1
      FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness'
        AND c.table_name = tbl
        AND c.column_name = 'attachments'
    ) THEN
      EXECUTE format('ALTER TABLE dynamicbusiness.%I RENAME COLUMN source_ref TO attachments', tbl);
    END IF;
    IF EXISTS (
      SELECT 1
      FROM information_schema.columns c
      WHERE c.table_schema = 'dynamicbusiness'
        AND c.table_name = tbl
        AND c.column_name = 'attachments'
    ) THEN
      EXECUTE format(
        'COMMENT ON COLUMN dynamicbusiness.%I.attachments IS %L',
        tbl,
        '规范附件（手册、报文、扫描件等），供介绍、预览、下载'
      );
    END IF;
  END LOOP;
END $$;

-- 2) 字段库：编码与语义改为 attachments
UPDATE dynamic_field
SET code = 'attachments',
    semantic_type = 'attachments',
    name = '附件',
    description = '规范相关附件（手册、报文、扫描件等），供介绍、预览、下载',
    updater = 'flyway-v123',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND code = 'source_ref'
  AND (semantic_type = 'source_ref' OR id = 5612);

-- 3) 类型基础字段
UPDATE dynamic_entity_type_base_field
SET field_code = 'attachments',
    field_name = '附件',
    description = '规范附件，供介绍、预览、下载',
    updater = 'flyway-v123',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND entity_type_code = 'standard'
  AND field_code = 'source_ref';

-- 4) 型号分配
UPDATE dynamic_model_field_assignment
SET field_code = 'attachments',
    updater = 'flyway-v123',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND field_code = 'source_ref';

-- 5) 检索索引字段编码
DO $$
DECLARE
  tbl text;
BEGIN
  FOR tbl IN
    SELECT t.table_name
    FROM information_schema.tables t
    WHERE t.table_schema = 'dynamicbusiness'
      AND t.table_type = 'BASE TABLE'
      AND t.table_name ~ '^dynamic_entity_field_index(_t[0-9]+)?$'
  LOOP
    EXECUTE format(
      'UPDATE dynamicbusiness.%I SET field_code = %L, updater = %L, update_time = CURRENT_TIMESTAMP WHERE field_code = %L',
      tbl,
      'attachments',
      'flyway-v123',
      'source_ref'
    );
  END LOOP;
END $$;

-- 6) 物理列映射
UPDATE dynamic_entity_type
SET physical_column_mapping = (
      physical_column_mapping - 'source_ref'
    ) || jsonb_build_object(
      'attachments',
      jsonb_build_object('type', 'TEXT', 'column', 'attachments')
    ),
    updater = 'flyway-v123',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND code = 'standard'
  AND physical_column_mapping ? 'source_ref';

UPDATE dynamic_entity_type_config
SET physical_column_mapping = (
      physical_column_mapping - 'source_ref'
    ) || jsonb_build_object(
      'attachments',
      jsonb_build_object('type', 'TEXT', 'column', 'attachments')
    ),
    updater = 'flyway-v123',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND entity_type_code = 'standard'
  AND physical_column_mapping ? 'source_ref';

-- 7) 清规范标准 CRUD 缓存，下次按 attachments 重算
DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'standard';

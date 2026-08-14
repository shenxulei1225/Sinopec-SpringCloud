-- V44: 检查项—SOP、实体—SOP 关联（替代 inspection_item.method_template_id / inspection_method）
-- 检查项详情「检查方法」分组 = 本表多行 SOP；实体在本台勾选适用 SOP

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamicbusiness.dynamic_inspection_item_sop (
  id                  BIGSERIAL PRIMARY KEY,
  tenant_id           BIGINT       NOT NULL,
  inspection_item_id  BIGINT       NOT NULL,
  sop_id              BIGINT       NOT NULL,
  execution_means     VARCHAR(64),
  sort                INTEGER      DEFAULT 0,
  creator             VARCHAR(64),
  create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  updater             VARCHAR(64),
  update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  deleted             BOOLEAN      DEFAULT FALSE
);

COMMENT ON TABLE dynamicbusiness.dynamic_inspection_item_sop IS
  '检查项—现场作业标准（SOP）多对多；execution_means=执行手段标签（MANUAL/UAV/ROBOT/FIXED_VIDEO 等）';
COMMENT ON COLUMN dynamicbusiness.dynamic_inspection_item_sop.inspection_item_id IS '检查项实体 id（inspection_item）';
COMMENT ON COLUMN dynamicbusiness.dynamic_inspection_item_sop.sop_id IS 'SOP 实体 id（field_work_standard）';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_inspection_item_sop_identity
  ON dynamicbusiness.dynamic_inspection_item_sop (tenant_id, inspection_item_id, sop_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_inspection_item_sop_item
  ON dynamicbusiness.dynamic_inspection_item_sop (inspection_item_id, tenant_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_inspection_item_sop_sop
  ON dynamicbusiness.dynamic_inspection_item_sop (sop_id, tenant_id)
  WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamicbusiness.dynamic_entity_inspection_sop (
  id                  BIGSERIAL PRIMARY KEY,
  tenant_id           BIGINT       NOT NULL,
  entity_type_code    VARCHAR(64)  NOT NULL,
  entity_id           BIGINT       NOT NULL,
  inspection_item_id  BIGINT       NOT NULL,
  sop_id              BIGINT       NOT NULL,
  creator             VARCHAR(64),
  create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  updater             VARCHAR(64),
  update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  deleted             BOOLEAN      DEFAULT FALSE
);

COMMENT ON TABLE dynamicbusiness.dynamic_entity_inspection_sop IS
  '实体在本台选用的 SOP（须在检查项—SOP 关联中存在）；entity_type_code 为存储类型编码';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_entity_inspection_sop_identity
  ON dynamicbusiness.dynamic_entity_inspection_sop (
    tenant_id, entity_type_code, entity_id, inspection_item_id, sop_id
  )
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_entity_inspection_sop_entity
  ON dynamicbusiness.dynamic_entity_inspection_sop (entity_id, entity_type_code, tenant_id)
  WHERE deleted = FALSE;

CREATE OR REPLACE FUNCTION dynamicbusiness._v44_table_exists(p_name text)
RETURNS boolean
LANGUAGE sql
STABLE
AS $$
  SELECT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_name)
  );
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v44_ensure_id_seq(p_table text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  seq_name text := format('%s_id_seq', p_table);
  max_id bigint;
  has_rows boolean;
BEGIN
  IF NOT dynamicbusiness._v44_table_exists(p_table) THEN
    RETURN;
  END IF;
  EXECUTE format('CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.%I', seq_name);
  EXECUTE format(
    'ALTER TABLE dynamicbusiness.%I ALTER COLUMN id SET DEFAULT nextval(%L::regclass)',
    p_table, 'dynamicbusiness.' || seq_name);
  BEGIN
    EXECUTE format(
      'ALTER SEQUENCE dynamicbusiness.%I OWNED BY dynamicbusiness.%I.id',
      seq_name, p_table);
  EXCEPTION WHEN OTHERS THEN
    NULL;
  END;
  EXECUTE format(
    'SELECT COALESCE(MAX(id), 1), EXISTS (SELECT 1 FROM dynamicbusiness.%I LIMIT 1) FROM dynamicbusiness.%I',
    p_table, p_table)
    INTO max_id, has_rows;
  PERFORM setval(('dynamicbusiness.' || seq_name)::regclass, max_id, has_rows);
END;
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v44_split_one(p_base text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  tid bigint;
  physical text;
  tenants bigint[];
BEGIN
  IF NOT dynamicbusiness._v44_table_exists(p_base) THEN
    RETURN;
  END IF;
  IF p_base ~ '_t[0-9]+$' THEN
    RETURN;
  END IF;

  SELECT ARRAY(
    SELECT DISTINCT tenant_id
    FROM dynamicbusiness.dynamic_entity_type
    WHERE deleted = false AND tenant_id IS NOT NULL AND tenant_id > 0
    ORDER BY tenant_id
  ) INTO tenants;

  IF tenants IS NULL OR array_length(tenants, 1) IS NULL THEN
    tenants := ARRAY[1]::bigint[];
  END IF;

  FOREACH tid IN ARRAY tenants
  LOOP
    physical := p_base || '_t' || tid::text;
    IF NOT dynamicbusiness._v44_table_exists(physical) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.%I INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical, p_base);
      PERFORM dynamicbusiness._v44_ensure_id_seq(physical);
    END IF;
  END LOOP;

  EXECUTE format('TRUNCATE TABLE dynamicbusiness.%I', p_base);
  PERFORM dynamicbusiness._v44_ensure_id_seq(p_base);
END;
$$;

SELECT dynamicbusiness._v44_split_one('dynamic_inspection_item_sop');
SELECT dynamicbusiness._v44_split_one('dynamic_entity_inspection_sop');

DROP FUNCTION IF EXISTS dynamicbusiness._v44_split_one(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v44_ensure_id_seq(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v44_table_exists(text);

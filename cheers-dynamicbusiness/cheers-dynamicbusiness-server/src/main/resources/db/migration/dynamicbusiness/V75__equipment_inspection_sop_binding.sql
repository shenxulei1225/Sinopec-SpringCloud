-- V75: 被检设备 + 检查项 + 执行手段 → SOP 实例 独占绑定表
-- 设计：docs/superpowers/specs/2026-08-27-inspection-sop-template-instance-design.md
-- 与 V44 dynamic_entity_inspection_sop 并存：本表为设备检查绑定权威（含 execution_means 唯一）

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamic_equipment_inspection_sop_binding (
  id                  BIGSERIAL PRIMARY KEY,
  tenant_id           BIGINT       NOT NULL,
  equipment_id        BIGINT       NOT NULL,
  inspection_item_id  BIGINT       NOT NULL,
  execution_means     VARCHAR(64)  NOT NULL,
  sop_instance_id     BIGINT       NOT NULL,
  creator             VARCHAR(64),
  create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  updater             VARCHAR(64),
  update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  deleted             BOOLEAN      DEFAULT FALSE
);

COMMENT ON TABLE dynamic_equipment_inspection_sop_binding IS
  '设备检查绑定：被检设备 + 检查项 + 执行手段 → 独占 SOP 实例 id（is_template=false）';
COMMENT ON COLUMN dynamic_equipment_inspection_sop_binding.equipment_id IS '被检设备 ent_equipment.id';
COMMENT ON COLUMN dynamic_equipment_inspection_sop_binding.inspection_item_id IS '检查项 ent_inspection_item.id';
COMMENT ON COLUMN dynamic_equipment_inspection_sop_binding.execution_means IS '执行手段（MANUAL/UAV/ROBOT/FIXED_CAMERA 等）';
COMMENT ON COLUMN dynamic_equipment_inspection_sop_binding.sop_instance_id IS 'SOP 实例 id（REF → sop，is_template=false）';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_equipment_inspection_sop_binding_identity
  ON dynamic_equipment_inspection_sop_binding (
    tenant_id, equipment_id, inspection_item_id, execution_means
  )
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_equipment_inspection_sop_binding_equipment
  ON dynamic_equipment_inspection_sop_binding (equipment_id, tenant_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_equipment_inspection_sop_binding_item
  ON dynamic_equipment_inspection_sop_binding (inspection_item_id, tenant_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_equipment_inspection_sop_binding_instance
  ON dynamic_equipment_inspection_sop_binding (sop_instance_id, tenant_id)
  WHERE deleted = FALSE;

CREATE OR REPLACE FUNCTION dynamicbusiness._v75_table_exists(p_name text)
RETURNS boolean
LANGUAGE sql
STABLE
AS $$
  SELECT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_name)
  );
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v75_ensure_id_seq(p_table text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  seq_name text := format('%s_id_seq', p_table);
  max_id bigint;
  has_rows boolean;
BEGIN
  IF NOT dynamicbusiness._v75_table_exists(p_table) THEN
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

CREATE OR REPLACE FUNCTION dynamicbusiness._v75_split_one(p_base text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  tid bigint;
  physical text;
  tenants bigint[];
BEGIN
  IF NOT dynamicbusiness._v75_table_exists(p_base) THEN
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
    IF NOT dynamicbusiness._v75_table_exists(physical) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.%I INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical, p_base);
      PERFORM dynamicbusiness._v75_ensure_id_seq(physical);
    END IF;
  END LOOP;

  EXECUTE format('TRUNCATE TABLE dynamicbusiness.%I', p_base);
  PERFORM dynamicbusiness._v75_ensure_id_seq(p_base);
END;
$$;

SELECT dynamicbusiness._v75_split_one('dynamic_equipment_inspection_sop_binding');

DROP FUNCTION IF EXISTS dynamicbusiness._v75_split_one(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v75_ensure_id_seq(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v75_table_exists(text);

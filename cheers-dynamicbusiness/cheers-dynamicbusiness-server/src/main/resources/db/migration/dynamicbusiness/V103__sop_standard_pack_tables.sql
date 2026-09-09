-- V103: SOP 标准包一期表结构（适用范围 + 标准检查项包）
-- 目标：SOP 成为“该查什么”的单一权威来源；动作细节仍归检查项动作树。

SET search_path TO dynamicbusiness, public;

-- ---------------------------------------------------------------------------
-- 1) SOP 适用范围：分类/型号
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dynamic_sop_scope_rule (
  id                  BIGSERIAL PRIMARY KEY,
  tenant_id           BIGINT       NOT NULL,
  sop_template_id     BIGINT       NOT NULL,
  scope_type          VARCHAR(32)  NOT NULL,
  target_id           BIGINT       NOT NULL,
  sort_no             INTEGER      NOT NULL DEFAULT 0,
  note                VARCHAR(500),
  creator             VARCHAR(64),
  create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  updater             VARCHAR(64),
  update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  deleted             BOOLEAN      DEFAULT FALSE
);

COMMENT ON TABLE dynamic_sop_scope_rule IS
  'SOP 适用范围：按模板维护分类/型号范围。';
COMMENT ON COLUMN dynamic_sop_scope_rule.sop_template_id IS 'SOP 模板实体 id（is_template=true）';
COMMENT ON COLUMN dynamic_sop_scope_rule.scope_type IS '范围类型：CATEGORY / MODEL';
COMMENT ON COLUMN dynamic_sop_scope_rule.target_id IS 'scope_type 对应目标 id（分类 id 或型号 id）';
COMMENT ON COLUMN dynamic_sop_scope_rule.sort_no IS '展示顺序（同模板内）';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_sop_scope_rule_identity
  ON dynamic_sop_scope_rule (tenant_id, sop_template_id, scope_type, target_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_sop_scope_rule_template
  ON dynamic_sop_scope_rule (tenant_id, sop_template_id, sort_no, id)
  WHERE deleted = FALSE;

-- ---------------------------------------------------------------------------
-- 2) SOP 标准检查项包
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS dynamic_sop_item_pack (
  id                  BIGSERIAL PRIMARY KEY,
  tenant_id           BIGINT       NOT NULL,
  sop_template_id     BIGINT       NOT NULL,
  inspection_item_id  BIGINT       NOT NULL,
  required            BOOLEAN      NOT NULL DEFAULT TRUE,
  sort_no             INTEGER      NOT NULL DEFAULT 0,
  note                VARCHAR(500),
  creator             VARCHAR(64),
  create_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  updater             VARCHAR(64),
  update_time         TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  deleted             BOOLEAN      DEFAULT FALSE
);

COMMENT ON TABLE dynamic_sop_item_pack IS
  'SOP 标准检查项包：模板下应检查哪些标准检查项。';
COMMENT ON COLUMN dynamic_sop_item_pack.sop_template_id IS 'SOP 模板实体 id（is_template=true）';
COMMENT ON COLUMN dynamic_sop_item_pack.inspection_item_id IS '检查项实体 id（inspection_item）';
COMMENT ON COLUMN dynamic_sop_item_pack.required IS '是否必做';
COMMENT ON COLUMN dynamic_sop_item_pack.sort_no IS '执行推荐顺序';

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_sop_item_pack_identity
  ON dynamic_sop_item_pack (tenant_id, sop_template_id, inspection_item_id)
  WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_sop_item_pack_template
  ON dynamic_sop_item_pack (tenant_id, sop_template_id, sort_no, id)
  WHERE deleted = FALSE;

-- ---------------------------------------------------------------------------
-- 3) 租户物理表拆分（与 V80 保持一致）
-- ---------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION dynamicbusiness._v103_table_exists(p_name text)
RETURNS boolean
LANGUAGE sql
STABLE
AS $$
  SELECT EXISTS (
    SELECT 1 FROM information_schema.tables
    WHERE table_schema = 'dynamicbusiness' AND table_name = lower(p_name)
  );
$$;

CREATE OR REPLACE FUNCTION dynamicbusiness._v103_ensure_id_seq(p_table text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  seq_name text := format('%s_id_seq', p_table);
  max_id bigint;
  has_rows boolean;
BEGIN
  IF NOT dynamicbusiness._v103_table_exists(p_table) THEN
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

CREATE OR REPLACE FUNCTION dynamicbusiness._v103_split_one(p_base text)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
  tid bigint;
  physical text;
  tenants bigint[];
BEGIN
  IF NOT dynamicbusiness._v103_table_exists(p_base) THEN
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
    IF NOT dynamicbusiness._v103_table_exists(physical) THEN
      EXECUTE format(
        'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.%I INCLUDING DEFAULTS INCLUDING CONSTRAINTS INCLUDING INDEXES)',
        physical, p_base);
      PERFORM dynamicbusiness._v103_ensure_id_seq(physical);
    END IF;
  END LOOP;

  EXECUTE format('TRUNCATE TABLE dynamicbusiness.%I', p_base);
  PERFORM dynamicbusiness._v103_ensure_id_seq(p_base);
END;
$$;

SELECT dynamicbusiness._v103_split_one('dynamic_sop_scope_rule');
SELECT dynamicbusiness._v103_split_one('dynamic_sop_item_pack');

DROP FUNCTION IF EXISTS dynamicbusiness._v103_split_one(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v103_ensure_id_seq(text);
DROP FUNCTION IF EXISTS dynamicbusiness._v103_table_exists(text);

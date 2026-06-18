-- ============================================================================
-- Dynamic Business Flyway baseline (schema)
-- Generated: 2026-06-18 by scripts/generate-flyway-baseline.py
-- Source: live sinopec.dynamicbusiness (post zhgl migration)
--
-- Fresh install: V1 (schema) -> V2 (seed). No zhgl / dblink required.
-- Regenerate after metadata changes on Mac, then commit V1/V2.
-- ============================================================================

CREATE SCHEMA IF NOT EXISTS dynamicbusiness;
SET search_path TO dynamicbusiness;

--
-- PostgreSQL database dump
--


-- Dumped from database version 16.14 (Postgres.app)
-- Dumped by pg_dump version 16.14 (Postgres.app)


--
-- Name: dynamicbusiness; Type: SCHEMA; Schema: -; Owner: -
--



--
-- Name: java_string_hash(text); Type: FUNCTION; Schema: dynamicbusiness; Owner: -
--

CREATE FUNCTION dynamicbusiness.java_string_hash(input text) RETURNS bigint
    LANGUAGE plpgsql IMMUTABLE
    AS $$
DECLARE
  h bigint := 0;
  i int;
  c int;
  h32 bigint;
BEGIN
  IF input IS NULL OR input = '' THEN
    RETURN NULL;
  END IF;
  FOR i IN 1..length(input) LOOP
    c := ascii(substr(input, i, 1));
    h := 31 * h + c;
    -- fold to signed 32-bit like Java int
    h32 := h & 4294967295;
    IF h32 >= 2147483648 THEN
      h := h32 - 4294967296;
    ELSE
      h := h32;
    END IF;
  END LOOP;
  RETURN h;
END;
$$;


--
-- Name: merge_base_field_library_config(text, character varying, boolean); Type: FUNCTION; Schema: dynamicbusiness; Owner: -
--

CREATE FUNCTION dynamicbusiness.merge_base_field_library_config(p_config text, p_lib_code character varying, p_is_ref boolean) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
  cfg jsonb;
BEGIN
  IF p_lib_code IS NULL OR btrim(p_lib_code) = '' THEN
    RETURN p_config;
  END IF;

  IF p_config IS NULL OR btrim(p_config) = '' THEN
    cfg := '{}'::jsonb;
  ELSE
    BEGIN
      cfg := p_config::jsonb;
    EXCEPTION WHEN OTHERS THEN
      cfg := '{}'::jsonb;
    END;
  END IF;

  cfg := cfg || jsonb_build_object('libraryFieldCode', p_lib_code);

  IF p_is_ref THEN
    cfg := cfg || jsonb_build_object('refField', p_lib_code);
  END IF;

  RETURN cfg::text;
END;
$$;


--
-- Name: remap_field_ids_in_config(text); Type: FUNCTION; Schema: dynamicbusiness; Owner: -
--

CREATE FUNCTION dynamicbusiness.remap_field_ids_in_config(p_config text) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
  cfg jsonb;
  new_groups jsonb := '[]'::jsonb;
  g jsonb;
  f jsonb;
  new_fields jsonb;
  old_id bigint;
  new_id bigint;
BEGIN
  IF p_config IS NULL OR btrim(p_config) = '' THEN
    RETURN p_config;
  END IF;
  BEGIN
    cfg := p_config::jsonb;
  EXCEPTION WHEN OTHERS THEN
    RETURN p_config;
  END;
  IF cfg->'groups' IS NULL THEN
    RETURN p_config;
  END IF;

  FOR g IN SELECT * FROM jsonb_array_elements(cfg->'groups') LOOP
    new_fields := '[]'::jsonb;
    FOR f IN SELECT * FROM jsonb_array_elements(COALESCE(g->'fields', '[]'::jsonb)) LOOP
      IF NOT (f ? 'fieldId') THEN
        new_fields := new_fields || jsonb_build_array(f);
        CONTINUE;
      END IF;
      BEGIN
        old_id := (f->>'fieldId')::bigint;
      EXCEPTION WHEN OTHERS THEN
        CONTINUE;
      END;
      new_id := old_id;
      SELECT m.local_field_id INTO new_id
      FROM legacy_zhgl_field_id_map m
      WHERE m.zhgl_field_id = old_id;
      IF new_id IS NULL AND NOT EXISTS (SELECT 1 FROM dynamic_field df WHERE df.id = old_id AND df.deleted = false) THEN
        CONTINUE;
      END IF;
      IF new_id IS NULL THEN
        new_id := old_id;
      END IF;
      IF EXISTS (SELECT 1 FROM dynamic_field df WHERE df.id = new_id AND df.deleted = false) THEN
        new_fields := new_fields || jsonb_build_array(jsonb_set(f, '{fieldId}', to_jsonb(new_id)));
      END IF;
    END LOOP;
    new_groups := new_groups || jsonb_build_array(g || jsonb_build_object('fields', new_fields));
  END LOOP;

  RETURN jsonb_set(cfg, '{groups}', new_groups)::text;
END;
$$;


--
-- Name: sanitize_field_groups_config(bigint, text); Type: FUNCTION; Schema: dynamicbusiness; Owner: -
--

CREATE FUNCTION dynamicbusiness.sanitize_field_groups_config(p_model_id bigint, p_config text) RETURNS text
    LANGUAGE plpgsql
    AS $$
DECLARE
  cfg jsonb;
  new_groups jsonb := '[]'::jsonb;
  g jsonb;
  f jsonb;
  new_fields jsonb;
  fid bigint;
BEGIN
  IF p_config IS NULL OR btrim(p_config) = '' THEN
    RETURN p_config;
  END IF;
  BEGIN
    cfg := p_config::jsonb;
  EXCEPTION WHEN OTHERS THEN
    RETURN p_config;
  END;
  IF cfg->'groups' IS NULL THEN
    RETURN p_config;
  END IF;

  FOR g IN SELECT * FROM jsonb_array_elements(cfg->'groups') LOOP
    new_fields := '[]'::jsonb;
    FOR f IN SELECT * FROM jsonb_array_elements(COALESCE(g->'fields', '[]'::jsonb)) LOOP
      BEGIN
        fid := (f->>'fieldId')::bigint;
      EXCEPTION WHEN OTHERS THEN
        CONTINUE;
      END;
      IF NOT EXISTS (
        SELECT 1 FROM dynamic_field df
        WHERE df.id = fid AND df.deleted = false
      ) THEN
        CONTINUE;
      END IF;
      IF NOT EXISTS (
        SELECT 1 FROM dynamic_model_field_assignment mfa
        WHERE mfa.model_id = p_model_id AND mfa.field_id = fid AND mfa.deleted = false
      ) THEN
        CONTINUE;
      END IF;
      new_fields := new_fields || jsonb_build_array(f);
    END LOOP;
    new_groups := new_groups || jsonb_build_array(
      g || jsonb_build_object('fields', new_fields)
    );
  END LOOP;

  RETURN jsonb_set(cfg, '{groups}', new_groups)::text;
END;
$$;




--
-- Name: base_field_library_name_alias; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.base_field_library_name_alias (
    base_field_name character varying(200) NOT NULL,
    library_field_code character varying(64) NOT NULL
);


--
-- Name: biz_billing; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_billing (
    id bigint NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: biz_customer; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_customer (
    id bigint NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: biz_inspection_point; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_inspection_point (
    id bigint NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    rel_region character varying(255),
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: COLUMN biz_inspection_point.rel_region; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_inspection_point.rel_region IS '????????????????????: REL_REGION';


--
-- Name: biz_dian_wei_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_dian_wei_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_dian_wei_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.biz_dian_wei_id_seq OWNED BY dynamicbusiness.biz_inspection_point.id;


--
-- Name: biz_emergency_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_emergency_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_emergency; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_emergency (
    id bigint DEFAULT nextval('dynamicbusiness.biz_emergency_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: biz_emergency_resource; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_emergency_resource (
    id bigint NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: biz_emergency_team; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_emergency_team (
    id bigint NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: biz_equipment; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_equipment (
    id bigint NOT NULL,
    business_type_code character varying DEFAULT 'equipment'::character varying NOT NULL,
    model_id bigint NOT NULL,
    name character varying NOT NULL,
    custom_fields jsonb,
    _deprecated_status integer DEFAULT 1 NOT NULL,
    parent_id bigint,
    tree_path character varying,
    region_id bigint,
    _deprecated_manufacturer character varying,
    model_number character varying,
    serial_number character varying,
    purchase_date date,
    warranty_expiry date,
    health_score numeric,
    last_maintenance_time timestamp without time zone,
    equipment_status character varying DEFAULT 'NORMAL'::character varying,
    creator character varying DEFAULT 'system'::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tenant_id bigint DEFAULT 0,
    _deprecated_modelnumber character varying,
    _deprecated_serialnumber character varying,
    _deprecated_regionid numeric,
    _deprecated_purchasedate date,
    _deprecated_warrantyexpiry date,
    _deprecated_equipmentstatus character varying,
    _deprecated_lastmaintenancetime timestamp without time zone,
    _deprecated_healthscore numeric,
    _deprecated_equipment_code character varying,
    _deprecated_equipment_name character varying,
    _deprecated_install_date date,
    _deprecated_equipment_model character varying,
    equipment_code character varying,
    equipment_name character varying,
    status character varying,
    install_date date,
    manufacturer character varying,
    equipment_model character varying,
    guid character varying,
    device_type numeric,
    device_code character varying,
    coordinate_3d character varying,
    coordinate_gis character varying,
    code character varying NOT NULL,
    rel_region character varying,
    di_zuo_lei_xing character varying,
    rel_ke_hu character varying,
    rel_spare_part character varying,
    sort integer DEFAULT 0
);


--
-- Name: biz_equipment_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE dynamicbusiness.biz_equipment ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME dynamicbusiness.biz_equipment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: biz_fault_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_fault_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_fault; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_fault (
    id bigint DEFAULT nextval('dynamicbusiness.biz_fault_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    fault_no character varying(255),
    fault_type character varying(100),
    fault_level character varying(100),
    phenomenon character varying(255),
    occur_time timestamp without time zone,
    resolved boolean,
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: TABLE biz_fault; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.biz_fault IS 'fault????????????';


--
-- Name: COLUMN biz_fault.business_type_code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_fault.business_type_code IS '?????????';


--
-- Name: COLUMN biz_fault.model_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_fault.model_id IS '????Model ID';


--
-- Name: COLUMN biz_fault.name; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_fault.name IS '???';


--
-- Name: COLUMN biz_fault.code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_fault.code IS '???';


--
-- Name: biz_jian_cha_nei_rong_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_jian_cha_nei_rong_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_inspection_item; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_inspection_item (
    id bigint DEFAULT nextval('dynamicbusiness.biz_jian_cha_nei_rong_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: biz_inspection_management_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_inspection_management_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_ke_hu_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_ke_hu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_ke_hu_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.biz_ke_hu_id_seq OWNED BY dynamicbusiness.biz_customer.id;


--
-- Name: biz_route; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_route (
    id bigint NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: biz_lu_xian_guan_li_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_lu_xian_guan_li_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_lu_xian_guan_li_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.biz_lu_xian_guan_li_id_seq OWNED BY dynamicbusiness.biz_route.id;


--
-- Name: biz_maintenance_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_maintenance_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_maintenance; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_maintenance (
    id bigint DEFAULT nextval('dynamicbusiness.biz_maintenance_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    _deprecated_order_no character varying(255),
    _deprecated_maintenance_type character varying(100),
    _deprecated_plan_time timestamp without time zone,
    _deprecated_executor character varying(255),
    _deprecated_order_status character varying(100),
    order_no character varying(255),
    maintenance_type character varying(100),
    plan_time timestamp without time zone,
    executor character varying(255),
    order_status character varying(100),
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: biz_patrol; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_patrol (
    id bigint DEFAULT nextval('dynamicbusiness.biz_inspection_management_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: TABLE biz_patrol; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.biz_patrol IS '??????';


--
-- Name: COLUMN biz_patrol.business_type_code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_patrol.business_type_code IS '?????????';


--
-- Name: COLUMN biz_patrol.model_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_patrol.model_id IS '????Model ID';


--
-- Name: COLUMN biz_patrol.name; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_patrol.name IS '???';


--
-- Name: COLUMN biz_patrol.code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_patrol.code IS '???';


--
-- Name: biz_personnel_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_personnel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_personnel; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_personnel (
    id bigint DEFAULT nextval('dynamicbusiness.biz_personnel_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: biz_pipeline_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_pipeline_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_pipeline; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_pipeline (
    id bigint DEFAULT nextval('dynamicbusiness.biz_pipeline_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    pipeline_code character varying(255),
    pipeline_name character varying(255),
    install_date date,
    manufacturer character varying(255),
    pipeline_model character varying(255),
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: TABLE biz_pipeline; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.biz_pipeline IS 'pipeline????????????';


--
-- Name: COLUMN biz_pipeline.business_type_code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_pipeline.business_type_code IS '?????????';


--
-- Name: COLUMN biz_pipeline.model_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_pipeline.model_id IS '????Model ID';


--
-- Name: COLUMN biz_pipeline.name; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_pipeline.name IS '???';


--
-- Name: COLUMN biz_pipeline.code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_pipeline.code IS '???';


--
-- Name: biz_region_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_region_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_region; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_region (
    id bigint DEFAULT nextval('dynamicbusiness.biz_region_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    _deprecated_region_code character varying(255),
    _deprecated_region_name character varying(255),
    _deprecated_region_type character varying(100),
    region_code character varying(255),
    region_name character varying(255),
    region_type character varying(100),
    rel_equipment character varying(255),
    rel_dian_wei character varying(255),
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: TABLE biz_region; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.biz_region IS '???????????????????ategory isEntity??????????????????';


--
-- Name: COLUMN biz_region.business_type_code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_region.business_type_code IS '?????????';


--
-- Name: COLUMN biz_region.model_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_region.model_id IS '????Model ID';


--
-- Name: COLUMN biz_region.name; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_region.name IS '???';


--
-- Name: COLUMN biz_region.code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_region.code IS '???';


--
-- Name: COLUMN biz_region.status; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_region.status IS '?????1-?????-???';


--
-- Name: COLUMN biz_region.area_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_region.area_id IS '???????ID';


--
-- Name: COLUMN biz_region.parent_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_region.parent_id IS '?????ID?? ?????????';


--
-- Name: biz_shou_fei_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_shou_fei_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_shou_fei_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.biz_shou_fei_id_seq OWNED BY dynamicbusiness.biz_billing.id;


--
-- Name: biz_spare_part_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_spare_part_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_spare_part; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_spare_part (
    id bigint DEFAULT nextval('dynamicbusiness.biz_spare_part_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    spare_part_code character varying(255),
    spare_part_name character varying(255),
    stock_quantity numeric(18,4),
    unit character varying(255),
    min_stock numeric(18,4),
    rel_equipment character varying(255),
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: TABLE biz_spare_part; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.biz_spare_part IS 'spare_part????????????';


--
-- Name: COLUMN biz_spare_part.business_type_code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_spare_part.business_type_code IS '?????????';


--
-- Name: COLUMN biz_spare_part.model_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_spare_part.model_id IS '????Model ID';


--
-- Name: COLUMN biz_spare_part.name; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_spare_part.name IS '???';


--
-- Name: COLUMN biz_spare_part.code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.biz_spare_part.code IS '???';


--
-- Name: biz_task_model_6bfa9a8c9be648cebc21737115690e21_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_task_model_6bfa9a8c9be648cebc21737115690e21_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_task; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.biz_task (
    id bigint DEFAULT nextval('dynamicbusiness.biz_task_model_6bfa9a8c9be648cebc21737115690e21_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_type_code character varying(64),
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100),
    status integer DEFAULT 1,
    area_id bigint,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    f_f_b035eeaa884243269178a06374ae8438 character varying(100),
    f_f_ed97f195ccbe495d8766026635e4f5e3 character varying(100),
    f_f_e8289a51a3dd41c6b7f1539efd003258 character varying(100),
    f_f_8e92b92f84e74cfd853cdd8cf4f6361a character varying(100),
    f_f_c367374d455f4024b29560be44f7c031 character varying(100),
    f_f_4f3b40defe104f1b94c3462c568bfb79 character varying(255),
    f_f_1d3bcec7a5df4ff985610db69a2c467b timestamp without time zone,
    f_f_3071c55d68ac4e03872d9758512aa17e text,
    f_f_443c8879f02b479e8af045a80c9fdcca character varying(255),
    f_f_0436a31b31dc4b4d8a5314177e254312 timestamp without time zone,
    f_f_7e858ec039604fd5841722ddf3d3176a character varying(255),
    f_f_8bf33d80787a411cb398f74c54b7843b character varying(255),
    f_f_40617730a826424faa4b40ccd8a58414 character varying(255),
    f_f_1d9efb587ce0496aa8a967ebd7216627 character varying(255),
    f_f_209f806919c0492b97ee9a8aa98aaee9 character varying(100),
    f_f_6e001ac5256943b6840f5b9a3a17ac0b character varying(255),
    f_f_524fd7c27947494384391f83aabbb9e6 character varying(255),
    f_f_224b4af4e1474d5d85002c03d0bdf89a text,
    f_f_3c6a384288b84d4e9086206ff2673eb3 text,
    f_f_d9b5d2f7cb524d17bb2223a9e5a56f4c timestamp without time zone,
    f_f_ec6b7dec58434d76a6b0e9aeb79c197b timestamp without time zone,
    f_f_92d768ce991649fca38c345cf9959e05 timestamp without time zone,
    f_f_39ab7f71e1d945fcbe6af9c4fbae1b25 numeric(18,4),
    f_f_6116465add0949b593f1dcd3f2b6e29d text,
    f_f_cefb517417c643cdbd044929d1fe03f5 text,
    f_f_3dc0dc43a3894d698a38c2550ce47fdb text,
    f_f_31d7dc332d2846c9837fb0d7f9e2ec51 text,
    f_f_e30a052673b6438d86ee0a99585cb9bb text,
    f_f_c8dc3d61b1494b61a1197065fa41501d character varying(255),
    f_f_65e1d5ebc4e4442d9483aa3f4313f7a9 character varying(100),
    f_f_981ca5abfa2840eeb5b4a97918029e9f numeric(18,4),
    f_f_dc7aaac23a384e4b85ea70d2c35efe4a text,
    f_f_9d05b9b3a3d24a6da5a41c8143bd448e character varying(255),
    f_f_3d37caf40b634e3c9527bba283d92622 text,
    f_f_0b1047d4121b4b23af936cf6e6f06b7b text,
    f_f_7c6e9af941d04fcfbb36dbe80134f119 text,
    f_f_5c4fbd6a48ca457ba09a97c4ccea8e71 character varying(100),
    f_f_b8a976c165824f848daac3c7bbe0fa1f text,
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: biz_ying_ji_dui_wu_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_ying_ji_dui_wu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_ying_ji_dui_wu_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.biz_ying_ji_dui_wu_id_seq OWNED BY dynamicbusiness.biz_emergency_team.id;


--
-- Name: biz_ying_ji_zi_yuan_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.biz_ying_ji_zi_yuan_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_ying_ji_zi_yuan_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.biz_ying_ji_zi_yuan_id_seq OWNED BY dynamicbusiness.biz_emergency_resource.id;


--
-- Name: business_capability_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.business_capability_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: business_capability; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.business_capability (
    id bigint DEFAULT nextval('dynamicbusiness.business_capability_seq'::regclass) NOT NULL,
    business_type_code character varying(64) NOT NULL,
    capability_full jsonb NOT NULL,
    version bigint DEFAULT 1 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying NOT NULL,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_category character varying(16) DEFAULT 'dynamic'::character varying NOT NULL
);


--
-- Name: TABLE business_capability; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.business_capability IS '业务能力全集表（按 businessTypeCode 索引）';


--
-- Name: COLUMN business_capability.business_category; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.business_capability.business_category IS '业务分类：dynamic（动态业务）/ system（系统业务）';


--
-- Name: capability_component_projection_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.capability_component_projection_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: capability_component_projection; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.capability_component_projection (
    id bigint DEFAULT nextval('dynamicbusiness.capability_component_projection_seq'::regclass) NOT NULL,
    business_type_code character varying(64) NOT NULL,
    component_code character varying(32) NOT NULL,
    component_interface jsonb NOT NULL,
    version bigint DEFAULT 1 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying NOT NULL,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    data_kind character varying(16) DEFAULT 'entity'::character varying NOT NULL
);


--
-- Name: TABLE capability_component_projection; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.capability_component_projection IS '组件能力投影表（按 businessTypeCode + componentCode 索引）';


--
-- Name: COLUMN capability_component_projection.data_kind; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.capability_component_projection.data_kind IS '数据种类：model（模型目录）/ entity（实例数据）';


--
-- Name: dynamic_business_type; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_business_type (
    id bigint NOT NULL,
    code character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    parent_id bigint,
    description character varying(500),
    icon character varying(128),
    alias character varying(128),
    sort integer DEFAULT 0 NOT NULL,
    status character varying(32) DEFAULT 'active'::character varying NOT NULL,
    type_level character varying(32) DEFAULT 'USER'::character varying NOT NULL,
    association_fields jsonb,
    storage_type character varying(32) DEFAULT 'GENERIC'::character varying NOT NULL,
    dedicated_table_name character varying(128),
    enable_rule_engine boolean DEFAULT false NOT NULL,
    physical_column_mapping jsonb,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_business_type_base_field; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_business_type_base_field (
    id bigint NOT NULL,
    business_type_code character varying(64) NOT NULL,
    field_code character varying(64) NOT NULL,
    field_name character varying(128) NOT NULL,
    data_type character varying(32) NOT NULL,
    required boolean DEFAULT false NOT NULL,
    default_value character varying(500),
    description character varying(500),
    type_config text,
    sort_order integer DEFAULT 0 NOT NULL,
    status smallint DEFAULT 1 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_business_type_base_field_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_business_type_base_field_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_business_type_base_field_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_business_type_base_field_id_seq OWNED BY dynamicbusiness.dynamic_business_type_base_field.id;


--
-- Name: dynamic_business_type_config; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_business_type_config (
    id bigint NOT NULL,
    business_type_code character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    storage_type character varying(32) DEFAULT 'GENERIC'::character varying NOT NULL,
    dedicated_table_name character varying(128),
    strategy_bean_name character varying(128),
    enable_rule_engine boolean DEFAULT false NOT NULL,
    description character varying(500),
    status smallint DEFAULT 1 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    physical_column_mapping jsonb
);


--
-- Name: dynamic_business_type_config_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_business_type_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_business_type_config_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_business_type_config_id_seq OWNED BY dynamicbusiness.dynamic_business_type_config.id;


--
-- Name: dynamic_business_type_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_business_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_business_type_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_business_type_id_seq OWNED BY dynamicbusiness.dynamic_business_type.id;


--
-- Name: dynamic_business_type_relation; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_business_type_relation (
    id bigint NOT NULL,
    source_business_type_code character varying(64) NOT NULL,
    target_business_type_code character varying(64) NOT NULL,
    relation_name character varying(128),
    auto_create_field boolean DEFAULT false NOT NULL,
    default_field_name character varying(128),
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_business_type_relation_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_business_type_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_business_type_relation_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_business_type_relation_id_seq OWNED BY dynamicbusiness.dynamic_business_type_relation.id;


--
-- Name: dynamic_category; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_category (
    id bigint NOT NULL,
    parent_id bigint,
    name character varying(200) NOT NULL,
    code character varying(64) NOT NULL,
    category_type_code character varying(50) NOT NULL,
    tree_path character varying(1024),
    level integer,
    sort integer DEFAULT 0 NOT NULL,
    status smallint DEFAULT 1 NOT NULL,
    description character varying(500),
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_category_entity_link; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_category_entity_link (
    id bigint NOT NULL,
    category_id bigint NOT NULL,
    entity_id bigint NOT NULL,
    entity_model_id bigint,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_category_entity_link_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_category_entity_link_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_category_entity_link_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_category_entity_link_id_seq OWNED BY dynamicbusiness.dynamic_category_entity_link.id;


--
-- Name: dynamic_category_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_category_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_category_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_category_id_seq OWNED BY dynamicbusiness.dynamic_category.id;


--
-- Name: dynamic_category_permission; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_category_permission (
    id bigint NOT NULL,
    role_id bigint NOT NULL,
    category_id bigint NOT NULL,
    can_view boolean DEFAULT true NOT NULL,
    can_manage boolean DEFAULT false NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_category_permission_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_category_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_category_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_category_permission_id_seq OWNED BY dynamicbusiness.dynamic_category_permission.id;


--
-- Name: dynamic_category_type; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_category_type (
    id bigint NOT NULL,
    category_type_code character varying(50) NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(500),
    status integer DEFAULT 1 NOT NULL,
    top_level_category_id bigint,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_category_type_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_category_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_category_type_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_category_type_id_seq OWNED BY dynamicbusiness.dynamic_category_type.id;


--
-- Name: dynamic_computed_field; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_computed_field (
    id bigint NOT NULL,
    model_id bigint NOT NULL,
    field_name character varying(64) NOT NULL,
    field_code character varying(64) NOT NULL,
    compute_type character varying(32) NOT NULL,
    aggregate_function character varying(16),
    target_business_type character varying(64),
    target_model_code character varying(64),
    target_field_code character varying(64),
    relation_condition jsonb,
    filter_condition jsonb,
    formula_expression character varying(512),
    formula_fields jsonb,
    result_type character varying(32) NOT NULL,
    decimal_places integer DEFAULT 0 NOT NULL,
    null_display character varying(32) DEFAULT '0'::character varying NOT NULL,
    compute_strategy character varying(32) DEFAULT 'REALTIME'::character varying NOT NULL,
    cache_ttl_minutes integer DEFAULT 5 NOT NULL,
    description character varying(512),
    sort_order integer DEFAULT 0 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_computed_field_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_computed_field_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_computed_field_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_computed_field_id_seq OWNED BY dynamicbusiness.dynamic_computed_field.id;


--
-- Name: dynamic_data_migration_log; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_data_migration_log (
    id bigint NOT NULL,
    migration_type character varying(64) NOT NULL,
    migration_version character varying(32),
    dry_run boolean DEFAULT false NOT NULL,
    start_time timestamp without time zone NOT NULL,
    end_time timestamp without time zone,
    duration_ms bigint,
    total_count integer DEFAULT 0 NOT NULL,
    success_count integer DEFAULT 0 NOT NULL,
    skipped_count integer DEFAULT 0 NOT NULL,
    failed_count integer DEFAULT 0 NOT NULL,
    status character varying(32) DEFAULT 'RUNNING'::character varying NOT NULL,
    error_message text,
    details jsonb,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_data_migration_log_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_data_migration_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_data_migration_log_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_data_migration_log_id_seq OWNED BY dynamicbusiness.dynamic_data_migration_log.id;


--
-- Name: dynamic_dynamic_sql_audit_log; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_dynamic_sql_audit_log (
    id bigint NOT NULL,
    table_name character varying(128) NOT NULL,
    operation_type character varying(32) NOT NULL,
    executed_sql text,
    parameters text,
    success boolean NOT NULL,
    error_message text,
    affected_rows integer,
    execution_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    operator_id bigint,
    operator_name character varying(64),
    client_ip character varying(64),
    request_id character varying(64),
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_dynamic_sql_audit_log_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_dynamic_sql_audit_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_dynamic_sql_audit_log_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_dynamic_sql_audit_log_id_seq OWNED BY dynamicbusiness.dynamic_dynamic_sql_audit_log.id;


--
-- Name: dynamic_dynamic_table; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_dynamic_table (
    id bigint NOT NULL,
    model_id bigint NOT NULL,
    business_type_code character varying(64),
    table_name character varying(128) NOT NULL,
    table_comment character varying(500),
    column_config text,
    status integer DEFAULT 1 NOT NULL,
    version integer DEFAULT 1 NOT NULL,
    last_sync_time timestamp without time zone,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_dynamic_table_audit_log; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_dynamic_table_audit_log (
    id bigint NOT NULL,
    dynamic_table_id bigint NOT NULL,
    operation_type character varying(32) NOT NULL,
    operation_desc character varying(500),
    before_config text,
    after_config text,
    executed_sql text,
    execute_result character varying(32) NOT NULL,
    error_message text,
    operation_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    operator_id bigint,
    operator_name character varying(64),
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_dynamic_table_audit_log_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_dynamic_table_audit_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_dynamic_table_audit_log_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_dynamic_table_audit_log_id_seq OWNED BY dynamicbusiness.dynamic_dynamic_table_audit_log.id;


--
-- Name: dynamic_dynamic_table_column; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_dynamic_table_column (
    id bigint NOT NULL,
    dynamic_table_id bigint NOT NULL,
    field_id bigint NOT NULL,
    column_name character varying(64) NOT NULL,
    data_type character varying(64) NOT NULL,
    nullable boolean DEFAULT true NOT NULL,
    default_value character varying(500),
    column_comment character varying(500),
    sort_order integer DEFAULT 0 NOT NULL,
    status integer DEFAULT 1 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_dynamic_table_column_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_dynamic_table_column_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_dynamic_table_column_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_dynamic_table_column_id_seq OWNED BY dynamicbusiness.dynamic_dynamic_table_column.id;


--
-- Name: dynamic_dynamic_table_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_dynamic_table_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_dynamic_table_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_dynamic_table_id_seq OWNED BY dynamicbusiness.dynamic_dynamic_table.id;


--
-- Name: dynamic_entity; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity (
    id bigint NOT NULL,
    business_type_code character varying(64) NOT NULL,
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    parent_id bigint,
    tree_path character varying(1024),
    sort integer DEFAULT 0 NOT NULL,
    status smallint DEFAULT 1 NOT NULL,
    custom_fields jsonb,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_entity_access_permission; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_access_permission (
    id bigint NOT NULL,
    role_id bigint NOT NULL,
    entity_id bigint NOT NULL,
    can_view boolean DEFAULT true NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_entity_access_permission_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_access_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_entity_access_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_entity_access_permission_id_seq OWNED BY dynamicbusiness.dynamic_entity_access_permission.id;


--
-- Name: dynamic_entity_category_relation; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_category_relation (
    id bigint NOT NULL,
    entity_id bigint NOT NULL,
    category_id bigint NOT NULL,
    business_type_code character varying(64),
    sort integer DEFAULT 0 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_entity_category_relation_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_category_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_entity_category_relation_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_entity_category_relation_id_seq OWNED BY dynamicbusiness.dynamic_entity_category_relation.id;


--
-- Name: dynamic_entity_field_index; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_field_index (
    id bigint NOT NULL,
    entity_id bigint NOT NULL,
    model_id bigint NOT NULL,
    field_code character varying(64) NOT NULL,
    value_string character varying(1024),
    value_number numeric(20,6),
    value_date date,
    value_datetime timestamp without time zone,
    value_boolean boolean,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_entity_field_index_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_field_index_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_entity_field_index_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_entity_field_index_id_seq OWNED BY dynamicbusiness.dynamic_entity_field_index.id;


--
-- Name: dynamic_entity_field_permission; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_field_permission (
    id bigint NOT NULL,
    role_id bigint NOT NULL,
    model_id bigint NOT NULL,
    field_id bigint NOT NULL,
    can_view boolean DEFAULT true NOT NULL,
    can_edit boolean DEFAULT false NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_entity_field_permission_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_field_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_entity_field_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_entity_field_permission_id_seq OWNED BY dynamicbusiness.dynamic_entity_field_permission.id;


--
-- Name: dynamic_entity_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_entity_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_entity_id_seq OWNED BY dynamicbusiness.dynamic_entity.id;


--
-- Name: dynamic_entity_operation_permission; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_operation_permission (
    id bigint NOT NULL,
    role_id bigint NOT NULL,
    entity_id bigint,
    model_id bigint,
    can_create boolean DEFAULT false NOT NULL,
    can_update boolean DEFAULT false NOT NULL,
    can_delete boolean DEFAULT false NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_entity_operation_permission_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_operation_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_entity_operation_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_entity_operation_permission_id_seq OWNED BY dynamicbusiness.dynamic_entity_operation_permission.id;


--
-- Name: dynamic_entity_relation; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_relation (
    id bigint NOT NULL,
    source_entity_id bigint NOT NULL,
    target_entity_id bigint NOT NULL,
    relation_type character varying(32) NOT NULL,
    relation_name character varying(255),
    description text,
    relation_attributes text,
    status smallint DEFAULT 1 NOT NULL,
    field_code character varying(64),
    source_model_code character varying(64),
    target_model_code character varying(64),
    source_business_type_code character varying(64),
    target_business_type_code character varying(64),
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_entity_relation_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_entity_relation_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_entity_relation_id_seq OWNED BY dynamicbusiness.dynamic_entity_relation.id;


--
-- Name: dynamic_entity_sync_fail_log; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_sync_fail_log (
    id bigint NOT NULL,
    entity_id bigint NOT NULL,
    business_type_code character varying(64) NOT NULL,
    model_id bigint NOT NULL,
    engine_type character varying(32) DEFAULT 'postgresql'::character varying NOT NULL,
    fail_reason text,
    retry_count integer DEFAULT 0 NOT NULL,
    last_retry_at timestamp without time zone,
    status character varying(32) DEFAULT 'PENDING'::character varying NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_entity_sync_fail_log_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_sync_fail_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_entity_sync_fail_log_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_entity_sync_fail_log_id_seq OWNED BY dynamicbusiness.dynamic_entity_sync_fail_log.id;


--
-- Name: dynamic_field; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_field (
    id bigint NOT NULL,
    code character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    type character varying(32) NOT NULL,
    unit character varying(64),
    description character varying(500),
    source character varying(32) DEFAULT 'USER'::character varying NOT NULL,
    status smallint DEFAULT 1 NOT NULL,
    max_relations integer,
    index_strategy character varying(32),
    options text,
    provider_code character varying(64),
    semantic_type character varying(64),
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_field_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_field_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_field_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_field_id_seq OWNED BY dynamicbusiness.dynamic_field.id;


--
-- Name: dynamic_group; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_group (
    id bigint NOT NULL,
    group_type character varying(32) NOT NULL,
    code character varying(128),
    name character varying(255) NOT NULL,
    description character varying(1024),
    parent_id bigint,
    path character varying(1024),
    level integer,
    sort integer,
    status integer,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_group_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_group_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_group_id_seq OWNED BY dynamicbusiness.dynamic_group.id;


--
-- Name: dynamic_group_relation; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_group_relation (
    id bigint NOT NULL,
    group_type character varying(32) NOT NULL,
    group_id bigint NOT NULL,
    target_id bigint NOT NULL,
    sort integer,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_group_relation_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_group_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_group_relation_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_group_relation_id_seq OWNED BY dynamicbusiness.dynamic_group_relation.id;


--
-- Name: dynamic_mail_account; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_mail_account (
    id bigint NOT NULL,
    mail character varying(255) NOT NULL,
    username character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    host character varying(255) NOT NULL,
    port integer NOT NULL,
    ssl_enable boolean DEFAULT false NOT NULL,
    starttls_enable boolean DEFAULT false NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL
);


--
-- Name: dynamic_mail_account_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_mail_account_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_mail_account_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_mail_account_id_seq OWNED BY dynamicbusiness.dynamic_mail_account.id;


--
-- Name: dynamic_mail_log; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_mail_log (
    id bigint NOT NULL,
    user_id bigint,
    user_type smallint,
    to_mails text,
    cc_mails text,
    bcc_mails text,
    account_id bigint NOT NULL,
    from_mail character varying(255) NOT NULL,
    template_id bigint NOT NULL,
    template_code character varying(63) NOT NULL,
    template_nickname character varying(255),
    template_title character varying(255) NOT NULL,
    template_content text NOT NULL,
    template_params jsonb,
    send_status smallint DEFAULT 0 NOT NULL,
    send_time timestamp without time zone,
    send_message_id character varying(255),
    send_exception text,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL
);


--
-- Name: dynamic_mail_log_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_mail_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_mail_log_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_mail_log_id_seq OWNED BY dynamicbusiness.dynamic_mail_log.id;


--
-- Name: dynamic_mail_template; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_mail_template (
    id bigint NOT NULL,
    name character varying(63) NOT NULL,
    code character varying(63) NOT NULL,
    account_id bigint NOT NULL,
    nickname character varying(255),
    title character varying(255) NOT NULL,
    content text NOT NULL,
    params jsonb DEFAULT '[]'::jsonb NOT NULL,
    status smallint DEFAULT 0 NOT NULL,
    remark character varying(255),
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL
);


--
-- Name: dynamic_mail_template_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_mail_template_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_mail_template_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_mail_template_id_seq OWNED BY dynamicbusiness.dynamic_mail_template.id;


--
-- Name: dynamic_model; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_model (
    id bigint NOT NULL,
    code character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    business_type_code character varying(64) NOT NULL,
    description character varying(500),
    status smallint DEFAULT 1 NOT NULL,
    sort integer DEFAULT 0 NOT NULL,
    field_groups_config text,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_model_category_relation; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_model_category_relation (
    id bigint NOT NULL,
    model_id bigint NOT NULL,
    category_id bigint NOT NULL,
    business_type_code character varying(64),
    sort integer DEFAULT 0 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_model_category_relation_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_model_category_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_model_category_relation_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_model_category_relation_id_seq OWNED BY dynamicbusiness.dynamic_model_category_relation.id;


--
-- Name: dynamic_model_field_assignment; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_model_field_assignment (
    id bigint NOT NULL,
    model_id bigint NOT NULL,
    field_id bigint NOT NULL,
    required boolean DEFAULT false NOT NULL,
    is_searchable boolean DEFAULT false NOT NULL,
    is_filterable boolean DEFAULT false NOT NULL,
    is_sortable boolean DEFAULT false NOT NULL,
    default_value character varying(500),
    validation_rules text,
    sort integer DEFAULT 0 NOT NULL,
    field_group_id bigint,
    field_source character varying(32),
    ref_library_id bigint,
    model_relation_id bigint,
    target_business_type character varying(64),
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_model_field_assignment_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_model_field_assignment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_model_field_assignment_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_model_field_assignment_id_seq OWNED BY dynamicbusiness.dynamic_model_field_assignment.id;


--
-- Name: dynamic_model_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_model_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_model_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_model_id_seq OWNED BY dynamicbusiness.dynamic_model.id;


--
-- Name: dynamic_model_relation; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_model_relation (
    id bigint NOT NULL,
    business_type_relation_id bigint,
    source_model_id bigint NOT NULL,
    source_model_code character varying(64) NOT NULL,
    target_model_id bigint NOT NULL,
    target_model_code character varying(64) NOT NULL,
    relation_name character varying(128),
    field_code character varying(64),
    auto_generated boolean DEFAULT false NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_model_relation_declaration; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_model_relation_declaration (
    id bigint NOT NULL,
    model_id bigint NOT NULL,
    target_business_type character varying(64) NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_model_relation_declaration_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_model_relation_declaration_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_model_relation_declaration_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_model_relation_declaration_id_seq OWNED BY dynamicbusiness.dynamic_model_relation_declaration.id;


--
-- Name: dynamic_model_relation_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_model_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_model_relation_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_model_relation_id_seq OWNED BY dynamicbusiness.dynamic_model_relation.id;


--
-- Name: dynamic_page; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_page (
    id bigint NOT NULL,
    page_code character varying(100) NOT NULL,
    page_name character varying(100) NOT NULL,
    page_type character varying(50) NOT NULL,
    description character varying(255) DEFAULT ''::character varying,
    status smallint DEFAULT 0 NOT NULL,
    parent_menu_id bigint,
    menu_id bigint,
    page_config_id bigint,
    icon character varying(100) DEFAULT ''::character varying,
    tags character varying(255) DEFAULT ''::character varying,
    route_path character varying(255) DEFAULT ''::character varying,
    component character varying(255) DEFAULT ''::character varying,
    layout character varying(50) DEFAULT ''::character varying,
    ui_schema jsonb DEFAULT '{}'::jsonb NOT NULL,
    ui_version character varying(50) DEFAULT ''::character varying,
    data_source jsonb DEFAULT '{}'::jsonb NOT NULL,
    remark character varying(255) DEFAULT ''::character varying,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_page_config; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_page_config (
    id bigint NOT NULL,
    config_code character varying(100),
    page_code character varying(100) NOT NULL,
    menu_id bigint,
    page_type character varying(50) DEFAULT 'data_management'::character varying NOT NULL,
    config jsonb DEFAULT '{}'::jsonb NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_page_config_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_page_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_page_config_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_page_config_id_seq OWNED BY dynamicbusiness.dynamic_page_config.id;


--
-- Name: dynamic_page_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_page_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_page_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_page_id_seq OWNED BY dynamicbusiness.dynamic_page.id;


--
-- Name: dynamic_precomputed_value; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_precomputed_value (
    id bigint NOT NULL,
    model_id bigint NOT NULL,
    entity_id bigint NOT NULL,
    field_code character varying(64) NOT NULL,
    computed_value text,
    value_type character varying(32),
    compute_status character varying(32) DEFAULT 'PENDING'::character varying NOT NULL,
    last_compute_time timestamp without time zone,
    next_compute_time timestamp without time zone,
    compute_duration_ms bigint,
    error_message text,
    retry_count integer DEFAULT 0 NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_precomputed_value_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_precomputed_value_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_precomputed_value_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_precomputed_value_id_seq OWNED BY dynamicbusiness.dynamic_precomputed_value.id;


--
-- Name: dynamic_ref_constraint_library; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_ref_constraint_library (
    id bigint NOT NULL,
    business_type_code character varying(64) NOT NULL,
    ref_target_type character varying(64) NOT NULL,
    constraint_type character varying(64) NOT NULL,
    constraint_name character varying(128) NOT NULL,
    status smallint DEFAULT 0 NOT NULL,
    sort integer DEFAULT 0 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_ref_constraint_library_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_ref_constraint_library_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_ref_constraint_library_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_ref_constraint_library_id_seq OWNED BY dynamicbusiness.dynamic_ref_constraint_library.id;


--
-- Name: dynamic_reference_provider; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_reference_provider (
    id bigint NOT NULL,
    provider_code character varying(64) NOT NULL,
    provider_name character varying(128) NOT NULL,
    provider_type character varying(32) DEFAULT 'INTERNAL'::character varying NOT NULL,
    semantic_type character varying(64),
    capability_flags text,
    config_json text,
    status smallint DEFAULT 1 NOT NULL,
    tenant_scope character varying(32) DEFAULT 'GLOBAL'::character varying,
    priority integer DEFAULT 100,
    health_status character varying(32) DEFAULT 'UNKNOWN'::character varying,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_reference_provider_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_reference_provider_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_reference_provider_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_reference_provider_id_seq OWNED BY dynamicbusiness.dynamic_reference_provider.id;


--
-- Name: dynamic_relation_field_library; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_relation_field_library (
    id bigint NOT NULL,
    field_name character varying(64) NOT NULL,
    field_code character varying(64) NOT NULL,
    ref_business_type character varying(64) NOT NULL,
    display_field_code character varying(64),
    constraint_enabled boolean DEFAULT false NOT NULL,
    constraint_type character varying(64) DEFAULT 'NONE'::character varying NOT NULL,
    description character varying(512),
    usage_count integer DEFAULT 0 NOT NULL,
    is_system boolean DEFAULT false NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_relation_field_library_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_relation_field_library_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_relation_field_library_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_relation_field_library_id_seq OWNED BY dynamicbusiness.dynamic_relation_field_library.id;


--
-- Name: dynamic_template; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_template (
    id bigint NOT NULL,
    code character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    business_type_code character varying(64) NOT NULL,
    description character varying(500),
    status smallint DEFAULT 1 NOT NULL,
    is_system boolean DEFAULT false NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_template_field_assignment; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_template_field_assignment (
    id bigint NOT NULL,
    template_id bigint NOT NULL,
    field_id bigint NOT NULL,
    sort_order integer DEFAULT 0 NOT NULL,
    required boolean DEFAULT false NOT NULL,
    default_value character varying(500),
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_template_field_assignment_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_template_field_assignment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_template_field_assignment_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_template_field_assignment_id_seq OWNED BY dynamicbusiness.dynamic_template_field_assignment.id;


--
-- Name: dynamic_template_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_template_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_template_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_template_id_seq OWNED BY dynamicbusiness.dynamic_template.id;


--
-- Name: dynamic_unit; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_unit (
    id bigint NOT NULL,
    name character varying(100) NOT NULL,
    code character varying(64) NOT NULL,
    unit_type character varying(64),
    sort integer DEFAULT 1 NOT NULL,
    status smallint DEFAULT 1 NOT NULL,
    remark character varying(500),
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: dynamic_unit_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_unit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: dynamic_unit_id_seq; Type: SEQUENCE OWNED BY; Schema: dynamicbusiness; Owner: -
--

ALTER SEQUENCE dynamicbusiness.dynamic_unit_id_seq OWNED BY dynamicbusiness.dynamic_unit.id;


--
-- Name: legacy_business_type_code_map; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.legacy_business_type_code_map (
    legacy_code character varying(64) NOT NULL,
    canonical_code character varying(64) NOT NULL
);


--
-- Name: legacy_zhgl_field_id_map; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.legacy_zhgl_field_id_map (
    zhgl_field_id bigint NOT NULL,
    local_field_id bigint NOT NULL,
    field_code character varying(64) NOT NULL,
    field_name character varying(200),
    match_type character varying(16) DEFAULT 'code'::character varying NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: model_crud_form_definition_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.model_crud_form_definition_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: model_crud_form_definition; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.model_crud_form_definition (
    id bigint DEFAULT nextval('dynamicbusiness.model_crud_form_definition_seq'::regclass) NOT NULL,
    business_type_code character varying(64) NOT NULL,
    model_id bigint NOT NULL,
    crud_form_fields jsonb NOT NULL,
    version bigint DEFAULT 1 NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying NOT NULL,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);


--
-- Name: TABLE model_crud_form_definition; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.model_crud_form_definition IS '模型 CRUD 表单定义表（按 businessTypeCode + modelId 索引）';


--
-- Name: biz_billing id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.biz_billing ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.biz_shou_fei_id_seq'::regclass);


--
-- Name: biz_customer id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.biz_customer ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.biz_ke_hu_id_seq'::regclass);


--
-- Name: biz_emergency_resource id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.biz_emergency_resource ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.biz_ying_ji_zi_yuan_id_seq'::regclass);


--
-- Name: biz_emergency_team id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.biz_emergency_team ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.biz_ying_ji_dui_wu_id_seq'::regclass);


--
-- Name: biz_inspection_point id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.biz_inspection_point ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.biz_dian_wei_id_seq'::regclass);


--
-- Name: biz_route id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.biz_route ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.biz_lu_xian_guan_li_id_seq'::regclass);


--
-- Name: dynamic_business_type id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_business_type ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_business_type_id_seq'::regclass);


--
-- Name: dynamic_business_type_base_field id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_business_type_base_field ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_business_type_base_field_id_seq'::regclass);


--
-- Name: dynamic_business_type_config id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_business_type_config ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_business_type_config_id_seq'::regclass);


--
-- Name: dynamic_business_type_relation id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_business_type_relation ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_business_type_relation_id_seq'::regclass);


--
-- Name: dynamic_category id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_category ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_category_id_seq'::regclass);


--
-- Name: dynamic_category_entity_link id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_category_entity_link ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_category_entity_link_id_seq'::regclass);


--
-- Name: dynamic_category_permission id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_category_permission ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_category_permission_id_seq'::regclass);


--
-- Name: dynamic_category_type id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_category_type ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_category_type_id_seq'::regclass);


--
-- Name: dynamic_computed_field id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_computed_field ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_computed_field_id_seq'::regclass);


--
-- Name: dynamic_data_migration_log id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_data_migration_log ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_data_migration_log_id_seq'::regclass);


--
-- Name: dynamic_dynamic_sql_audit_log id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_dynamic_sql_audit_log ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_dynamic_sql_audit_log_id_seq'::regclass);


--
-- Name: dynamic_dynamic_table id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_dynamic_table ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_dynamic_table_id_seq'::regclass);


--
-- Name: dynamic_dynamic_table_audit_log id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_dynamic_table_audit_log ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_dynamic_table_audit_log_id_seq'::regclass);


--
-- Name: dynamic_dynamic_table_column id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_dynamic_table_column ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_dynamic_table_column_id_seq'::regclass);


--
-- Name: dynamic_entity id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_entity_id_seq'::regclass);


--
-- Name: dynamic_entity_access_permission id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_access_permission ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_entity_access_permission_id_seq'::regclass);


--
-- Name: dynamic_entity_category_relation id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_category_relation ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_entity_category_relation_id_seq'::regclass);


--
-- Name: dynamic_entity_field_index id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_field_index ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_entity_field_index_id_seq'::regclass);


--
-- Name: dynamic_entity_field_permission id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_field_permission ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_entity_field_permission_id_seq'::regclass);


--
-- Name: dynamic_entity_operation_permission id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_operation_permission ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_entity_operation_permission_id_seq'::regclass);


--
-- Name: dynamic_entity_relation id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_relation ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_entity_relation_id_seq'::regclass);


--
-- Name: dynamic_entity_sync_fail_log id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_sync_fail_log ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_entity_sync_fail_log_id_seq'::regclass);


--
-- Name: dynamic_field id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_field ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_field_id_seq'::regclass);


--
-- Name: dynamic_group id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_group ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_group_id_seq'::regclass);


--
-- Name: dynamic_group_relation id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_group_relation ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_group_relation_id_seq'::regclass);


--
-- Name: dynamic_mail_account id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_mail_account ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_mail_account_id_seq'::regclass);


--
-- Name: dynamic_mail_log id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_mail_log ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_mail_log_id_seq'::regclass);


--
-- Name: dynamic_mail_template id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_mail_template ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_mail_template_id_seq'::regclass);


--
-- Name: dynamic_model id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_model ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_model_id_seq'::regclass);


--
-- Name: dynamic_model_category_relation id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_model_category_relation ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_model_category_relation_id_seq'::regclass);


--
-- Name: dynamic_model_field_assignment id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_model_field_assignment ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_model_field_assignment_id_seq'::regclass);


--
-- Name: dynamic_model_relation id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_model_relation ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_model_relation_id_seq'::regclass);


--
-- Name: dynamic_model_relation_declaration id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_model_relation_declaration ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_model_relation_declaration_id_seq'::regclass);


--
-- Name: dynamic_page id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_page ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_page_id_seq'::regclass);


--
-- Name: dynamic_page_config id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_page_config ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_page_config_id_seq'::regclass);


--
-- Name: dynamic_precomputed_value id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_precomputed_value ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_precomputed_value_id_seq'::regclass);


--
-- Name: dynamic_ref_constraint_library id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_ref_constraint_library ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_ref_constraint_library_id_seq'::regclass);


--
-- Name: dynamic_reference_provider id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_reference_provider ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_reference_provider_id_seq'::regclass);


--
-- Name: dynamic_relation_field_library id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_relation_field_library ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_relation_field_library_id_seq'::regclass);


--
-- Name: dynamic_template id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_template ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_template_id_seq'::regclass);


--
-- Name: dynamic_template_field_assignment id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_template_field_assignment ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_template_field_assignment_id_seq'::regclass);


--
-- Name: dynamic_unit id; Type: DEFAULT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_unit ALTER COLUMN id SET DEFAULT nextval('dynamicbusiness.dynamic_unit_id_seq'::regclass);


--
-- Name: base_field_library_name_alias base_field_library_name_alias_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.base_field_library_name_alias
    ADD CONSTRAINT base_field_library_name_alias_pkey PRIMARY KEY (base_field_name);


--
-- Name: biz_route biz_lu_xian_guan_li_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.biz_route
    ADD CONSTRAINT biz_lu_xian_guan_li_pkey PRIMARY KEY (id);


--
-- Name: biz_billing biz_shou_fei_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.biz_billing
    ADD CONSTRAINT biz_shou_fei_pkey PRIMARY KEY (id);


--
-- Name: business_capability business_capability_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.business_capability
    ADD CONSTRAINT business_capability_pkey PRIMARY KEY (id);


--
-- Name: capability_component_projection capability_component_projection_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.capability_component_projection
    ADD CONSTRAINT capability_component_projection_pkey PRIMARY KEY (id);


--
-- Name: dynamic_business_type_base_field dynamic_business_type_base_field_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_business_type_base_field
    ADD CONSTRAINT dynamic_business_type_base_field_pkey PRIMARY KEY (id);


--
-- Name: dynamic_business_type_config dynamic_business_type_config_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_business_type_config
    ADD CONSTRAINT dynamic_business_type_config_pkey PRIMARY KEY (id);


--
-- Name: dynamic_business_type dynamic_business_type_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_business_type
    ADD CONSTRAINT dynamic_business_type_pkey PRIMARY KEY (id);


--
-- Name: dynamic_business_type_relation dynamic_business_type_relation_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_business_type_relation
    ADD CONSTRAINT dynamic_business_type_relation_pkey PRIMARY KEY (id);


--
-- Name: dynamic_category_entity_link dynamic_category_entity_link_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_category_entity_link
    ADD CONSTRAINT dynamic_category_entity_link_pkey PRIMARY KEY (id);


--
-- Name: dynamic_category_permission dynamic_category_permission_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_category_permission
    ADD CONSTRAINT dynamic_category_permission_pkey PRIMARY KEY (id);


--
-- Name: dynamic_category dynamic_category_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_category
    ADD CONSTRAINT dynamic_category_pkey PRIMARY KEY (id);


--
-- Name: dynamic_category_type dynamic_category_type_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_category_type
    ADD CONSTRAINT dynamic_category_type_pkey PRIMARY KEY (id);


--
-- Name: dynamic_computed_field dynamic_computed_field_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_computed_field
    ADD CONSTRAINT dynamic_computed_field_pkey PRIMARY KEY (id);


--
-- Name: dynamic_data_migration_log dynamic_data_migration_log_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_data_migration_log
    ADD CONSTRAINT dynamic_data_migration_log_pkey PRIMARY KEY (id);


--
-- Name: dynamic_dynamic_sql_audit_log dynamic_dynamic_sql_audit_log_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_dynamic_sql_audit_log
    ADD CONSTRAINT dynamic_dynamic_sql_audit_log_pkey PRIMARY KEY (id);


--
-- Name: dynamic_dynamic_table_audit_log dynamic_dynamic_table_audit_log_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_dynamic_table_audit_log
    ADD CONSTRAINT dynamic_dynamic_table_audit_log_pkey PRIMARY KEY (id);


--
-- Name: dynamic_dynamic_table_column dynamic_dynamic_table_column_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_dynamic_table_column
    ADD CONSTRAINT dynamic_dynamic_table_column_pkey PRIMARY KEY (id);


--
-- Name: dynamic_dynamic_table dynamic_dynamic_table_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_dynamic_table
    ADD CONSTRAINT dynamic_dynamic_table_pkey PRIMARY KEY (id);


--
-- Name: dynamic_entity_access_permission dynamic_entity_access_permission_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_access_permission
    ADD CONSTRAINT dynamic_entity_access_permission_pkey PRIMARY KEY (id);


--
-- Name: dynamic_entity_category_relation dynamic_entity_category_relation_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_category_relation
    ADD CONSTRAINT dynamic_entity_category_relation_pkey PRIMARY KEY (id);


--
-- Name: dynamic_entity_field_index dynamic_entity_field_index_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_field_index
    ADD CONSTRAINT dynamic_entity_field_index_pkey PRIMARY KEY (id);


--
-- Name: dynamic_entity_field_permission dynamic_entity_field_permission_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_field_permission
    ADD CONSTRAINT dynamic_entity_field_permission_pkey PRIMARY KEY (id);


--
-- Name: dynamic_entity_operation_permission dynamic_entity_operation_permission_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_operation_permission
    ADD CONSTRAINT dynamic_entity_operation_permission_pkey PRIMARY KEY (id);


--
-- Name: dynamic_entity dynamic_entity_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity
    ADD CONSTRAINT dynamic_entity_pkey PRIMARY KEY (id);


--
-- Name: dynamic_entity_relation dynamic_entity_relation_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_relation
    ADD CONSTRAINT dynamic_entity_relation_pkey PRIMARY KEY (id);


--
-- Name: dynamic_entity_sync_fail_log dynamic_entity_sync_fail_log_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_sync_fail_log
    ADD CONSTRAINT dynamic_entity_sync_fail_log_pkey PRIMARY KEY (id);


--
-- Name: dynamic_field dynamic_field_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_field
    ADD CONSTRAINT dynamic_field_pkey PRIMARY KEY (id);


--
-- Name: dynamic_group dynamic_group_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_group
    ADD CONSTRAINT dynamic_group_pkey PRIMARY KEY (id);


--
-- Name: dynamic_group_relation dynamic_group_relation_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_group_relation
    ADD CONSTRAINT dynamic_group_relation_pkey PRIMARY KEY (id);


--
-- Name: dynamic_mail_account dynamic_mail_account_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_mail_account
    ADD CONSTRAINT dynamic_mail_account_pkey PRIMARY KEY (id);


--
-- Name: dynamic_mail_log dynamic_mail_log_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_mail_log
    ADD CONSTRAINT dynamic_mail_log_pkey PRIMARY KEY (id);


--
-- Name: dynamic_mail_template dynamic_mail_template_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_mail_template
    ADD CONSTRAINT dynamic_mail_template_pkey PRIMARY KEY (id);


--
-- Name: dynamic_model_category_relation dynamic_model_category_relation_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_model_category_relation
    ADD CONSTRAINT dynamic_model_category_relation_pkey PRIMARY KEY (id);


--
-- Name: dynamic_model_field_assignment dynamic_model_field_assignment_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_model_field_assignment
    ADD CONSTRAINT dynamic_model_field_assignment_pkey PRIMARY KEY (id);


--
-- Name: dynamic_model dynamic_model_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_model
    ADD CONSTRAINT dynamic_model_pkey PRIMARY KEY (id);


--
-- Name: dynamic_model_relation_declaration dynamic_model_relation_declaration_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_model_relation_declaration
    ADD CONSTRAINT dynamic_model_relation_declaration_pkey PRIMARY KEY (id);


--
-- Name: dynamic_model_relation dynamic_model_relation_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_model_relation
    ADD CONSTRAINT dynamic_model_relation_pkey PRIMARY KEY (id);


--
-- Name: dynamic_page_config dynamic_page_config_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_page_config
    ADD CONSTRAINT dynamic_page_config_pkey PRIMARY KEY (id);


--
-- Name: dynamic_page dynamic_page_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_page
    ADD CONSTRAINT dynamic_page_pkey PRIMARY KEY (id);


--
-- Name: dynamic_precomputed_value dynamic_precomputed_value_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_precomputed_value
    ADD CONSTRAINT dynamic_precomputed_value_pkey PRIMARY KEY (id);


--
-- Name: dynamic_ref_constraint_library dynamic_ref_constraint_library_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_ref_constraint_library
    ADD CONSTRAINT dynamic_ref_constraint_library_pkey PRIMARY KEY (id);


--
-- Name: dynamic_reference_provider dynamic_reference_provider_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_reference_provider
    ADD CONSTRAINT dynamic_reference_provider_pkey PRIMARY KEY (id);


--
-- Name: dynamic_relation_field_library dynamic_relation_field_library_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_relation_field_library
    ADD CONSTRAINT dynamic_relation_field_library_pkey PRIMARY KEY (id);


--
-- Name: dynamic_template_field_assignment dynamic_template_field_assignment_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_template_field_assignment
    ADD CONSTRAINT dynamic_template_field_assignment_pkey PRIMARY KEY (id);


--
-- Name: dynamic_template dynamic_template_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_template
    ADD CONSTRAINT dynamic_template_pkey PRIMARY KEY (id);


--
-- Name: dynamic_unit dynamic_unit_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_unit
    ADD CONSTRAINT dynamic_unit_pkey PRIMARY KEY (id);


--
-- Name: legacy_business_type_code_map legacy_business_type_code_map_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.legacy_business_type_code_map
    ADD CONSTRAINT legacy_business_type_code_map_pkey PRIMARY KEY (legacy_code);


--
-- Name: legacy_zhgl_field_id_map legacy_zhgl_field_id_map_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.legacy_zhgl_field_id_map
    ADD CONSTRAINT legacy_zhgl_field_id_map_pkey PRIMARY KEY (zhgl_field_id);


--
-- Name: model_crud_form_definition model_crud_form_definition_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.model_crud_form_definition
    ADD CONSTRAINT model_crud_form_definition_pkey PRIMARY KEY (id);


--
-- Name: biz_billing_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_billing_tree_path_idx ON dynamicbusiness.biz_billing USING btree (tree_path);


--
-- Name: biz_customer_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_customer_tree_path_idx ON dynamicbusiness.biz_customer USING btree (tree_path);


--
-- Name: biz_emergency_resource_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_emergency_resource_tree_path_idx ON dynamicbusiness.biz_emergency_resource USING btree (tree_path);


--
-- Name: biz_emergency_team_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_emergency_team_tree_path_idx ON dynamicbusiness.biz_emergency_team USING btree (tree_path);


--
-- Name: biz_emergency_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_emergency_tree_path_idx ON dynamicbusiness.biz_emergency USING btree (tree_path);


--
-- Name: biz_equipment_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_equipment_tree_path_idx ON dynamicbusiness.biz_equipment USING btree (tree_path);


--
-- Name: biz_fault_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_fault_tree_path_idx ON dynamicbusiness.biz_fault USING btree (tree_path);


--
-- Name: biz_inspection_item_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_inspection_item_tree_path_idx ON dynamicbusiness.biz_inspection_item USING btree (tree_path);


--
-- Name: biz_inspection_point_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_inspection_point_tree_path_idx ON dynamicbusiness.biz_inspection_point USING btree (tree_path);


--
-- Name: biz_maintenance_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_maintenance_tree_path_idx ON dynamicbusiness.biz_maintenance USING btree (tree_path);


--
-- Name: biz_patrol_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_patrol_tree_path_idx ON dynamicbusiness.biz_patrol USING btree (tree_path);


--
-- Name: biz_personnel_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_personnel_tree_path_idx ON dynamicbusiness.biz_personnel USING btree (tree_path);


--
-- Name: biz_pipeline_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_pipeline_tree_path_idx ON dynamicbusiness.biz_pipeline USING btree (tree_path);


--
-- Name: biz_region_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_region_tree_path_idx ON dynamicbusiness.biz_region USING btree (tree_path);


--
-- Name: biz_route_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_route_tree_path_idx ON dynamicbusiness.biz_route USING btree (tree_path);


--
-- Name: biz_spare_part_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_spare_part_tree_path_idx ON dynamicbusiness.biz_spare_part USING btree (tree_path);


--
-- Name: biz_task_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX biz_task_tree_path_idx ON dynamicbusiness.biz_task USING btree (tree_path);


--
-- Name: idx_biz_emergency_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_emergency_area ON dynamicbusiness.biz_emergency USING btree (area_id);


--
-- Name: idx_biz_emergency_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_emergency_attrs ON dynamicbusiness.biz_emergency USING gin (attrs);


--
-- Name: idx_biz_emergency_biz_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_emergency_biz_type ON dynamicbusiness.biz_emergency USING btree (business_type_code);


--
-- Name: idx_biz_emergency_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_emergency_deleted ON dynamicbusiness.biz_emergency USING btree (deleted);


--
-- Name: idx_biz_emergency_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_emergency_model ON dynamicbusiness.biz_emergency USING btree (model_id);


--
-- Name: idx_biz_emergency_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_emergency_parent ON dynamicbusiness.biz_emergency USING btree (parent_id);


--
-- Name: idx_biz_emergency_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_emergency_status ON dynamicbusiness.biz_emergency USING btree (status);


--
-- Name: idx_biz_emergency_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_emergency_tenant ON dynamicbusiness.biz_emergency USING btree (tenant_id);


--
-- Name: idx_biz_fault_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_fault_area ON dynamicbusiness.biz_fault USING btree (area_id);


--
-- Name: idx_biz_fault_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_fault_attrs ON dynamicbusiness.biz_fault USING gin (attrs);


--
-- Name: idx_biz_fault_biz_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_fault_biz_type ON dynamicbusiness.biz_fault USING btree (business_type_code);


--
-- Name: idx_biz_fault_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_fault_deleted ON dynamicbusiness.biz_fault USING btree (deleted);


--
-- Name: idx_biz_fault_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_fault_model ON dynamicbusiness.biz_fault USING btree (model_id);


--
-- Name: idx_biz_fault_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_fault_parent ON dynamicbusiness.biz_fault USING btree (parent_id);


--
-- Name: idx_biz_fault_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_fault_status ON dynamicbusiness.biz_fault USING btree (status);


--
-- Name: idx_biz_fault_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_fault_tenant ON dynamicbusiness.biz_fault USING btree (tenant_id);


--
-- Name: idx_biz_inspection_management_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_inspection_management_area ON dynamicbusiness.biz_patrol USING btree (area_id);


--
-- Name: idx_biz_inspection_management_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_inspection_management_attrs ON dynamicbusiness.biz_patrol USING gin (attrs);


--
-- Name: idx_biz_inspection_management_biz_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_inspection_management_biz_type ON dynamicbusiness.biz_patrol USING btree (business_type_code);


--
-- Name: idx_biz_inspection_management_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_inspection_management_deleted ON dynamicbusiness.biz_patrol USING btree (deleted);


--
-- Name: idx_biz_inspection_management_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_inspection_management_model ON dynamicbusiness.biz_patrol USING btree (model_id);


--
-- Name: idx_biz_inspection_management_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_inspection_management_parent ON dynamicbusiness.biz_patrol USING btree (parent_id);


--
-- Name: idx_biz_inspection_management_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_inspection_management_status ON dynamicbusiness.biz_patrol USING btree (status);


--
-- Name: idx_biz_inspection_management_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_inspection_management_tenant ON dynamicbusiness.biz_patrol USING btree (tenant_id);


--
-- Name: idx_biz_jian_cha_nei_rong_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_jian_cha_nei_rong_area ON dynamicbusiness.biz_inspection_item USING btree (area_id);


--
-- Name: idx_biz_jian_cha_nei_rong_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_jian_cha_nei_rong_attrs ON dynamicbusiness.biz_inspection_item USING gin (attrs);


--
-- Name: idx_biz_jian_cha_nei_rong_biz_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_jian_cha_nei_rong_biz_type ON dynamicbusiness.biz_inspection_item USING btree (business_type_code);


--
-- Name: idx_biz_jian_cha_nei_rong_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_jian_cha_nei_rong_deleted ON dynamicbusiness.biz_inspection_item USING btree (deleted);


--
-- Name: idx_biz_jian_cha_nei_rong_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_jian_cha_nei_rong_model ON dynamicbusiness.biz_inspection_item USING btree (model_id);


--
-- Name: idx_biz_jian_cha_nei_rong_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_jian_cha_nei_rong_parent ON dynamicbusiness.biz_inspection_item USING btree (parent_id);


--
-- Name: idx_biz_jian_cha_nei_rong_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_jian_cha_nei_rong_status ON dynamicbusiness.biz_inspection_item USING btree (status);


--
-- Name: idx_biz_jian_cha_nei_rong_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_jian_cha_nei_rong_tenant ON dynamicbusiness.biz_inspection_item USING btree (tenant_id);


--
-- Name: idx_biz_maintenance_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_maintenance_area ON dynamicbusiness.biz_maintenance USING btree (area_id);


--
-- Name: idx_biz_maintenance_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_maintenance_attrs ON dynamicbusiness.biz_maintenance USING gin (attrs);


--
-- Name: idx_biz_maintenance_biz_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_maintenance_biz_type ON dynamicbusiness.biz_maintenance USING btree (business_type_code);


--
-- Name: idx_biz_maintenance_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_maintenance_deleted ON dynamicbusiness.biz_maintenance USING btree (deleted);


--
-- Name: idx_biz_maintenance_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_maintenance_model ON dynamicbusiness.biz_maintenance USING btree (model_id);


--
-- Name: idx_biz_maintenance_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_maintenance_parent ON dynamicbusiness.biz_maintenance USING btree (parent_id);


--
-- Name: idx_biz_maintenance_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_maintenance_status ON dynamicbusiness.biz_maintenance USING btree (status);


--
-- Name: idx_biz_maintenance_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_maintenance_tenant ON dynamicbusiness.biz_maintenance USING btree (tenant_id);


--
-- Name: idx_biz_personnel_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_personnel_area ON dynamicbusiness.biz_personnel USING btree (area_id);


--
-- Name: idx_biz_personnel_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_personnel_attrs ON dynamicbusiness.biz_personnel USING gin (attrs);


--
-- Name: idx_biz_personnel_biz_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_personnel_biz_type ON dynamicbusiness.biz_personnel USING btree (business_type_code);


--
-- Name: idx_biz_personnel_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_personnel_deleted ON dynamicbusiness.biz_personnel USING btree (deleted);


--
-- Name: idx_biz_personnel_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_personnel_model ON dynamicbusiness.biz_personnel USING btree (model_id);


--
-- Name: idx_biz_personnel_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_personnel_parent ON dynamicbusiness.biz_personnel USING btree (parent_id);


--
-- Name: idx_biz_personnel_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_personnel_status ON dynamicbusiness.biz_personnel USING btree (status);


--
-- Name: idx_biz_personnel_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_personnel_tenant ON dynamicbusiness.biz_personnel USING btree (tenant_id);


--
-- Name: idx_biz_pipeline_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_pipeline_area ON dynamicbusiness.biz_pipeline USING btree (area_id);


--
-- Name: idx_biz_pipeline_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_pipeline_attrs ON dynamicbusiness.biz_pipeline USING gin (attrs);


--
-- Name: idx_biz_pipeline_biz_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_pipeline_biz_type ON dynamicbusiness.biz_pipeline USING btree (business_type_code);


--
-- Name: idx_biz_pipeline_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_pipeline_deleted ON dynamicbusiness.biz_pipeline USING btree (deleted);


--
-- Name: idx_biz_pipeline_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_pipeline_model ON dynamicbusiness.biz_pipeline USING btree (model_id);


--
-- Name: idx_biz_pipeline_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_pipeline_parent ON dynamicbusiness.biz_pipeline USING btree (parent_id);


--
-- Name: idx_biz_pipeline_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_pipeline_status ON dynamicbusiness.biz_pipeline USING btree (status);


--
-- Name: idx_biz_pipeline_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_pipeline_tenant ON dynamicbusiness.biz_pipeline USING btree (tenant_id);


--
-- Name: idx_biz_region_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_region_area ON dynamicbusiness.biz_region USING btree (area_id);


--
-- Name: idx_biz_region_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_region_attrs ON dynamicbusiness.biz_region USING gin (attrs);


--
-- Name: idx_biz_region_biz_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_region_biz_type ON dynamicbusiness.biz_region USING btree (business_type_code);


--
-- Name: idx_biz_region_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_region_deleted ON dynamicbusiness.biz_region USING btree (deleted);


--
-- Name: idx_biz_region_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_region_model ON dynamicbusiness.biz_region USING btree (model_id);


--
-- Name: idx_biz_region_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_region_parent ON dynamicbusiness.biz_region USING btree (parent_id);


--
-- Name: idx_biz_region_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_region_status ON dynamicbusiness.biz_region USING btree (status);


--
-- Name: idx_biz_region_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_region_tenant ON dynamicbusiness.biz_region USING btree (tenant_id);


--
-- Name: idx_biz_spare_part_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_spare_part_area ON dynamicbusiness.biz_spare_part USING btree (area_id);


--
-- Name: idx_biz_spare_part_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_spare_part_attrs ON dynamicbusiness.biz_spare_part USING gin (attrs);


--
-- Name: idx_biz_spare_part_biz_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_spare_part_biz_type ON dynamicbusiness.biz_spare_part USING btree (business_type_code);


--
-- Name: idx_biz_spare_part_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_spare_part_deleted ON dynamicbusiness.biz_spare_part USING btree (deleted);


--
-- Name: idx_biz_spare_part_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_spare_part_model ON dynamicbusiness.biz_spare_part USING btree (model_id);


--
-- Name: idx_biz_spare_part_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_spare_part_parent ON dynamicbusiness.biz_spare_part USING btree (parent_id);


--
-- Name: idx_biz_spare_part_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_spare_part_status ON dynamicbusiness.biz_spare_part USING btree (status);


--
-- Name: idx_biz_spare_part_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_spare_part_tenant ON dynamicbusiness.biz_spare_part USING btree (tenant_id);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_area ON dynamicbusiness.biz_task USING btree (area_id);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_attrs ON dynamicbusiness.biz_task USING gin (attrs);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_biz_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_biz_type ON dynamicbusiness.biz_task USING btree (business_type_code);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_deleted ON dynamicbusiness.biz_task USING btree (deleted);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_model ON dynamicbusiness.biz_task USING btree (model_id);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_parent ON dynamicbusiness.biz_task USING btree (parent_id);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_status ON dynamicbusiness.biz_task USING btree (status);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_tenant ON dynamicbusiness.biz_task USING btree (tenant_id);


--
-- Name: idx_business_capability_category; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_business_capability_category ON dynamicbusiness.business_capability USING btree (business_category, tenant_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_category_entity_link_category; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_category_entity_link_category ON dynamicbusiness.dynamic_category_entity_link USING btree (category_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_category_type_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_category_type_code ON dynamicbusiness.dynamic_category USING btree (category_type_code, tenant_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_data_migration_log_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_data_migration_log_type ON dynamicbusiness.dynamic_data_migration_log USING btree (migration_type) WHERE (deleted = false);


--
-- Name: idx_dynamic_dynamic_table_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_dynamic_table_model ON dynamicbusiness.dynamic_dynamic_table USING btree (model_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_entity_business_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_entity_business_type ON dynamicbusiness.dynamic_entity USING btree (business_type_code, tenant_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_entity_category_relation_entity; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_entity_category_relation_entity ON dynamicbusiness.dynamic_entity_category_relation USING btree (entity_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_entity_field_index_entity; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_entity_field_index_entity ON dynamicbusiness.dynamic_entity_field_index USING btree (entity_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_entity_field_index_lookup; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_entity_field_index_lookup ON dynamicbusiness.dynamic_entity_field_index USING btree (model_id, field_code, tenant_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_entity_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_entity_model ON dynamicbusiness.dynamic_entity USING btree (model_id, tenant_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_entity_operation_permission_role; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_entity_operation_permission_role ON dynamicbusiness.dynamic_entity_operation_permission USING btree (role_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_entity_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_entity_parent ON dynamicbusiness.dynamic_entity USING btree (parent_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_entity_relation_source; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_entity_relation_source ON dynamicbusiness.dynamic_entity_relation USING btree (source_entity_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_entity_relation_target; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_entity_relation_target ON dynamicbusiness.dynamic_entity_relation USING btree (target_entity_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_entity_sync_fail_log_entity; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_entity_sync_fail_log_entity ON dynamicbusiness.dynamic_entity_sync_fail_log USING btree (entity_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_group_tenant_type_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_group_tenant_type_parent ON dynamicbusiness.dynamic_group USING btree (tenant_id, group_type, parent_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_model_business_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_model_business_type ON dynamicbusiness.dynamic_model USING btree (business_type_code, tenant_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_model_category_relation_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_model_category_relation_model ON dynamicbusiness.dynamic_model_category_relation USING btree (model_id) WHERE (deleted = false);


--
-- Name: idx_dynamic_model_relation_source; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_model_relation_source ON dynamicbusiness.dynamic_model_relation USING btree (source_model_id) WHERE (deleted = false);


--
-- Name: idx_legacy_zhgl_field_id_map_local; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_legacy_zhgl_field_id_map_local ON dynamicbusiness.legacy_zhgl_field_id_map USING btree (local_field_id);


--
-- Name: uk_business_capability_btc_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_business_capability_btc_tenant ON dynamicbusiness.business_capability USING btree (business_type_code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_capability_projection_key; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_capability_projection_key ON dynamicbusiness.capability_component_projection USING btree (business_type_code, component_code, data_kind, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_business_type_base_field; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_business_type_base_field ON dynamicbusiness.dynamic_business_type_base_field USING btree (business_type_code, field_code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_business_type_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_business_type_code ON dynamicbusiness.dynamic_business_type USING btree (code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_business_type_config; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_business_type_config ON dynamicbusiness.dynamic_business_type_config USING btree (business_type_code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_business_type_relation; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_business_type_relation ON dynamicbusiness.dynamic_business_type_relation USING btree (source_business_type_code, target_business_type_code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_category_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_category_code ON dynamicbusiness.dynamic_category USING btree (code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_category_permission; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_category_permission ON dynamicbusiness.dynamic_category_permission USING btree (role_id, category_id, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_category_type_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_category_type_code ON dynamicbusiness.dynamic_category_type USING btree (category_type_code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_computed_field_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_computed_field_code ON dynamicbusiness.dynamic_computed_field USING btree (model_id, field_code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_dynamic_table_column; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_dynamic_table_column ON dynamicbusiness.dynamic_dynamic_table_column USING btree (dynamic_table_id, field_id, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_entity_access_permission; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_entity_access_permission ON dynamicbusiness.dynamic_entity_access_permission USING btree (role_id, entity_id, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_entity_field_permission; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_entity_field_permission ON dynamicbusiness.dynamic_entity_field_permission USING btree (role_id, model_id, field_id, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_field_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_field_code ON dynamicbusiness.dynamic_field USING btree (code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_group_relation; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_group_relation ON dynamicbusiness.dynamic_group_relation USING btree (tenant_id, group_type, group_id, target_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_mail_template_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_mail_template_code ON dynamicbusiness.dynamic_mail_template USING btree (code) WHERE (deleted = false);


--
-- Name: uk_dynamic_model_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_model_code ON dynamicbusiness.dynamic_model USING btree (code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_model_field_assignment; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_model_field_assignment ON dynamicbusiness.dynamic_model_field_assignment USING btree (model_id, field_id, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_model_relation_declaration; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_model_relation_declaration ON dynamicbusiness.dynamic_model_relation_declaration USING btree (model_id, target_business_type, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_page_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_page_code ON dynamicbusiness.dynamic_page USING btree (page_code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_page_config_page_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_page_config_page_code ON dynamicbusiness.dynamic_page_config USING btree (page_code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_precomputed_value; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_precomputed_value ON dynamicbusiness.dynamic_precomputed_value USING btree (model_id, entity_id, field_code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_ref_constraint_library; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_ref_constraint_library ON dynamicbusiness.dynamic_ref_constraint_library USING btree (business_type_code, ref_target_type, constraint_type, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_reference_provider_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_reference_provider_code ON dynamicbusiness.dynamic_reference_provider USING btree (provider_code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_relation_field_library_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_relation_field_library_code ON dynamicbusiness.dynamic_relation_field_library USING btree (field_code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_template_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_template_code ON dynamicbusiness.dynamic_template USING btree (code, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_template_field_assignment; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_template_field_assignment ON dynamicbusiness.dynamic_template_field_assignment USING btree (template_id, field_id, tenant_id) WHERE (deleted = false);


--
-- Name: uk_dynamic_unit_tenant_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_unit_tenant_code ON dynamicbusiness.dynamic_unit USING btree (tenant_id, code) WHERE (deleted = false);


--
-- Name: uk_model_crud_form_definition_key; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_model_crud_form_definition_key ON dynamicbusiness.model_crud_form_definition USING btree (business_type_code, model_id, tenant_id) WHERE (deleted = false);


--
-- PostgreSQL database dump complete
--

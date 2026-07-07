-- ============================================================================
-- 系统共用 · 01 建表（与 Flyway V1 同源）
-- Generated: 2026-07-08 by scripts/regenerate-v1-from-db.py
-- ============================================================================

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
-- Name: base_field_library_name_alias; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.base_field_library_name_alias (
    base_field_name character varying(200) NOT NULL,
    library_field_code character varying(64) NOT NULL
);

--
-- Name: business_capability_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.business_capability_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: business_capability; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.business_capability (
    id bigint DEFAULT nextval('dynamicbusiness.business_capability_id_seq'::regclass) NOT NULL,
    entity_type_code character varying(64) NOT NULL,
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

COMMENT ON TABLE dynamicbusiness.business_capability IS '业务能力全集表（按 entityTypeCode 索引）';

--
-- Name: COLUMN business_capability.business_category; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.business_capability.business_category IS '业务分类：dynamic（动态业务）/ system（系统业务）';

--
-- Name: capability_component_projection_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.capability_component_projection_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: capability_component_projection; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.capability_component_projection (
    id bigint DEFAULT nextval('dynamicbusiness.capability_component_projection_id_seq'::regclass) NOT NULL,
    entity_type_code character varying(64) NOT NULL,
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

COMMENT ON TABLE dynamicbusiness.capability_component_projection IS '组件能力投影表（按 entityTypeCode + componentCode 索引）';

--
-- Name: COLUMN capability_component_projection.data_kind; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.capability_component_projection.data_kind IS '数据种类：model（模型目录）/ entity（实例数据）';

--
-- Name: dynamic_business_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_business_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_business; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_business (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_business_id_seq'::regclass) NOT NULL,
    code character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    parent_id bigint,
    node_kind character varying(32) DEFAULT 'LEAF'::character varying NOT NULL,
    description character varying(500),
    icon character varying(128),
    alias character varying(128),
    sort integer DEFAULT 0 NOT NULL,
    status character varying(32) DEFAULT 'active'::character varying NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);

--
-- Name: TABLE dynamic_business; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.dynamic_business IS '门户业务注册（与实体类型分层）';

--
-- Name: COLUMN dynamic_business.code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.dynamic_business.code IS '业务编码 businessCode';

--
-- Name: COLUMN dynamic_business.node_kind; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.dynamic_business.node_kind IS 'GROUP=仅分组；LEAF=可有业务入口';

--
-- Name: dynamic_business_entry_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_business_entry_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_business_entry; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_business_entry (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_business_entry_id_seq'::regclass) NOT NULL,
    business_id bigint NOT NULL,
    code character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    entry_type character varying(32) NOT NULL,
    entity_type_code character varying(64),
    scope_config jsonb,
    page_config_id bigint,
    sort integer DEFAULT 0 NOT NULL,
    status character varying(32) DEFAULT 'active'::character varying NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);

--
-- Name: TABLE dynamic_business_entry; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.dynamic_business_entry IS '业务入口：台账管理、scoped 列表等';

--
-- Name: COLUMN dynamic_business_entry.entity_type_code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.dynamic_business_entry.entity_type_code IS '可选绑定的实体类型编码 entityTypeCode';

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
-- Name: dynamic_category; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_category (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_category_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_category_entity_link_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_category_entity_link_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_category_entity_link; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_category_entity_link (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_category_entity_link_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_category_permission_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_category_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_category_permission; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_category_permission (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_category_permission_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_category_type_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_category_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_category_type; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_category_type (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_category_type_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_computed_field_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_computed_field_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_computed_field; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_computed_field (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_computed_field_id_seq'::regclass) NOT NULL,
    model_id bigint NOT NULL,
    field_name character varying(64) NOT NULL,
    field_code character varying(64) NOT NULL,
    compute_type character varying(32) NOT NULL,
    aggregate_function character varying(16),
    target_entity_type character varying(64),
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
-- Name: dynamic_data_migration_log_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_data_migration_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_data_migration_log; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_data_migration_log (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_data_migration_log_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_dynamic_sql_audit_log_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_dynamic_sql_audit_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_dynamic_sql_audit_log; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_dynamic_sql_audit_log (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_dynamic_sql_audit_log_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_dynamic_table_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_dynamic_table_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_dynamic_table; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_dynamic_table (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_dynamic_table_id_seq'::regclass) NOT NULL,
    model_id bigint NOT NULL,
    entity_type_code character varying(64),
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
-- Name: dynamic_dynamic_table_audit_log_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_dynamic_table_audit_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_dynamic_table_audit_log; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_dynamic_table_audit_log (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_dynamic_table_audit_log_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_dynamic_table_column_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_dynamic_table_column_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_dynamic_table_column; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_dynamic_table_column (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_dynamic_table_column_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_entity_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_entity; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_id_seq'::regclass) NOT NULL,
    entity_type_code character varying(64) NOT NULL,
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
-- Name: dynamic_entity_access_permission_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_access_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_entity_access_permission; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_access_permission (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_access_permission_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_entity_category_relation_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_category_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_entity_category_relation; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_category_relation (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_category_relation_id_seq'::regclass) NOT NULL,
    entity_id bigint NOT NULL,
    category_id bigint NOT NULL,
    entity_type_code character varying(64),
    sort integer DEFAULT 0 NOT NULL,
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
-- Name: dynamic_entity_field_index; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_field_index (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_field_index_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_entity_field_permission_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_field_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_entity_field_permission; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_field_permission (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_field_permission_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_entity_operation_permission_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_operation_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_entity_operation_permission; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_operation_permission (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_operation_permission_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_entity_relation_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_entity_relation; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_relation (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_relation_id_seq'::regclass) NOT NULL,
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
    source_entity_type_code character varying(64),
    target_entity_type_code character varying(64),
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
-- Name: dynamic_entity_sync_fail_log; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_sync_fail_log (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_sync_fail_log_id_seq'::regclass) NOT NULL,
    entity_id bigint NOT NULL,
    entity_type_code character varying(64) NOT NULL,
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
-- Name: dynamic_entity_type_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_entity_type; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_type (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_type_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_entity_type_base_field_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_type_base_field_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_entity_type_base_field; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_type_base_field (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_type_base_field_id_seq'::regclass) NOT NULL,
    entity_type_code character varying(64) NOT NULL,
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
-- Name: dynamic_entity_type_config_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_type_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_entity_type_config; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_type_config (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_type_config_id_seq'::regclass) NOT NULL,
    entity_type_code character varying(64) NOT NULL,
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
-- Name: dynamic_entity_type_relation_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_entity_type_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_entity_type_relation; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_entity_type_relation (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_entity_type_relation_id_seq'::regclass) NOT NULL,
    source_entity_type_code character varying(64) NOT NULL,
    target_entity_type_code character varying(64) NOT NULL,
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
-- Name: dynamic_field_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_field_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_field; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_field (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_field_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_group_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_group; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_group (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_group_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_group_relation_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_group_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_group_relation; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_group_relation (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_group_relation_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_mail_account_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_mail_account_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_mail_account; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_mail_account (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_mail_account_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_mail_log_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_mail_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_mail_log; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_mail_log (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_mail_log_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_mail_template_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_mail_template_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_mail_template; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_mail_template (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_mail_template_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_model_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_model_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_model; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_model (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_model_id_seq'::regclass) NOT NULL,
    code character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    entity_type_code character varying(64) NOT NULL,
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
-- Name: dynamic_model_category_relation_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_model_category_relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_model_category_relation; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_model_category_relation (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_model_category_relation_id_seq'::regclass) NOT NULL,
    model_id bigint NOT NULL,
    category_id bigint NOT NULL,
    entity_type_code character varying(64),
    sort integer DEFAULT 0 NOT NULL,
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
-- Name: dynamic_model_field_assignment; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_model_field_assignment (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_model_field_assignment_id_seq'::regclass) NOT NULL,
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
    target_entity_type character varying(64),
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);

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
-- Name: dynamic_model_relation; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_model_relation (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_model_relation_id_seq'::regclass) NOT NULL,
    entity_type_relation_id bigint,
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
-- Name: dynamic_model_relation_declaration_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_model_relation_declaration_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_model_relation_declaration; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_model_relation_declaration (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_model_relation_declaration_id_seq'::regclass) NOT NULL,
    model_id bigint NOT NULL,
    target_entity_type character varying(64) NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL
);

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
-- Name: dynamic_page; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_page (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_page_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_page_config_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_page_config_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_page_config; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_page_config (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_page_config_id_seq'::regclass) NOT NULL,
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
    tenant_id bigint DEFAULT 0 NOT NULL,
    business_id bigint
);

--
-- Name: COLUMN dynamic_page_config.business_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.dynamic_page_config.business_id IS '所属门户业务 id（dynamic_business.id）';

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
-- Name: dynamic_precomputed_value; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_precomputed_value (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_precomputed_value_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_ref_constraint_library_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_ref_constraint_library_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_ref_constraint_library; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_ref_constraint_library (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_ref_constraint_library_id_seq'::regclass) NOT NULL,
    entity_type_code character varying(64) NOT NULL,
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
-- Name: dynamic_reference_provider_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_reference_provider_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_reference_provider; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_reference_provider (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_reference_provider_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_relation_field_library_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_relation_field_library_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_relation_field_library; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_relation_field_library (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_relation_field_library_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_template_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_template_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_template; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_template (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_template_id_seq'::regclass) NOT NULL,
    code character varying(64) NOT NULL,
    name character varying(200) NOT NULL,
    entity_type_code character varying(64) NOT NULL,
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
-- Name: dynamic_template_field_assignment_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_template_field_assignment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_template_field_assignment; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_template_field_assignment (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_template_field_assignment_id_seq'::regclass) NOT NULL,
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
-- Name: dynamic_unit_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.dynamic_unit_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: dynamic_unit; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.dynamic_unit (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_unit_id_seq'::regclass) NOT NULL,
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
-- Name: ent_billing_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_billing_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_billing; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_billing (
    id bigint DEFAULT nextval('dynamicbusiness.ent_billing_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: ent_customer_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_customer_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_customer; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_customer (
    id bigint DEFAULT nextval('dynamicbusiness.ent_customer_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: ent_emergency_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_emergency_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_emergency; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_emergency (
    id bigint DEFAULT nextval('dynamicbusiness.ent_emergency_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: ent_emergency_resource_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_emergency_resource_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_emergency_resource; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_emergency_resource (
    id bigint DEFAULT nextval('dynamicbusiness.ent_emergency_resource_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: ent_emergency_team_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_emergency_team_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_emergency_team; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_emergency_team (
    id bigint DEFAULT nextval('dynamicbusiness.ent_emergency_team_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: ent_equipment; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_equipment (
    id bigint NOT NULL,
    entity_type_code character varying DEFAULT 'equipment'::character varying NOT NULL,
    model_id bigint NOT NULL,
    name character varying NOT NULL,
    custom_fields jsonb,
    parent_id bigint,
    tree_path character varying,
    region_id bigint,
    creator character varying DEFAULT 'system'::character varying NOT NULL,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tenant_id bigint DEFAULT 0,
    guid character varying,
    device_type numeric,
    device_code character varying,
    coordinate_3d character varying,
    coordinate_gis character varying,
    code character varying NOT NULL,
    sort integer DEFAULT 0,
    status integer DEFAULT 1,
    facility_id bigint,
    zone_id bigint
);

--
-- Name: COLUMN ent_equipment.status; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_equipment.status IS '状态（1-启用，0-禁用）';

--
-- Name: ent_facility_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_facility_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_facility; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_facility (
    id bigint DEFAULT nextval('dynamicbusiness.ent_facility_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64) DEFAULT 'facility'::character varying,
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100) NOT NULL,
    status integer DEFAULT 1,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0,
    region_id bigint,
    address character varying(500),
    longitude numeric(12,8),
    latitude numeric(12,8),
    facility_type character varying(100)
);

--
-- Name: TABLE ent_facility; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.ent_facility IS '设施点（站场/厂区等）；entityTypeCode=facility';

--
-- Name: ent_fault_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_fault_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_fault; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_fault (
    id bigint DEFAULT nextval('dynamicbusiness.ent_fault_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: TABLE ent_fault; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.ent_fault IS 'fault????????????';

--
-- Name: COLUMN ent_fault.entity_type_code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_fault.entity_type_code IS '?????????';

--
-- Name: COLUMN ent_fault.model_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_fault.model_id IS '????Model ID';

--
-- Name: COLUMN ent_fault.name; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_fault.name IS '???';

--
-- Name: COLUMN ent_fault.code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_fault.code IS '???';

--
-- Name: ent_inspection_item_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_inspection_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_inspection_item; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_inspection_item (
    id bigint DEFAULT nextval('dynamicbusiness.ent_inspection_item_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: ent_inspection_point_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_inspection_point_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_inspection_point; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_inspection_point (
    id bigint DEFAULT nextval('dynamicbusiness.ent_inspection_point_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: COLUMN ent_inspection_point.rel_region; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_inspection_point.rel_region IS '????????????????????: REL_REGION';

--
-- Name: ent_maintenance_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_maintenance_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_maintenance; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_maintenance (
    id bigint DEFAULT nextval('dynamicbusiness.ent_maintenance_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
    order_no character varying(255),
    maintenance_type character varying(100),
    plan_time timestamp without time zone,
    executor character varying(255),
    order_status character varying(100),
    tree_path character varying(500),
    sort integer DEFAULT 0
);

--
-- Name: ent_patrol_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_patrol_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_patrol; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_patrol (
    id bigint DEFAULT nextval('dynamicbusiness.ent_patrol_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: TABLE ent_patrol; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.ent_patrol IS '??????';

--
-- Name: COLUMN ent_patrol.entity_type_code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_patrol.entity_type_code IS '?????????';

--
-- Name: COLUMN ent_patrol.model_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_patrol.model_id IS '????Model ID';

--
-- Name: COLUMN ent_patrol.name; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_patrol.name IS '???';

--
-- Name: COLUMN ent_patrol.code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_patrol.code IS '???';

--
-- Name: ent_personnel_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_personnel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_personnel; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_personnel (
    id bigint DEFAULT nextval('dynamicbusiness.ent_personnel_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: ent_pipeline_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_pipeline_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_pipeline; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_pipeline (
    id bigint DEFAULT nextval('dynamicbusiness.ent_pipeline_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: TABLE ent_pipeline; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.ent_pipeline IS 'pipeline????????????';

--
-- Name: COLUMN ent_pipeline.entity_type_code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_pipeline.entity_type_code IS '?????????';

--
-- Name: COLUMN ent_pipeline.model_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_pipeline.model_id IS '????Model ID';

--
-- Name: COLUMN ent_pipeline.name; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_pipeline.name IS '???';

--
-- Name: COLUMN ent_pipeline.code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_pipeline.code IS '???';

--
-- Name: ent_region_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_region_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_region; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_region (
    id bigint DEFAULT nextval('dynamicbusiness.ent_region_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
    region_code character varying(255),
    region_name character varying(255),
    region_type character varying(100),
    rel_equipment character varying(255),
    rel_dian_wei character varying(255),
    tree_path character varying(500),
    sort integer DEFAULT 0,
    boundary_geojson jsonb,
    boundary_crs character varying(32) DEFAULT 'EPSG:4326'::character varying,
    boundary_status character varying(32) DEFAULT 'none'::character varying,
    min_height_m numeric(10,3),
    max_height_m numeric(10,3),
    centroid_lng numeric(12,8),
    centroid_lat numeric(12,8),
    bbox jsonb,
    region_level character varying(32),
    admin_code character varying(64)
);

--
-- Name: TABLE ent_region; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.ent_region IS '???????????????????ategory isEntity??????????????????';

--
-- Name: COLUMN ent_region.entity_type_code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.entity_type_code IS '?????????';

--
-- Name: COLUMN ent_region.model_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.model_id IS '????Model ID';

--
-- Name: COLUMN ent_region.name; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.name IS '???';

--
-- Name: COLUMN ent_region.code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.code IS '???';

--
-- Name: COLUMN ent_region.status; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.status IS '?????1-?????-???';

--
-- Name: COLUMN ent_region.area_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.area_id IS '???????ID';

--
-- Name: COLUMN ent_region.parent_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.parent_id IS '?????ID?? ?????????';

--
-- Name: COLUMN ent_region.boundary_geojson; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.boundary_geojson IS '区域边界 GeoJSON（WGS84），Polygon/MultiPolygon';

--
-- Name: COLUMN ent_region.boundary_crs; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.boundary_crs IS '边界坐标参考系，默认 EPSG:4326';

--
-- Name: COLUMN ent_region.boundary_status; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.boundary_status IS '边界状态：none | draft | published';

--
-- Name: COLUMN ent_region.min_height_m; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.min_height_m IS '区域最小高度（米，可选）';

--
-- Name: COLUMN ent_region.max_height_m; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.max_height_m IS '区域最大高度（米，可选）';

--
-- Name: COLUMN ent_region.centroid_lng; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.centroid_lng IS '边界质心经度（可派生缓存）';

--
-- Name: COLUMN ent_region.centroid_lat; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.centroid_lat IS '边界质心纬度（可派生缓存）';

--
-- Name: COLUMN ent_region.bbox; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_region.bbox IS '边界外包框 [minLng,minLat,maxLng,maxLat]（可派生缓存）';

--
-- Name: ent_route_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_route_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_route; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_route (
    id bigint DEFAULT nextval('dynamicbusiness.ent_route_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: ent_spare_part_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_spare_part_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_spare_part; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_spare_part (
    id bigint DEFAULT nextval('dynamicbusiness.ent_spare_part_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: TABLE ent_spare_part; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.ent_spare_part IS 'spare_part????????????';

--
-- Name: COLUMN ent_spare_part.entity_type_code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_spare_part.entity_type_code IS '?????????';

--
-- Name: COLUMN ent_spare_part.model_id; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_spare_part.model_id IS '????Model ID';

--
-- Name: COLUMN ent_spare_part.name; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_spare_part.name IS '???';

--
-- Name: COLUMN ent_spare_part.code; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON COLUMN dynamicbusiness.ent_spare_part.code IS '???';

--
-- Name: ent_task_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_task_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_task; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_task (
    id bigint DEFAULT nextval('dynamicbusiness.ent_task_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64),
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
-- Name: ent_zone_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.ent_zone_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: ent_zone; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.ent_zone (
    id bigint DEFAULT nextval('dynamicbusiness.ent_zone_id_seq'::regclass) NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    entity_type_code character varying(64) DEFAULT 'zone'::character varying,
    model_id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(100) NOT NULL,
    status integer DEFAULT 1,
    parent_id bigint DEFAULT 0,
    attrs jsonb DEFAULT '{}'::jsonb,
    custom_fields text,
    creator character varying(64),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updater character varying(64),
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    tree_path character varying(500),
    sort integer DEFAULT 0,
    facility_id bigint NOT NULL,
    zone_type character varying(100),
    description character varying(500),
    boundary_geojson jsonb,
    boundary_crs character varying(32) DEFAULT 'EPSG:4326'::character varying,
    boundary_status character varying(32) DEFAULT 'none'::character varying,
    min_height_m numeric(10,3),
    max_height_m numeric(10,3),
    centroid_lng numeric(12,8),
    centroid_lat numeric(12,8)
);

--
-- Name: TABLE ent_zone; Type: COMMENT; Schema: dynamicbusiness; Owner: -
--

COMMENT ON TABLE dynamicbusiness.ent_zone IS '站内空间分区（罐组等）；entityTypeCode=zone';

--
-- Name: model_crud_form_definition_id_seq; Type: SEQUENCE; Schema: dynamicbusiness; Owner: -
--

CREATE SEQUENCE dynamicbusiness.model_crud_form_definition_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

--
-- Name: model_crud_form_definition; Type: TABLE; Schema: dynamicbusiness; Owner: -
--

CREATE TABLE dynamicbusiness.model_crud_form_definition (
    id bigint DEFAULT nextval('dynamicbusiness.model_crud_form_definition_id_seq'::regclass) NOT NULL,
    entity_type_code character varying(64) NOT NULL,
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

COMMENT ON TABLE dynamicbusiness.model_crud_form_definition IS '模型 CRUD 表单定义表（按 entityTypeCode + modelId 索引）';

--
-- Name: base_field_library_name_alias base_field_library_name_alias_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.base_field_library_name_alias
    ADD CONSTRAINT base_field_library_name_alias_pkey PRIMARY KEY (base_field_name);

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
-- Name: dynamic_business_entry dynamic_business_entry_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_business_entry
    ADD CONSTRAINT dynamic_business_entry_pkey PRIMARY KEY (id);

--
-- Name: dynamic_business dynamic_business_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_business
    ADD CONSTRAINT dynamic_business_pkey PRIMARY KEY (id);

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
-- Name: dynamic_entity_type_base_field dynamic_entity_type_base_field_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_type_base_field
    ADD CONSTRAINT dynamic_entity_type_base_field_pkey PRIMARY KEY (id);

--
-- Name: dynamic_entity_type_config dynamic_entity_type_config_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_type_config
    ADD CONSTRAINT dynamic_entity_type_config_pkey PRIMARY KEY (id);

--
-- Name: dynamic_entity_type dynamic_entity_type_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_type
    ADD CONSTRAINT dynamic_entity_type_pkey PRIMARY KEY (id);

--
-- Name: dynamic_entity_type_relation dynamic_entity_type_relation_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.dynamic_entity_type_relation
    ADD CONSTRAINT dynamic_entity_type_relation_pkey PRIMARY KEY (id);

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
-- Name: ent_billing ent_billing_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_billing
    ADD CONSTRAINT ent_billing_pkey PRIMARY KEY (id);

--
-- Name: ent_customer ent_customer_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_customer
    ADD CONSTRAINT ent_customer_pkey PRIMARY KEY (id);

--
-- Name: ent_emergency ent_emergency_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_emergency
    ADD CONSTRAINT ent_emergency_pkey PRIMARY KEY (id);

--
-- Name: ent_emergency_resource ent_emergency_resource_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_emergency_resource
    ADD CONSTRAINT ent_emergency_resource_pkey PRIMARY KEY (id);

--
-- Name: ent_emergency_team ent_emergency_team_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_emergency_team
    ADD CONSTRAINT ent_emergency_team_pkey PRIMARY KEY (id);

--
-- Name: ent_equipment ent_equipment_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_equipment
    ADD CONSTRAINT ent_equipment_pkey PRIMARY KEY (id);

--
-- Name: ent_facility ent_facility_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_facility
    ADD CONSTRAINT ent_facility_pkey PRIMARY KEY (id);

--
-- Name: ent_fault ent_fault_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_fault
    ADD CONSTRAINT ent_fault_pkey PRIMARY KEY (id);

--
-- Name: ent_inspection_item ent_inspection_item_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_inspection_item
    ADD CONSTRAINT ent_inspection_item_pkey PRIMARY KEY (id);

--
-- Name: ent_inspection_point ent_inspection_point_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_inspection_point
    ADD CONSTRAINT ent_inspection_point_pkey PRIMARY KEY (id);

--
-- Name: ent_maintenance ent_maintenance_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_maintenance
    ADD CONSTRAINT ent_maintenance_pkey PRIMARY KEY (id);

--
-- Name: ent_patrol ent_patrol_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_patrol
    ADD CONSTRAINT ent_patrol_pkey PRIMARY KEY (id);

--
-- Name: ent_personnel ent_personnel_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_personnel
    ADD CONSTRAINT ent_personnel_pkey PRIMARY KEY (id);

--
-- Name: ent_pipeline ent_pipeline_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_pipeline
    ADD CONSTRAINT ent_pipeline_pkey PRIMARY KEY (id);

--
-- Name: ent_region ent_region_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_region
    ADD CONSTRAINT ent_region_pkey PRIMARY KEY (id);

--
-- Name: ent_route ent_route_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_route
    ADD CONSTRAINT ent_route_pkey PRIMARY KEY (id);

--
-- Name: ent_spare_part ent_spare_part_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_spare_part
    ADD CONSTRAINT ent_spare_part_pkey PRIMARY KEY (id);

--
-- Name: ent_task ent_task_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_task
    ADD CONSTRAINT ent_task_pkey PRIMARY KEY (id);

--
-- Name: ent_zone ent_zone_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.ent_zone
    ADD CONSTRAINT ent_zone_pkey PRIMARY KEY (id);

--
-- Name: model_crud_form_definition model_crud_form_definition_pkey; Type: CONSTRAINT; Schema: dynamicbusiness; Owner: -
--

ALTER TABLE ONLY dynamicbusiness.model_crud_form_definition
    ADD CONSTRAINT model_crud_form_definition_pkey PRIMARY KEY (id);

--
-- Name: ent_billing_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_billing_tree_path_idx ON dynamicbusiness.ent_billing USING btree (tree_path);

--
-- Name: ent_customer_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_customer_tree_path_idx ON dynamicbusiness.ent_customer USING btree (tree_path);

--
-- Name: ent_emergency_resource_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_emergency_resource_tree_path_idx ON dynamicbusiness.ent_emergency_resource USING btree (tree_path);

--
-- Name: ent_emergency_team_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_emergency_team_tree_path_idx ON dynamicbusiness.ent_emergency_team USING btree (tree_path);

--
-- Name: ent_emergency_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_emergency_tree_path_idx ON dynamicbusiness.ent_emergency USING btree (tree_path);

--
-- Name: ent_equipment_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_equipment_tree_path_idx ON dynamicbusiness.ent_equipment USING btree (tree_path);

--
-- Name: ent_fault_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_fault_tree_path_idx ON dynamicbusiness.ent_fault USING btree (tree_path);

--
-- Name: ent_inspection_item_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_inspection_item_tree_path_idx ON dynamicbusiness.ent_inspection_item USING btree (tree_path);

--
-- Name: ent_inspection_point_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_inspection_point_tree_path_idx ON dynamicbusiness.ent_inspection_point USING btree (tree_path);

--
-- Name: ent_maintenance_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_maintenance_tree_path_idx ON dynamicbusiness.ent_maintenance USING btree (tree_path);

--
-- Name: ent_patrol_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_patrol_tree_path_idx ON dynamicbusiness.ent_patrol USING btree (tree_path);

--
-- Name: ent_personnel_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_personnel_tree_path_idx ON dynamicbusiness.ent_personnel USING btree (tree_path);

--
-- Name: ent_pipeline_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_pipeline_tree_path_idx ON dynamicbusiness.ent_pipeline USING btree (tree_path);

--
-- Name: ent_region_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_region_tree_path_idx ON dynamicbusiness.ent_region USING btree (tree_path);

--
-- Name: ent_route_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_route_tree_path_idx ON dynamicbusiness.ent_route USING btree (tree_path);

--
-- Name: ent_spare_part_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_spare_part_tree_path_idx ON dynamicbusiness.ent_spare_part USING btree (tree_path);

--
-- Name: ent_task_tree_path_idx; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX ent_task_tree_path_idx ON dynamicbusiness.ent_task USING btree (tree_path);

--
-- Name: idx_business_capability_category; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_business_capability_category ON dynamicbusiness.business_capability USING btree (business_category, tenant_id) WHERE (deleted = false);

--
-- Name: idx_dynamic_business_entry_business; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_business_entry_business ON dynamicbusiness.dynamic_business_entry USING btree (business_id, sort) WHERE (deleted = false);

--
-- Name: idx_dynamic_business_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_business_parent ON dynamicbusiness.dynamic_business USING btree (parent_id, sort) WHERE (deleted = false);

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

CREATE INDEX idx_dynamic_entity_business_type ON dynamicbusiness.dynamic_entity USING btree (entity_type_code, tenant_id) WHERE (deleted = false);

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

CREATE INDEX idx_dynamic_model_business_type ON dynamicbusiness.dynamic_model USING btree (entity_type_code, tenant_id) WHERE (deleted = false);

--
-- Name: idx_dynamic_model_category_relation_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_model_category_relation_model ON dynamicbusiness.dynamic_model_category_relation USING btree (model_id) WHERE (deleted = false);

--
-- Name: idx_dynamic_model_relation_source; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_model_relation_source ON dynamicbusiness.dynamic_model_relation USING btree (source_model_id) WHERE (deleted = false);

--
-- Name: idx_dynamic_page_config_business_id; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_dynamic_page_config_business_id ON dynamicbusiness.dynamic_page_config USING btree (business_id) WHERE (deleted = false);

--
-- Name: idx_ent_emergency_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_emergency_area ON dynamicbusiness.ent_emergency USING btree (area_id);

--
-- Name: idx_ent_emergency_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_emergency_attrs ON dynamicbusiness.ent_emergency USING gin (attrs);

--
-- Name: idx_ent_emergency_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_emergency_deleted ON dynamicbusiness.ent_emergency USING btree (deleted);

--
-- Name: idx_ent_emergency_ent_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_emergency_ent_type ON dynamicbusiness.ent_emergency USING btree (entity_type_code);

--
-- Name: idx_ent_emergency_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_emergency_model ON dynamicbusiness.ent_emergency USING btree (model_id);

--
-- Name: idx_ent_emergency_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_emergency_parent ON dynamicbusiness.ent_emergency USING btree (parent_id);

--
-- Name: idx_ent_emergency_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_emergency_status ON dynamicbusiness.ent_emergency USING btree (status);

--
-- Name: idx_ent_emergency_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_emergency_tenant ON dynamicbusiness.ent_emergency USING btree (tenant_id);

--
-- Name: idx_ent_equipment_facility; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_equipment_facility ON dynamicbusiness.ent_equipment USING btree (facility_id) WHERE (deleted = false);

--
-- Name: idx_ent_equipment_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_equipment_status ON dynamicbusiness.ent_equipment USING btree (status);

--
-- Name: idx_ent_equipment_zone; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_equipment_zone ON dynamicbusiness.ent_equipment USING btree (zone_id) WHERE (deleted = false);

--
-- Name: idx_ent_facility_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_facility_code ON dynamicbusiness.ent_facility USING btree (code, tenant_id) WHERE (deleted = false);

--
-- Name: idx_ent_facility_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_facility_model ON dynamicbusiness.ent_facility USING btree (model_id) WHERE (deleted = false);

--
-- Name: idx_ent_facility_region; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_facility_region ON dynamicbusiness.ent_facility USING btree (region_id) WHERE (deleted = false);

--
-- Name: idx_ent_facility_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_facility_tenant ON dynamicbusiness.ent_facility USING btree (tenant_id) WHERE (deleted = false);

--
-- Name: idx_ent_fault_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_fault_area ON dynamicbusiness.ent_fault USING btree (area_id);

--
-- Name: idx_ent_fault_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_fault_attrs ON dynamicbusiness.ent_fault USING gin (attrs);

--
-- Name: idx_ent_fault_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_fault_deleted ON dynamicbusiness.ent_fault USING btree (deleted);

--
-- Name: idx_ent_fault_ent_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_fault_ent_type ON dynamicbusiness.ent_fault USING btree (entity_type_code);

--
-- Name: idx_ent_fault_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_fault_model ON dynamicbusiness.ent_fault USING btree (model_id);

--
-- Name: idx_ent_fault_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_fault_parent ON dynamicbusiness.ent_fault USING btree (parent_id);

--
-- Name: idx_ent_fault_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_fault_status ON dynamicbusiness.ent_fault USING btree (status);

--
-- Name: idx_ent_fault_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_fault_tenant ON dynamicbusiness.ent_fault USING btree (tenant_id);

--
-- Name: idx_ent_inspection_item_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_inspection_item_area ON dynamicbusiness.ent_inspection_item USING btree (area_id);

--
-- Name: idx_ent_inspection_item_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_inspection_item_attrs ON dynamicbusiness.ent_inspection_item USING gin (attrs);

--
-- Name: idx_ent_inspection_item_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_inspection_item_deleted ON dynamicbusiness.ent_inspection_item USING btree (deleted);

--
-- Name: idx_ent_inspection_item_ent_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_inspection_item_ent_type ON dynamicbusiness.ent_inspection_item USING btree (entity_type_code);

--
-- Name: idx_ent_inspection_item_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_inspection_item_model ON dynamicbusiness.ent_inspection_item USING btree (model_id);

--
-- Name: idx_ent_inspection_item_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_inspection_item_parent ON dynamicbusiness.ent_inspection_item USING btree (parent_id);

--
-- Name: idx_ent_inspection_item_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_inspection_item_status ON dynamicbusiness.ent_inspection_item USING btree (status);

--
-- Name: idx_ent_inspection_item_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_inspection_item_tenant ON dynamicbusiness.ent_inspection_item USING btree (tenant_id);

--
-- Name: idx_ent_maintenance_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_maintenance_area ON dynamicbusiness.ent_maintenance USING btree (area_id);

--
-- Name: idx_ent_maintenance_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_maintenance_attrs ON dynamicbusiness.ent_maintenance USING gin (attrs);

--
-- Name: idx_ent_maintenance_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_maintenance_deleted ON dynamicbusiness.ent_maintenance USING btree (deleted);

--
-- Name: idx_ent_maintenance_ent_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_maintenance_ent_type ON dynamicbusiness.ent_maintenance USING btree (entity_type_code);

--
-- Name: idx_ent_maintenance_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_maintenance_model ON dynamicbusiness.ent_maintenance USING btree (model_id);

--
-- Name: idx_ent_maintenance_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_maintenance_parent ON dynamicbusiness.ent_maintenance USING btree (parent_id);

--
-- Name: idx_ent_maintenance_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_maintenance_status ON dynamicbusiness.ent_maintenance USING btree (status);

--
-- Name: idx_ent_maintenance_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_maintenance_tenant ON dynamicbusiness.ent_maintenance USING btree (tenant_id);

--
-- Name: idx_ent_patrol_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_patrol_area ON dynamicbusiness.ent_patrol USING btree (area_id);

--
-- Name: idx_ent_patrol_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_patrol_attrs ON dynamicbusiness.ent_patrol USING gin (attrs);

--
-- Name: idx_ent_patrol_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_patrol_deleted ON dynamicbusiness.ent_patrol USING btree (deleted);

--
-- Name: idx_ent_patrol_ent_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_patrol_ent_type ON dynamicbusiness.ent_patrol USING btree (entity_type_code);

--
-- Name: idx_ent_patrol_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_patrol_model ON dynamicbusiness.ent_patrol USING btree (model_id);

--
-- Name: idx_ent_patrol_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_patrol_parent ON dynamicbusiness.ent_patrol USING btree (parent_id);

--
-- Name: idx_ent_patrol_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_patrol_status ON dynamicbusiness.ent_patrol USING btree (status);

--
-- Name: idx_ent_patrol_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_patrol_tenant ON dynamicbusiness.ent_patrol USING btree (tenant_id);

--
-- Name: idx_ent_personnel_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_personnel_area ON dynamicbusiness.ent_personnel USING btree (area_id);

--
-- Name: idx_ent_personnel_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_personnel_attrs ON dynamicbusiness.ent_personnel USING gin (attrs);

--
-- Name: idx_ent_personnel_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_personnel_deleted ON dynamicbusiness.ent_personnel USING btree (deleted);

--
-- Name: idx_ent_personnel_ent_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_personnel_ent_type ON dynamicbusiness.ent_personnel USING btree (entity_type_code);

--
-- Name: idx_ent_personnel_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_personnel_model ON dynamicbusiness.ent_personnel USING btree (model_id);

--
-- Name: idx_ent_personnel_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_personnel_parent ON dynamicbusiness.ent_personnel USING btree (parent_id);

--
-- Name: idx_ent_personnel_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_personnel_status ON dynamicbusiness.ent_personnel USING btree (status);

--
-- Name: idx_ent_personnel_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_personnel_tenant ON dynamicbusiness.ent_personnel USING btree (tenant_id);

--
-- Name: idx_ent_pipeline_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_pipeline_area ON dynamicbusiness.ent_pipeline USING btree (area_id);

--
-- Name: idx_ent_pipeline_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_pipeline_attrs ON dynamicbusiness.ent_pipeline USING gin (attrs);

--
-- Name: idx_ent_pipeline_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_pipeline_deleted ON dynamicbusiness.ent_pipeline USING btree (deleted);

--
-- Name: idx_ent_pipeline_ent_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_pipeline_ent_type ON dynamicbusiness.ent_pipeline USING btree (entity_type_code);

--
-- Name: idx_ent_pipeline_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_pipeline_model ON dynamicbusiness.ent_pipeline USING btree (model_id);

--
-- Name: idx_ent_pipeline_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_pipeline_parent ON dynamicbusiness.ent_pipeline USING btree (parent_id);

--
-- Name: idx_ent_pipeline_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_pipeline_status ON dynamicbusiness.ent_pipeline USING btree (status);

--
-- Name: idx_ent_pipeline_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_pipeline_tenant ON dynamicbusiness.ent_pipeline USING btree (tenant_id);

--
-- Name: idx_ent_region_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_region_area ON dynamicbusiness.ent_region USING btree (area_id);

--
-- Name: idx_ent_region_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_region_attrs ON dynamicbusiness.ent_region USING gin (attrs);

--
-- Name: idx_ent_region_boundary_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_region_boundary_status ON dynamicbusiness.ent_region USING btree (boundary_status) WHERE (deleted = false);

--
-- Name: idx_ent_region_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_region_deleted ON dynamicbusiness.ent_region USING btree (deleted);

--
-- Name: idx_ent_region_ent_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_region_ent_type ON dynamicbusiness.ent_region USING btree (entity_type_code);

--
-- Name: idx_ent_region_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_region_model ON dynamicbusiness.ent_region USING btree (model_id);

--
-- Name: idx_ent_region_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_region_parent ON dynamicbusiness.ent_region USING btree (parent_id);

--
-- Name: idx_ent_region_region_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_region_region_type ON dynamicbusiness.ent_region USING btree (region_type) WHERE (deleted = false);

--
-- Name: idx_ent_region_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_region_status ON dynamicbusiness.ent_region USING btree (status);

--
-- Name: idx_ent_region_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_region_tenant ON dynamicbusiness.ent_region USING btree (tenant_id);

--
-- Name: idx_ent_spare_part_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_spare_part_area ON dynamicbusiness.ent_spare_part USING btree (area_id);

--
-- Name: idx_ent_spare_part_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_spare_part_attrs ON dynamicbusiness.ent_spare_part USING gin (attrs);

--
-- Name: idx_ent_spare_part_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_spare_part_deleted ON dynamicbusiness.ent_spare_part USING btree (deleted);

--
-- Name: idx_ent_spare_part_ent_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_spare_part_ent_type ON dynamicbusiness.ent_spare_part USING btree (entity_type_code);

--
-- Name: idx_ent_spare_part_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_spare_part_model ON dynamicbusiness.ent_spare_part USING btree (model_id);

--
-- Name: idx_ent_spare_part_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_spare_part_parent ON dynamicbusiness.ent_spare_part USING btree (parent_id);

--
-- Name: idx_ent_spare_part_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_spare_part_status ON dynamicbusiness.ent_spare_part USING btree (status);

--
-- Name: idx_ent_spare_part_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_spare_part_tenant ON dynamicbusiness.ent_spare_part USING btree (tenant_id);

--
-- Name: idx_ent_task_area; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_task_area ON dynamicbusiness.ent_task USING btree (area_id);

--
-- Name: idx_ent_task_attrs; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_task_attrs ON dynamicbusiness.ent_task USING gin (attrs);

--
-- Name: idx_ent_task_deleted; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_task_deleted ON dynamicbusiness.ent_task USING btree (deleted);

--
-- Name: idx_ent_task_ent_type; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_task_ent_type ON dynamicbusiness.ent_task USING btree (entity_type_code);

--
-- Name: idx_ent_task_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_task_model ON dynamicbusiness.ent_task USING btree (model_id);

--
-- Name: idx_ent_task_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_task_parent ON dynamicbusiness.ent_task USING btree (parent_id);

--
-- Name: idx_ent_task_status; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_task_status ON dynamicbusiness.ent_task USING btree (status);

--
-- Name: idx_ent_task_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_task_tenant ON dynamicbusiness.ent_task USING btree (tenant_id);

--
-- Name: idx_ent_zone_facility; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_zone_facility ON dynamicbusiness.ent_zone USING btree (facility_id) WHERE (deleted = false);

--
-- Name: idx_ent_zone_model; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_zone_model ON dynamicbusiness.ent_zone USING btree (model_id) WHERE (deleted = false);

--
-- Name: idx_ent_zone_parent; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_zone_parent ON dynamicbusiness.ent_zone USING btree (parent_id) WHERE (deleted = false);

--
-- Name: idx_ent_zone_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE INDEX idx_ent_zone_tenant ON dynamicbusiness.ent_zone USING btree (tenant_id) WHERE (deleted = false);

--
-- Name: uk_business_capability_btc_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_business_capability_btc_tenant ON dynamicbusiness.business_capability USING btree (entity_type_code, tenant_id) WHERE (deleted = false);

--
-- Name: uk_capability_projection_key; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_capability_projection_key ON dynamicbusiness.capability_component_projection USING btree (entity_type_code, component_code, data_kind, tenant_id) WHERE (deleted = false);

--
-- Name: uk_dynamic_business_code_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_business_code_tenant ON dynamicbusiness.dynamic_business USING btree (code, tenant_id) WHERE (deleted = false);

--
-- Name: uk_dynamic_business_entry_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_business_entry_code ON dynamicbusiness.dynamic_business_entry USING btree (business_id, code, tenant_id) WHERE (deleted = false);

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
-- Name: uk_dynamic_entity_type_base_field; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_entity_type_base_field ON dynamicbusiness.dynamic_entity_type_base_field USING btree (entity_type_code, field_code, tenant_id) WHERE (deleted = false);

--
-- Name: uk_dynamic_entity_type_code; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_entity_type_code ON dynamicbusiness.dynamic_entity_type USING btree (code, tenant_id) WHERE (deleted = false);

--
-- Name: uk_dynamic_entity_type_config; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_entity_type_config ON dynamicbusiness.dynamic_entity_type_config USING btree (entity_type_code, tenant_id) WHERE (deleted = false);

--
-- Name: uk_dynamic_entity_type_relation; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_dynamic_entity_type_relation ON dynamicbusiness.dynamic_entity_type_relation USING btree (source_entity_type_code, target_entity_type_code, tenant_id) WHERE (deleted = false);

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

CREATE UNIQUE INDEX uk_dynamic_model_relation_declaration ON dynamicbusiness.dynamic_model_relation_declaration USING btree (model_id, target_entity_type, tenant_id) WHERE (deleted = false);

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

CREATE UNIQUE INDEX uk_dynamic_ref_constraint_library ON dynamicbusiness.dynamic_ref_constraint_library USING btree (entity_type_code, ref_target_type, constraint_type, tenant_id) WHERE (deleted = false);

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
-- Name: uk_ent_facility_code_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_ent_facility_code_tenant ON dynamicbusiness.ent_facility USING btree (code, tenant_id) WHERE ((deleted = false) AND (code IS NOT NULL));

--
-- Name: uk_ent_region_code_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_ent_region_code_tenant ON dynamicbusiness.ent_region USING btree (region_code, tenant_id) WHERE ((deleted = false) AND (region_code IS NOT NULL));

--
-- Name: uk_ent_zone_code_tenant; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_ent_zone_code_tenant ON dynamicbusiness.ent_zone USING btree (code, tenant_id) WHERE ((deleted = false) AND (code IS NOT NULL));

--
-- Name: uk_model_crud_form_definition_key; Type: INDEX; Schema: dynamicbusiness; Owner: -
--

CREATE UNIQUE INDEX uk_model_crud_form_definition_key ON dynamicbusiness.model_crud_form_definition USING btree (entity_type_code, model_id, tenant_id) WHERE (deleted = false);

--
-- PostgreSQL database dump complete
--
-- seed 幂等：FIELD 等业务分组按 (group_type, code) 唯一
CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_group_type_code_tenant
    ON dynamicbusiness.dynamic_group (tenant_id, group_type, code)
    WHERE deleted = false;

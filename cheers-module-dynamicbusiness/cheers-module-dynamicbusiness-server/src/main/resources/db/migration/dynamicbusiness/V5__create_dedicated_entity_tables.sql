-- Dedicated entity tables (DDL from zhgl public.biz_*)
SET search_path TO dynamicbusiness;

--
--






--
-- Name: biz_billing; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_billing (
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
-- Name: biz_customer; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_customer (
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
-- Name: biz_inspection_point; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_inspection_point (
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
-- Name: biz_dian_wei_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_dian_wei_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_dian_wei_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_emergency; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_emergency (
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
-- Name: biz_emergency_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_emergency_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_emergency_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_emergency_resource; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_emergency_resource (
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
-- Name: biz_emergency_team; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_emergency_team (
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
-- Name: biz_equipment; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_equipment (
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
-- Name: biz_equipment_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

ALTER TABLE biz_equipment ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY (
    SEQUENCE NAME biz_equipment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: biz_fault; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_fault (
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
-- Name: biz_fault_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_fault_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_fault_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_inspection_item; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_inspection_item (
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
-- Name: biz_patrol; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_patrol (
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
-- Name: biz_inspection_management_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_inspection_management_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_inspection_management_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_jian_cha_nei_rong_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_jian_cha_nei_rong_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_jian_cha_nei_rong_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_ke_hu_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_ke_hu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_ke_hu_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_route; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_route (
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
-- Name: biz_lu_xian_guan_li_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_lu_xian_guan_li_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_lu_xian_guan_li_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_maintenance; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_maintenance (
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
-- Name: biz_maintenance_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_maintenance_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_maintenance_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_personnel; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_personnel (
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
-- Name: biz_personnel_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_personnel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_personnel_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_pipeline; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_pipeline (
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
    pipeline_code character varying(255),
    pipeline_name character varying(255),
    install_date date,
    manufacturer character varying(255),
    pipeline_model character varying(255),
    tree_path character varying(500),
    sort integer DEFAULT 0
);


--
-- Name: biz_pipeline_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_pipeline_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_pipeline_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_region; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_region (
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
-- Name: biz_region_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_region_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_region_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_shou_fei_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_shou_fei_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_shou_fei_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_spare_part; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_spare_part (
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
-- Name: biz_spare_part_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_spare_part_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_spare_part_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_task; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS biz_task (
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
-- Name: biz_task_model_6bfa9a8c9be648cebc21737115690e21_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_task_model_6bfa9a8c9be648cebc21737115690e21_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_task_model_6bfa9a8c9be648cebc21737115690e21_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_ying_ji_dui_wu_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_ying_ji_dui_wu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_ying_ji_dui_wu_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_ying_ji_zi_yuan_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE IF NOT EXISTS biz_ying_ji_zi_yuan_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: biz_ying_ji_zi_yuan_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--



--
-- Name: biz_billing id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_billing ALTER COLUMN id SET DEFAULT nextval('biz_shou_fei_id_seq'::regclass);


--
-- Name: biz_customer id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_customer ALTER COLUMN id SET DEFAULT nextval('biz_ke_hu_id_seq'::regclass);


--
-- Name: biz_emergency id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_emergency ALTER COLUMN id SET DEFAULT nextval('biz_emergency_id_seq'::regclass);


--
-- Name: biz_emergency_resource id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_emergency_resource ALTER COLUMN id SET DEFAULT nextval('biz_ying_ji_zi_yuan_id_seq'::regclass);


--
-- Name: biz_emergency_team id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_emergency_team ALTER COLUMN id SET DEFAULT nextval('biz_ying_ji_dui_wu_id_seq'::regclass);


--
-- Name: biz_fault id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_fault ALTER COLUMN id SET DEFAULT nextval('biz_fault_id_seq'::regclass);


--
-- Name: biz_inspection_item id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_inspection_item ALTER COLUMN id SET DEFAULT nextval('biz_jian_cha_nei_rong_id_seq'::regclass);


--
-- Name: biz_inspection_point id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_inspection_point ALTER COLUMN id SET DEFAULT nextval('biz_dian_wei_id_seq'::regclass);


--
-- Name: biz_maintenance id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_maintenance ALTER COLUMN id SET DEFAULT nextval('biz_maintenance_id_seq'::regclass);


--
-- Name: biz_patrol id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_patrol ALTER COLUMN id SET DEFAULT nextval('biz_inspection_management_id_seq'::regclass);


--
-- Name: biz_personnel id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_personnel ALTER COLUMN id SET DEFAULT nextval('biz_personnel_id_seq'::regclass);


--
-- Name: biz_pipeline id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_pipeline ALTER COLUMN id SET DEFAULT nextval('biz_pipeline_id_seq'::regclass);


--
-- Name: biz_region id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_region ALTER COLUMN id SET DEFAULT nextval('biz_region_id_seq'::regclass);


--
-- Name: biz_route id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_route ALTER COLUMN id SET DEFAULT nextval('biz_lu_xian_guan_li_id_seq'::regclass);


--
-- Name: biz_spare_part id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_spare_part ALTER COLUMN id SET DEFAULT nextval('biz_spare_part_id_seq'::regclass);


--
-- Name: biz_task id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE biz_task ALTER COLUMN id SET DEFAULT nextval('biz_task_model_6bfa9a8c9be648cebc21737115690e21_id_seq'::regclass);


--
-- Name: biz_inspection_point biz_dian_wei_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_emergency biz_emergency_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_fault biz_fault_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_patrol biz_inspection_management_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_inspection_item biz_jian_cha_nei_rong_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_customer biz_ke_hu_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_route biz_lu_xian_guan_li_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_maintenance biz_maintenance_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_personnel biz_personnel_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_pipeline biz_pipeline_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_region biz_region_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_billing biz_shou_fei_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_spare_part biz_spare_part_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_task biz_task_model_6bfa9a8c9be648cebc21737115690e21_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_emergency_team biz_ying_ji_dui_wu_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_emergency_resource biz_ying_ji_zi_yuan_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--



--
-- Name: biz_billing_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_billing_tree_path_idx ON biz_billing USING btree (tree_path);


--
-- Name: biz_customer_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_customer_tree_path_idx ON biz_customer USING btree (tree_path);


--
-- Name: biz_emergency_resource_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_emergency_resource_tree_path_idx ON biz_emergency_resource USING btree (tree_path);


--
-- Name: biz_emergency_team_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_emergency_team_tree_path_idx ON biz_emergency_team USING btree (tree_path);


--
-- Name: biz_emergency_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_emergency_tree_path_idx ON biz_emergency USING btree (tree_path);


--
-- Name: biz_equipment_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_equipment_tree_path_idx ON biz_equipment USING btree (tree_path);


--
-- Name: biz_fault_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_fault_tree_path_idx ON biz_fault USING btree (tree_path);


--
-- Name: biz_inspection_item_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_inspection_item_tree_path_idx ON biz_inspection_item USING btree (tree_path);


--
-- Name: biz_inspection_point_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_inspection_point_tree_path_idx ON biz_inspection_point USING btree (tree_path);


--
-- Name: biz_maintenance_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_maintenance_tree_path_idx ON biz_maintenance USING btree (tree_path);


--
-- Name: biz_patrol_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_patrol_tree_path_idx ON biz_patrol USING btree (tree_path);


--
-- Name: biz_personnel_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_personnel_tree_path_idx ON biz_personnel USING btree (tree_path);


--
-- Name: biz_pipeline_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_pipeline_tree_path_idx ON biz_pipeline USING btree (tree_path);


--
-- Name: biz_region_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_region_tree_path_idx ON biz_region USING btree (tree_path);


--
-- Name: biz_route_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_route_tree_path_idx ON biz_route USING btree (tree_path);


--
-- Name: biz_spare_part_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_spare_part_tree_path_idx ON biz_spare_part USING btree (tree_path);


--
-- Name: biz_task_tree_path_idx; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS biz_task_tree_path_idx ON biz_task USING btree (tree_path);


--
-- Name: idx_biz_emergency_area; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_emergency_area ON biz_emergency USING btree (area_id);


--
-- Name: idx_biz_emergency_attrs; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_emergency_attrs ON biz_emergency USING gin (attrs);


--
-- Name: idx_biz_emergency_biz_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_emergency_biz_type ON biz_emergency USING btree (business_type_code);


--
-- Name: idx_biz_emergency_deleted; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_emergency_deleted ON biz_emergency USING btree (deleted);


--
-- Name: idx_biz_emergency_model; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_emergency_model ON biz_emergency USING btree (model_id);


--
-- Name: idx_biz_emergency_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_emergency_parent ON biz_emergency USING btree (parent_id);


--
-- Name: idx_biz_emergency_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_emergency_status ON biz_emergency USING btree (status);


--
-- Name: idx_biz_emergency_tenant; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_emergency_tenant ON biz_emergency USING btree (tenant_id);


--
-- Name: idx_biz_fault_area; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_fault_area ON biz_fault USING btree (area_id);


--
-- Name: idx_biz_fault_attrs; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_fault_attrs ON biz_fault USING gin (attrs);


--
-- Name: idx_biz_fault_biz_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_fault_biz_type ON biz_fault USING btree (business_type_code);


--
-- Name: idx_biz_fault_deleted; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_fault_deleted ON biz_fault USING btree (deleted);


--
-- Name: idx_biz_fault_model; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_fault_model ON biz_fault USING btree (model_id);


--
-- Name: idx_biz_fault_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_fault_parent ON biz_fault USING btree (parent_id);


--
-- Name: idx_biz_fault_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_fault_status ON biz_fault USING btree (status);


--
-- Name: idx_biz_fault_tenant; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_fault_tenant ON biz_fault USING btree (tenant_id);


--
-- Name: idx_biz_inspection_management_area; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_inspection_management_area ON biz_patrol USING btree (area_id);


--
-- Name: idx_biz_inspection_management_attrs; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_inspection_management_attrs ON biz_patrol USING gin (attrs);


--
-- Name: idx_biz_inspection_management_biz_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_inspection_management_biz_type ON biz_patrol USING btree (business_type_code);


--
-- Name: idx_biz_inspection_management_deleted; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_inspection_management_deleted ON biz_patrol USING btree (deleted);


--
-- Name: idx_biz_inspection_management_model; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_inspection_management_model ON biz_patrol USING btree (model_id);


--
-- Name: idx_biz_inspection_management_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_inspection_management_parent ON biz_patrol USING btree (parent_id);


--
-- Name: idx_biz_inspection_management_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_inspection_management_status ON biz_patrol USING btree (status);


--
-- Name: idx_biz_inspection_management_tenant; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_inspection_management_tenant ON biz_patrol USING btree (tenant_id);


--
-- Name: idx_biz_jian_cha_nei_rong_area; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_jian_cha_nei_rong_area ON biz_inspection_item USING btree (area_id);


--
-- Name: idx_biz_jian_cha_nei_rong_attrs; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_jian_cha_nei_rong_attrs ON biz_inspection_item USING gin (attrs);


--
-- Name: idx_biz_jian_cha_nei_rong_biz_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_jian_cha_nei_rong_biz_type ON biz_inspection_item USING btree (business_type_code);


--
-- Name: idx_biz_jian_cha_nei_rong_deleted; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_jian_cha_nei_rong_deleted ON biz_inspection_item USING btree (deleted);


--
-- Name: idx_biz_jian_cha_nei_rong_model; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_jian_cha_nei_rong_model ON biz_inspection_item USING btree (model_id);


--
-- Name: idx_biz_jian_cha_nei_rong_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_jian_cha_nei_rong_parent ON biz_inspection_item USING btree (parent_id);


--
-- Name: idx_biz_jian_cha_nei_rong_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_jian_cha_nei_rong_status ON biz_inspection_item USING btree (status);


--
-- Name: idx_biz_jian_cha_nei_rong_tenant; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_jian_cha_nei_rong_tenant ON biz_inspection_item USING btree (tenant_id);


--
-- Name: idx_biz_maintenance_area; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_maintenance_area ON biz_maintenance USING btree (area_id);


--
-- Name: idx_biz_maintenance_attrs; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_maintenance_attrs ON biz_maintenance USING gin (attrs);


--
-- Name: idx_biz_maintenance_biz_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_maintenance_biz_type ON biz_maintenance USING btree (business_type_code);


--
-- Name: idx_biz_maintenance_deleted; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_maintenance_deleted ON biz_maintenance USING btree (deleted);


--
-- Name: idx_biz_maintenance_model; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_maintenance_model ON biz_maintenance USING btree (model_id);


--
-- Name: idx_biz_maintenance_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_maintenance_parent ON biz_maintenance USING btree (parent_id);


--
-- Name: idx_biz_maintenance_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_maintenance_status ON biz_maintenance USING btree (status);


--
-- Name: idx_biz_maintenance_tenant; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_maintenance_tenant ON biz_maintenance USING btree (tenant_id);


--
-- Name: idx_biz_personnel_area; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_personnel_area ON biz_personnel USING btree (area_id);


--
-- Name: idx_biz_personnel_attrs; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_personnel_attrs ON biz_personnel USING gin (attrs);


--
-- Name: idx_biz_personnel_biz_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_personnel_biz_type ON biz_personnel USING btree (business_type_code);


--
-- Name: idx_biz_personnel_deleted; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_personnel_deleted ON biz_personnel USING btree (deleted);


--
-- Name: idx_biz_personnel_model; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_personnel_model ON biz_personnel USING btree (model_id);


--
-- Name: idx_biz_personnel_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_personnel_parent ON biz_personnel USING btree (parent_id);


--
-- Name: idx_biz_personnel_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_personnel_status ON biz_personnel USING btree (status);


--
-- Name: idx_biz_personnel_tenant; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_personnel_tenant ON biz_personnel USING btree (tenant_id);


--
-- Name: idx_biz_pipeline_area; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_pipeline_area ON biz_pipeline USING btree (area_id);


--
-- Name: idx_biz_pipeline_attrs; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_pipeline_attrs ON biz_pipeline USING gin (attrs);


--
-- Name: idx_biz_pipeline_biz_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_pipeline_biz_type ON biz_pipeline USING btree (business_type_code);


--
-- Name: idx_biz_pipeline_deleted; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_pipeline_deleted ON biz_pipeline USING btree (deleted);


--
-- Name: idx_biz_pipeline_model; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_pipeline_model ON biz_pipeline USING btree (model_id);


--
-- Name: idx_biz_pipeline_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_pipeline_parent ON biz_pipeline USING btree (parent_id);


--
-- Name: idx_biz_pipeline_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_pipeline_status ON biz_pipeline USING btree (status);


--
-- Name: idx_biz_pipeline_tenant; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_pipeline_tenant ON biz_pipeline USING btree (tenant_id);


--
-- Name: idx_biz_region_area; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_region_area ON biz_region USING btree (area_id);


--
-- Name: idx_biz_region_attrs; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_region_attrs ON biz_region USING gin (attrs);


--
-- Name: idx_biz_region_biz_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_region_biz_type ON biz_region USING btree (business_type_code);


--
-- Name: idx_biz_region_deleted; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_region_deleted ON biz_region USING btree (deleted);


--
-- Name: idx_biz_region_model; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_region_model ON biz_region USING btree (model_id);


--
-- Name: idx_biz_region_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_region_parent ON biz_region USING btree (parent_id);


--
-- Name: idx_biz_region_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_region_status ON biz_region USING btree (status);


--
-- Name: idx_biz_region_tenant; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_region_tenant ON biz_region USING btree (tenant_id);


--
-- Name: idx_biz_spare_part_area; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_spare_part_area ON biz_spare_part USING btree (area_id);


--
-- Name: idx_biz_spare_part_attrs; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_spare_part_attrs ON biz_spare_part USING gin (attrs);


--
-- Name: idx_biz_spare_part_biz_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_spare_part_biz_type ON biz_spare_part USING btree (business_type_code);


--
-- Name: idx_biz_spare_part_deleted; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_spare_part_deleted ON biz_spare_part USING btree (deleted);


--
-- Name: idx_biz_spare_part_model; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_spare_part_model ON biz_spare_part USING btree (model_id);


--
-- Name: idx_biz_spare_part_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_spare_part_parent ON biz_spare_part USING btree (parent_id);


--
-- Name: idx_biz_spare_part_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_spare_part_status ON biz_spare_part USING btree (status);


--
-- Name: idx_biz_spare_part_tenant; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_spare_part_tenant ON biz_spare_part USING btree (tenant_id);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_area; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_area ON biz_task USING btree (area_id);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_attrs; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_attrs ON biz_task USING gin (attrs);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_biz_type; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_biz_type ON biz_task USING btree (business_type_code);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_deleted; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_deleted ON biz_task USING btree (deleted);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_model; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_model ON biz_task USING btree (model_id);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_parent; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_parent ON biz_task USING btree (parent_id);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_status ON biz_task USING btree (status);


--
-- Name: idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_tenant; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX IF NOT EXISTS idx_biz_task_model_6bfa9a8c9be648cebc21737115690e21_tenant ON biz_task USING btree (tenant_id);


--
--



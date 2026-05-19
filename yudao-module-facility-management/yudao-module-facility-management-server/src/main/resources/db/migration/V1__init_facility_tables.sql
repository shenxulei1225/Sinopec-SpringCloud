-- ============================================================
-- 设施管理模块初始表结构
-- 表名前缀: fac_ (facility 缩写)
-- ============================================================

-- 站场表
CREATE TABLE fac_site (
    site_id bigint NOT NULL,
    site_code character varying(64) NOT NULL,
    site_name character varying(128) NOT NULL,
    parent_id bigint DEFAULT 0 NOT NULL,
    sort_no integer DEFAULT 0,
    node_type smallint NOT NULL,
    site_type_id bigint,
    level integer DEFAULT 1 NOT NULL,
    path character varying(255) NOT NULL,
    status smallint DEFAULT 1 NOT NULL,
    province_code integer,
    city_code integer,
    area_code integer,
    address character varying(255),
    director character varying(64),
    phone character varying(32),
    email character varying(128),
    routing_url character varying(255),
    longitude numeric(10,6),
    latitude numeric(10,6),
    remark character varying(500),
    owner_user_id bigint,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    updater character varying(64) DEFAULT ''::character varying NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT fac_site_pkey PRIMARY KEY (site_id)
);

COMMENT ON TABLE fac_site IS '站场表';
COMMENT ON COLUMN fac_site.site_id IS '站场ID';
COMMENT ON COLUMN fac_site.site_code IS '站场编码';
COMMENT ON COLUMN fac_site.site_name IS '站场名称';
COMMENT ON COLUMN fac_site.parent_id IS '父级节点ID';
COMMENT ON COLUMN fac_site.sort_no IS '排序号';
COMMENT ON COLUMN fac_site.node_type IS '节点类型：1-分组，2-站场';
COMMENT ON COLUMN fac_site.site_type_id IS '站场类型ID';
COMMENT ON COLUMN fac_site.level IS '层级';
COMMENT ON COLUMN fac_site.path IS '路径';
COMMENT ON COLUMN fac_site.status IS '状态：0-正常，1-停用';
COMMENT ON COLUMN fac_site.province_code IS '省份编码';
COMMENT ON COLUMN fac_site.city_code IS '城市编码';
COMMENT ON COLUMN fac_site.area_code IS '区县编码';
COMMENT ON COLUMN fac_site.address IS '详细地址';
COMMENT ON COLUMN fac_site.director IS '负责人';
COMMENT ON COLUMN fac_site.phone IS '联系电话';
COMMENT ON COLUMN fac_site.email IS '邮箱';
COMMENT ON COLUMN fac_site.routing_url IS '路由地址';
COMMENT ON COLUMN fac_site.longitude IS '经度';
COMMENT ON COLUMN fac_site.latitude IS '纬度';
COMMENT ON COLUMN fac_site.remark IS '备注';
COMMENT ON COLUMN fac_site.owner_user_id IS '负责人用户ID';
COMMENT ON COLUMN fac_site.tenant_id IS '租户编号';

CREATE INDEX idx_fac_site_parent_id ON fac_site(parent_id);
CREATE INDEX idx_fac_site_code ON fac_site(site_code);
CREATE INDEX idx_fac_site_node_type ON fac_site(node_type);
CREATE INDEX idx_fac_site_deleted ON fac_site(deleted);

-- 站场类型表
CREATE TABLE fac_site_type (
    id bigint NOT NULL,
    type_code character varying(64) NOT NULL,
    type_name character varying(128) NOT NULL,
    description character varying(500),
    sort_no integer DEFAULT 0,
    status smallint DEFAULT 0,
    remark character varying(500),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    updater character varying(64) DEFAULT ''::character varying NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT fac_site_type_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE fac_site_type IS '站场类型表';
COMMENT ON COLUMN fac_site_type.id IS '类型ID';
COMMENT ON COLUMN fac_site_type.type_code IS '类型编码';
COMMENT ON COLUMN fac_site_type.type_name IS '类型名称';
COMMENT ON COLUMN fac_site_type.description IS '类型描述';
COMMENT ON COLUMN fac_site_type.sort_no IS '排序号';
COMMENT ON COLUMN fac_site_type.status IS '状态：0-正常，1-停用';
COMMENT ON COLUMN fac_site_type.remark IS '备注';

CREATE INDEX idx_fac_site_type_code ON fac_site_type(type_code);
CREATE INDEX idx_fac_site_type_deleted ON fac_site_type(deleted);

-- 设施台账表
CREATE TABLE fac_facility (
    id bigint NOT NULL,
    facility_code character varying(64) NOT NULL,
    facility_name character varying(128) NOT NULL,
    category_id bigint NOT NULL,
    category_name character varying(128),
    site_id bigint NOT NULL,
    site_name character varying(128),
    sort_no integer DEFAULT 0,
    status smallint DEFAULT 1 NOT NULL,
    manufacturer character varying(128),
    model character varying(128),
    install_date character varying(32),
    location character varying(255),
    attribute_json jsonb,
    remark character varying(500),
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    creator character varying(64) DEFAULT ''::character varying NOT NULL,
    updater character varying(64) DEFAULT ''::character varying NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT fac_facility_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE fac_facility IS '设施台账表';
COMMENT ON COLUMN fac_facility.id IS '主键ID';
COMMENT ON COLUMN fac_facility.facility_code IS '设施编码';
COMMENT ON COLUMN fac_facility.facility_name IS '设施名称';
COMMENT ON COLUMN fac_facility.category_id IS '分类ID';
COMMENT ON COLUMN fac_facility.category_name IS '分类名称';
COMMENT ON COLUMN fac_facility.site_id IS '所属站场ID';
COMMENT ON COLUMN fac_facility.site_name IS '所属站场名称';
COMMENT ON COLUMN fac_facility.sort_no IS '排序号';
COMMENT ON COLUMN fac_facility.status IS '状态：0-正常，1-停用';
COMMENT ON COLUMN fac_facility.manufacturer IS '生产厂家';
COMMENT ON COLUMN fac_facility.model IS '规格型号';
COMMENT ON COLUMN fac_facility.install_date IS '安装日期';
COMMENT ON COLUMN fac_facility.location IS '安装位置';
COMMENT ON COLUMN fac_facility.attribute_json IS '扩展属性JSON';
COMMENT ON COLUMN fac_facility.remark IS '备注';
COMMENT ON COLUMN fac_facility.tenant_id IS '租户编号';

CREATE INDEX idx_fac_facility_code ON fac_facility(facility_code);
CREATE INDEX idx_fac_facility_site_id ON fac_facility(site_id);
CREATE INDEX idx_fac_facility_category_id ON fac_facility(category_id);
CREATE INDEX idx_fac_facility_deleted ON fac_facility(deleted);

-- 外键约束（如果需要）
-- ALTER TABLE fac_facility ADD CONSTRAINT fk_fac_facility_site FOREIGN KEY (site_id) REFERENCES fac_site(site_id);

-- 站场表
CREATE TABLE sys_site (
    site_id bigint NOT NULL,
    site_code character varying(64) NOT NULL,
    site_name character varying(128) NOT NULL,
    parent_id bigint DEFAULT 0 NOT NULL,
    sort_no integer DEFAULT 0,
    node_type smallint NOT NULL,
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
    CONSTRAINT sys_site_pkey PRIMARY KEY (site_id)
);

COMMENT ON TABLE sys_site IS '站场表';
COMMENT ON COLUMN sys_site.site_id IS '站场ID';
COMMENT ON COLUMN sys_site.site_code IS '站场编码';
COMMENT ON COLUMN sys_site.site_name IS '站场名称';
COMMENT ON COLUMN sys_site.parent_id IS '父级节点ID';
COMMENT ON COLUMN sys_site.sort_no IS '排序号';
COMMENT ON COLUMN sys_site.node_type IS '节点类型：1-分组，2-站场';
COMMENT ON COLUMN sys_site.level IS '层级';
COMMENT ON COLUMN sys_site.path IS '路径';
COMMENT ON COLUMN sys_site.status IS '状态：0-正常，1-停用';
COMMENT ON COLUMN sys_site.province_code IS '省份编码';
COMMENT ON COLUMN sys_site.city_code IS '城市编码';
COMMENT ON COLUMN sys_site.area_code IS '区县编码';
COMMENT ON COLUMN sys_site.address IS '详细地址';
COMMENT ON COLUMN sys_site.director IS '负责人';
COMMENT ON COLUMN sys_site.phone IS '联系电话';
COMMENT ON COLUMN sys_site.email IS '邮箱';
COMMENT ON COLUMN sys_site.routing_url IS '路由地址';
COMMENT ON COLUMN sys_site.longitude IS '经度';
COMMENT ON COLUMN sys_site.latitude IS '纬度';
COMMENT ON COLUMN sys_site.remark IS '备注';
COMMENT ON COLUMN sys_site.owner_user_id IS '负责人用户ID';
COMMENT ON COLUMN sys_site.tenant_id IS '租户编号';

-- 索引
CREATE INDEX idx_sys_site_parent_id ON sys_site(parent_id);
CREATE INDEX idx_sys_site_code ON sys_site(site_code);
CREATE INDEX idx_sys_site_node_type ON sys_site(node_type);
CREATE INDEX idx_sys_site_deleted ON sys_site(deleted);

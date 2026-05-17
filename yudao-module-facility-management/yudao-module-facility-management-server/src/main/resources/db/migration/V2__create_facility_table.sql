-- 设施台账表
CREATE TABLE facility (
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
    CONSTRAINT facility_pkey PRIMARY KEY (id)
);

COMMENT ON TABLE facility IS '设施台账表';
COMMENT ON COLUMN facility.id IS '主键ID';
COMMENT ON COLUMN facility.facility_code IS '设施编码';
COMMENT ON COLUMN facility.facility_name IS '设施名称';
COMMENT ON COLUMN facility.category_id IS '分类ID';
COMMENT ON COLUMN facility.category_name IS '分类名称';
COMMENT ON COLUMN facility.site_id IS '所属站场ID';
COMMENT ON COLUMN facility.site_name IS '所属站场名称';
COMMENT ON COLUMN facility.sort_no IS '排序号';
COMMENT ON COLUMN facility.status IS '状态：0-正常，1-停用';
COMMENT ON COLUMN facility.manufacturer IS '生产厂家';
COMMENT ON COLUMN facility.model IS '规格型号';
COMMENT ON COLUMN facility.install_date IS '安装日期';
COMMENT ON COLUMN facility.location IS '安装位置';
COMMENT ON COLUMN facility.attribute_json IS '扩展属性JSON';
COMMENT ON COLUMN facility.remark IS '备注';
COMMENT ON COLUMN facility.tenant_id IS '租户编号';

-- 索引
CREATE INDEX idx_facility_code ON facility(facility_code);
CREATE INDEX idx_facility_site_id ON facility(site_id);
CREATE INDEX idx_facility_category_id ON facility(category_id);
CREATE INDEX idx_facility_deleted ON facility(deleted);

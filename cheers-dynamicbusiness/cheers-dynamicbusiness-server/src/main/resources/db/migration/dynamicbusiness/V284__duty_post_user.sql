-- 职务上的人员。只记人和职务，不记管理单元。
-- 谁在哪个管理单元能看数据仍在 dynamic_post_assignment，由数据资源管理写入。
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamic_duty_post_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_duty_post_user (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_duty_post_user_id_seq'::regclass) NOT NULL,
    duty_post_id bigint NOT NULL,
    user_id bigint NOT NULL,
    creator character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_duty_post_user_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_duty_post_user
    ON dynamic_duty_post_user (tenant_id, duty_post_id, user_id)
    WHERE (deleted = false);

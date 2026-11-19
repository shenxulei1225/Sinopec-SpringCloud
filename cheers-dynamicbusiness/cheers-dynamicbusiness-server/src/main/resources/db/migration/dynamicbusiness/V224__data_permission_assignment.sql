-- 数据权限从「角色×目录的场站范围」切到「权限维度 + 归属声明 + 授权分配」。
-- 权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md 第 2.1～2.4 节。
-- 术语：docs/治理机制/能力体系/术语对照与命名决议-数据权限分配-20261116.md。
--
-- 本迁移做的事：
--   1. 数据目录加「作为数据权限维度」列。
--   2. 新建归属声明、授权分配、任职、职务绑定角色四张表。
--   3. 租户 1 的一次性数据：
--      管线目录登记为维度；建「实验基地」节点挂东营油库、金桥厂区、洛阳圣瑞；港尾分输站挂漳州LNG联络线；
--      原站场级目录写「所属场站 → 场站」声明（客户、事件主题、账号不声明）；
--      场站、管道、构筑物、应急事件写「所属管线段 → 管线」声明；
--      异常 4 条样例按来源巡检任务挂金桥厂区，场景 1 条挂洛阳圣瑞，6 条早已断开的事件订阅删除；
--      场站管理员按账号名「站名+管理员」生成授权分配，名字对不上的不写（迁移日志列出）。
--      原来不限场站的角色不迁移，由管理员上线后手工分配。
--   4. 删除老的场站范围表。
-- 租户 1 的表不存在时（新环境）只建表，不写数据。
SET search_path TO dynamicbusiness;

ALTER TABLE dynamic_entity_type
    ADD COLUMN IF NOT EXISTS permission_dimension boolean DEFAULT false NOT NULL;

COMMENT ON COLUMN dynamic_entity_type.permission_dimension IS
    '作为数据权限维度。只允许有上下级的原生数据类型；一个租户最多两本。';

-- ---------------------------------------------------------------------------
-- 归属声明：一本目录往上一跳，用哪个引用字段指向哪本目录
-- ---------------------------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS dynamic_catalog_ownership_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_catalog_ownership (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_catalog_ownership_id_seq'::regclass) NOT NULL,
    registry_code character varying(64) NOT NULL,
    field_code character varying(128) NOT NULL,
    target_registry_code character varying(64) NOT NULL,
    required boolean DEFAULT true NOT NULL,
    multi_valued boolean DEFAULT false NOT NULL,
    creator character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_catalog_ownership_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_catalog_ownership
    ON dynamic_catalog_ownership (tenant_id, registry_code, field_code)
    WHERE (deleted = false);

-- ---------------------------------------------------------------------------
-- 授权分配：人、角色、数据范围、是否含下级、有效期、来源
-- ---------------------------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS dynamic_grant_assignment_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_grant_assignment (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_grant_assignment_id_seq'::regclass) NOT NULL,
    user_id bigint NOT NULL,
    role_id bigint NOT NULL,
    scope_registry_code character varying(64) NOT NULL,
    scope_entity_id bigint NOT NULL,
    include_descendants boolean DEFAULT false NOT NULL,
    valid_from timestamp without time zone,
    valid_to timestamp without time zone,
    source character varying(16) NOT NULL,
    post_assignment_id bigint,
    creator character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_grant_assignment_pkey PRIMARY KEY (id),
    CONSTRAINT ck_dynamic_grant_assignment_source CHECK (source IN ('MANUAL', 'POST')),
    CONSTRAINT ck_dynamic_grant_assignment_post
        CHECK ((source = 'POST') = (post_assignment_id IS NOT NULL))
);

CREATE INDEX IF NOT EXISTS idx_dynamic_grant_assignment_user
    ON dynamic_grant_assignment (tenant_id, user_id)
    WHERE (deleted = false);

CREATE INDEX IF NOT EXISTS idx_dynamic_grant_assignment_post
    ON dynamic_grant_assignment (post_assignment_id)
    WHERE (deleted = false);

-- ---------------------------------------------------------------------------
-- 任职：人在哪个节点担任什么职务，主岗 / 兼任 / 借调，起止日期
-- ---------------------------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS dynamic_post_assignment_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_post_assignment (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_post_assignment_id_seq'::regclass) NOT NULL,
    user_id bigint NOT NULL,
    duty_post_id bigint NOT NULL,
    scope_registry_code character varying(64) NOT NULL,
    scope_entity_id bigint NOT NULL,
    include_descendants boolean DEFAULT false NOT NULL,
    kind character varying(16) NOT NULL,
    valid_from date,
    valid_to date,
    creator character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_post_assignment_pkey PRIMARY KEY (id),
    CONSTRAINT ck_dynamic_post_assignment_kind CHECK (kind IN ('PRIMARY', 'CONCURRENT', 'SECONDED'))
);

CREATE INDEX IF NOT EXISTS idx_dynamic_post_assignment_user
    ON dynamic_post_assignment (tenant_id, user_id)
    WHERE (deleted = false);

-- ---------------------------------------------------------------------------
-- 职务绑定角色：任职生效时，按这张表生成授权分配
-- ---------------------------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS dynamic_duty_post_role_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_duty_post_role (
    id bigint DEFAULT nextval('dynamicbusiness.dynamic_duty_post_role_id_seq'::regclass) NOT NULL,
    duty_post_id bigint NOT NULL,
    role_id bigint NOT NULL,
    creator character varying(64) DEFAULT ''::character varying,
    create_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updater character varying(64) DEFAULT ''::character varying,
    update_time timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    deleted boolean DEFAULT false NOT NULL,
    tenant_id bigint DEFAULT 0 NOT NULL,
    CONSTRAINT dynamic_duty_post_role_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_duty_post_role
    ON dynamic_duty_post_role (tenant_id, duty_post_id, role_id)
    WHERE (deleted = false);

-- ---------------------------------------------------------------------------
-- 租户 1 一次性数据
-- ---------------------------------------------------------------------------
DO $$
DECLARE
    lab_id bigint;
    root_id bigint;
    unmatched text;
BEGIN
    IF to_regclass('dynamicbusiness.ent_pipeline_t1') IS NULL
        OR to_regclass('dynamicbusiness.ent_facility_t1') IS NULL
        OR to_regclass('dynamicbusiness.dynamic_entity_relation_t1') IS NULL THEN
        RAISE NOTICE 'V224: 租户 1 的管线或场站表不存在，跳过数据迁移';
        RETURN;
    END IF;

    -- 1) 管线目录登记为权限维度
    UPDATE dynamic_entity_type
    SET permission_dimension = true, updater = 'V224', update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = 1 AND deleted = false AND code = 'pipeline';

    -- 2) 实验基地：与各条管线并列，挂不在任何管线上的场站
    SELECT id INTO root_id FROM ent_pipeline_t1
    WHERE deleted = false AND code = 'PIPE-CN-ROOT';
    IF root_id IS NULL THEN
        RAISE EXCEPTION 'V224: 管线树缺少根节点 PIPE-CN-ROOT';
    END IF;

    INSERT INTO ent_pipeline_t1 (tenant_id, entity_type_code, model_id, name, code, status, parent_id, sort,
                                 creator, updater)
    VALUES (1, 'pipeline', 1173, '实验基地', 'PIPE-LAB-BASE', 1, root_id, 99, 'V224', 'V224')
    RETURNING id INTO lab_id;

    UPDATE ent_pipeline_t1
    SET tree_path = '/' || root_id || '/' || lab_id || '/'
    WHERE id = lab_id;

    UPDATE ent_facility_t1
    SET pipeline_id = lab_id, pipeline_segment_id = lab_id, updater = 'V224', update_time = CURRENT_TIMESTAMP
    WHERE deleted = false AND id IN (1, 44, 45) AND pipeline_segment_id IS NULL;

    UPDATE ent_facility_t1
    SET pipeline_segment_id = 6, updater = 'V224', update_time = CURRENT_TIMESTAMP
    WHERE deleted = false AND id = 39 AND pipeline_id = 6 AND pipeline_segment_id IS NULL;

    -- 引用字段保存时本来就同步写关系表。这里按同一格式补上这 4 座站的行。
    INSERT INTO dynamic_entity_relation_t1 (source_entity_id, target_entity_id, relation_type, relation_name,
                                            status, field_code, source_model_code, source_entity_type_code,
                                            target_entity_type_code, creator, updater, tenant_id)
    SELECT f.id, f.pipeline_segment_id, 'ENTITY_REF', '所属管线段', 1, 'pipeline_segment_id', m.code,
           'facility', 'pipeline', 'V224', 'V224', 1
    FROM ent_facility_t1 f
    JOIN dynamic_model m ON m.id = f.model_id
    WHERE f.deleted = false AND f.id IN (1, 39, 44, 45) AND f.pipeline_segment_id IS NOT NULL
      AND NOT EXISTS (
          SELECT 1 FROM dynamic_entity_relation_t1 r
          WHERE r.deleted = false AND r.source_entity_id = f.id
            AND r.source_entity_type_code = 'facility' AND r.field_code = 'pipeline_segment_id'
      );

    -- 3) 异常样例按来源巡检任务（都在金桥厂区）挂站；洛阳圣瑞场景挂洛阳圣瑞
    IF to_regclass('dynamicbusiness.ent_anomaly_t1') IS NOT NULL THEN
        UPDATE ent_anomaly_t1
        SET facility_id = 44, updater = 'V224', update_time = CURRENT_TIMESTAMP
        WHERE deleted = false AND facility_id IS NULL
          AND code IN ('REC-PI-001', 'REC-PI-002', 'REC-PI-003', 'REC-PI-004');
    END IF;
    IF to_regclass('dynamicbusiness.ent_scene_t1') IS NOT NULL THEN
        UPDATE ent_scene_t1
        SET facility_id = 45, updater = 'V224', update_time = CURRENT_TIMESTAMP
        WHERE deleted = false AND facility_id IS NULL AND code = 'SCENE-LUOYANG-SHENGRUI';
    END IF;

    -- 4) 写入场站之前留下、早已断开的订阅连接
    IF to_regclass('dynamicbusiness.ent_push_subscription_t1') IS NOT NULL THEN
        UPDATE dynamic_entity_relation_t1
        SET deleted = true, updater = 'V224', update_time = CURRENT_TIMESTAMP
        WHERE deleted = false AND source_entity_type_code = 'event_subscription'
          AND source_entity_id IN (
              SELECT id FROM ent_push_subscription_t1 WHERE deleted = false AND facility_id IS NULL
          );
        UPDATE ent_push_subscription_t1
        SET deleted = true, updater = 'V224', update_time = CURRENT_TIMESTAMP
        WHERE deleted = false AND facility_id IS NULL;
    END IF;

    -- 5) 归属声明
    INSERT INTO dynamic_catalog_ownership (registry_code, field_code, target_registry_code, required,
                                           multi_valued, creator, updater, tenant_id)
    SELECT t.code, 'facility_id', 'facility', true, false, 'V224', 'V224', 1
    FROM dynamic_entity_type t
    WHERE t.tenant_id = 1 AND t.deleted = false
      AND t.code IN (
          'alarm', 'alarm_rule', 'alarm_strategy', 'anomaly', 'billing', 'Constructure', 'Document',
          'emergency_resource', 'emergency_response', 'equipment', 'event_subscription', 'fault', 'linkage',
          'maintanance', 'maintenance', 'point', 'route', 'scene', 'scene_placement', 'schedule',
          'spare_parts', 'task', 'task_excution_record', 'task_execution_step', 'vehicle_lane',
          'vehicle_list', 'vehicle_pass', 'visitor', 'work_order', 'zone',
          'linkage_alarm', 'maintanence_schedule', 'route_network_point', 'strategy_alarm',
          'task_maintenance', 'task_patrol', 'task_record_patrol',
          'equipment_inspection_config', 'patrol_equipment', 'patrol_target'
      );

    INSERT INTO dynamic_catalog_ownership (registry_code, field_code, target_registry_code, required,
                                           multi_valued, creator, updater, tenant_id)
    SELECT v.registry_code, 'pipeline_segment_id', 'pipeline', v.required, false, 'V224', 'V224', 1
    FROM (VALUES ('facility', true), ('pipeline_pipe', true), ('pipeline_structure', true),
                 ('emergency', false)) AS v(registry_code, required)
    WHERE EXISTS (
        SELECT 1 FROM dynamic_entity_type t
        WHERE t.tenant_id = 1 AND t.deleted = false AND t.code = v.registry_code
    );

    -- 6) 场站管理员：账号名正好是「站名+管理员」的，范围就是这座站
    INSERT INTO dynamic_grant_assignment (user_id, role_id, scope_registry_code, scope_entity_id,
                                          include_descendants, source, creator, updater, tenant_id)
    SELECT DISTINCT ur.user_id, ur.role_id, 'facility', f.id, false, 'MANUAL', 'V224', 'V224', 1
    FROM public.system_user_role ur
    JOIN public.system_role r ON r.id = ur.role_id AND r.deleted = false
        AND r.tenant_id = 1 AND r.code = 'station_admin'
    JOIN ent_user_account_t1 a ON a.id = ur.user_id AND a.deleted = false
    JOIN ent_facility_t1 f ON f.deleted = false AND f.name || '管理员' = a.name
    WHERE ur.deleted = false;

    SELECT string_agg(a.name, '、' ORDER BY a.name) INTO unmatched
    FROM public.system_user_role ur
    JOIN public.system_role r ON r.id = ur.role_id AND r.deleted = false
        AND r.tenant_id = 1 AND r.code = 'station_admin'
    JOIN ent_user_account_t1 a ON a.id = ur.user_id AND a.deleted = false
    WHERE ur.deleted = false
      AND NOT EXISTS (
          SELECT 1 FROM ent_facility_t1 f WHERE f.deleted = false AND f.name || '管理员' = a.name
      );
    RAISE NOTICE 'V224: 场站管理员名字对不上场站、未写授权分配：%', coalesce(unmatched, '无');
END $$;

DROP TABLE IF EXISTS dynamic_catalog_role_station_scope;
DROP SEQUENCE IF EXISTS dynamic_catalog_role_station_scope_id_seq;

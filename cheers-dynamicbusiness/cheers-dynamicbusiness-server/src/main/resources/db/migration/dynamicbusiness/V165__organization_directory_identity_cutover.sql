-- 组织架构只保留动态 department 与 user_account。
-- 本迁移一次性迁移系统用户编号、角色关系、部门范围和负责人，并保留历史编号对照。

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS public.system_user_account_id_map (
    tenant_id bigint NOT NULL,
    old_system_user_id bigint NOT NULL,
    user_account_id bigint NOT NULL,
    username varchar(64) NOT NULL,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tenant_id, old_system_user_id),
    UNIQUE (tenant_id, user_account_id)
);

COMMENT ON TABLE public.system_user_account_id_map IS
    '旧系统用户编号到用户目录编号的历史对照。只用于解释旧记录，不参与登录。';

CREATE TABLE IF NOT EXISTS public.system_department_id_map (
    tenant_id bigint NOT NULL,
    old_system_dept_id bigint NOT NULL,
    department_id bigint NOT NULL,
    department_name varchar(100) NOT NULL,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tenant_id, old_system_dept_id)
);

COMMENT ON TABLE public.system_department_id_map IS
    '旧系统部门编号到动态部门编号的一次性迁移对照。运行时不得读取本表兜底。';

CREATE TABLE IF NOT EXISTS public.organization_directory_migration_issue (
    issue_type varchar(64) NOT NULL,
    tenant_id bigint NOT NULL,
    source_id bigint NOT NULL,
    source_value text NOT NULL,
    detail text NOT NULL,
    create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (issue_type, tenant_id, source_id, source_value)
);

-- 空库先建立用户目录最小写入结构。平台导入随后补字段库、基础字段和页面布局。
CREATE SEQUENCE IF NOT EXISTS dynamicbusiness.ent_user_account_t1_id_seq;

CREATE TABLE IF NOT EXISTS dynamicbusiness.ent_user_account_t1 (
    id bigint PRIMARY KEY DEFAULT nextval('dynamicbusiness.ent_user_account_t1_id_seq'),
    tenant_id bigint NOT NULL DEFAULT 1,
    entity_type_code varchar(64),
    model_id bigint NOT NULL,
    domain varchar(128),
    name varchar(255) NOT NULL,
    code varchar(255),
    parent_id bigint DEFAULT 0,
    tree_path varchar(1024),
    sort integer DEFAULT 0,
    status integer DEFAULT 1,
    custom_fields jsonb DEFAULT '{}'::jsonb,
    creator varchar(64),
    create_time timestamp DEFAULT CURRENT_TIMESTAMP,
    updater varchar(64),
    update_time timestamp DEFAULT CURRENT_TIMESTAMP,
    deleted boolean DEFAULT false,
    mobile varchar(64),
    station_facility_id bigint,
    department_id bigint,
    visit_tenant_ids varchar(2000)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_user_account_t1_code
    ON dynamicbusiness.ent_user_account_t1 (tenant_id, code)
    WHERE deleted = false AND code IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_ent_user_account_t1_department
    ON dynamicbusiness.ent_user_account_t1 (tenant_id, department_id)
    WHERE deleted = false;

INSERT INTO dynamicbusiness.dynamic_entity_type (
    code, name, description, alias, sort, status, type_level, association_fields,
    storage_type, dedicated_table_name, enable_rule_engine, creator, updater, deleted,
    tenant_id, group_name, entry_kind, domain, model_workbench_mode, work_scope
)
SELECT
    'user_account', '用户', '全系统只在这里维护用户', '用户', 80, 'active', 'USER',
    '{}'::jsonb, 'DEDICATED', 'ent_user_account_t1', false, 'migration', 'migration',
    false, 1, '用户', 'NATIVE', NULL, 'MULTI', 'NETWORK'
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_entity_type
    WHERE tenant_id = 1 AND code = 'user_account' AND deleted = false
);

INSERT INTO dynamicbusiness.dynamic_model (
    code, name, entity_type_code, description, status, sort, creator, updater,
    deleted, tenant_id, governance_status
)
SELECT
    'MODEL-user-account', '用户', 'user_account', '用户目录上的人', 1, 0,
    'migration', 'migration', false, 1, 'COMPANY'
WHERE NOT EXISTS (
    SELECT 1 FROM dynamicbusiness.dynamic_model
    WHERE tenant_id = 1 AND entity_type_code = 'user_account' AND deleted = false
);

INSERT INTO dynamicbusiness.dynamic_field (
    code, name, type, description, source, status, provider_code, tenant_id,
    creator, updater, deleted, governance_status
) VALUES
    ('mobile', '手机', 'TEXT', '用户手机', 'SYSTEM', 1, NULL, 1,
     'migration', 'migration', false, 'COMPANY'),
    ('station_facility_id', '所属场站', 'ENTITY_REF', '引用设施目录', 'SYSTEM', 1,
     'dynamic-entity:facility', 1, 'migration', 'migration', false, 'COMPANY'),
    ('department_id', '所属部门', 'ENTITY_REF', '引用部门目录', 'SYSTEM', 1,
     'dynamic-entity:department', 1, 'migration', 'migration', false, 'COMPANY'),
    ('visit_tenant_ids', '可进入的业务租户', 'TEXT', '平台用户允许进入的业务租户编号',
     'SYSTEM', 1, NULL, 1, 'migration', 'migration', false, 'COMPANY')
ON CONFLICT (code, tenant_id) WHERE deleted = false DO NOTHING;

INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
    entity_type_code, library_field_id, field_code, field_name, data_type, required,
    description, sort_order, status, tenant_id, creator, updater, deleted,
    is_searchable, is_filterable, is_sortable
)
SELECT
    'user_account', field.id, field.code, field.name, field.type, false,
    field.description,
    CASE field.code
        WHEN 'mobile' THEN 10
        WHEN 'station_facility_id' THEN 20
        WHEN 'department_id' THEN 30
        ELSE 40
    END,
    1, 1, 'migration', 'migration', false, false, true, false
FROM dynamicbusiness.dynamic_field field
WHERE field.tenant_id = 1
  AND field.deleted = false
  AND field.code IN ('mobile', 'station_facility_id', 'department_id', 'visit_tenant_ids')
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field base
      WHERE base.tenant_id = 1
        AND base.entity_type_code = 'user_account'
        AND base.field_code = field.code
        AND base.deleted = false
  );

DO $$
DECLARE
    tenant_row record;
    source_type dynamicbusiness.dynamic_entity_type%ROWTYPE;
    source_model dynamicbusiness.dynamic_model%ROWTYPE;
    user_table text;
    department_table text;
    target_model_id bigint;
BEGIN
    SELECT * INTO source_type
    FROM dynamicbusiness.dynamic_entity_type
    WHERE tenant_id = 1 AND code = 'user_account' AND deleted = false
    ORDER BY id
    LIMIT 1;
    IF source_type.id IS NULL THEN
        RAISE EXCEPTION '用户目录元数据不存在，不能迁移系统用户';
    END IF;

    SELECT * INTO source_model
    FROM dynamicbusiness.dynamic_model
    WHERE tenant_id = 1 AND entity_type_code = 'user_account' AND deleted = false
    ORDER BY id
    LIMIT 1;
    IF source_model.id IS NULL THEN
        RAISE EXCEPTION '用户目录型号不存在，不能迁移系统用户';
    END IF;

    FOR tenant_row IN
        SELECT t.id
        FROM public.system_tenant t
        WHERE t.deleted = false
          AND EXISTS (
              SELECT 1 FROM public.system_users u
              WHERE u.tenant_id = t.id AND u.deleted = false
          )
        ORDER BY t.id
    LOOP
        user_table := 'ent_user_account_t' || tenant_row.id;
        department_table := 'ent_department_t' || tenant_row.id;

        IF to_regclass('dynamicbusiness.' || user_table) IS NULL THEN
            EXECUTE format(
                'CREATE TABLE dynamicbusiness.%I (LIKE dynamicbusiness.ent_user_account_t1 INCLUDING ALL)',
                user_table
            );
        END IF;

        IF NOT EXISTS (
            SELECT 1 FROM dynamicbusiness.dynamic_entity_type
            WHERE tenant_id = tenant_row.id AND code = 'user_account' AND deleted = false
        ) THEN
            INSERT INTO dynamicbusiness.dynamic_entity_type (
                code, name, parent_id, description, icon, alias, sort, status, type_level,
                association_fields, storage_type, dedicated_table_name, enable_rule_engine,
                physical_column_mapping, creator, create_time, updater, update_time, deleted,
                tenant_id, group_name, entry_kind, base_entity_type_code, domain,
                model_workbench_mode, work_scope, data_layout_id, model_layout_id
            ) VALUES (
                source_type.code, source_type.name, source_type.parent_id, source_type.description,
                source_type.icon, source_type.alias, source_type.sort, source_type.status,
                source_type.type_level, source_type.association_fields, source_type.storage_type,
                user_table, source_type.enable_rule_engine, source_type.physical_column_mapping,
                'migration', CURRENT_TIMESTAMP, 'migration', CURRENT_TIMESTAMP, false,
                tenant_row.id, source_type.group_name, source_type.entry_kind,
                source_type.base_entity_type_code, source_type.domain,
                source_type.model_workbench_mode, source_type.work_scope,
                source_type.data_layout_id, source_type.model_layout_id
            );
        ELSE
            UPDATE dynamicbusiness.dynamic_entity_type
            SET dedicated_table_name = user_table,
                physical_column_mapping = source_type.physical_column_mapping,
                updater = 'migration',
                update_time = CURRENT_TIMESTAMP
            WHERE tenant_id = tenant_row.id
              AND code = 'user_account'
              AND deleted = false;
        END IF;

        INSERT INTO dynamicbusiness.dynamic_field (
            code, name, type, unit, description, source, status, max_relations,
            index_strategy, options, provider_code, semantic_type, creator, create_time,
            updater, update_time, deleted, tenant_id, governance_status,
            origin_facility_id, creator_user_id
        )
        SELECT
            source_field.code, source_field.name, source_field.type, source_field.unit,
            source_field.description, source_field.source, source_field.status,
            source_field.max_relations, source_field.index_strategy, source_field.options,
            source_field.provider_code, source_field.semantic_type, 'migration',
            CURRENT_TIMESTAMP, 'migration', CURRENT_TIMESTAMP, false, tenant_row.id,
            source_field.governance_status, source_field.origin_facility_id,
            source_field.creator_user_id
        FROM dynamicbusiness.dynamic_field source_field
        WHERE source_field.tenant_id = 1
          AND source_field.deleted = false
          AND source_field.id IN (
              SELECT source_base.library_field_id
              FROM dynamicbusiness.dynamic_entity_type_base_field source_base
              WHERE source_base.tenant_id = 1
                AND source_base.entity_type_code = 'user_account'
                AND source_base.deleted = false
          )
          AND NOT EXISTS (
              SELECT 1 FROM dynamicbusiness.dynamic_field target_field
              WHERE target_field.tenant_id = tenant_row.id
                AND target_field.code = source_field.code
                AND target_field.deleted = false
          );

        INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
            entity_type_code, field_code, field_name, data_type, required, default_value,
            description, type_config, sort_order, status, creator, create_time, updater,
            update_time, deleted, tenant_id, library_field_id, is_searchable,
            is_filterable, is_sortable
        )
        SELECT
            source_base.entity_type_code, source_base.field_code, source_base.field_name,
            source_base.data_type, source_base.required, source_base.default_value,
            source_base.description, source_base.type_config, source_base.sort_order,
            source_base.status, 'migration', CURRENT_TIMESTAMP, 'migration',
            CURRENT_TIMESTAMP, false, tenant_row.id, target_field.id,
            source_base.is_searchable, source_base.is_filterable, source_base.is_sortable
        FROM dynamicbusiness.dynamic_entity_type_base_field source_base
        JOIN dynamicbusiness.dynamic_field source_field
          ON source_field.id = source_base.library_field_id
         AND source_field.tenant_id = 1
        JOIN dynamicbusiness.dynamic_field target_field
          ON target_field.tenant_id = tenant_row.id
         AND target_field.code = source_field.code
         AND target_field.deleted = false
        WHERE source_base.tenant_id = 1
          AND source_base.entity_type_code = 'user_account'
          AND source_base.deleted = false
          AND NOT EXISTS (
              SELECT 1
              FROM dynamicbusiness.dynamic_entity_type_base_field target_base
              WHERE target_base.tenant_id = tenant_row.id
                AND target_base.entity_type_code = 'user_account'
                AND target_base.field_code = source_base.field_code
                AND target_base.deleted = false
          );

        SELECT id INTO target_model_id
        FROM dynamicbusiness.dynamic_model
        WHERE tenant_id = tenant_row.id
          AND entity_type_code = 'user_account'
          AND deleted = false
        ORDER BY id
        LIMIT 1;

        IF target_model_id IS NULL THEN
            INSERT INTO dynamicbusiness.dynamic_model (
                code, name, entity_type_code, description, status, sort, field_groups_config,
                creator, create_time, updater, update_time, deleted, tenant_id, domain,
                governance_status, origin_facility_id, creator_user_id, auto_param_defaults
            ) VALUES (
                source_model.code, source_model.name, source_model.entity_type_code,
                source_model.description, source_model.status, source_model.sort,
                source_model.field_groups_config, 'migration', CURRENT_TIMESTAMP,
                'migration', CURRENT_TIMESTAMP, false, tenant_row.id, source_model.domain,
                source_model.governance_status, source_model.origin_facility_id,
                source_model.creator_user_id, source_model.auto_param_defaults
            )
            RETURNING id INTO target_model_id;
        END IF;

        -- 旧部门只按同租户、唯一同名部门建立一次性对照。歧义项写入问题清单。
        IF to_regclass('dynamicbusiness.' || department_table) IS NOT NULL THEN
            EXECUTE format($sql$
                INSERT INTO public.system_department_id_map (
                    tenant_id, old_system_dept_id, department_id, department_name
                )
                SELECT d.tenant_id, d.id, min(nd.id), d.name
                FROM public.system_dept d
                JOIN dynamicbusiness.%I nd
                  ON nd.deleted = false
                 AND btrim(nd.name) = btrim(d.name)
                WHERE d.tenant_id = $1
                GROUP BY d.tenant_id, d.id, d.name
                HAVING count(*) = 1
                ON CONFLICT (tenant_id, old_system_dept_id) DO UPDATE
                SET department_id = EXCLUDED.department_id,
                    department_name = EXCLUDED.department_name
            $sql$, department_table) USING tenant_row.id;
        END IF;

        EXECUTE format($sql$
            INSERT INTO dynamicbusiness.%I (
                tenant_id, entity_type_code, model_id, domain, name, code, parent_id,
                tree_path, sort, status, custom_fields, creator, create_time, updater,
                update_time, deleted, mobile, station_facility_id, department_id,
                visit_tenant_ids
            )
            SELECT
                u.tenant_id, 'user_account', $2, NULL, u.nickname, u.username, 0,
                NULL, 0, CASE WHEN u.status = 0 THEN 1 ELSE 0 END, '{}'::jsonb,
                'migration', u.create_time, 'migration', CURRENT_TIMESTAMP, false,
                u.mobile, NULL, dm.department_id,
                CASE WHEN t.scope = 'PLATFORM' THEN (
                    SELECT string_agg(bt.id::text, ',' ORDER BY bt.id)
                    FROM public.system_tenant bt
                    WHERE bt.deleted = false AND bt.status = 0 AND bt.scope = 'BUSINESS'
                ) ELSE NULL END
            FROM public.system_users u
            JOIN public.system_tenant t ON t.id = u.tenant_id AND t.deleted = false
            LEFT JOIN public.system_department_id_map dm
              ON dm.tenant_id = u.tenant_id AND dm.old_system_dept_id = u.dept_id
            WHERE u.tenant_id = $1
              AND u.deleted = false
              AND NOT EXISTS (
                  SELECT 1 FROM dynamicbusiness.%I existing_account
                  WHERE existing_account.deleted = false
                    AND lower(existing_account.code) = lower(u.username)
              )
        $sql$, user_table, user_table) USING tenant_row.id, target_model_id;

        EXECUTE format($sql$
            INSERT INTO public.system_user_account_id_map (
                tenant_id, old_system_user_id, user_account_id, username
            )
            SELECT u.tenant_id, u.id, account.id, u.username
            FROM public.system_users u
            JOIN dynamicbusiness.%I account
              ON account.deleted = false
             AND lower(account.code) = lower(u.username)
            WHERE u.tenant_id = $1
              AND u.deleted = false
            ON CONFLICT (tenant_id, old_system_user_id) DO UPDATE
            SET user_account_id = EXCLUDED.user_account_id,
                username = EXCLUDED.username
        $sql$, user_table) USING tenant_row.id;

        -- 没有活动用户记录的凭证是脏数据。迁移时显式停用，不让孤儿凭证继续登录。
        EXECUTE format($sql$
            UPDATE public.system_login_credential c
            SET deleted = true,
                updater = 'migration',
                update_time = CURRENT_TIMESTAMP
            WHERE c.tenant_id = $1
              AND c.deleted = false
              AND NOT EXISTS (
                  SELECT 1 FROM dynamicbusiness.%I account
                  WHERE account.id = c.user_id AND account.deleted = false
              )
        $sql$, user_table) USING tenant_row.id;
    END LOOP;
END $$;

-- 用户目录编号生成后，凭证和授权关系一次性切换到新编号。
INSERT INTO public.system_login_credential (
    user_id, password, tenant_id, creator, create_time, updater, update_time, deleted
)
SELECT m.user_account_id, u.password, m.tenant_id, 'migration', CURRENT_TIMESTAMP,
       'migration', CURRENT_TIMESTAMP, false
FROM public.system_user_account_id_map m
JOIN public.system_users u
  ON u.tenant_id = m.tenant_id AND u.id = m.old_system_user_id
WHERE u.deleted = false
  AND u.password IS NOT NULL
  AND btrim(u.password) <> ''
  AND NOT EXISTS (
      SELECT 1 FROM public.system_login_credential c
      WHERE c.tenant_id = m.tenant_id
        AND c.user_id = m.user_account_id
        AND c.deleted = false
  );

UPDATE public.system_user_role relation
SET user_id = mapping.user_account_id,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
FROM public.system_user_account_id_map mapping
WHERE relation.tenant_id = mapping.tenant_id
  AND relation.user_id = mapping.old_system_user_id
  AND relation.deleted = false;

UPDATE public.system_user_post relation
SET user_id = mapping.user_account_id,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
FROM public.system_user_account_id_map mapping
WHERE relation.tenant_id = mapping.tenant_id
  AND relation.user_id = mapping.old_system_user_id
  AND relation.deleted = false;

UPDATE public.system_social_user_bind relation
SET user_id = mapping.user_account_id,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
FROM public.system_user_account_id_map mapping
WHERE relation.tenant_id = mapping.tenant_id
  AND relation.user_id = mapping.old_system_user_id
  AND relation.user_type = 1
  AND relation.deleted = false;

UPDATE public.system_oauth2_approve relation
SET user_id = mapping.user_account_id,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
FROM public.system_user_account_id_map mapping
WHERE relation.tenant_id = mapping.tenant_id
  AND relation.user_id = mapping.old_system_user_id
  AND relation.user_type = 1
  AND relation.deleted = false;

-- 旧令牌上的用户编号已经失效。迁移后所有人重新登录。
DELETE FROM public.system_oauth2_access_token token
USING public.system_user_account_id_map mapping
WHERE token.tenant_id = mapping.tenant_id
  AND token.user_id = mapping.old_system_user_id
  AND token.user_type = 1;

DELETE FROM public.system_oauth2_refresh_token token
USING public.system_user_account_id_map mapping
WHERE token.tenant_id = mapping.tenant_id
  AND token.user_id = mapping.old_system_user_id
  AND token.user_type = 1;

DELETE FROM public.system_oauth2_code code
USING public.system_user_account_id_map mapping
WHERE code.tenant_id = mapping.tenant_id
  AND code.user_id = mapping.old_system_user_id
  AND code.user_type = 1;

-- 自定义部门范围只保留能映射到动态部门的编号。被清掉的旧编号进入问题清单。
INSERT INTO public.organization_directory_migration_issue (
    issue_type, tenant_id, source_id, source_value, detail
)
SELECT 'ROLE_DEPARTMENT_UNMAPPED', role.tenant_id, role.id, old_id.value,
       '角色部门范围中的旧编号没有唯一对应的动态部门，已从运行时范围移除'
FROM public.system_role role
CROSS JOIN LATERAL jsonb_array_elements_text(
    CASE
        WHEN COALESCE(btrim(role.data_scope_dept_ids), '') ~ '^\[.*\]$'
            THEN role.data_scope_dept_ids::jsonb
        ELSE '[]'::jsonb
    END
) old_id(value)
LEFT JOIN public.system_department_id_map mapping
  ON mapping.tenant_id = role.tenant_id
 AND mapping.old_system_dept_id = old_id.value::bigint
WHERE role.deleted = false
  AND mapping.department_id IS NULL
ON CONFLICT (issue_type, tenant_id, source_id, source_value) DO UPDATE
SET detail = EXCLUDED.detail,
    create_time = CURRENT_TIMESTAMP;

UPDATE public.system_role role
SET data_scope_dept_ids = mapped.ids,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT r.id, r.tenant_id,
           COALESCE(jsonb_agg(m.department_id ORDER BY m.department_id)
                    FILTER (WHERE m.department_id IS NOT NULL), '[]'::jsonb)::text AS ids
    FROM public.system_role r
    CROSS JOIN LATERAL jsonb_array_elements_text(
        CASE
            WHEN COALESCE(btrim(r.data_scope_dept_ids), '') ~ '^\[.*\]$'
                THEN r.data_scope_dept_ids::jsonb
            ELSE '[]'::jsonb
        END
    ) old_id(value)
    LEFT JOIN public.system_department_id_map m
      ON m.tenant_id = r.tenant_id
     AND m.old_system_dept_id = old_id.value::bigint
    WHERE r.deleted = false
    GROUP BY r.id, r.tenant_id
) mapped
WHERE role.id = mapped.id
  AND role.tenant_id = mapped.tenant_id;

-- 把旧部门负责人换成用户目录编号。只迁移唯一部门对照和唯一用户对照。
DO $$
DECLARE
    row record;
    department_table text;
BEGIN
    FOR row IN
        SELECT dm.tenant_id, dm.department_id, um.user_account_id
        FROM public.system_department_id_map dm
        JOIN public.system_dept old_dept
          ON old_dept.tenant_id = dm.tenant_id
         AND old_dept.id = dm.old_system_dept_id
        JOIN public.system_user_account_id_map um
          ON um.tenant_id = old_dept.tenant_id
         AND um.old_system_user_id = old_dept.leader_user_id
        WHERE old_dept.leader_user_id IS NOT NULL
    LOOP
        department_table := 'ent_department_t' || row.tenant_id;
        IF to_regclass('dynamicbusiness.' || department_table) IS NOT NULL THEN
            EXECUTE format(
                'UPDATE dynamicbusiness.%I SET leader_user_id = $1, updater = ''migration'', update_time = CURRENT_TIMESTAMP WHERE id = $2 AND deleted = false',
                department_table
            ) USING row.user_account_id, row.department_id;
        END IF;
    END LOOP;
END $$;

-- 运行时注册不再发布 system/dept。
UPDATE dynamicbusiness.business_capability
SET deleted = true,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND entity_type_code = 'dept'
  AND business_category = 'system';

UPDATE dynamicbusiness.dynamic_reference_provider
SET deleted = true,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND upper(provider_code) = 'SYSTEM_DEPT';

UPDATE public.system_role_menu
SET deleted = true,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND menu_id IN (
      SELECT id FROM public.system_menu
      WHERE deleted = false
        AND (
          permission LIKE 'system:dept:%'
          OR (path = 'dept' AND component = 'system/dept/index')
        )
  );

UPDATE public.system_menu
SET deleted = true,
    updater = 'migration',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND (
    permission LIKE 'system:dept:%'
    OR (path = 'dept' AND component = 'system/dept/index')
  );

-- 活跃租户仍有未迁出的旧部门时阻断删表。已删除租户和已删除旧行只留在迁移审计中。
DO $$
DECLARE
    blocking_count bigint;
BEGIN
    SELECT count(*) INTO blocking_count
    FROM public.system_dept dept
    JOIN public.system_tenant tenant ON tenant.id = dept.tenant_id
    LEFT JOIN public.system_department_id_map mapping
      ON mapping.tenant_id = dept.tenant_id
     AND mapping.old_system_dept_id = dept.id
    WHERE dept.deleted = false
      AND tenant.deleted = false
      AND mapping.department_id IS NULL;

    IF blocking_count > 0 THEN
        RAISE EXCEPTION '仍有 % 个活动系统部门无法映射到动态部门，拒绝删除 system_dept', blocking_count;
    END IF;
END $$;

DROP TABLE IF EXISTS public.system_dept;
DROP SEQUENCE IF EXISTS public.system_dept_seq;

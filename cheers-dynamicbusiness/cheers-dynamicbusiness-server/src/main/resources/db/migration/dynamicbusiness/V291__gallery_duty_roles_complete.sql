-- 智慧管廊：职务挂上能把这个岗位的事做完的角色。
-- 项目公司总经理不再带租户管理员。总经理和项目公司总经理补上财务人员。
-- 补财务这个职务，并挂财务人员。施工员挂维修工程师。物设部部长挂技术管理人员。
-- 计合部部长挂经营管理人员和财务人员。
-- 门卫要登记访客并放行。监控值班员只保留车辆放行，不跟着打开访客登记。
-- 已经挂过、已经有财务职务、权限已经抄过的，再跑不改。

SET search_path TO dynamicbusiness, public;

DO $$
DECLARE
    finance_duty_id bigint;
    branch_model_id bigint;
BEGIN
    UPDATE dynamic_duty_post_role
    SET deleted = true,
        updater = 'V291',
        update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = 123
      AND deleted = false
      AND duty_post_id = 1
      AND role_id = 203;

    INSERT INTO dynamic_duty_post_role (
        duty_post_id, role_id, creator, create_time, updater, update_time, deleted, tenant_id
    )
    SELECT pair.duty_post_id, pair.role_id, 'V291', CURRENT_TIMESTAMP, 'V291', CURRENT_TIMESTAMP, false, 123
    FROM (VALUES
        (1, 252),
        (13, 252),
        (6, 255),
        (7, 249),
        (8, 247),
        (8, 252)
    ) AS pair(duty_post_id, role_id)
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamic_duty_post_role r
        WHERE r.deleted = false
          AND r.tenant_id = 123
          AND r.duty_post_id = pair.duty_post_id
          AND r.role_id = pair.role_id
    );

    SELECT id INTO finance_duty_id
    FROM ent_duty_post_t123
    WHERE deleted = false AND name = '财务'
    ORDER BY id LIMIT 1;

    IF finance_duty_id IS NULL THEN
        INSERT INTO ent_duty_post_t123 (
            tenant_id, entity_type_code, model_id, name, code, parent_id, sort, status,
            custom_fields, creator, create_time, updater, update_time, deleted
        )
        VALUES (
            123, 'duty_post', 2639, '财务', 'DUTY-GGGL-FINANCE', 0, 0, 1,
            '{}'::jsonb, 'V291', CURRENT_TIMESTAMP, 'V291', CURRENT_TIMESTAMP, false
        )
        RETURNING id INTO finance_duty_id;

        UPDATE ent_duty_post_t123
        SET tree_path = '/' || finance_duty_id || '/'
        WHERE id = finance_duty_id;
    END IF;

    SELECT MIN(o.model_id) INTO branch_model_id
    FROM ent_organization_t123 o
    JOIN dynamic_model m ON m.id = o.model_id AND m.deleted = false AND m.name = '分公司'
    WHERE o.deleted = false;

    IF branch_model_id IS NULL THEN
        RAISE EXCEPTION 'V291: 没有分公司这一级，财务职务无法挂到组织';
    END IF;

    UPDATE ent_duty_post_t123
    SET custom_fields = jsonb_set(
            COALESCE(custom_fields, '{}'::jsonb),
            '{applicableOrgModelIds}',
            jsonb_build_array(branch_model_id),
            true
        ),
        updater = 'V291',
        update_time = CURRENT_TIMESTAMP
    WHERE id = finance_duty_id
      AND (
          custom_fields->'applicableOrgModelIds' IS NULL
          OR jsonb_typeof(custom_fields->'applicableOrgModelIds') IS DISTINCT FROM 'array'
          OR jsonb_array_length(custom_fields->'applicableOrgModelIds') = 0
      );

    INSERT INTO dynamic_entity_category_relation_t123 (
        entity_id, category_id, entity_type_code, sort,
        creator, create_time, updater, update_time, deleted, tenant_id
    )
    SELECT finance_duty_id, c.id, 'duty_post', 0,
           'V291', CURRENT_TIMESTAMP, 'V291', CURRENT_TIMESTAMP, false, 123
    FROM ent_organization_t123 o
    JOIN dynamic_category c
      ON c.deleted = false
     AND c.tenant_id = 123
     AND c.category_type_code = 'organization'
     AND c.name = o.name
    WHERE o.deleted = false
      AND o.model_id = branch_model_id
      AND NOT EXISTS (
          SELECT 1 FROM dynamic_entity_category_relation_t123 r
          WHERE r.deleted = false
            AND r.entity_id = finance_duty_id
            AND r.category_id = c.id
            AND r.entity_type_code = 'duty_post'
      );

    INSERT INTO dynamic_duty_post_role (
        duty_post_id, role_id, creator, create_time, updater, update_time, deleted, tenant_id
    )
    SELECT finance_duty_id, 252, 'V291', CURRENT_TIMESTAMP, 'V291', CURRENT_TIMESTAMP, false, 123
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamic_duty_post_role r
        WHERE r.deleted = false AND r.tenant_id = 123
          AND r.duty_post_id = finance_duty_id AND r.role_id = 252
    );

    INSERT INTO dynamic_page_role_grant (
        role_id, page_code, action, creator, create_time, updater, update_time, deleted, tenant_id
    )
    SELECT 253, g.page_code, g.action, 'V291', CURRENT_TIMESTAMP, 'V291', CURRENT_TIMESTAMP, false, 123
    FROM dynamic_page_role_grant g
    WHERE g.deleted = false
      AND g.role_id = 256
      AND g.page_code IN ('security-vehicle', 'security-vehicle-gate', 'security-vehicle-pass')
      AND NOT EXISTS (
          SELECT 1 FROM dynamic_page_role_grant x
          WHERE x.deleted = false AND x.role_id = 253
            AND x.page_code = g.page_code AND x.action = g.action
      );

    INSERT INTO dynamic_catalog_role_grant (
        role_id, registry_code, operation, creator, create_time, updater, update_time, deleted, tenant_id, subject
    )
    SELECT 253, g.registry_code, g.operation, 'V291', CURRENT_TIMESTAMP, 'V291', CURRENT_TIMESTAMP, false, 123, g.subject
    FROM dynamic_catalog_role_grant g
    WHERE g.deleted = false
      AND g.role_id = 256
      AND g.registry_code IN ('security_vehicle', 'vehicle_lane', 'vehicle_list', 'vehicle_pass')
      AND NOT EXISTS (
          SELECT 1 FROM dynamic_catalog_role_grant x
          WHERE x.deleted = false AND x.role_id = 253
            AND x.registry_code = g.registry_code
            AND x.operation = g.operation
            AND x.subject IS NOT DISTINCT FROM g.subject
      );

    UPDATE dynamic_duty_post_role
    SET deleted = true,
        updater = 'V291',
        update_time = CURRENT_TIMESTAMP
    WHERE tenant_id = 123
      AND deleted = false
      AND duty_post_id = 14
      AND role_id = 256;

    INSERT INTO dynamic_page_role_grant (
        role_id, page_code, action, creator, create_time, updater, update_time, deleted, tenant_id
    )
    SELECT 256, page.page_code, page.action, 'V291', CURRENT_TIMESTAMP, 'V291', CURRENT_TIMESTAMP, false, 123
    FROM (VALUES ('security-visitor', 'view'), ('security-visitor', 'maintain')) AS page(page_code, action)
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamic_page_role_grant x
        WHERE x.deleted = false AND x.role_id = 256
          AND x.page_code = page.page_code AND x.action = page.action
    );

    INSERT INTO dynamic_catalog_role_grant (
        role_id, registry_code, operation, creator, create_time, updater, update_time, deleted, tenant_id, subject
    )
    SELECT 256, 'visitor', op.operation, 'V291', CURRENT_TIMESTAMP, 'V291', CURRENT_TIMESTAMP, false, 123, op.subject
    FROM (VALUES
        ('category', 'query'), ('category', 'create'), ('category', 'update'),
        ('detail', 'query'), ('detail', 'create'), ('detail', 'update'),
        ('entity', 'query'), ('entity', 'create'), ('entity', 'update'),
        ('model', 'query'), ('model', 'create'), ('model', 'update')
    ) AS op(subject, operation)
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamic_catalog_role_grant x
        WHERE x.deleted = false AND x.role_id = 256
          AND x.registry_code = 'visitor'
          AND x.operation = op.operation
          AND x.subject = op.subject
    );
END $$;

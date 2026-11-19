-- 一次性修数据（租户 1）：把职务挂到职务目录的「组织机构」分类上。
--
-- 数据管理打开「职务」时，左边是组织分类，中间只列挂在该分类下的职务。
-- 「按职务」走同一份挂靠。按职务上已写的适用组织类型，挂到同名组织分类。
-- 实验基地四个组织原先没有分类节点，补上后再挂。可重复执行。
SET search_path TO dynamicbusiness;

DO $$
DECLARE
    group_category_id bigint;
    lab_root_id bigint;
    inserted integer := 0;
BEGIN
    IF to_regclass('dynamic_category') IS NULL
       OR to_regclass('dynamic_entity_category_relation_t1') IS NULL THEN
        RAISE NOTICE 'V241: 缺少分类或挂靠表，跳过';
        RETURN;
    END IF;

    SELECT id INTO group_category_id
    FROM dynamic_category
    WHERE deleted = false AND category_type_code = 'organization' AND name = '国家管网集团'
    ORDER BY id LIMIT 1;

    IF group_category_id IS NOT NULL THEN
        INSERT INTO dynamic_category (
            parent_id, name, code, category_type_code, sort, status,
            creator, create_time, updater, update_time, deleted, tenant_id
        )
        SELECT group_category_id, '实验基地', 'ORG-LAB-ROOT', 'organization', 900, 1,
               'V241', CURRENT_TIMESTAMP, 'V241', CURRENT_TIMESTAMP, false, 1
        WHERE NOT EXISTS (
            SELECT 1 FROM dynamic_category
            WHERE deleted = false AND category_type_code = 'organization' AND name = '实验基地'
        );
        SELECT id INTO lab_root_id
        FROM dynamic_category
        WHERE deleted = false AND category_type_code = 'organization' AND name = '实验基地'
        ORDER BY id LIMIT 1;

        INSERT INTO dynamic_category (
            parent_id, name, code, category_type_code, sort, status,
            creator, create_time, updater, update_time, deleted, tenant_id
        )
        SELECT lab_root_id, lab.name, lab.code, 'organization', lab.sort_no, 1,
               'V241', CURRENT_TIMESTAMP, 'V241', CURRENT_TIMESTAMP, false, 1
        FROM (VALUES
            ('金桥实验基地', 'ORG-LAB-JINQIAO', 1),
            ('洛阳实验基地', 'ORG-LAB-LUOYANG', 2),
            ('东营实验基地', 'ORG-LAB-DONGYING', 3)
        ) AS lab(name, code, sort_no)
        WHERE lab_root_id IS NOT NULL
          AND NOT EXISTS (
              SELECT 1 FROM dynamic_category c
              WHERE c.deleted = false AND c.category_type_code = 'organization' AND c.name = lab.name
          );
    END IF;

    INSERT INTO dynamic_entity_category_relation_t1 (
        entity_id, category_id, entity_type_code, sort,
        creator, create_time, updater, update_time, deleted, tenant_id
    )
    SELECT DISTINCT d.id, c.id, 'duty_post', 0,
           'V241', CURRENT_TIMESTAMP, 'V241', CURRENT_TIMESTAMP, false, 1
    FROM ent_duty_post_t1 d
    JOIN ent_organization_t1 o
      ON o.deleted = false
     AND d.custom_fields->'applicableOrgModelIds' @> to_jsonb(o.model_id)
    JOIN dynamic_category c
      ON c.deleted = false
     AND c.category_type_code = 'organization'
     AND c.tenant_id = 1
     AND c.name = o.name
    WHERE d.deleted = false
      AND NOT EXISTS (
          SELECT 1 FROM dynamic_entity_category_relation_t1 r
          WHERE r.deleted = false AND r.entity_id = d.id AND r.category_id = c.id
      );

    GET DIAGNOSTICS inserted = ROW_COUNT;
    RAISE NOTICE 'V241: 职务挂到组织分类 % 条', inserted;

    -- 清掉对不上的旧挂靠：职务已删，或这个组织的类型不在该职务的适用类型里。
    UPDATE dynamic_entity_category_relation_t1 r
    SET deleted = true,
        updater = 'V241',
        update_time = CURRENT_TIMESTAMP
    WHERE r.deleted = false
      AND r.entity_type_code = 'duty_post'
      AND NOT EXISTS (
          SELECT 1
          FROM ent_duty_post_t1 d
          JOIN dynamic_category c
            ON c.id = r.category_id
           AND c.deleted = false
           AND c.category_type_code = 'organization'
          JOIN ent_organization_t1 o
            ON o.deleted = false
           AND o.name = c.name
           AND d.custom_fields->'applicableOrgModelIds' @> to_jsonb(o.model_id)
          WHERE d.deleted = false
            AND d.id = r.entity_id
      );
END $$;

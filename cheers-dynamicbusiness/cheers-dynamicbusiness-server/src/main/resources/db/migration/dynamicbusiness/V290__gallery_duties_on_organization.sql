-- 智慧管廊的职务和智慧巡检一样挂到组织上。
-- 职务目录左侧用组织机构分类。点中一家公司，只列出挂在这个分类下的职务。
-- 现有职务都写上适用于分公司，并挂到同名的组织分类。不新建职务。集团这一级不挂。
-- 已经写过适用组织类型、已经挂上、分类栏已经指向组织机构的，再跑不改。

SET search_path TO dynamicbusiness;

DO $$
DECLARE
    branch_model_id bigint;
    branch_model_count integer;
    linked integer := 0;
BEGIN
    IF to_regclass('ent_duty_post_t123') IS NULL
       OR to_regclass('ent_organization_t123') IS NULL
       OR to_regclass('dynamic_entity_category_relation_t123') IS NULL THEN
        RAISE NOTICE 'V290: 智慧管廊还没有职务或组织表，跳过';
        RETURN;
    END IF;

    SELECT COUNT(DISTINCT o.model_id), MIN(o.model_id)
      INTO branch_model_count, branch_model_id
    FROM ent_organization_t123 o
    JOIN dynamic_model m
      ON m.id = o.model_id
     AND m.deleted = false
     AND m.name = '分公司'
    WHERE o.deleted = false;

    IF branch_model_count IS DISTINCT FROM 1 THEN
        RAISE EXCEPTION 'V290: 分公司型号应当只有一个，实际 %', branch_model_count;
    END IF;

    UPDATE ent_duty_post_t123 d
    SET custom_fields = jsonb_set(
            COALESCE(d.custom_fields, '{}'::jsonb),
            '{applicableOrgModelIds}',
            jsonb_build_array(branch_model_id),
            true
        ),
        updater = 'V290',
        update_time = CURRENT_TIMESTAMP
    WHERE d.deleted = false
      AND (
          d.custom_fields->'applicableOrgModelIds' IS NULL
          OR jsonb_typeof(d.custom_fields->'applicableOrgModelIds') IS DISTINCT FROM 'array'
          OR jsonb_array_length(d.custom_fields->'applicableOrgModelIds') = 0
      );

    INSERT INTO dynamic_entity_category_relation_t123 (
        entity_id, category_id, entity_type_code, sort,
        creator, create_time, updater, update_time, deleted, tenant_id
    )
    SELECT DISTINCT d.id, c.id, 'duty_post', 0,
           'V290', CURRENT_TIMESTAMP, 'V290', CURRENT_TIMESTAMP, false, 123
    FROM ent_duty_post_t123 d
    JOIN ent_organization_t123 o
      ON o.deleted = false
     AND d.custom_fields->'applicableOrgModelIds' @> to_jsonb(o.model_id)
    JOIN dynamic_category c
      ON c.deleted = false
     AND c.tenant_id = 123
     AND c.category_type_code = 'organization'
     AND c.name = o.name
    WHERE d.deleted = false
      AND NOT EXISTS (
          SELECT 1
          FROM dynamic_entity_category_relation_t123 r
          WHERE r.deleted = false
            AND r.entity_id = d.id
            AND r.category_id = c.id
            AND r.entity_type_code = 'duty_post'
      );

    GET DIAGNOSTICS linked = ROW_COUNT;
    RAISE NOTICE 'V290: 职务挂到组织分类 % 条', linked;

    UPDATE dynamic_entity_category_relation_t123 r
    SET deleted = true,
        updater = 'V290',
        update_time = CURRENT_TIMESTAMP
    WHERE r.deleted = false
      AND r.entity_type_code = 'duty_post'
      AND r.tenant_id = 123
      AND NOT EXISTS (
          SELECT 1
          FROM ent_duty_post_t123 d
          JOIN dynamic_category c
            ON c.id = r.category_id
           AND c.deleted = false
           AND c.tenant_id = 123
           AND c.category_type_code = 'organization'
          JOIN ent_organization_t123 o
            ON o.deleted = false
           AND o.name = c.name
           AND d.custom_fields->'applicableOrgModelIds' @> to_jsonb(o.model_id)
          WHERE d.deleted = false
            AND d.id = r.entity_id
      );
END $$;

UPDATE dm_data_tab_layout AS row
SET enabled = true,
    column_meta = jsonb_set(
        jsonb_set(COALESCE(row.column_meta, '{}'::jsonb), '{categoryTypeCode}', '"organization"'),
        '{label}',
        '"组织机构"'
    ),
    updater = 'V290',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_entity_type AS catalog
WHERE catalog.code = 'duty_post'
  AND catalog.tenant_id = 123
  AND catalog.deleted = false
  AND row.tenant_id = catalog.tenant_id
  AND row.layout_id = catalog.data_layout_id
  AND row.deleted = false
  AND row.column_kind = 'CATEGORY'
  AND (
      row.enabled = false
      OR row.column_meta->>'categoryTypeCode' IS DISTINCT FROM 'organization'
  );

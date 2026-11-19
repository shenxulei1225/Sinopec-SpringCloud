-- 组织机构和智慧巡检一样：分类即实体。数据页只留分类和详情。
-- 已有组织记录挂到现有分类根下，不新建一条组织记录，不把智慧巡检的公司抄过来。
-- 种类已经是高级分类、数据页栏已经关掉、记录已经挂上的，再跑不改。

SET search_path TO dynamicbusiness;

-- 智慧巡检已经是高级分类，下面不给它补节点。只处理还是普通分类的租户。
CREATE TEMP TABLE org_category_was_simple ON COMMIT DROP AS
SELECT tenant_id
FROM dynamic_category_type
WHERE deleted = false
  AND category_type_code = 'organization'
  AND category_mode IS DISTINCT FROM 'ADVANCED';

UPDATE dynamic_category_type
SET category_mode = 'ADVANCED',
    updater = 'V289',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND category_type_code = 'organization'
  AND category_mode IS DISTINCT FROM 'ADVANCED';

UPDATE dm_data_tab_layout AS row
SET enabled = false,
    updater = 'V289',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_entity_type AS catalog
WHERE catalog.code = 'organization'
  AND catalog.deleted = false
  AND row.tenant_id = catalog.tenant_id
  AND row.layout_id = catalog.data_layout_id
  AND row.deleted = false
  AND row.column_kind IN ('MODEL', 'ENTITY')
  AND row.enabled = true;

DO $$
DECLARE
    tenant_row record;
    org_row record;
    root_id bigint;
    root_code text;
    root_path text;
    root_level integer;
    parent_category_id bigint;
    parent_category_code text;
    parent_category_path text;
    parent_category_level integer;
    new_category_id bigint;
    link_table text;
    entity_table text;
    progressed boolean;
    guard integer;
BEGIN
    FOR tenant_row IN
        SELECT t.tenant_id, t.top_level_category_id
        FROM dynamic_category_type t
        JOIN org_category_was_simple s ON s.tenant_id = t.tenant_id
        WHERE t.deleted = false
          AND t.category_type_code = 'organization'
          AND t.top_level_category_id IS NOT NULL
    LOOP
        entity_table := 'ent_organization_t' || tenant_row.tenant_id;
        link_table := 'dynamic_category_entity_link_t' || tenant_row.tenant_id;
        IF to_regclass('dynamicbusiness.' || entity_table) IS NULL
           OR to_regclass('dynamicbusiness.' || link_table) IS NULL THEN
            CONTINUE;
        END IF;

        SELECT id, code, tree_path, COALESCE(level, 1)
          INTO root_id, root_code, root_path, root_level
        FROM dynamic_category
        WHERE id = tenant_row.top_level_category_id
          AND deleted = false
          AND tenant_id = tenant_row.tenant_id
          AND category_type_code = 'organization';
        IF root_id IS NULL THEN
            CONTINUE;
        END IF;

        guard := 0;
        LOOP
            progressed := false;
            guard := guard + 1;
            EXIT WHEN guard > 20;

            FOR org_row IN EXECUTE format(
                'SELECT e.id, e.name, e.code, e.model_id, e.parent_id
                 FROM dynamicbusiness.%I e
                 WHERE e.deleted = false
                   AND e.tenant_id = $1
                   AND NOT EXISTS (
                       SELECT 1 FROM dynamicbusiness.%I l
                       WHERE l.deleted = false
                         AND l.entity_id = e.id
                         AND l.entity_type_code = ''organization'')
                 ORDER BY e.id',
                entity_table, link_table)
            USING tenant_row.tenant_id
            LOOP
                parent_category_id := NULL;
                IF org_row.parent_id IS NULL OR org_row.parent_id = 0 THEN
                    parent_category_id := root_id;
                    parent_category_code := root_code;
                    parent_category_path := root_path;
                    parent_category_level := root_level;
                ELSE
                    EXECUTE format(
                        'SELECT c.id, c.code, c.tree_path, c.level
                         FROM dynamicbusiness.%I l
                         JOIN dynamicbusiness.dynamic_category c
                           ON c.id = l.category_id AND c.deleted = false
                         WHERE l.deleted = false
                           AND l.entity_id = $1
                           AND l.entity_type_code = ''organization''
                         LIMIT 1',
                        link_table)
                    INTO parent_category_id, parent_category_code, parent_category_path, parent_category_level
                    USING org_row.parent_id;
                END IF;
                IF parent_category_id IS NULL THEN
                    CONTINUE;
                END IF;

                INSERT INTO dynamic_category (
                    parent_id, parent_code, name, code, category_type_code,
                    tree_path, level, sort, status,
                    creator, updater, deleted, tenant_id
                ) VALUES (
                    parent_category_id,
                    parent_category_code,
                    org_row.name,
                    left(COALESCE(NULLIF(btrim(org_row.code), ''), 'org-entity-' || org_row.id::text), 64),
                    'organization',
                    '/',
                    COALESCE(parent_category_level, 1) + 1,
                    1,
                    1,
                    'V289',
                    'V289',
                    false,
                    tenant_row.tenant_id
                )
                RETURNING id INTO new_category_id;

                UPDATE dynamic_category
                SET tree_path = COALESCE(NULLIF(parent_category_path, ''), '/') || new_category_id || '/'
                WHERE id = new_category_id;

                EXECUTE format(
                    'INSERT INTO dynamicbusiness.%I (
                        category_id, entity_id, entity_model_id, entity_type_code,
                        tenant_id, creator, updater, deleted)
                     VALUES ($1, $2, $3, ''organization'', $4, ''V289'', ''V289'', false)',
                    link_table)
                USING new_category_id, org_row.id, org_row.model_id, tenant_row.tenant_id;

                progressed := true;
            END LOOP;

            EXIT WHEN NOT progressed;
        END LOOP;
    END LOOP;
END $$;

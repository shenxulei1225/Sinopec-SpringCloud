-- 管线目录是高级分类：树上每个节点对应一条记录。
-- 账上已有国家管网、各条管线和管线段。目录树原先只有五条管线绑了记录。
-- 这里按记录的上级，把缺的节点补上，并把每个节点绑到那条记录。
-- 只写租户物理表 dynamic_category_entity_link_t1，不写无后缀基表。

SELECT setval(
    'dynamicbusiness.dynamic_category_id_seq',
    GREATEST(
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_category),
        (SELECT last_value FROM dynamicbusiness.dynamic_category_id_seq)
    )
);

SELECT setval(
    'dynamicbusiness.dynamic_category_entity_link_id_seq',
    GREATEST(
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.dynamic_category_entity_link_t1),
        (SELECT last_value FROM dynamicbusiness.dynamic_category_entity_link_id_seq)
    )
);

INSERT INTO dynamicbusiness.dynamic_category_entity_link_t1 (
    category_id, entity_id, entity_model_id, creator, updater, deleted, tenant_id, entity_type_code
)
SELECT c.id, e.id, e.model_id, 'V255', 'V255', false, e.tenant_id, 'pipeline'
FROM dynamicbusiness.ent_pipeline_t1 e
JOIN dynamicbusiness.dynamic_category c
  ON c.deleted = false
 AND c.tenant_id = e.tenant_id
 AND c.category_type_code = 'pipeline'
 AND c.parent_id IS NOT NULL
 AND c.name = e.name
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_category_entity_link_t1 linked_entity
      WHERE linked_entity.deleted = false
        AND linked_entity.entity_type_code = 'pipeline'
        AND linked_entity.entity_id = e.id
  )
  AND NOT EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_category_entity_link_t1 linked_category
      WHERE linked_category.deleted = false
        AND linked_category.category_id = c.id
  );

DO $$
DECLARE
    pipeline_row record;
    parent_category bigint;
    parent_path text;
    parent_level integer;
    new_id bigint;
    inserted integer;
BEGIN
    LOOP
        inserted := 0;
        FOR pipeline_row IN
            SELECT e.id, e.name, e.parent_id, e.model_id, e.tenant_id
            FROM dynamicbusiness.ent_pipeline_t1 e
            WHERE e.deleted = false
              AND e.tenant_id = 1
              AND e.status = 1
              AND NOT EXISTS (
                  SELECT 1
                  FROM dynamicbusiness.dynamic_category_entity_link_t1 linked_entity
                  WHERE linked_entity.deleted = false
                    AND linked_entity.entity_type_code = 'pipeline'
                    AND linked_entity.entity_id = e.id
              )
              AND (
                  COALESCE(e.parent_id, 0) = 0
                  OR EXISTS (
                      SELECT 1
                      FROM dynamicbusiness.dynamic_category_entity_link_t1 parent_link
                      WHERE parent_link.deleted = false
                        AND parent_link.entity_type_code = 'pipeline'
                        AND parent_link.entity_id = e.parent_id
                  )
              )
            ORDER BY e.id
        LOOP
            IF COALESCE(pipeline_row.parent_id, 0) = 0 THEN
                SELECT id, tree_path, level
                  INTO parent_category, parent_path, parent_level
                FROM dynamicbusiness.dynamic_category
                WHERE deleted = false
                  AND tenant_id = pipeline_row.tenant_id
                  AND category_type_code = 'pipeline'
                  AND parent_id IS NULL
                LIMIT 1;
            ELSE
                SELECT c.id, c.tree_path, c.level
                  INTO parent_category, parent_path, parent_level
                FROM dynamicbusiness.dynamic_category_entity_link_t1 parent_link
                JOIN dynamicbusiness.dynamic_category c ON c.id = parent_link.category_id
                WHERE parent_link.deleted = false
                  AND parent_link.entity_type_code = 'pipeline'
                  AND parent_link.entity_id = pipeline_row.parent_id
                LIMIT 1;
            END IF;

            IF parent_category IS NULL OR parent_path IS NULL THEN
                CONTINUE;
            END IF;

            new_id := nextval('dynamicbusiness.dynamic_category_id_seq');
            INSERT INTO dynamicbusiness.dynamic_category (
                id, parent_id, name, code, category_type_code, tree_path, level, sort, status,
                creator, updater, deleted, tenant_id, parent_code
            )
            VALUES (
                new_id,
                parent_category,
                pipeline_row.name,
                'PIPE-ENT-' || pipeline_row.id,
                'pipeline',
                parent_path || new_id || '/',
                parent_level + 1,
                COALESCE(
                    (SELECT MAX(sort)
                     FROM dynamicbusiness.dynamic_category
                     WHERE parent_id = parent_category AND deleted = false),
                    0
                ) + 1,
                1,
                'V255',
                'V255',
                false,
                pipeline_row.tenant_id,
                (SELECT code FROM dynamicbusiness.dynamic_category WHERE id = parent_category)
            );

            INSERT INTO dynamicbusiness.dynamic_category_entity_link_t1 (
                category_id, entity_id, entity_model_id, creator, updater, deleted, tenant_id, entity_type_code
            )
            VALUES (
                new_id,
                pipeline_row.id,
                pipeline_row.model_id,
                'V255',
                'V255',
                false,
                pipeline_row.tenant_id,
                'pipeline'
            );
            inserted := inserted + 1;
        END LOOP;
        EXIT WHEN inserted = 0;
    END LOOP;
END $$;

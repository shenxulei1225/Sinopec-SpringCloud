-- 跨环境迁移：关联表补充 code 幂等键（字段分组、模型-分类、关联声明、模板字段分配、树 parent_code）
-- 运行时仍保留 id 列便于 JOIN；platform-import seed 应写 code，INSERT 再解析 id。

SET search_path TO dynamicbusiness;

-- ========== dynamic_group：parent_code（树导入不依赖 parent_id 顺序） ==========
ALTER TABLE dynamic_group
    ADD COLUMN IF NOT EXISTS parent_code character varying(128);

COMMENT ON COLUMN dynamic_group.parent_code IS '父分组编码（迁移用，对应同 group_type 下父节点 code）';

UPDATE dynamic_group g
SET parent_code = pg.code,
    update_time = CURRENT_TIMESTAMP
FROM dynamic_group pg
WHERE g.parent_id IS NOT NULL
  AND g.parent_id = pg.id
  AND pg.deleted = false
  AND (g.parent_code IS NULL OR g.parent_code <> pg.code);

-- ========== dynamic_category：parent_code ==========
ALTER TABLE dynamic_category
    ADD COLUMN IF NOT EXISTS parent_code character varying(64);

COMMENT ON COLUMN dynamic_category.parent_code IS '父分类编码（迁移用，对应 dynamic_category.code）';

UPDATE dynamic_category c
SET parent_code = p.code,
    update_time = CURRENT_TIMESTAMP
FROM dynamic_category p
WHERE c.parent_id IS NOT NULL
  AND c.parent_id = p.id
  AND p.deleted = false
  AND (c.parent_code IS NULL OR c.parent_code <> p.code);

-- ========== dynamic_group_relation：group_code + target_code ==========
ALTER TABLE dynamic_group_relation
    ADD COLUMN IF NOT EXISTS group_code character varying(128),
    ADD COLUMN IF NOT EXISTS target_code character varying(128);

COMMENT ON COLUMN dynamic_group_relation.group_code IS '分组编码（迁移幂等键，对应 dynamic_group.code）';
COMMENT ON COLUMN dynamic_group_relation.target_code IS '目标编码（FIELD 类型时为 dynamic_field.code）';

UPDATE dynamic_group_relation gr
SET group_code = g.code,
    target_code = f.code,
    update_time = CURRENT_TIMESTAMP
FROM dynamic_group g,
     dynamic_field f
WHERE gr.group_type = 'FIELD'
  AND gr.group_id = g.id
  AND gr.target_id = f.id
  AND g.deleted = false
  AND f.deleted = false
  AND (gr.group_code IS NULL OR gr.target_code IS NULL);

UPDATE dynamic_group_relation gr
SET deleted = true,
    updater = 'V3-group-relation-codes',
    update_time = CURRENT_TIMESTAMP
WHERE gr.deleted = false
  AND gr.group_type = 'FIELD'
  AND (gr.group_code IS NULL OR gr.target_code IS NULL);

ALTER TABLE dynamic_group_relation
    ALTER COLUMN group_code SET NOT NULL,
    ALTER COLUMN target_code SET NOT NULL;

DROP INDEX IF EXISTS uk_dynamic_group_relation;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_group_relation_code
    ON dynamic_group_relation (group_type, group_code, target_code, tenant_id)
    WHERE deleted = false;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_group_relation_id
    ON dynamic_group_relation (tenant_id, group_type, group_id, target_id)
    WHERE deleted = false;

-- ========== dynamic_model_category_relation：model_code + category_code ==========
ALTER TABLE dynamic_model_category_relation
    ADD COLUMN IF NOT EXISTS model_code character varying(128),
    ADD COLUMN IF NOT EXISTS category_code character varying(64);

COMMENT ON COLUMN dynamic_model_category_relation.model_code IS '模型编码（迁移幂等键，对应 dynamic_model.code）';
COMMENT ON COLUMN dynamic_model_category_relation.category_code IS '分类编码（迁移幂等键，对应 dynamic_category.code）';

UPDATE dynamic_model_category_relation r
SET model_code = m.code,
    category_code = c.code,
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m,
     dynamic_category c
WHERE r.model_id = m.id
  AND r.category_id = c.id
  AND m.deleted = false
  AND c.deleted = false
  AND (r.model_code IS NULL OR r.category_code IS NULL);

UPDATE dynamic_model_category_relation r
SET deleted = true,
    updater = 'V3-model-category-codes',
    update_time = CURRENT_TIMESTAMP
WHERE r.deleted = false
  AND (r.model_code IS NULL OR r.category_code IS NULL);

ALTER TABLE dynamic_model_category_relation
    ALTER COLUMN model_code SET NOT NULL,
    ALTER COLUMN category_code SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_model_category_relation_code
    ON dynamic_model_category_relation (model_code, category_code, entity_type_code, tenant_id)
    WHERE deleted = false;

-- ========== dynamic_model_relation_declaration：model_code ==========
ALTER TABLE dynamic_model_relation_declaration
    ADD COLUMN IF NOT EXISTS model_code character varying(128);

COMMENT ON COLUMN dynamic_model_relation_declaration.model_code IS '模型编码（迁移幂等键，对应 dynamic_model.code）';

UPDATE dynamic_model_relation_declaration d
SET model_code = m.code,
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE d.model_id = m.id
  AND m.deleted = false
  AND (d.model_code IS NULL OR d.model_code <> m.code);

UPDATE dynamic_model_relation_declaration d
SET deleted = true,
    updater = 'V3-relation-declaration-codes',
    update_time = CURRENT_TIMESTAMP
WHERE d.deleted = false
  AND d.model_code IS NULL;

ALTER TABLE dynamic_model_relation_declaration
    ALTER COLUMN model_code SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_model_relation_declaration_code
    ON dynamic_model_relation_declaration (model_code, target_entity_type, tenant_id)
    WHERE deleted = false;

-- ========== dynamic_template_field_assignment：template_code + field_code ==========
ALTER TABLE dynamic_template_field_assignment
    ADD COLUMN IF NOT EXISTS template_code character varying(128),
    ADD COLUMN IF NOT EXISTS field_code character varying(128);

COMMENT ON COLUMN dynamic_template_field_assignment.template_code IS '模板编码（迁移幂等键，对应 dynamic_template.code）';
COMMENT ON COLUMN dynamic_template_field_assignment.field_code IS '字段编码（迁移幂等键，对应 dynamic_field.code）';

UPDATE dynamic_template_field_assignment a
SET template_code = t.code,
    field_code = f.code,
    update_time = CURRENT_TIMESTAMP
FROM dynamic_template t,
     dynamic_field f
WHERE a.template_id = t.id
  AND a.field_id = f.id
  AND t.deleted = false
  AND f.deleted = false
  AND (a.template_code IS NULL OR a.field_code IS NULL);

UPDATE dynamic_template_field_assignment a
SET deleted = true,
    updater = 'V3-template-assignment-codes',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND (a.template_code IS NULL OR a.field_code IS NULL);

ALTER TABLE dynamic_template_field_assignment
    ALTER COLUMN template_code SET NOT NULL,
    ALTER COLUMN field_code SET NOT NULL;

DROP INDEX IF EXISTS uk_dynamic_template_field_assignment;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_template_field_assignment_code
    ON dynamic_template_field_assignment (template_code, field_code, tenant_id)
    WHERE deleted = false;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_template_field_assignment_id
    ON dynamic_template_field_assignment (template_id, field_id, tenant_id)
    WHERE deleted = false;

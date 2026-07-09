-- 模型字段分配：增加 model_code / field_code，跨环境迁移以 code 为幂等键（与 dynamic_model_relation 一致）
-- 运行时仍保留 model_id / field_id 便于 JOIN；导入 seed 应写 code，由本表或 INSERT 解析 id。

SET search_path TO dynamicbusiness;

ALTER TABLE dynamic_model_field_assignment
    ADD COLUMN IF NOT EXISTS model_code character varying(128),
    ADD COLUMN IF NOT EXISTS field_code character varying(128);

COMMENT ON COLUMN dynamic_model_field_assignment.model_code IS '模型编码（迁移幂等键，对应 dynamic_model.code）';
COMMENT ON COLUMN dynamic_model_field_assignment.field_code IS '字段编码（迁移幂等键，对应 dynamic_field.code）';

-- 从现有关联回填 code
UPDATE dynamic_model_field_assignment a
SET model_code = m.code,
    field_code = f.code,
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m,
     dynamic_field f
WHERE a.deleted = false
  AND a.model_id = m.id
  AND a.field_id = f.id
  AND m.deleted = false
  AND f.deleted = false
  AND (a.model_code IS NULL OR a.field_code IS NULL);

-- 无法解析的行（孤儿 id）软删，避免 NOT NULL 约束失败
UPDATE dynamic_model_field_assignment a
SET deleted = true,
    updater = 'V2-assignment-codes',
    update_time = CURRENT_TIMESTAMP
WHERE a.deleted = false
  AND (a.model_code IS NULL OR a.field_code IS NULL);

ALTER TABLE dynamic_model_field_assignment
    ALTER COLUMN model_code SET NOT NULL,
    ALTER COLUMN field_code SET NOT NULL;

DROP INDEX IF EXISTS uk_dynamic_model_field_assignment;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_model_field_assignment_code
    ON dynamic_model_field_assignment (model_code, field_code, tenant_id)
    WHERE deleted = false;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_model_field_assignment_id
    ON dynamic_model_field_assignment (model_id, field_id, tenant_id)
    WHERE deleted = false;

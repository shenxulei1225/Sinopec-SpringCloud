-- 校验 platform-import seed 是否满足 Flyway V2/V3 的 code 列约束（导入后执行）
SET search_path TO dynamicbusiness;

DO $$
DECLARE
  n bigint;
BEGIN
  SELECT count(*) INTO n FROM dynamic_group_relation
  WHERE deleted = false AND (group_code IS NULL OR target_code IS NULL);
  IF n > 0 THEN
    RAISE EXCEPTION 'dynamic_group_relation: % row(s) missing group_code/target_code', n;
  END IF;

  SELECT count(*) INTO n FROM dynamic_model_field_assignment
  WHERE deleted = false AND (model_code IS NULL OR field_code IS NULL);
  IF n > 0 THEN
    RAISE EXCEPTION 'dynamic_model_field_assignment: % row(s) missing model_code/field_code', n;
  END IF;

  SELECT count(*) INTO n FROM dynamic_model_category_relation
  WHERE deleted = false AND (model_code IS NULL OR category_code IS NULL);
  IF n > 0 THEN
    RAISE EXCEPTION 'dynamic_model_category_relation: % row(s) missing model_code/category_code', n;
  END IF;

  SELECT count(*) INTO n FROM dynamic_model_relation_declaration
  WHERE deleted = false AND model_code IS NULL;
  IF n > 0 THEN
    RAISE EXCEPTION 'dynamic_model_relation_declaration: % row(s) missing model_code', n;
  END IF;

  SELECT count(*) INTO n FROM dynamic_template_field_assignment
  WHERE deleted = false AND (template_code IS NULL OR field_code IS NULL);
  IF n > 0 THEN
    RAISE EXCEPTION 'dynamic_template_field_assignment: % row(s) missing template_code/field_code', n;
  END IF;

  RAISE NOTICE 'verify-v3-seed: OK (all V3 code columns populated where rows exist)';
END $$;

-- 摘要（便于人工核对）
SELECT 'dynamic_group_relation' AS tbl, count(*) AS rows,
       count(*) FILTER (WHERE group_code IS NOT NULL AND target_code IS NOT NULL) AS with_codes
FROM dynamic_group_relation WHERE deleted = false
UNION ALL
SELECT 'dynamic_model_field_assignment', count(*),
       count(*) FILTER (WHERE model_code IS NOT NULL AND field_code IS NOT NULL)
FROM dynamic_model_field_assignment WHERE deleted = false;

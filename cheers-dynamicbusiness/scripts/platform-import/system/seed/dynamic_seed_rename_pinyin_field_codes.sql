-- ============================================================================
-- 一次性修复：将字段库/基础字段中的中文拼音 field_code 改为英文
--
-- 影响范围：
--   dynamic_entity_type_base_field.field_code / library_field_id
--   dynamic_field.code（FLD-BASE-{entityType}-{fieldCode}）
--   dynamic_group_relation.target_code
--   dynamic_model_field_assignment.field_code
--   ent_customer.custom_fields（库字段键）
--
-- 幂等：已迁移则跳过
-- ============================================================================

SET search_path TO dynamicbusiness;

DO $$
DECLARE
  rec RECORD;
  old_lib_code TEXT;
  new_lib_code TEXT;
BEGIN
  FOR rec IN
    SELECT *
    FROM (VALUES
      ('billing',            'shi_fou_han_shui',   'tax_included'),
      ('billing',            'shou_fei_shi_jian',  'billing_time'),
      ('billing',            'shou_fei_zhuang_tai', 'billing_status'),
      ('customer',           'lian_xi_dian_hua',   'contact_phone'),
      ('emergency_resource', 'ke_yong_shu_liang',  'available_quantity'),
      ('emergency_resource', 'zong_shu_liang',     'total_quantity')
    ) AS t(entity_type_code, old_field_code, new_field_code)
  LOOP
    old_lib_code := 'FLD-BASE-' || rec.entity_type_code || '-' || rec.old_field_code;
    new_lib_code := 'FLD-BASE-' || rec.entity_type_code || '-' || rec.new_field_code;

    IF NOT EXISTS (
      SELECT 1
      FROM dynamic_entity_type_base_field
      WHERE entity_type_code = rec.entity_type_code
        AND field_code IN (rec.old_field_code, old_lib_code)
        AND deleted = false
    ) THEN
      CONTINUE;
    END IF;

    UPDATE dynamic_field
    SET code = new_lib_code,
        updater = 'seed-rename-pinyin',
        update_time = CURRENT_TIMESTAMP
    WHERE code = old_lib_code
      AND deleted = false;

    UPDATE dynamic_entity_type_base_field
    SET field_code = new_lib_code,
        library_field_id = (
          SELECT f.id
          FROM dynamic_field f
          WHERE f.code = new_lib_code
            AND f.deleted = false
            AND f.tenant_id = dynamic_entity_type_base_field.tenant_id
          LIMIT 1
        ),
        updater = 'seed-rename-pinyin',
        update_time = CURRENT_TIMESTAMP
    WHERE entity_type_code = rec.entity_type_code
      AND field_code IN (rec.old_field_code, old_lib_code)
      AND deleted = false;

    UPDATE dynamic_group_relation
    SET target_code = new_lib_code,
        updater = 'seed-rename-pinyin',
        update_time = CURRENT_TIMESTAMP
    WHERE target_code = old_lib_code
      AND deleted = false;

    UPDATE dynamic_model_field_assignment
    SET field_code = new_lib_code,
        updater = 'seed-rename-pinyin',
        update_time = CURRENT_TIMESTAMP
    WHERE field_code = old_lib_code
      AND deleted = false;
  END LOOP;
END $$;

-- 实体实例：custom_fields 中以库 code 为键的存量数据
UPDATE ent_customer
SET custom_fields = (custom_fields - 'FLD-BASE-customer-lian_xi_dian_hua')
  || jsonb_build_object(
    'FLD-BASE-customer-contact_phone',
    custom_fields -> 'FLD-BASE-customer-lian_xi_dian_hua'
  ),
  updater = 'seed-rename-pinyin',
  update_time = CURRENT_TIMESTAMP
WHERE custom_fields ? 'FLD-BASE-customer-lian_xi_dian_hua';

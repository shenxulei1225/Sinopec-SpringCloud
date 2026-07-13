-- 还原 cleanup-field-dedupe 追加的「（业务类型）」后缀，恢复简短显示名
SET search_path TO dynamicbusiness;

BEGIN;

UPDATE dynamic_field f
SET name        = regexp_replace(f.name, '（[^）]*）$', ''),
    updater     = 'revert-field-dedupe-rename',
    update_time = CURRENT_TIMESTAMP
WHERE f.deleted = false
  AND f.updater = 'cleanup-field-dedupe'
  AND f.name ~ '（[^）]*）$';

UPDATE dynamic_entity_type_base_field bf
SET field_name  = f.name,
    updater     = 'revert-field-dedupe-rename',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_field f
WHERE bf.library_field_id = f.id
  AND bf.deleted = false
  AND f.deleted = false
  AND bf.field_name IS DISTINCT FROM f.name;

COMMIT;

-- 所属设施类字段应恢复为简短名称
SELECT id, code, name
FROM dynamic_field
WHERE deleted = false
  AND code LIKE '%REF_FACILITY%'
ORDER BY id;

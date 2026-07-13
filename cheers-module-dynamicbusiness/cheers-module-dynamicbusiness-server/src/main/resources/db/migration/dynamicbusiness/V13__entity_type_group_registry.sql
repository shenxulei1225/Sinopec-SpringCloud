-- 数据类型分组写入通用 dynamic_group（group_type=ENTITY_TYPE）；清理误建的独立注册表
SET search_path TO dynamicbusiness;

DROP TABLE IF EXISTS dynamic_entity_type_group;
DROP SEQUENCE IF EXISTS dynamic_entity_type_group_id_seq;

INSERT INTO dynamic_group (group_type, code, name, sort, status, creator, create_time, updater, update_time, deleted, tenant_id)
SELECT DISTINCT
    'ENTITY_TYPE',
    'ETG-SEED-' || md5(trim(et.group_name) || '-' || et.tenant_id::text),
    trim(et.group_name),
    0,
    1,
    'system',
    CURRENT_TIMESTAMP,
    'system',
    CURRENT_TIMESTAMP,
    false,
    et.tenant_id
FROM dynamic_entity_type et
WHERE et.deleted = false
  AND et.group_name IS NOT NULL
  AND trim(et.group_name) <> ''
  AND NOT EXISTS (
      SELECT 1
      FROM dynamic_group g
      WHERE g.deleted = false
        AND g.group_type = 'ENTITY_TYPE'
        AND g.tenant_id = et.tenant_id
        AND g.name = trim(et.group_name)
  );

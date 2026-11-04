-- 联动这几列的名称和用途一次性写上。
-- 详情标题读字段名称。执行按用途找到列。不另建列。

SET search_path TO dynamicbusiness, public;

UPDATE dynamic_field
SET name = '联动事件',
    semantic_type = 'LINKAGE_EVENT',
    updater = 'flyway-v187',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'F-52da82e319184f80b6cb40d030d5c017'
  AND (name IS DISTINCT FROM '联动事件' OR semantic_type IS DISTINCT FROM 'LINKAGE_EVENT');

UPDATE dynamic_field
SET name = '触发方式',
    semantic_type = 'LINKAGE_MATCH',
    updater = 'flyway-v187',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'F-4caa3bbacc99442b9cefdc192af6a362'
  AND (name IS DISTINCT FROM '触发方式' OR semantic_type IS DISTINCT FROM 'LINKAGE_MATCH');

UPDATE dynamic_field
SET name = '联动方式',
    semantic_type = 'LINKAGE_MODE',
    updater = 'flyway-v187',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'F-471fe2c2cedc4181987e6fcdb5afc47c'
  AND (name IS DISTINCT FROM '联动方式' OR semantic_type IS DISTINCT FROM 'LINKAGE_MODE');

UPDATE dynamic_field
SET name = '执行动作',
    semantic_type = 'LINKAGE_ACTION',
    updater = 'flyway-v187',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'F-59784b7cbf854b509a8624165a7f0cee'
  AND (name IS DISTINCT FROM '执行动作' OR semantic_type IS DISTINCT FROM 'LINKAGE_ACTION');

UPDATE dynamic_field
SET semantic_type = 'LINKAGE_SOURCE',
    updater = 'flyway-v187',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'F-f28221a960e44a05a02454b1b11563c1'
  AND semantic_type IS DISTINCT FROM 'LINKAGE_SOURCE';

UPDATE dynamic_field
SET semantic_type = 'LINKAGE_EXECUTION',
    updater = 'flyway-v187',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND code = 'F-6fb90a47274748a3b708ad32a7dc4ce0'
  AND semantic_type IS DISTINCT FROM 'LINKAGE_EXECUTION';

UPDATE dynamic_entity_type_base_field
SET field_name = '联动事件',
    updater = 'flyway-v187',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND field_code = 'F-52da82e319184f80b6cb40d030d5c017'
  AND field_name IS DISTINCT FROM '联动事件';

UPDATE dynamic_entity_type_base_field
SET field_name = '触发方式',
    updater = 'flyway-v187',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND field_code = 'F-4caa3bbacc99442b9cefdc192af6a362'
  AND field_name IS DISTINCT FROM '触发方式';

UPDATE dynamic_entity_type_base_field
SET field_name = '联动方式',
    updater = 'flyway-v187',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND field_code = 'F-471fe2c2cedc4181987e6fcdb5afc47c'
  AND field_name IS DISTINCT FROM '联动方式';

UPDATE dynamic_entity_type_base_field
SET field_name = '执行动作',
    updater = 'flyway-v187',
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND field_code = 'F-59784b7cbf854b509a8624165a7f0cee'
  AND field_name IS DISTINCT FROM '执行动作';

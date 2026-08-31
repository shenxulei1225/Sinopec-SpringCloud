-- ============================================================================
-- 下线 DOMAIN「巡检点」(patrol_point)：点位唯一台账 + 分类区分用途；
-- 安防干净视图以后用 SCOPE「巡逻点」等命名，禁止再用「巡检点」。
-- 定稿：docs/superpowers/specs/2026-08-30-point-catalog-retire-patrol-point-domain.md
-- 幂等；仅本地/导入后执行，不替代 Flyway。
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 软删目录注册、配置、门户叶子
UPDATE dynamic_entity_type
SET deleted = true, status = 'inactive', data_layout_id = NULL,
    updater = 'retire-patrol-point-domain', update_time = CURRENT_TIMESTAMP
WHERE code = 'patrol_point' AND deleted = false;

UPDATE dynamic_entity_type_config
SET deleted = true, status = 0,
    updater = 'retire-patrol-point-domain', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'patrol_point' AND deleted = false;

UPDATE dynamic_business
SET deleted = true, status = 'inactive',
    updater = 'retire-patrol-point-domain', update_time = CURRENT_TIMESTAMP
WHERE code = 'patrol_point' AND deleted = false;

UPDATE dynamic_business_entry e
SET deleted = true, status = 'inactive',
    updater = 'retire-patrol-point-domain', update_time = CURRENT_TIMESTAMP
FROM dynamic_business b
WHERE e.business_id = b.id
  AND b.code = 'patrol_point'
  AND e.deleted = false;

-- 2) 软删该目录数据页布局行
UPDATE dm_data_tab_layout
SET deleted = true,
    updater = 'retire-patrol-point-domain', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'patrol_point' AND deleted = false;

UPDATE dm_data_tab_column_relation
SET deleted = true,
    updater = 'retire-patrol-point-domain', update_time = CURRENT_TIMESTAMP
WHERE entity_type_code = 'patrol_point' AND deleted = false;

-- 3) 软删 DOMAIN 自动建的域分组节点（分类树上的「巡检点」文件夹）
--    必须同时 status=0：禁止 deleted=true 仍 status=1（启用），否则按启用过滤/缓存幽灵节点会回树
UPDATE dynamic_category
SET deleted = true, status = 0,
    updater = 'retire-patrol-point-domain', update_time = CURRENT_TIMESTAMP
WHERE code = 'patrol_point_dir' AND (deleted = false OR status = 1);

-- 4) 软删「巡检点」型号（point_patrol）；其下实体并入标准点位
UPDATE ent_point_t1
SET model_id = (SELECT id FROM dynamic_model WHERE code = 'point_standard' AND deleted = false LIMIT 1),
    domain = NULL,
    updater = 'retire-patrol-point-domain', update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND model_id = (SELECT id FROM dynamic_model WHERE code = 'point_patrol' AND deleted = false LIMIT 1);

UPDATE dynamic_model
SET deleted = true, status = 0,
    updater = 'retire-patrol-point-domain', update_time = CURRENT_TIMESTAMP
WHERE code = 'point_patrol' AND deleted = false;

-- 5) 共享点位脏域：标准点位型号却带 domain=patrol → 与型号权威对齐为空
UPDATE ent_point_t1 e
SET domain = NULL,
    updater = 'retire-patrol-point-domain', update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.model_id = m.id
  AND e.deleted = false
  AND m.code = 'point_standard'
  AND m.deleted = false
  AND e.domain IS NOT NULL
  AND btrim(e.domain) <> '';

-- 软删废止型号的字段分配
UPDATE dynamic_model_field_assignment
SET deleted = true,
    updater = 'retire-patrol-point-domain', update_time = CURRENT_TIMESTAMP
WHERE model_code = 'point_patrol' AND deleted = false;

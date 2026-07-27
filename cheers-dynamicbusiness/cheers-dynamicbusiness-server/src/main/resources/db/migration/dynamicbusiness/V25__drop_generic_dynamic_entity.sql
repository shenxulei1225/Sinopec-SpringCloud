-- =============================================================================
-- V25: 废止 GENERIC 通用实体表 dynamic_entity
-- =============================================================================
-- 动态业务实体仅存 ent_* 专用表；EntityDO 注解改用非物理占位符 __entity_dynamic__，
-- 由 EntityTableNameHandler + EntityRepository 改写。空壳通用表删除，避免无上下文时静默回落。
-- =============================================================================

DROP TABLE IF EXISTS dynamic_entity CASCADE;

DROP SEQUENCE IF EXISTS dynamic_entity_id_seq CASCADE;
DROP SEQUENCE IF EXISTS dynamic_entity_seq CASCADE;

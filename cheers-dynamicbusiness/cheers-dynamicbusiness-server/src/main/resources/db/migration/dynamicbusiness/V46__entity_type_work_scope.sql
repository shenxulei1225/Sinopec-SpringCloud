-- V46: 数据目录作用域（全网 / 站场级）
-- NETWORK=全网；FACILITY=站场级（默认）

SET search_path TO dynamicbusiness, public;

ALTER TABLE dynamicbusiness.dynamic_entity_type
  ADD COLUMN IF NOT EXISTS work_scope varchar(16) NOT NULL DEFAULT 'FACILITY';

COMMENT ON COLUMN dynamicbusiness.dynamic_entity_type.work_scope IS
  'Catalog work scope: NETWORK=network-wide; FACILITY=facility-scoped';

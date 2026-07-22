-- 设备台账（动态业务 entity）↔ 场景模型实例（ActorInstance）
ALTER TABLE twin.twin_mapping
    ADD COLUMN IF NOT EXISTS entity_id BIGINT,
    ADD COLUMN IF NOT EXISTS entity_type_code VARCHAR(64);

COMMENT ON COLUMN twin.twin_mapping.mapping_type IS '映射类型：1=Facility-ActorInstance；2=EquipmentEntity-ActorInstance';
COMMENT ON COLUMN twin.twin_mapping.entity_id IS '动态业务实体 ID（mapping_type=2 时为设备）';
COMMENT ON COLUMN twin.twin_mapping.entity_type_code IS '动态业务实体类型编码，如 equipment';

CREATE INDEX IF NOT EXISTS idx_twin_mapping_entity_active
    ON twin.twin_mapping (entity_id, entity_type_code, status, deleted)
    WHERE entity_id IS NOT NULL;

CREATE INDEX IF NOT EXISTS idx_twin_mapping_type_actor_active
    ON twin.twin_mapping (mapping_type, actor_instance_id, status, deleted);

ALTER TABLE twin.twin_mapping_history
    ADD COLUMN IF NOT EXISTS entity_id BIGINT,
    ADD COLUMN IF NOT EXISTS entity_type_code VARCHAR(64);

COMMENT ON COLUMN twin.twin_mapping_history.entity_id IS '动态业务实体 ID（可选）';
COMMENT ON COLUMN twin.twin_mapping_history.entity_type_code IS '动态业务实体类型编码（可选）';

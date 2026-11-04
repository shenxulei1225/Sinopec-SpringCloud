-- 点位名称给人看：哪台设备的哪个采集点。
-- 点位编码保持不动：分区编号加完整标签名，例如 GKYB01/SB1_I_LOW。Gateway 认这一整段。

SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.ent_point_binding_t123 point
SET name = equipment.name || '的' || field.name,
    code = point.zone_code || '/' || point.tag_name,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.ent_equipment_t123 equipment,
     dynamicbusiness.dynamic_field field
WHERE point.tenant_id = 123
  AND point.deleted = false
  AND equipment.id = point.equipment_id
  AND equipment.deleted = false
  AND field.tenant_id = 123
  AND field.deleted = false
  AND field.code = point.point_field_code;

DROP INDEX IF EXISTS dynamicbusiness.uk_ent_point_binding_t123_zone_code;

CREATE UNIQUE INDEX IF NOT EXISTS uk_ent_point_binding_t123_code
    ON dynamicbusiness.ent_point_binding_t123 (tenant_id, code)
    WHERE deleted = false AND code IS NOT NULL;

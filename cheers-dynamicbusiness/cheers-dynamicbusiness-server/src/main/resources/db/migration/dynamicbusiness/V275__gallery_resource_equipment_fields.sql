-- 应急资源型号参照设备管理的档案和能力字段，打开后能看规格、厂家、防爆等级等，不只剩单位。
-- 放置位置仍是点位。不把设备的安装位置、分区、坐标再抄一份。
-- 设备上的检查参数、回路和水位是在役设备的运行量，不挂到应急资源。

ALTER TABLE dynamicbusiness.ent_emergency_resource_t123
    ADD COLUMN IF NOT EXISTS brand text,
    ADD COLUMN IF NOT EXISTS equipment_model text,
    ADD COLUMN IF NOT EXISTS place_of_origin text,
    ADD COLUMN IF NOT EXISTS fld_com_012 character varying,
    ADD COLUMN IF NOT EXISTS criticality text,
    ADD COLUMN IF NOT EXISTS serial_number text,
    ADD COLUMN IF NOT EXISTS archive_no text,
    ADD COLUMN IF NOT EXISTS barcode text,
    ADD COLUMN IF NOT EXISTS manufacturer text,
    ADD COLUMN IF NOT EXISTS supplier text,
    ADD COLUMN IF NOT EXISTS responsible_person text,
    ADD COLUMN IF NOT EXISTS custodian text,
    ADD COLUMN IF NOT EXISTS maintainer text,
    ADD COLUMN IF NOT EXISTS install_date date,
    ADD COLUMN IF NOT EXISTS commission_date date,
    ADD COLUMN IF NOT EXISTS purchase_date date,
    ADD COLUMN IF NOT EXISTS warranty_expiry date,
    ADD COLUMN IF NOT EXISTS expected_service_life numeric,
    ADD COLUMN IF NOT EXISTS maintenance_cycle text,
    ADD COLUMN IF NOT EXISTS safety_level text,
    ADD COLUMN IF NOT EXISTS explosion_proof_grade text,
    ADD COLUMN IF NOT EXISTS waterproof_grade text,
    ADD COLUMN IF NOT EXISTS remark text,
    ADD COLUMN IF NOT EXISTS operation_status character varying;

INSERT INTO dynamicbusiness.dynamic_entity_type_base_field (
    entity_type_code, field_code, field_name, data_type, required, status,
    creator, tenant_id, library_field_id, sort_order
)
SELECT 'emergency_resource', b.field_code, b.field_name, b.data_type, false, 1,
       'V275', 123, b.library_field_id, b.sort_order
FROM dynamicbusiness.dynamic_entity_type_base_field b
WHERE b.tenant_id = 123
  AND b.entity_type_code = 'equipment'
  AND b.deleted = false
  AND b.field_code IN (
      'brand', 'equipment_model', 'place_of_origin', 'FLD-COM-012', 'criticality',
      'serial_number', 'archive_no', 'barcode', 'manufacturer', 'supplier',
      'responsible_person', 'custodian', 'maintainer', 'install_date', 'commission_date',
      'purchase_date', 'warranty_expiry', 'expected_service_life', 'maintenance_cycle',
      'safety_level', 'explosion_proof_grade', 'waterproof_grade', 'remark', 'operation_status'
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_type_base_field x
      WHERE x.tenant_id = 123 AND x.deleted = false
        AND x.entity_type_code = 'emergency_resource'
        AND x.field_code = b.field_code
  );

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, sort, field_source, creator, tenant_id, model_code, field_code,
    detail_visible, create_visible, edit_visible
)
SELECT m.id, b.library_field_id, false, COALESCE(b.sort_order, 40), 'BASE', 'V275', 123,
       m.code, b.field_code, true, true, true
FROM dynamicbusiness.dynamic_model m
JOIN dynamicbusiness.dynamic_entity_type_base_field b
  ON b.tenant_id = 123 AND b.entity_type_code = 'emergency_resource' AND b.deleted = false
 AND b.creator = 'V275'
WHERE m.tenant_id = 123 AND m.deleted = false AND m.entity_type_code = 'emergency_resource'
  AND m.creator = 'V271'
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment a
      WHERE a.tenant_id = 123 AND a.deleted = false
        AND a.model_id = m.id AND a.field_code = b.field_code
  );

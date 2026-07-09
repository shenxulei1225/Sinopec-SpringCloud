-- ============================================================================
-- 迁移脚本：将 equipment 表的 area_id 列重命名为 region_id
-- 版本：V1.0.26
-- 描述：统一命名规范,与 Region 业务类型保持一致
-- 执行日期：2026-01-07
-- ============================================================================

-- 1. 重命名 equipment 表的 area_id 列为 region_id
ALTER TABLE equipment RENAME COLUMN area_id TO region_id;

-- 2. 更新列注释
COMMENT ON COLUMN equipment.region_id IS '区域ID（关联 Region 业务类型的实体）';

-- 3. 删除旧索引并创建新索引
DROP INDEX IF EXISTS idx_equipment_area;
DROP INDEX IF EXISTS idx_equipment_area_status;

CREATE INDEX IF NOT EXISTS idx_equipment_region ON equipment(region_id);
CREATE INDEX IF NOT EXISTS idx_equipment_region_status ON equipment(region_id, equipment_status);

-- 4. 更新 system_business_type_base_field 表中的字段配置
UPDATE system_business_type_base_field 
SET field_code = 'regionId',
    field_name = '所属区域',
    description = '设备所属区域（关联 Region 业务类型）',
    type_config = '{"refBusinessType": "Region", "refDisplayField": "name"}'
WHERE entity_type_code = 'equipment' 
  AND field_code = 'areaId';

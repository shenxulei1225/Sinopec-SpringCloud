-- 设备表仅保留配置中的所属分区 / 所属区域 REF 固定列；删除无基础字段配置的 facility 残留列。
ALTER TABLE IF EXISTS ent_equipment DROP COLUMN IF EXISTS fld_base_equipment_ref_facility;
ALTER TABLE IF EXISTS ent_equipment_t1 DROP COLUMN IF EXISTS fld_base_equipment_ref_facility;
ALTER TABLE IF EXISTS ent_equipment_t2 DROP COLUMN IF EXISTS fld_base_equipment_ref_facility;

-- Fix legacy pinyin entity_type_code / dedicated_table_name before re-import.
SET search_path TO dynamicbusiness;

UPDATE dynamic_model SET entity_type_code = 'billing' WHERE entity_type_code = 'shou_fei' AND deleted = false;
UPDATE dynamic_model SET entity_type_code = 'customer' WHERE entity_type_code = 'ke_hu' AND deleted = false;
UPDATE dynamic_model SET entity_type_code = 'inspection_item' WHERE entity_type_code = 'jian_cha_nei_rong' AND deleted = false;
UPDATE dynamic_model SET entity_type_code = 'inspection_point' WHERE entity_type_code = 'dian_wei' AND deleted = false;
UPDATE dynamic_model SET entity_type_code = 'route' WHERE entity_type_code = 'lu_xian_guan_li' AND deleted = false;
UPDATE dynamic_model SET entity_type_code = 'patrol' WHERE entity_type_code = 'xun_jian' AND deleted = false;
UPDATE dynamic_model SET entity_type_code = 'emergency_resource' WHERE entity_type_code = 'ying_ji_zi_yuan' AND deleted = false;
UPDATE dynamic_model SET entity_type_code = 'emergency_team' WHERE entity_type_code = 'ying_ji_dui_wu' AND deleted = false;
UPDATE dynamic_model SET entity_type_code = 'spare_parts' WHERE entity_type_code = 'spare_part' AND deleted = false;

UPDATE dynamic_entity_type_config SET dedicated_table_name = 'ent_billing' WHERE entity_type_code = 'billing' AND deleted = false;
UPDATE dynamic_entity_type_config SET dedicated_table_name = 'ent_customer' WHERE entity_type_code = 'customer' AND deleted = false;
UPDATE dynamic_entity_type_config SET dedicated_table_name = 'ent_emergency_resource' WHERE entity_type_code = 'emergency_resource' AND deleted = false;
UPDATE dynamic_entity_type_config SET dedicated_table_name = 'ent_emergency_team' WHERE entity_type_code = 'emergency_team' AND deleted = false;
UPDATE dynamic_entity_type_config SET dedicated_table_name = 'ent_inspection_item' WHERE entity_type_code = 'inspection_item' AND deleted = false;
UPDATE dynamic_entity_type_config SET dedicated_table_name = 'ent_inspection_point' WHERE entity_type_code = 'inspection_point' AND deleted = false;
UPDATE dynamic_entity_type_config SET dedicated_table_name = 'ent_patrol' WHERE entity_type_code = 'patrol' AND deleted = false;
UPDATE dynamic_entity_type_config SET dedicated_table_name = 'ent_route' WHERE entity_type_code = 'route' AND deleted = false;

UPDATE dynamic_entity_type SET dedicated_table_name = 'ent_billing' WHERE code = 'billing' AND deleted = false;
UPDATE dynamic_entity_type SET dedicated_table_name = 'ent_customer' WHERE code = 'customer' AND deleted = false;
UPDATE dynamic_entity_type SET dedicated_table_name = 'ent_emergency_resource' WHERE code = 'emergency_resource' AND deleted = false;
UPDATE dynamic_entity_type SET dedicated_table_name = 'ent_emergency_team' WHERE code = 'emergency_team' AND deleted = false;
UPDATE dynamic_entity_type SET dedicated_table_name = 'ent_inspection_item' WHERE code = 'inspection_item' AND deleted = false;
UPDATE dynamic_entity_type SET dedicated_table_name = 'ent_inspection_point' WHERE code = 'inspection_point' AND deleted = false;
UPDATE dynamic_entity_type SET dedicated_table_name = 'ent_patrol' WHERE code = 'patrol' AND deleted = false;
UPDATE dynamic_entity_type SET dedicated_table_name = 'ent_route' WHERE code = 'route' AND deleted = false;

UPDATE dynamic_model_field_assignment a
SET target_entity_type = m.entity_type_code
FROM dynamic_model m
WHERE a.model_id = m.id AND a.deleted = false AND m.deleted = false
  AND a.target_entity_type IS NOT NULL
  AND a.target_entity_type IN (
    'shou_fei', 'ke_hu', 'jian_cha_nei_rong', 'dian_wei', 'lu_xian_guan_li',
    'xun_jian', 'ying_ji_zi_yuan', 'ying_ji_dui_wu', 'spare_part'
  );

UPDATE dynamic_model_field_assignment SET target_entity_type = 'billing' WHERE target_entity_type = 'shou_fei' AND deleted = false;
UPDATE dynamic_model_field_assignment SET target_entity_type = 'customer' WHERE target_entity_type = 'ke_hu' AND deleted = false;
UPDATE dynamic_model_field_assignment SET target_entity_type = 'inspection_item' WHERE target_entity_type = 'jian_cha_nei_rong' AND deleted = false;
UPDATE dynamic_model_field_assignment SET target_entity_type = 'inspection_point' WHERE target_entity_type = 'dian_wei' AND deleted = false;
UPDATE dynamic_model_field_assignment SET target_entity_type = 'route' WHERE target_entity_type = 'lu_xian_guan_li' AND deleted = false;
UPDATE dynamic_model_field_assignment SET target_entity_type = 'patrol' WHERE target_entity_type = 'xun_jian' AND deleted = false;
UPDATE dynamic_model_field_assignment SET target_entity_type = 'emergency_resource' WHERE target_entity_type = 'ying_ji_zi_yuan' AND deleted = false;
UPDATE dynamic_model_field_assignment SET target_entity_type = 'emergency_team' WHERE target_entity_type = 'ying_ji_dui_wu' AND deleted = false;
UPDATE dynamic_model_field_assignment SET target_entity_type = 'spare_parts' WHERE target_entity_type = 'spare_part' AND deleted = false;

-- 设备位置跟它的点位走。点位已经在舱室里的，设备改挂到这些舱室，并去掉防火区那条。
-- 井盖、配电柜的点位没有舱室，设备继续挂在防火区。设备种类（照明开关、液压井盖）那条不动。

SET search_path TO dynamicbusiness, public;

INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t123 (
    entity_id, category_id, entity_type_code, sort, creator, updater,
    deleted, tenant_id, domain
)
SELECT DISTINCT
    equipment.id,
    cabin.id,
    'equipment',
    zone_link.sort,
    'point-table-seed',
    'point-table-seed',
    false,
    123,
    zone_link.domain
FROM dynamicbusiness.ent_equipment_t123 equipment
JOIN dynamicbusiness.dynamic_entity_category_relation_t123 zone_link
  ON zone_link.entity_id = equipment.id
 AND zone_link.tenant_id = 123
 AND zone_link.deleted = false
 AND zone_link.entity_type_code = 'equipment'
JOIN dynamicbusiness.dynamic_category zone_cat
  ON zone_cat.id = zone_link.category_id
 AND zone_cat.tenant_id = 123
 AND zone_cat.deleted = false
 AND zone_cat.category_type_code = 'zone'
 AND zone_cat.level = 4
JOIN dynamicbusiness.ent_point_binding_t123 point
  ON point.equipment_id = equipment.id
 AND point.tenant_id = 123
 AND point.deleted = false
JOIN dynamicbusiness.dynamic_entity_category_relation_t123 point_link
  ON point_link.entity_id = point.id
 AND point_link.tenant_id = 123
 AND point_link.deleted = false
 AND point_link.entity_type_code = 'point_binding'
JOIN dynamicbusiness.dynamic_category cabin
  ON cabin.id = point_link.category_id
 AND cabin.tenant_id = 123
 AND cabin.deleted = false
 AND cabin.category_type_code = 'zone'
 AND cabin.level = 5
 AND cabin.parent_id = zone_cat.id
WHERE equipment.tenant_id = 123
  AND equipment.deleted = false
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_entity_category_relation_t123 existing
      WHERE existing.tenant_id = 123
        AND existing.entity_type_code = 'equipment'
        AND existing.entity_id = equipment.id
        AND existing.category_id = cabin.id
  );

UPDATE dynamicbusiness.dynamic_entity_category_relation_t123 zone_link
SET deleted = true,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.dynamic_category zone_cat
WHERE zone_link.tenant_id = 123
  AND zone_link.deleted = false
  AND zone_link.entity_type_code = 'equipment'
  AND zone_cat.id = zone_link.category_id
  AND zone_cat.category_type_code = 'zone'
  AND zone_cat.level = 4
  AND zone_cat.deleted = false
  AND EXISTS (
      SELECT 1
      FROM dynamicbusiness.dynamic_entity_category_relation_t123 cabin_link
      JOIN dynamicbusiness.dynamic_category cabin
        ON cabin.id = cabin_link.category_id
       AND cabin.deleted = false
       AND cabin.category_type_code = 'zone'
       AND cabin.level = 5
       AND cabin.parent_id = zone_cat.id
      WHERE cabin_link.tenant_id = 123
        AND cabin_link.deleted = false
        AND cabin_link.entity_type_code = 'equipment'
        AND cabin_link.entity_id = zone_link.entity_id
  );

DO $$
DECLARE
    switch_on_zone integer;
    manhole_on_zone integer;
    cabinet_on_zone integer;
    switch_without_cabin integer;
BEGIN
    SELECT count(DISTINCT equipment.id) INTO switch_on_zone
    FROM dynamicbusiness.ent_equipment_t123 equipment
    JOIN dynamicbusiness.dynamic_model model ON model.id = equipment.model_id AND model.name = '开关'
    JOIN dynamicbusiness.dynamic_entity_category_relation_t123 link
      ON link.entity_id = equipment.id AND link.deleted = false AND link.entity_type_code = 'equipment'
    JOIN dynamicbusiness.dynamic_category category_row
      ON category_row.id = link.category_id AND category_row.level = 4
     AND category_row.category_type_code = 'zone' AND category_row.deleted = false
    WHERE equipment.tenant_id = 123 AND equipment.deleted = false;

    SELECT count(DISTINCT equipment.id) INTO manhole_on_zone
    FROM dynamicbusiness.ent_equipment_t123 equipment
    JOIN dynamicbusiness.dynamic_model model ON model.id = equipment.model_id AND model.name = '液压井盖'
    JOIN dynamicbusiness.dynamic_entity_category_relation_t123 link
      ON link.entity_id = equipment.id AND link.deleted = false AND link.entity_type_code = 'equipment'
    JOIN dynamicbusiness.dynamic_category category_row
      ON category_row.id = link.category_id AND category_row.level = 4
     AND category_row.category_type_code = 'zone' AND category_row.deleted = false
    WHERE equipment.tenant_id = 123 AND equipment.deleted = false;

    SELECT count(DISTINCT equipment.id) INTO cabinet_on_zone
    FROM dynamicbusiness.ent_equipment_t123 equipment
    JOIN dynamicbusiness.dynamic_model model
      ON model.id = equipment.model_id
     AND model.name IN ('应急配电柜', '照明配电柜进线', '照明配电柜馈线')
    JOIN dynamicbusiness.dynamic_entity_category_relation_t123 link
      ON link.entity_id = equipment.id AND link.deleted = false AND link.entity_type_code = 'equipment'
    JOIN dynamicbusiness.dynamic_category category_row
      ON category_row.id = link.category_id AND category_row.level = 4
     AND category_row.category_type_code = 'zone' AND category_row.deleted = false
    WHERE equipment.tenant_id = 123 AND equipment.deleted = false;

    SELECT count(*) INTO switch_without_cabin
    FROM dynamicbusiness.ent_equipment_t123 equipment
    JOIN dynamicbusiness.dynamic_model model ON model.id = equipment.model_id AND model.name = '开关'
    WHERE equipment.tenant_id = 123 AND equipment.deleted = false
      AND NOT EXISTS (
          SELECT 1
          FROM dynamicbusiness.dynamic_entity_category_relation_t123 link
          JOIN dynamicbusiness.dynamic_category category_row
            ON category_row.id = link.category_id AND category_row.level = 5
           AND category_row.category_type_code = 'zone' AND category_row.deleted = false
          WHERE link.entity_id = equipment.id
            AND link.deleted = false
            AND link.entity_type_code = 'equipment'
            AND link.tenant_id = 123
      );

    IF switch_on_zone <> 0 OR manhole_on_zone <> 144 OR cabinet_on_zone <> 9
       OR switch_without_cabin <> 0 THEN
        RAISE EXCEPTION
            'equipment cabin adjust failed switch_on_zone=% manhole=% cabinet=% switch_without_cabin=%',
            switch_on_zone, manhole_on_zone, cabinet_on_zone, switch_without_cabin;
    END IF;
END $$;

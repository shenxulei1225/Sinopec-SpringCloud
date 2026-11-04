-- 照明和水泵共用的「开」改成「开关」。同一个标签写成 1 是开，写成 0 是关。
-- 点位挂在舱室、名称里还没有舱室名时，把舱室名加进名称。风机和井盖各自的「开」不动。点位编码不动。

SET search_path TO dynamicbusiness, public;

UPDATE dynamicbusiness.dynamic_field
SET name = '开关',
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
WHERE code = 'F-ab068b1dd098457cba93ad89cf095f90'
  AND deleted = false
  AND name IS DISTINCT FROM '开关';

UPDATE dynamicbusiness.ent_point_binding_t123 AS point
SET name = built.point_name,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
FROM (
    SELECT
        point_row.id,
        CASE
            WHEN place.cabin_name IS NOT NULL
             AND equipment.name NOT LIKE '%' || place.cabin_name || '%'
             AND equipment.name LIKE place.prefix || '%'
            THEN place.prefix
                 || place.cabin_name
                 || substring(equipment.name FROM char_length(place.prefix) + 1)
                 || '的' || field.name
            ELSE equipment.name || '的' || field.name
        END AS point_name
    FROM dynamicbusiness.ent_point_binding_t123 point_row
    JOIN dynamicbusiness.ent_equipment_t123 equipment
      ON equipment.id = point_row.equipment_id
     AND equipment.deleted = false
    JOIN dynamicbusiness.dynamic_field field
      ON field.code = point_row.point_field_code
     AND field.deleted = false
    LEFT JOIN LATERAL (
        SELECT cabin.name AS cabin_name,
               segment.name || regexp_replace(fire.name, '#.*$', '') AS prefix
        FROM dynamicbusiness.dynamic_entity_category_relation_t123 link
        JOIN dynamicbusiness.dynamic_category cabin
          ON cabin.id = link.category_id
         AND cabin.deleted = false
         AND cabin.category_type_code = 'zone'
         AND cabin.level = 5
        JOIN dynamicbusiness.dynamic_category fire ON fire.id = cabin.parent_id
        JOIN dynamicbusiness.dynamic_category segment ON segment.id = fire.parent_id
        WHERE link.entity_id = point_row.id
          AND link.entity_type_code = 'point_binding'
          AND link.deleted = false
          AND link.tenant_id = 123
        LIMIT 1
    ) place ON true
    WHERE point_row.deleted = false
      AND point_row.tenant_id = 123
) built
WHERE point.id = built.id
  AND point.name IS DISTINCT FROM built.point_name;

DO $$
DECLARE
    shared_name text;
    fan_open text;
    manhole_open text;
    yjal2_open text;
    yjal1_open text;
    yjal2_feedback text;
    pump_open text;
    fan_point text;
    lighting_without_cabin integer;
    bad_code integer;
BEGIN
    SELECT name INTO shared_name
    FROM dynamicbusiness.dynamic_field
    WHERE code = 'F-ab068b1dd098457cba93ad89cf095f90' AND deleted = false;

    SELECT name INTO fan_open
    FROM dynamicbusiness.dynamic_field
    WHERE code = 'F-a11c0e0000014e0a8f0e00000000c002' AND deleted = false;

    SELECT name INTO manhole_open
    FROM dynamicbusiness.dynamic_field
    WHERE code = 'F-a11c0e0000014e0a8f0e00000000c003' AND deleted = false;

    SELECT name INTO yjal2_open
    FROM dynamicbusiness.ent_point_binding_t123
    WHERE code = 'GKYB01/YJAL2_PT_OPEN' AND deleted = false;

    SELECT name INTO yjal1_open
    FROM dynamicbusiness.ent_point_binding_t123
    WHERE code = 'GKYB01/YJAL1_PT_OPEN' AND deleted = false;

    SELECT name INTO yjal2_feedback
    FROM dynamicbusiness.ent_point_binding_t123
    WHERE code = 'GKYB01/YJAL2_I_FK' AND deleted = false;

    SELECT name INTO pump_open
    FROM dynamicbusiness.ent_point_binding_t123
    WHERE code = 'GKYB01/SB1_PT_START1' AND deleted = false;

    SELECT name INTO fan_point
    FROM dynamicbusiness.ent_point_binding_t123
    WHERE code = 'GKYB01/FJ1_PT_OPEN' AND deleted = false;

    SELECT count(*) INTO lighting_without_cabin
    FROM dynamicbusiness.ent_point_binding_t123 point
    JOIN dynamicbusiness.ent_equipment_t123 equipment
      ON equipment.id = point.equipment_id AND equipment.deleted = false
    JOIN dynamicbusiness.dynamic_entity_category_relation_t123 link
      ON link.entity_id = point.id
     AND link.entity_type_code = 'point_binding'
     AND link.deleted = false
     AND link.tenant_id = 123
    JOIN dynamicbusiness.dynamic_category cabin
      ON cabin.id = link.category_id
     AND cabin.deleted = false
     AND cabin.category_type_code = 'zone'
     AND cabin.level = 5
    WHERE point.deleted = false
      AND equipment.name LIKE '%照明开关'
      AND point.name NOT LIKE '%' || cabin.name || '%';

    SELECT count(*) INTO bad_code
    FROM dynamicbusiness.ent_point_binding_t123 point
    WHERE point.deleted = false
      AND point.code IS DISTINCT FROM point.zone_code || '/' || point.tag_name;

    IF shared_name IS DISTINCT FROM '开关'
        OR fan_open IS DISTINCT FROM '开'
        OR manhole_open IS DISTINCT FROM '开'
        OR yjal2_open IS DISTINCT FROM '高科园路北1电力信息舱应急照明开关的开关'
        OR yjal1_open IS DISTINCT FROM '高科园路北1综合管道仓应急照明开关的开关'
        OR yjal2_feedback IS DISTINCT FROM '高科园路北1电力信息舱应急照明开关的运行反馈'
        OR pump_open IS DISTINCT FROM '高科园路北1综合管道仓水泵·1的开关'
        OR fan_point IS DISTINCT FROM '高科园路北1综合管道仓风机的开'
        OR lighting_without_cabin <> 0
        OR bad_code <> 0
    THEN
        RAISE EXCEPTION
            'point name adjust failed shared=% fan_field=% manhole_field=% yjal2=% yjal1=% feedback=% pump=% fan=% lighting_without_cabin=% bad_code=%',
            shared_name, fan_open, manhole_open, yjal2_open, yjal1_open, yjal2_feedback, pump_open, fan_point, lighting_without_cabin, bad_code;
    END IF;
END $$;

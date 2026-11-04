-- 照明、水泵：开和关是同一个写标签。开保持 1，关写入 0。
-- 照明不再保留关标签。水泵的 START1、START2 拆成两台设备，每台一个开标签。
-- 风机、井盖仍是开标签和关标签各一行。点位编码仍是分区编号加完整标签名。

SET search_path TO dynamicbusiness, public;

SELECT setval(
    'dynamicbusiness.ent_equipment_t123_id_seq',
    GREATEST(
        (SELECT COALESCE(MAX(id), 1) FROM dynamicbusiness.ent_equipment_t123),
        (SELECT last_value FROM dynamicbusiness.ent_equipment_t123_id_seq)
    )
);

CREATE TEMP TABLE pump_split ON COMMIT DROP AS
SELECT
    equipment.id AS src_id,
    nextval('dynamicbusiness.ent_equipment_t123_id_seq') AS dst_id,
    upper(replace(gen_random_uuid()::text, '-', '')) AS new_code
FROM dynamicbusiness.ent_equipment_t123 equipment
WHERE equipment.tenant_id = 123
  AND equipment.deleted = false
  AND equipment.model_id = 2544
  AND (
      (
          EXISTS (
              SELECT 1 FROM dynamicbusiness.ent_point_binding_t123 point
              WHERE point.equipment_id = equipment.id
                AND point.tenant_id = 123
                AND point.deleted = false
                AND point.tag_name ~ '_PT_START1$'
          )
          AND EXISTS (
              SELECT 1 FROM dynamicbusiness.ent_point_binding_t123 point
              WHERE point.equipment_id = equipment.id
                AND point.tenant_id = 123
                AND point.deleted = false
                AND point.tag_name ~ '_PT_START2$'
          )
      )
      OR (
          EXISTS (
              SELECT 1 FROM dynamicbusiness.ent_point_binding_t123 point
              WHERE point.equipment_id = equipment.id
                AND point.tenant_id = 123
                AND point.deleted = false
                AND point.tag_name ~ '_I_RUN1$'
          )
          AND EXISTS (
              SELECT 1 FROM dynamicbusiness.ent_point_binding_t123 point
              WHERE point.equipment_id = equipment.id
                AND point.tenant_id = 123
                AND point.deleted = false
                AND point.tag_name ~ '_I_RUN2$'
          )
      )
  );

INSERT INTO dynamicbusiness.ent_equipment_t123 (
    id, entity_type_code, model_id, name, custom_fields, parent_id,
    creator, create_time, updater, update_time, deleted, tenant_id,
    guid, code, status, sort, zone_id, facility_id, domain
)
SELECT
    split.dst_id,
    source.entity_type_code,
    source.model_id,
    source.name || '·2',
    source.custom_fields,
    source.parent_id,
    'point-table-seed',
    CURRENT_TIMESTAMP,
    'point-table-seed',
    CURRENT_TIMESTAMP,
    false,
    source.tenant_id,
    split.new_code,
    split.new_code,
    source.status,
    source.sort,
    source.zone_id,
    source.facility_id,
    source.domain
FROM pump_split split
JOIN dynamicbusiness.ent_equipment_t123 source ON source.id = split.src_id;

INSERT INTO dynamicbusiness.dynamic_entity_category_relation_t123 (
    entity_id, category_id, entity_type_code, sort, creator, updater,
    deleted, tenant_id, domain
)
SELECT
    split.dst_id,
    link.category_id,
    link.entity_type_code,
    link.sort,
    'point-table-seed',
    'point-table-seed',
    false,
    link.tenant_id,
    link.domain
FROM pump_split split
JOIN dynamicbusiness.dynamic_entity_category_relation_t123 link
  ON link.entity_id = split.src_id
 AND link.tenant_id = 123
 AND link.deleted = false
 AND link.entity_type_code = 'equipment';

UPDATE dynamicbusiness.ent_point_binding_t123 point
SET equipment_id = split.dst_id,
    point_field_code = CASE
        WHEN point.tag_name ~ '_PT_START2$' THEN 'F-ab068b1dd098457cba93ad89cf095f90'
        WHEN point.tag_name ~ '_I_RUN2$' THEN 'F-301fc454fb294c8ba3a1deebeecfe0ee'
        ELSE point.point_field_code
    END,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
FROM pump_split split
WHERE point.equipment_id = split.src_id
  AND point.tenant_id = 123
  AND point.deleted = false
  AND point.tag_name ~ '(_PT_START2|_I_RUN2)$';

UPDATE dynamicbusiness.ent_point_binding_t123 point
SET point_field_code = CASE
        WHEN point.tag_name ~ '_PT_START1$' THEN 'F-ab068b1dd098457cba93ad89cf095f90'
        WHEN point.tag_name ~ '_I_RUN1$' THEN 'F-301fc454fb294c8ba3a1deebeecfe0ee'
        ELSE point.point_field_code
    END,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.ent_equipment_t123 equipment
WHERE equipment.id = point.equipment_id
  AND equipment.tenant_id = 123
  AND equipment.deleted = false
  AND equipment.model_id = 2544
  AND point.tenant_id = 123
  AND point.deleted = false
  AND point.tag_name ~ '(_PT_START1|_I_RUN1)$'
  AND point.point_field_code IS DISTINCT FROM CASE
        WHEN point.tag_name ~ '_PT_START1$' THEN 'F-ab068b1dd098457cba93ad89cf095f90'
        WHEN point.tag_name ~ '_I_RUN1$' THEN 'F-301fc454fb294c8ba3a1deebeecfe0ee'
        ELSE point.point_field_code
    END;

UPDATE dynamicbusiness.ent_equipment_t123 equipment
SET name = equipment.name || '·1',
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
FROM pump_split split
WHERE equipment.id = split.src_id
  AND equipment.name !~ '·[12]$';

UPDATE dynamicbusiness.ent_point_binding_t123 point
SET name = equipment.name || '的' || field.name,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.ent_equipment_t123 equipment,
     dynamicbusiness.dynamic_field field
WHERE equipment.id = point.equipment_id
  AND equipment.tenant_id = 123
  AND equipment.deleted = false
  AND equipment.model_id = 2544
  AND field.tenant_id = 123
  AND field.deleted = false
  AND field.code = point.point_field_code
  AND point.tenant_id = 123
  AND point.deleted = false
  AND point.name IS DISTINCT FROM equipment.name || '的' || field.name;

UPDATE dynamicbusiness.ent_point_binding_t123 point
SET deleted = true,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.ent_equipment_t123 equipment
WHERE equipment.id = point.equipment_id
  AND equipment.model_id = 2594
  AND equipment.deleted = false
  AND point.tenant_id = 123
  AND point.deleted = false
  AND point.tag_name ~ '_PT_CLOSE$';

UPDATE dynamicbusiness.dynamic_entity_category_relation_t123 link
SET deleted = true,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamicbusiness.ent_point_binding_t123 point
JOIN dynamicbusiness.ent_equipment_t123 equipment ON equipment.id = point.equipment_id
WHERE link.entity_id = point.id
  AND link.tenant_id = 123
  AND link.deleted = false
  AND link.entity_type_code = 'point_binding'
  AND equipment.model_id = 2594
  AND point.tenant_id = 123
  AND point.deleted = true
  AND point.tag_name ~ '_PT_CLOSE$';

UPDATE dynamicbusiness.dynamic_model_field_assignment assignment
SET deleted = true,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
WHERE assignment.tenant_id = 123
  AND assignment.deleted = false
  AND assignment.model_id = 2594
  AND assignment.field_id = 11019;

INSERT INTO dynamicbusiness.dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable, sort,
    creator, updater, deleted, tenant_id, model_code, field_code, collection_point,
    create_visible, edit_visible, detail_visible, field_source
)
SELECT
    2544,
    field.id,
    false,
    false,
    false,
    false,
    CASE field.code
        WHEN 'F-ab068b1dd098457cba93ad89cf095f90' THEN 1003
        ELSE 1005
    END,
    'point-table-seed',
    'point-table-seed',
    false,
    123,
    'MODEL-0cd981fd17d3469ab323117cf878c07c',
    field.code,
    true,
    false,
    false,
    false,
    'USER_ADDED'
FROM dynamicbusiness.dynamic_field field
WHERE field.tenant_id = 123
  AND field.deleted = false
  AND field.code IN (
      'F-ab068b1dd098457cba93ad89cf095f90',
      'F-301fc454fb294c8ba3a1deebeecfe0ee'
  )
  AND NOT EXISTS (
      SELECT 1 FROM dynamicbusiness.dynamic_model_field_assignment existing
      WHERE existing.tenant_id = 123
        AND existing.deleted = false
        AND existing.model_id = 2544
        AND existing.field_id = field.id
  );

UPDATE dynamicbusiness.dynamic_model_field_assignment assignment
SET deleted = true,
    updater = 'point-table-seed',
    update_time = CURRENT_TIMESTAMP
WHERE assignment.tenant_id = 123
  AND assignment.deleted = false
  AND assignment.model_id = 2544
  AND assignment.field_id IN (11013, 11014, 11010, 11011);

DO $$
DECLARE
    close_left integer;
    both_left integer;
    old_fields integer;
    kept_close integer;
    bad_code integer;
    start2_wrong integer;
BEGIN
    SELECT count(*) INTO close_left
    FROM dynamicbusiness.ent_point_binding_t123 point
    JOIN dynamicbusiness.ent_equipment_t123 equipment ON equipment.id = point.equipment_id
    WHERE point.tenant_id = 123
      AND point.deleted = false
      AND equipment.model_id = 2594
      AND point.tag_name ~ '_PT_CLOSE$';

    SELECT count(*) INTO both_left
    FROM (
        SELECT point.equipment_id
        FROM dynamicbusiness.ent_point_binding_t123 point
        WHERE point.tenant_id = 123
          AND point.deleted = false
          AND point.tag_name ~ '_PT_START[12]$'
        GROUP BY point.equipment_id
        HAVING bool_or(point.tag_name ~ '_PT_START1$')
           AND bool_or(point.tag_name ~ '_PT_START2$')
    ) paired;

    SELECT count(*) INTO old_fields
    FROM dynamicbusiness.ent_point_binding_t123 point
    WHERE point.tenant_id = 123
      AND point.deleted = false
      AND point.point_field_code IN (
          'F-ba978da49b7e40a8848f71db77b034ad',
          'F-7aad6a8d3fef418b9d59ee8cf0131166',
          'F-2473419ead544c30a1c1ab1f96ca7077',
          'F-16dd57fa11f5492986e2e44dc022dd88'
      );

    SELECT count(*) INTO kept_close
    FROM dynamicbusiness.ent_point_binding_t123 point
    JOIN dynamicbusiness.ent_equipment_t123 equipment ON equipment.id = point.equipment_id
    WHERE point.tenant_id = 123
      AND point.deleted = false
      AND equipment.model_id IN (2593, 2595)
      AND point.tag_name ~ '_PT_CLOSE$';

    SELECT count(*) INTO bad_code
    FROM dynamicbusiness.ent_point_binding_t123 point
    WHERE point.tenant_id = 123
      AND point.deleted = false
      AND point.code IS DISTINCT FROM point.zone_code || '/' || point.tag_name;

    SELECT count(*) INTO start2_wrong
    FROM dynamicbusiness.ent_point_binding_t123 point
    WHERE point.tenant_id = 123
      AND point.deleted = false
      AND point.tag_name ~ '_PT_START2$'
      AND point.point_field_code IS DISTINCT FROM 'F-ab068b1dd098457cba93ad89cf095f90';

    IF close_left <> 0
        OR both_left <> 0
        OR old_fields <> 0
        OR kept_close = 0
        OR bad_code <> 0
        OR start2_wrong <> 0 THEN
        RAISE EXCEPTION
            'point adjust failed close=% both=% old=% kept_close=% bad_code=% start2_wrong=%',
            close_left, both_left, old_fields, kept_close, bad_code, start2_wrong;
    END IF;
END $$;

-- 消防分区不单独再建防火区。
-- 它是在现有防护区（分区账里的防火区）上划分的。
-- 本文件只撤回曾经按「另一套防火区」写入的记录。

BEGIN;

UPDATE dynamicbusiness.dynamic_entity_field_index i
SET deleted = true,
    updater = 'corridor-fire-compartment-withdraw',
    update_time = CURRENT_TIMESTAMP
WHERE i.tenant_id = 123
  AND COALESCE(i.deleted, false) = false
  AND i.entity_id IN (
      SELECT id
      FROM dynamicbusiness.ent_equipment_t123
      WHERE creator = 'corridor-fire-compartment'
        AND COALESCE(deleted, false) = false
  );

UPDATE dynamicbusiness.ent_equipment_t123
SET deleted = true,
    updater = 'corridor-fire-compartment-withdraw',
    update_time = CURRENT_TIMESTAMP
WHERE creator = 'corridor-fire-compartment'
  AND COALESCE(deleted, false) = false;

UPDATE dynamicbusiness.ent_zone_t123
SET deleted = true,
    updater = 'corridor-fire-compartment-withdraw',
    update_time = CURRENT_TIMESTAMP
WHERE creator = 'corridor-fire-compartment'
  AND COALESCE(deleted, false) = false;

COMMIT;

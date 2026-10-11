SET search_path TO dynamicbusiness;

BEGIN;

UPDATE ent_equipment
SET name = btrim(split_part(name, '，挂在', 1)),
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND name LIKE '%，挂在%';

UPDATE ent_equipment_t1
SET name = btrim(split_part(name, '，挂在', 1)),
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND name LIKE '%，挂在%';

UPDATE ent_equipment_t2
SET name = btrim(split_part(name, '，挂在', 1)),
    update_time = CURRENT_TIMESTAMP
WHERE deleted = false
  AND name LIKE '%，挂在%';

COMMIT;

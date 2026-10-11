-- 设备名称开头写明这一行是无人机、相机、机场还是遥控。
-- 负责：目录里大疆、东古示例设备的名称。同一款相机有多台时，后面写挂在哪一架。
-- 不负责：金桥两台实机（900157、900158）的名称。不改型号名称。

SET search_path TO dynamicbusiness;

BEGIN;

UPDATE ent_equipment_t1 AS e
SET name = '无人机 · ' || CASE
      WHEN e.name ~ '（禅思 ' THEN regexp_replace(e.name, '（禅思 ', '（搭配禅思 ')
      ELSE e.name
    END,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.deleted = false
  AND m.deleted = false
  AND e.model_id = m.id
  AND e.id <> 900158
  AND e.name NOT LIKE '1#%'
  AND e.name NOT LIKE '无人机 · %'
  AND (m.code LIKE 'dji-m%' OR m.code LIKE 'donggu-uav-%');

UPDATE ent_equipment_t1 AS e
SET name = '机场 · ' || e.name,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.deleted = false
  AND m.deleted = false
  AND e.model_id = m.id
  AND e.id <> 900157
  AND m.code LIKE 'dji-dock-%'
  AND e.name NOT LIKE '机场 · %';

UPDATE ent_equipment_t1 AS e
SET name = '遥控 · ' || e.name,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
WHERE e.deleted = false
  AND m.deleted = false
  AND e.model_id = m.id
  AND m.code LIKE 'dji-rc-%'
  AND e.name NOT LIKE '遥控 · %';

UPDATE ent_equipment_t1 AS e
SET name = CASE
      WHEN e.name LIKE '%舱内%' THEN '相机 · 舱内，挂在 ' || p.name
      WHEN e.name LIKE '%舱外%' THEN '相机 · 舱外，挂在 ' || p.name
      WHEN cnt.n > 1 THEN '相机 · ' || m.name || '，挂在 ' || p.name
      ELSE '相机 · ' || m.name
    END,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
JOIN ent_equipment_t1 p ON p.deleted = false
JOIN (
  SELECT model_id, count(*) AS n
  FROM ent_equipment_t1
  WHERE deleted = false
  GROUP BY model_id
) cnt ON true
WHERE e.deleted = false
  AND m.deleted = false
  AND e.model_id = m.id
  AND p.id = e.parent_id
  AND cnt.model_id = e.model_id
  AND (m.code LIKE 'dji-pl-%' OR m.code = 'hik-uav-cam');

COMMIT;

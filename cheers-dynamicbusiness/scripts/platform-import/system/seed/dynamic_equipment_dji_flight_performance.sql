-- 大疆飞机型号上的飞行性能。
-- 负责：按大疆企业官网规格页，把最大水平飞行速度、上升速度、下降速度、抗风风速、最长飞行时间、最长悬停时间写到对应飞机型号的默认值上；这一台还空着的，创建时该有的数补进这一台。品牌、厂家、防护等级这三行原来就有、但是空的，官网写了的一并补上。
-- 不负责：飞向目标点指令里的「最大速度」（协议上限 0 到 15，不写成飞机的最大水平速度）；到达位置、悬停等待多久、拍摄角度、焦距、目标点编号；东古、机场、摄像机、小型/大型无人机这两个尺寸档；Matrice 300 RTK（官网规格页这次没有返回性能表，不拿 350 的数去填）。
-- 再次执行会按下面这张表覆盖这些性能字段的型号默认值。某一台设备上已经有数的，不覆盖。
-- 出处：
--   M30 / M30T  https://enterprise.dji.com/matrice-30/specs
--   M350 RTK    https://enterprise.dji.com/matrice-350-rtk/specs  （该页没有最长悬停时间）
--   M3E / M3T / M3TA  https://enterprise.dji.com/mavic-3-enterprise/specs  （该页没有防护等级）
--   M3D / M3TD  https://enterprise.dji.com/dock-2/specs  飞机一节
--   M4E / M4T   https://enterprise.dji.com/matrice-4-series/specs  （该页写无标准防护等级）
--   M4D / M4TD  https://enterprise.dji.com/dock-3/specs  飞机一节（机场作业只用普通档；这里记的是飞机自己的运动档最大速度）
--   M400        https://enterprise.dji.com/matrice-400/specs
-- 有普通档和运动档时，水平速度记运动档向前飞，上升和下降记运动档。抗风在作业中和起降分开写时，记作业中的那个数。飞行时间和悬停时间有标准桨和降噪桨时，记标准桨。

SET search_path TO dynamicbusiness;

BEGIN;

INSERT INTO dynamic_field (
  code, name, type, description, source, status, index_strategy, options, tenant_id, creator
) VALUES
  (
    'max_horizontal_speed',
    '最大水平飞行速度',
    'NUMBER',
    '官网规格表上的最大水平飞行速度，单位米每秒。海平面、无风。规格按方向分档时，记运动档向前飞的那个数。飞向目标点指令里的最大速度另有 0 到 15 的限制，不写在这里。',
    'USER',
    1,
    'BTREE',
    NULL,
    1,
    'seed'
  ),
  (
    'max_ascent_speed',
    '最大上升速度',
    'NUMBER',
    '官网规格表上的最大上升速度，单位米每秒。有普通档和运动档时，记运动档的那个数。',
    'USER',
    1,
    'BTREE',
    NULL,
    1,
    'seed'
  ),
  (
    'max_descent_speed',
    '最大下降速度',
    'NUMBER',
    '官网规格表上的最大垂直下降速度，单位米每秒。有普通档和运动档时，记运动档的那个数。倾斜下降不写在这一行。',
    'USER',
    1,
    'BTREE',
    NULL,
    1,
    'seed'
  ),
  (
    'max_wind_resistance',
    '最大抗风速度',
    'NUMBER',
    '官网规格表上的最大抗风风速，单位米每秒。页面把作业中和起降分开写时，记作业中的那个数。',
    'USER',
    1,
    'BTREE',
    NULL,
    1,
    'seed'
  ),
  (
    'max_flight_time',
    '最长飞行时间',
    'NUMBER',
    '官网规格表上的最长飞行时间，单位分钟。实验室、无风，按该页所写速度飞到电量耗尽。有标准桨和降噪桨时，记标准桨的那个数。',
    'USER',
    1,
    'BTREE',
    NULL,
    1,
    'seed'
  ),
  (
    'max_hover_time',
    '最长悬停时间',
    'NUMBER',
    '官网规格表上的最长悬停时间，单位分钟。无风、海平面附近，从满电悬停到电量耗尽。有标准桨和降噪桨时，记标准桨的那个数。该页没有这一行时留空。',
    'USER',
    1,
    'BTREE',
    NULL,
    1,
    'seed'
  )
ON CONFLICT (code, tenant_id) WHERE deleted = false
DO UPDATE SET
  name = EXCLUDED.name,
  type = EXCLUDED.type,
  description = EXCLUDED.description,
  index_strategy = EXCLUDED.index_strategy,
  source = EXCLUDED.source,
  status = 1,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_field
SET
  options = '[
    {"label":"IP54","value":"IP54"},
    {"label":"IP55","value":"IP55"}
  ]',
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
WHERE code = 'waterproof_grade'
  AND tenant_id = 1
  AND deleted = false
  AND (options IS NULL OR btrim(options) IN ('', '[]', 'null'));

CREATE TEMP TABLE aircraft_performance (
  model_code text PRIMARY KEY,
  horizontal_speed text,
  ascent_speed text,
  descent_speed text,
  wind_resistance text,
  flight_time text,
  hover_time text,
  ingress text
) ON COMMIT DROP;

INSERT INTO aircraft_performance (
  model_code, horizontal_speed, ascent_speed, descent_speed,
  wind_resistance, flight_time, hover_time, ingress
) VALUES
  ('dji-m30', '23', '6', '5', '12', '41', '36', 'IP55'),
  ('dji-m30t', '23', '6', '5', '12', '41', '36', 'IP55'),
  ('dji-m350-rtk', '23', '6', '5', '12', '55', NULL, 'IP55'),
  ('dji-m3e', '21', '8', '6', '12', '45', '38', NULL),
  ('dji-m3t', '21', '8', '6', '12', '45', '38', NULL),
  ('dji-m3ta', '21', '8', '6', '12', '45', '38', NULL),
  ('dji-m3d', '21', '8', '6', '12', '50', '40', 'IP54'),
  ('dji-m3td', '21', '8', '6', '12', '50', '40', 'IP54'),
  ('dji-m4e', '21', '10', '8', '12', '49', '42', NULL),
  ('dji-m4t', '21', '10', '8', '12', '49', '42', NULL),
  ('dji-m4d', '21', '10', '8', '12', '54', '47', 'IP55'),
  ('dji-m4td', '21', '10', '8', '12', '54', '47', 'IP55'),
  ('dji-m400', '25', '10', '8', '12', '59', '53', 'IP55');

DO $$
BEGIN
  IF (
    SELECT count(*)
    FROM dynamic_model m
    JOIN aircraft_performance p ON p.model_code = m.code
    WHERE m.deleted = false AND m.tenant_id = 1
  ) <> 13 THEN
    RAISE EXCEPTION 'aircraft performance model count is not 13';
  END IF;
END $$;

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, tenant_id, creator
)
SELECT
  m.id, m.code, f.id, f.code,
  false, false, true, true, spec.sort_no,
  spec.default_value, 1, 'seed'
FROM dynamic_model m
JOIN aircraft_performance cap ON cap.model_code = m.code
JOIN (
  SELECT model_code, 'max_horizontal_speed' AS field_code, 60 AS sort_no, horizontal_speed AS default_value
  FROM aircraft_performance
  UNION ALL
  SELECT model_code, 'max_ascent_speed', 61, ascent_speed FROM aircraft_performance
  UNION ALL
  SELECT model_code, 'max_descent_speed', 62, descent_speed FROM aircraft_performance
  UNION ALL
  SELECT model_code, 'max_wind_resistance', 63, wind_resistance FROM aircraft_performance
  UNION ALL
  SELECT model_code, 'max_flight_time', 64, flight_time FROM aircraft_performance
  UNION ALL
  SELECT model_code, 'max_hover_time', 65, hover_time FROM aircraft_performance
) AS spec ON spec.model_code = m.code
JOIN dynamic_field f ON f.code = spec.field_code AND f.tenant_id = 1 AND f.deleted = false
WHERE m.deleted = false AND m.tenant_id = 1
ON CONFLICT (model_code, field_code, tenant_id) WHERE deleted = false
DO UPDATE SET
  model_id = EXCLUDED.model_id,
  field_id = EXCLUDED.field_id,
  default_value = EXCLUDED.default_value,
  is_searchable = EXCLUDED.is_searchable,
  is_filterable = EXCLUDED.is_filterable,
  is_sortable = EXCLUDED.is_sortable,
  sort = EXCLUDED.sort,
  deleted = false,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_model_field_assignment a
SET
  default_value = spec.default_value,
  updater = 'seed',
  update_time = CURRENT_TIMESTAMP
FROM dynamic_model m
JOIN (
  SELECT model_code, 'brand' AS field_code, '大疆' AS default_value FROM aircraft_performance
  UNION ALL
  SELECT model_code, 'manufacturer', '大疆' FROM aircraft_performance
  UNION ALL
  SELECT model_code, 'waterproof_grade', ingress FROM aircraft_performance WHERE ingress IS NOT NULL
) AS spec ON spec.model_code = m.code
WHERE a.model_id = m.id
  AND a.field_code = spec.field_code
  AND a.tenant_id = 1
  AND a.deleted = false
  AND m.tenant_id = 1
  AND m.deleted = false
  AND (a.default_value IS NULL OR btrim(a.default_value) = '');

INSERT INTO dynamic_model_field_assignment (
  model_id, model_code, field_id, field_code,
  required, is_searchable, is_filterable, is_sortable, sort,
  default_value, tenant_id, creator
)
SELECT
  m.id, m.code, f.id, f.code,
  false,
  f.type = 'TEXT',
  f.type = 'ENUM',
  false,
  CASE spec.field_code
    WHEN 'brand' THEN 20
    WHEN 'manufacturer' THEN 21
    ELSE 22
  END,
  spec.default_value,
  1,
  'seed'
FROM dynamic_model m
JOIN (
  SELECT model_code, 'brand' AS field_code, '大疆' AS default_value FROM aircraft_performance
  UNION ALL
  SELECT model_code, 'manufacturer', '大疆' FROM aircraft_performance
  UNION ALL
  SELECT model_code, 'waterproof_grade', ingress FROM aircraft_performance WHERE ingress IS NOT NULL
) AS spec ON spec.model_code = m.code
JOIN dynamic_field f ON f.code = spec.field_code AND f.tenant_id = 1 AND f.deleted = false
WHERE m.deleted = false
  AND m.tenant_id = 1
  AND NOT EXISTS (
    SELECT 1
    FROM dynamic_model_field_assignment existing
    WHERE existing.model_code = m.code
      AND existing.field_code = spec.field_code
      AND existing.tenant_id = 1
      AND existing.deleted = false
  );

-- 已经在库里的飞机：型号上有默认值、这一台还空着的，写进这一台。已经填过的不改。
UPDATE ent_equipment_t1 e
SET custom_fields = COALESCE(e.custom_fields, '{}'::jsonb) || patch.values,
    updater = 'seed',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT e2.id,
    jsonb_object_agg(
      a.field_code,
      CASE
        WHEN a.field_code IN (
          'max_horizontal_speed', 'max_ascent_speed', 'max_descent_speed',
          'max_wind_resistance', 'max_flight_time', 'max_hover_time'
        ) THEN to_jsonb(a.default_value::numeric)
        ELSE to_jsonb(a.default_value)
      END
    ) AS values
  FROM ent_equipment_t1 e2
  JOIN aircraft_performance cap ON cap.model_code = (
    SELECT m.code FROM dynamic_model m WHERE m.id = e2.model_id AND m.deleted = false
  )
  JOIN dynamic_model_field_assignment a
    ON a.model_id = e2.model_id AND a.deleted = false AND a.tenant_id = 1
  WHERE e2.deleted = false
    AND e2.tenant_id = 1
    AND a.default_value IS NOT NULL
    AND btrim(a.default_value) <> ''
    AND a.field_code IN (
      'max_horizontal_speed', 'max_ascent_speed', 'max_descent_speed',
      'max_wind_resistance', 'max_flight_time', 'max_hover_time',
      'brand', 'manufacturer', 'waterproof_grade'
    )
    AND NULLIF(btrim(e2.custom_fields ->> a.field_code), '') IS NULL
  GROUP BY e2.id
) patch
WHERE e.id = patch.id;

DELETE FROM dynamic_entity_field_index i
USING ent_equipment_t1 e
JOIN dynamic_model m ON m.id = e.model_id AND m.deleted = false
JOIN aircraft_performance cap ON cap.model_code = m.code
WHERE i.entity_id = e.id
  AND i.tenant_id = 1
  AND e.tenant_id = 1
  AND e.deleted = false
  AND i.field_code IN (
    'max_horizontal_speed', 'max_ascent_speed', 'max_descent_speed',
    'max_wind_resistance', 'max_flight_time', 'max_hover_time',
    'brand', 'manufacturer', 'waterproof_grade'
  );

INSERT INTO dynamic_entity_field_index (
  entity_id, model_id, field_code, value_string, value_number, tenant_id, creator
)
SELECT
  e.id,
  e.model_id,
  kv.key,
  CASE
    WHEN kv.key IN (
      'max_horizontal_speed', 'max_ascent_speed', 'max_descent_speed',
      'max_wind_resistance', 'max_flight_time', 'max_hover_time'
    ) THEN NULL
    ELSE kv.value
  END,
  CASE
    WHEN kv.key IN (
      'max_horizontal_speed', 'max_ascent_speed', 'max_descent_speed',
      'max_wind_resistance', 'max_flight_time', 'max_hover_time'
    ) THEN kv.value::numeric
    ELSE NULL
  END,
  1,
  'seed'
FROM ent_equipment_t1 e
JOIN dynamic_model m ON m.id = e.model_id AND m.deleted = false
JOIN aircraft_performance cap ON cap.model_code = m.code
CROSS JOIN LATERAL jsonb_each_text(e.custom_fields) kv
WHERE e.tenant_id = 1
  AND e.deleted = false
  AND kv.key IN (
    'max_horizontal_speed', 'max_ascent_speed', 'max_descent_speed',
    'max_wind_resistance', 'max_flight_time', 'max_hover_time',
    'brand', 'manufacturer', 'waterproof_grade'
  )
  AND NULLIF(btrim(kv.value), '') IS NOT NULL;

COMMIT;

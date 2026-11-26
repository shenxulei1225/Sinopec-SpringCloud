-- 智慧管廊租户 123 · 按 CAD 对照表写入防火区路长，并在一段里每个防火区都有路长时合计到管廊段。
-- 来源：智慧管廊/Excel表数据/工作簿7.csv 的「CAD图纸 / 路长」。
-- 高新大道不在这张对照表里改。高新大道以施工图设计说明为准，见 dynamic_corridor_gaoxin_drawing_tenant123.sql。
-- 不管什么：不写工作簿6里抄录的图纸标号。那些标号等正式 CAD 图纸再改。
-- 不新建分区。路长空着的行只在分区代码还空时补开发程序里的代码。
-- 长度可筛选，同一次写入索引表。

SET search_path TO dynamicbusiness;

BEGIN;

CREATE TEMP TABLE _cad_fire (
  seg_name text NOT NULL,
  fire_no int NOT NULL,
  length_m numeric,
  zone_code text,
  PRIMARY KEY (seg_name, fire_no)
);

INSERT INTO _cad_fire (seg_name, fire_no, length_m, zone_code) VALUES
  ('托月路', 1, 189.800, 'TY01'),
  ('托月路', 2, 154.000, 'TY02'),
  ('托月路', 3, 160.000, 'TY03'),
  ('托月路', 4, 150.000, 'TY04'),
  ('托月路', 5, 132.700, 'TY05'),
  ('神墩三路', 1, 144.000, 'SS01'),
  ('神墩三路', 2, 163.000, 'SS02'),
  ('神墩三路', 3, 180.000, 'SS03'),
  ('神墩三路', 4, 120.000, 'SS04'),
  ('神墩三路', 5, 200.000, 'SS05'),
  ('神墩三路', 6, 180.000, 'SS06'),
  ('神墩三路', 7, 155.000, 'SS07'),
  ('神墩三路', 8, 147.000, 'SS08'),
  ('神墩三路', 9, 150.000, 'SS09'),
  ('神墩三路', 10, 200.000, 'SS10'),
  ('神墩三路', 11, 140.000, 'SS11'),
  ('神墩三路', 12, 180.000, 'SS12'),
  ('神墩三路', 13, 150.000, 'SS13'),
  ('神墩三路', 14, 200.000, 'SS14'),
  ('神墩三路', 15, 180.000, 'SS15'),
  ('神墩三路', 16, 200.000, 'SS16'),
  ('神墩三路', 17, 143.000, 'SS17'),
  ('神墩三路', 18, 200.000, 'SS18'),
  ('神墩三路', 19, 200.000, 'SS19'),
  ('神墩三路', 20, 200.000, 'SS20'),
  ('神墩三路', 21, 180.000, 'SS21'),
  ('神墩三路', 22, 150.000, 'SS22'),
  ('神墩三路', 23, 184.000, 'SS23'),
  ('光谷五路北', 1, 177.500, 'GWB01'),
  ('光谷五路北', 2, 183.000, 'GWB02'),
  ('光谷五路北', 3, 138.000, 'GWB03'),
  ('光谷五路北', 4, 191.900, 'GWB04'),
  ('光谷五路北', 5, 198.900, 'GWB05'),
  ('光谷五路北', 6, 174.000, 'GWB06'),
  ('光谷五路北', 7, 187.000, 'GWB07'),
  ('光谷五路北', 8, 199.700, 'GWB08'),
  ('光谷五路北', 9, 197.700, 'GWB09'),
  ('光谷五路北', 10, 94.000, 'GWB10'),
  ('光谷五路北', 11, 173.800, 'GWB11'),
  ('光谷五路北', 12, 199.900, 'GWB12'),
  ('光谷五路北', 13, 182.600, 'GWB13'),
  ('光谷五路北', 14, 144.500, 'GWB14'),
  ('光谷五路北', 15, 170.000, 'GWB15'),
  ('光谷五路北', 16, 154.200, 'GWB16'),
  ('光谷五路北', 17, 168.400, 'GWB17'),
  ('光谷五路北', 18, 110.500, 'GWB18'),
  ('光谷五路北', 19, 191.100, 'GWB19'),
  ('光谷五路北', 20, 195.700, 'GWB20'),
  ('光谷五路北', 21, 191.400, 'GWB21'),
  ('光谷五路北', 22, 195.000, 'GWB22'),
  ('光谷五路北', 23, 63.900, 'GWB23'),
  ('神墩五路', 1, 200.000, 'SW01'),
  ('神墩五路', 2, 198.900, 'SW02'),
  ('高科园路北', 1, 189.900, 'GKB01'),
  ('高科园路北', 2, 190.000, 'GKB02'),
  ('高科园路北', 3, 200.000, 'GKB03'),
  ('高科园路北', 4, 200.000, 'GKB04'),
  ('高科园路北', 5, 200.000, 'GKB05'),
  ('高科园路北', 6, 185.000, 'GKB06'),
  ('高科园路北', 7, 102.000, 'GKB07'),
  ('高科园路北', 8, 140.000, 'GKB08'),
  ('高科园路南', 1, 160.000, 'GKN01'),
  ('高科园路南', 2, 160.000, 'GKN02'),
  ('高科园路南', 3, 180.000, 'GKN03'),
  ('高科园路南', 4, 180.000, 'GKN04'),
  ('高科园路南', 5, 180.000, 'GKN05'),
  ('高科园路南', 6, 180.000, 'GKN06'),
  ('高科园路南', 7, 180.000, 'GKN07'),
  ('光谷六路南', 1, NULL::numeric, 'GLN01'),
  ('光谷六路南', 2, NULL::numeric, 'GLN02'),
  ('光谷六路南', 3, NULL::numeric, 'GLN03'),
  ('光谷六路南', 4, NULL::numeric, 'GLN04'),
  ('光谷六路南', 5, NULL::numeric, 'GLN05'),
  ('光谷六路南', 6, NULL::numeric, 'GLN06'),
  ('光谷六路南', 7, NULL::numeric, 'GLN07'),
  ('光谷六路南', 8, NULL::numeric, 'GLN08'),
  ('光谷六路北', 1, 181.100, 'GLB01'),
  ('光谷六路北', 2, 130.000, 'GLB02'),
  ('光谷六路北', 3, 190.000, 'GLB03'),
  ('光谷六路北', 4, 180.000, 'GLB04'),
  ('光谷六路北', 5, 150.000, 'GLB05'),
  ('光谷六路北', 6, 150.000, 'GLB06'),
  ('光谷六路北', 7, 190.000, 'GLB07'),
  ('光谷六路北', 8, 170.000, 'GLB08'),
  ('光谷六路北', 9, 150.000, 'GLB09'),
  ('光谷六路北', 10, 200.000, 'GLB10'),
  ('光谷六路北', 11, 90.400, 'GLB11'),
  ('虎山东街', 1, 45.000, 'HD01'),
  ('虎山东街', 2, 196.862, 'HD02'),
  ('虎山东街', 3, 198.850, 'HD03'),
  ('虎山东街', 4, 161.069, 'HD04'),
  ('虎山东街', 5, 198.413, 'HD05'),
  ('虎山东街', 6, 157.420, 'HD06'),
  ('虎山东街', 7, 125.000, 'HD07'),
  ('虎山东街', 8, 155.000, 'HD08'),
  ('虎山东街', 9, 199.450, 'HD09'),
  ('虎山东街', 10, 180.000, 'HD10'),
  ('虎山东街', 11, 200.000, 'HD11'),
  ('虎山东街', 12, 200.000, 'HD12'),
  ('虎山东街', 13, 150.000, 'HD13'),
  ('虎山东街', 14, 84.800, 'HD14'),
  ('清湾路', 1, NULL::numeric, 'QW01'),
  ('清湾路', 2, NULL::numeric, 'QW02'),
  ('清湾路', 3, NULL::numeric, 'QW03');

UPDATE ent_zone_t123 z
SET custom_fields = COALESCE(z.custom_fields, '{}'::jsonb)
    || CASE
         WHEN v.length_m IS NOT NULL THEN jsonb_build_object('ut_length_m', v.length_m)
         ELSE '{}'::jsonb
       END
    || CASE
         WHEN v.zone_code IS NOT NULL
          AND COALESCE(z.custom_fields->>'F-736bb7c0d00c4e2b8f590c12d90b6fec', '') = ''
         THEN jsonb_build_object('F-736bb7c0d00c4e2b8f590c12d90b6fec', v.zone_code)
         ELSE '{}'::jsonb
       END,
    updater = 'corridor-cad-length',
    update_time = CURRENT_TIMESTAMP
FROM _cad_fire v
JOIN ent_zone_t123 p
  ON p.tenant_id = 123 AND p.deleted = false AND p.name = v.seg_name
 AND p.model_id = (SELECT id FROM dynamic_model WHERE tenant_id = 123 AND deleted = false AND name = '管廊段')
WHERE z.tenant_id = 123 AND z.deleted = false
  AND z.parent_id = p.id
  AND z.model_id = (SELECT id FROM dynamic_model WHERE tenant_id = 123 AND deleted = false AND name = '防火区')
  AND z.name = v.fire_no::text || '#防火区';

UPDATE ent_zone_t123 seg
SET custom_fields = COALESCE(seg.custom_fields, '{}'::jsonb)
      || jsonb_build_object('ut_length_m', s.total),
    updater = 'corridor-cad-length',
    update_time = CURRENT_TIMESTAMP
FROM (
  SELECT z.parent_id,
         SUM((z.custom_fields->>'ut_length_m')::numeric) AS total,
         COUNT(*) AS n,
         COUNT(*) FILTER (
           WHERE COALESCE(z.custom_fields->>'ut_length_m', '') <> ''
         ) AS filled
  FROM ent_zone_t123 z
  WHERE z.tenant_id = 123 AND z.deleted = false
    AND z.model_id = (SELECT id FROM dynamic_model WHERE tenant_id = 123 AND deleted = false AND name = '防火区')
  GROUP BY z.parent_id
) s
WHERE seg.id = s.parent_id
  AND seg.tenant_id = 123 AND seg.deleted = false
  AND seg.model_id = (SELECT id FROM dynamic_model WHERE tenant_id = 123 AND deleted = false AND name = '管廊段')
  AND s.n > 0 AND s.n = s.filled;

UPDATE dynamic_entity_field_index i
SET deleted = true,
    updater = 'corridor-cad-length',
    update_time = CURRENT_TIMESTAMP
WHERE i.tenant_id = 123 AND i.deleted = false AND i.field_code = 'ut_length_m'
  AND i.entity_id IN (
    SELECT id FROM ent_zone_t123
    WHERE tenant_id = 123 AND deleted = false
      AND updater = 'corridor-cad-length'
      AND COALESCE(custom_fields->>'ut_length_m', '') <> ''
  );

INSERT INTO dynamic_entity_field_index (
  entity_id, model_id, field_code, value_string, value_number, tenant_id, creator
)
SELECT z.id, z.model_id, 'ut_length_m',
       z.custom_fields->>'ut_length_m',
       (z.custom_fields->>'ut_length_m')::numeric,
       123, 'corridor-cad-length'
FROM ent_zone_t123 z
WHERE z.tenant_id = 123 AND z.deleted = false
  AND z.updater = 'corridor-cad-length'
  AND COALESCE(z.custom_fields->>'ut_length_m', '') <> ''
  AND NOT EXISTS (
    SELECT 1 FROM dynamic_entity_field_index i
    WHERE i.tenant_id = 123 AND i.deleted = false
      AND i.entity_id = z.id AND i.field_code = 'ut_length_m'
  );

COMMIT;

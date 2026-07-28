-- ============================================================================
-- 运营区域介绍：头图 URL + 驻地 Point GeoJSON
-- 依赖：dynamic_entity_region_intro_content.sql（已写质心）
-- 原则：
--   · 头图指向前端静态种子图 /seed-covers/*（外网拉取失败时的本地样例；正式可走 infra 上传替换）
--   · GeoJSON 仅为驻地 Point Feature，不编造管辖范围多边形
-- 幂等：可重复执行
-- ============================================================================

SET search_path TO dynamicbusiness;

-- 1) 按层级写入头图
UPDATE ent_region e
SET
  custom_fields = COALESCE(e.custom_fields, '{}'::jsonb) || jsonb_build_object(
    'FLD-BASE-region-cover_url',
    CASE
      WHEN e.id = 100001 THEN '/seed-covers/cover-group.svg'
      WHEN e.id = 100017 THEN '/seed-covers/cover-lng.svg'
      WHEN e.id BETWEEN 100101 AND 100113 THEN '/seed-covers/cover-operation.svg'
      ELSE '/seed-covers/cover-provincial.svg'
    END
  ),
  updater = 'seed-intro-cover',
  update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND e.id IN (
    100001,
    100011, 100012, 100013, 100014, 100015, 100016, 100017, 100018, 100019, 100020, 100021,
    100022, 100023, 100024, 100025, 100026, 100027,
    100101, 100102, 100103, 100104, 100105, 100106, 100107, 100108, 100109, 100110, 100111, 100112
  );

-- 2) 由质心生成驻地 Point Feature，并标记已发布
UPDATE ent_region e
SET
  custom_fields = COALESCE(e.custom_fields, '{}'::jsonb) || jsonb_build_object(
    'FLD-BASE-region-boundary_status', 'published',
    'FLD-BASE-region-boundary_geojson',
      jsonb_build_object(
        'type', 'Feature',
        'properties', jsonb_build_object('kind', 'hq_location', 'label', '驻地'),
        'geometry', jsonb_build_object(
          'type', 'Point',
          'coordinates', jsonb_build_array(
            (e.custom_fields->>'FLD-BASE-region-centroid_lng')::double precision,
            (e.custom_fields->>'FLD-BASE-region-centroid_lat')::double precision
          )
        )
      ),
    'FLD-BASE-region-remark',
      regexp_replace(
        COALESCE(e.custom_fields->>'FLD-BASE-region-remark', ''),
        '未配置(管辖)?边界 GeoJSON。?',
        '驻地位置已配置 Point GeoJSON（非管辖范围多边形）。',
        'g'
      )
  ),
  updater = 'seed-intro-geo',
  update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND e.custom_fields ? 'FLD-BASE-region-centroid_lng'
  AND e.custom_fields ? 'FLD-BASE-region-centroid_lat'
  AND NULLIF(trim(e.custom_fields->>'FLD-BASE-region-centroid_lng'), '') IS NOT NULL
  AND NULLIF(trim(e.custom_fields->>'FLD-BASE-region-centroid_lat'), '') IS NOT NULL;

-- 备注里原先写「未编造边界」的，补一句驻地 GeoJSON 说明（避免与上条重复时仍可读）
UPDATE ent_region e
SET
  custom_fields = e.custom_fields || jsonb_build_object(
    'FLD-BASE-region-remark',
    CASE
      WHEN COALESCE(e.custom_fields->>'FLD-BASE-region-remark', '') LIKE '%Point GeoJSON%'
        THEN e.custom_fields->>'FLD-BASE-region-remark'
      WHEN COALESCE(e.custom_fields->>'FLD-BASE-region-remark', '') = ''
        THEN '驻地位置已配置 Point GeoJSON（非管辖范围多边形）。'
      ELSE (e.custom_fields->>'FLD-BASE-region-remark') || ' 驻地位置已配置 Point GeoJSON（非管辖范围多边形）。'
    END
  ),
  updater = 'seed-intro-geo',
  update_time = CURRENT_TIMESTAMP
WHERE e.deleted = false
  AND e.tenant_id = 1
  AND e.custom_fields ? 'FLD-BASE-region-boundary_geojson'
  AND e.custom_fields->>'FLD-BASE-region-boundary_status' = 'published';

DO $$
DECLARE geo_cnt integer; cover_cnt integer;
BEGIN
  SELECT COUNT(*) INTO geo_cnt
  FROM ent_region
  WHERE deleted = false AND tenant_id = 1
    AND custom_fields ? 'FLD-BASE-region-boundary_geojson'
    AND custom_fields->>'FLD-BASE-region-boundary_status' = 'published';
  SELECT COUNT(*) INTO cover_cnt
  FROM ent_region
  WHERE deleted = false AND tenant_id = 1
    AND NULLIF(trim(custom_fields->>'FLD-BASE-region-cover_url'), '') IS NOT NULL
    AND id BETWEEN 100001 AND 100112;
  IF geo_cnt < 30 THEN
    RAISE EXCEPTION 'region intro geo seed incomplete: only % with published Point GeoJSON', geo_cnt;
  END IF;
  IF cover_cnt < 30 THEN
    RAISE EXCEPTION 'region intro cover seed incomplete: only % with cover_url', cover_cnt;
  END IF;
  RAISE NOTICE 'region intro cover+geo ok: cover=%, geo=%', cover_cnt, geo_cnt;
END $$;

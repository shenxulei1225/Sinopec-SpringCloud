-- 把原来的场站资产包、场站实时监控包拆成维修、设备管理、场站。
-- 应急里分出抢修、作业管理。目录跟着新模块走，不另写一份目录清单。
SET search_path TO dynamicbusiness;

INSERT INTO tenant_capability_pack (tenant_id, pack_code, opened_page_codes, creator, updater, deleted)
SELECT p.tenant_id, m.pack_code,
       CASE
           WHEN p.opened_page_codes IS NULL THEN NULL
           ELSE NULLIF((
               SELECT string_agg(page_code, ',' ORDER BY ord)
               FROM unnest(string_to_array(p.opened_page_codes, ',')) WITH ORDINALITY AS src(page_code, ord)
               WHERE btrim(page_code) = ANY (m.pages)
           ), '')
       END,
       'V297', 'V297', FALSE
FROM tenant_capability_pack p
JOIN (
    VALUES
        ('equipment', ARRAY['equipment-ledger', 'spare-parts']),
        ('maintenance', ARRAY['fault-ledger', 'repair-request', 'repair-accept', 'work-order-center', 'work-order-execute'])
) AS m(pack_code, pages) ON TRUE
WHERE p.deleted = FALSE
  AND p.pack_code = 'facility-ops'
  AND (
      p.opened_page_codes IS NULL
      OR EXISTS (
          SELECT 1
          FROM unnest(string_to_array(p.opened_page_codes, ',')) AS src(page_code)
          WHERE btrim(page_code) = ANY (m.pages)
      )
  )
  AND NOT EXISTS (
      SELECT 1 FROM tenant_capability_pack x
      WHERE x.deleted = FALSE AND x.tenant_id = p.tenant_id AND x.pack_code = m.pack_code
  );

INSERT INTO tenant_capability_pack (tenant_id, pack_code, opened_page_codes, creator, updater, deleted)
SELECT src.tenant_id, 'station',
       CASE
           WHEN bool_or(src.opened_page_codes IS NULL) THEN NULL
           ELSE NULLIF(string_agg(DISTINCT btrim(page_code), ','), '')
       END,
       'V297', 'V297', FALSE
FROM (
    SELECT tenant_id, opened_page_codes
    FROM tenant_capability_pack
    WHERE deleted = FALSE AND pack_code IN ('facility-ops', 'station-live-monitor')
) src
LEFT JOIN LATERAL unnest(
    CASE
        WHEN src.opened_page_codes IS NULL THEN ARRAY[
            'live-monitor', 'station-scene', 'road-network', 'station-vehicle-register', 'facility-gis']::text[]
        ELSE string_to_array(src.opened_page_codes, ',')
    END
) AS page_code ON btrim(page_code) IN (
    'live-monitor', 'station-scene', 'road-network', 'station-vehicle-register', 'facility-gis')
GROUP BY src.tenant_id
HAVING (bool_or(src.opened_page_codes IS NULL) OR count(page_code) > 0)
   AND NOT EXISTS (
       SELECT 1 FROM tenant_capability_pack x
       WHERE x.deleted = FALSE AND x.tenant_id = src.tenant_id AND x.pack_code = 'station'
   );

INSERT INTO tenant_capability_pack (tenant_id, pack_code, opened_page_codes, creator, updater, deleted)
SELECT p.tenant_id, m.pack_code, '', 'V297', 'V297', FALSE
FROM tenant_capability_pack p
JOIN (VALUES ('rush-repair'), ('work-management')) AS m(pack_code) ON TRUE
WHERE p.deleted = FALSE
  AND p.pack_code = 'emergency'
  AND NOT EXISTS (
      SELECT 1 FROM tenant_capability_pack x
      WHERE x.deleted = FALSE AND x.tenant_id = p.tenant_id AND x.pack_code = m.pack_code
  );

UPDATE tenant_capability_pack
SET deleted = TRUE, updater = 'V297', update_time = CURRENT_TIMESTAMP
WHERE deleted = FALSE
  AND pack_code IN ('facility-ops', 'station-live-monitor');

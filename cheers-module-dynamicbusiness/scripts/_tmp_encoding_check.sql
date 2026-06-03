SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness;

SELECT 'zhgl_equipment' AS src, count(*) FROM dblink(
  'host=127.0.0.1 dbname=zhgl user=postgres password=Coolhomer',
  'SET client_encoding TO ''UTF8''; SELECT count(*) FROM biz_equipment'
) AS t(cnt bigint);

SELECT id, name, equipment_name FROM biz_equipment WHERE deleted = false ORDER BY id LIMIT 5;

SELECT id, name FROM biz_region WHERE deleted = false ORDER BY id LIMIT 5;

SELECT id, name FROM dynamic_model WHERE deleted = false ORDER BY id LIMIT 5;

SELECT id, name FROM dynamic_field WHERE deleted = false ORDER BY id LIMIT 5;

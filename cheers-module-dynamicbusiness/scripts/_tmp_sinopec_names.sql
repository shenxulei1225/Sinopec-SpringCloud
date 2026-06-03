SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness;
SELECT id, name, equipment_name FROM biz_equipment WHERE deleted = false ORDER BY id LIMIT 8;
SELECT id, name, region_name FROM biz_region WHERE deleted = false ORDER BY id LIMIT 8;
SELECT id, name FROM dynamic_model WHERE deleted = false ORDER BY id LIMIT 8;

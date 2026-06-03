SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness;

SELECT id, name FROM dynamic_model WHERE deleted = false ORDER BY id LIMIT 25;

SELECT id, name FROM biz_region WHERE deleted = false ORDER BY id LIMIT 10;

SELECT id, name FROM dynamic_field WHERE deleted = false ORDER BY id LIMIT 10;

-- Mojibake pattern scan
SELECT 'dynamic_model' AS src, count(*) FROM dynamic_model WHERE name ~ '[ÃÂÐÑ]' OR name LIKE '%?%';
SELECT 'dynamic_field' AS src, count(*) FROM dynamic_field WHERE name ~ '[ÃÂÐÑ]' OR name LIKE '%?%';
SELECT 'biz_region' AS src, count(*) FROM biz_region WHERE name ~ '[ÃÂÐÑ]' OR name LIKE '%?%';
SELECT 'biz_equipment' AS src, count(*) FROM biz_equipment WHERE name ~ '[ÃÂÐÑ]' OR equipment_name ~ '[ÃÂÐÑ]';

-- Model 44 hex
SELECT id, name, encode(convert_to(name, 'UTF8'), 'hex') AS hex FROM dynamic_model WHERE id = 44;

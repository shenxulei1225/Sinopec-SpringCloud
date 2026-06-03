SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness;

-- entity field index uses field_code - sample
SELECT field_code, value_string, count(*) 
FROM dynamic_entity_field_index 
WHERE deleted=false 
GROUP BY field_code, value_string 
ORDER BY count(*) DESC LIMIT 15;

-- task table has f_f_* physical columns - sample row keys via json
SELECT column_name FROM information_schema.columns 
WHERE table_schema='dynamicbusiness' AND table_name='biz_task' 
AND column_name LIKE 'f_%' LIMIT 10;

SELECT id, name FROM biz_task WHERE deleted=false LIMIT 5;

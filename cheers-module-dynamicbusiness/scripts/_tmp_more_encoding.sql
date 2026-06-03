SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness;

SELECT id, name, group_type FROM dynamic_group WHERE deleted = false ORDER BY id LIMIT 15;

SELECT id, name, custom_fields::text
FROM biz_equipment
WHERE custom_fields IS NOT NULL AND custom_fields::text <> '{}'
ORDER BY id LIMIT 3;

SELECT id, name FROM dynamic_business_type WHERE deleted = false;

-- hex check first chinese field name byte sequence (should be e6 b8 90 for 名 in UTF8)
SELECT name, encode(convert_to(name, 'UTF8'), 'hex') AS utf8_hex
FROM dynamic_field WHERE id = 1;

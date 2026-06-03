SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness;

-- sample fields / categories
SELECT id, code, name FROM dynamic_field WHERE deleted = false ORDER BY id LIMIT 15;
SELECT id, name, code FROM dynamic_category WHERE deleted = false ORDER BY id LIMIT 15;

-- detect likely mojibake: contains latin extended chars typical of UTF8 mis-decoded
SELECT count(*) AS suspicious_field_names
FROM dynamic_field
WHERE deleted = false
  AND name ~ '[ÃÂåæèéêëìíîïðñòóôõöùúûüýÿ]' ;

SELECT id, code, name FROM dynamic_field
WHERE deleted = false AND name ~ '[ÃÂåæèéêë]'
LIMIT 20;

SELECT count(*) AS suspicious_category_names
FROM dynamic_category
WHERE deleted = false
  AND name ~ '[ÃÂåæèéêë]';

SELECT id, name FROM dynamic_category
WHERE deleted = false AND name ~ '[ÃÂåæèéêë]'
LIMIT 20;

-- base field names
SELECT id, field_code, field_name FROM dynamic_business_type_base_field
WHERE deleted = false AND field_name ~ '[ÃÂåæèéêë]'
LIMIT 10;

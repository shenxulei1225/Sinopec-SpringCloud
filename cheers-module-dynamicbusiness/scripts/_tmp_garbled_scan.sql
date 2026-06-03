SET client_encoding TO 'UTF8';
SET search_path TO dynamicbusiness;

-- Any non-printable or replacement chars in names?
SELECT 'dynamic_field' AS tbl, count(*) FROM dynamic_field WHERE deleted=false AND name ~ '[^\x20-\x7E\u4e00-\u9fff\u3000-\u303f\uff00-\uffef]';
SELECT 'dynamic_model' AS tbl, count(*) FROM dynamic_model WHERE deleted=false AND name ~ '[^\x20-\x7E\u4e00-\u9fff\u3000-\u303f\uff00-\uffef]';
SELECT 'dynamic_category' AS tbl, count(*) FROM dynamic_category WHERE deleted=false AND name ~ '[^\x20-\x7E\u4e00-\u9fff\u3000-\u303f\uff00-\uffef]';
SELECT 'biz_region' AS tbl, count(*) FROM biz_region WHERE deleted=false AND name ~ '[^\x20-\x7E\u4e00-\u9fff\u3000-\u303f\uff00-\uffef]';

-- classic double-encoding artifact: contains 'Ã' or 'å' as mojibake
SELECT id, name FROM dynamic_field WHERE name LIKE '%Ã%' OR name LIKE '%å%' LIMIT 10;
SELECT id, name FROM dynamic_model WHERE name LIKE '%Ã%' OR name LIKE '%å%' LIMIT 10;

-- compare model count/name with zhgl (manual sample from earlier conversation)
SELECT count(*) AS model_cnt FROM dynamic_model WHERE deleted=false;

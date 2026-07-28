-- 退役旧站场表；真源为 dynamicbusiness.ent_facility
DROP TABLE IF EXISTS fac_site CASCADE;
DROP TABLE IF EXISTS fac_site_type CASCADE;
DROP SEQUENCE IF EXISTS fac_site_seq;
DROP SEQUENCE IF EXISTS fac_site_type_seq;

ALTER TABLE fac_facility RENAME COLUMN site_id TO station_id;
ALTER TABLE fac_facility RENAME COLUMN site_name TO station_name;

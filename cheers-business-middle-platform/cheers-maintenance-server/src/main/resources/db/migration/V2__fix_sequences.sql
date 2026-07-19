SET search_path TO maintenance;

CREATE SEQUENCE IF NOT EXISTS mm_field_work_standard_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS mm_handbook_seq START WITH 1000 INCREMENT BY 1;

ALTER TABLE mm_field_work_standard ALTER COLUMN id DROP DEFAULT;
SELECT setval('mm_field_work_standard_seq',
              GREATEST(COALESCE((SELECT MAX(id) FROM mm_field_work_standard), 0), 999) + 1,
              false);

ALTER TABLE mm_handbook ALTER COLUMN id DROP DEFAULT;
SELECT setval('mm_handbook_seq',
              GREATEST(COALESCE((SELECT MAX(id) FROM mm_handbook), 0), 999) + 1,
              false);

DROP SEQUENCE IF EXISTS mm_field_work_standard_id_seq;
DROP SEQUENCE IF EXISTS mm_handbook_id_seq;

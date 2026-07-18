SET search_path TO work_order;

-- Align with alarm-server pattern: explicit sequences matching @KeySequence names.
CREATE SEQUENCE IF NOT EXISTS wo_field_work_standard_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS wo_work_order_seq START WITH 1000 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS wo_work_order_step_result_seq START WITH 1000 INCREMENT BY 1;

-- V1 used BIGSERIAL (implicit *_id_seq); drop defaults and bind to named sequences.
ALTER TABLE wo_field_work_standard ALTER COLUMN id DROP DEFAULT;
SELECT setval('wo_field_work_standard_seq',
              GREATEST(COALESCE((SELECT MAX(id) FROM wo_field_work_standard), 0), 999) + 1,
              false);

ALTER TABLE wo_work_order ALTER COLUMN id DROP DEFAULT;
SELECT setval('wo_work_order_seq',
              GREATEST(COALESCE((SELECT MAX(id) FROM wo_work_order), 0), 999) + 1,
              false);

ALTER TABLE wo_work_order_step_result ALTER COLUMN id DROP DEFAULT;
SELECT setval('wo_work_order_step_result_seq',
              GREATEST(COALESCE((SELECT MAX(id) FROM wo_work_order_step_result), 0), 999) + 1,
              false);

DROP SEQUENCE IF EXISTS wo_field_work_standard_id_seq;
DROP SEQUENCE IF EXISTS wo_work_order_id_seq;
DROP SEQUENCE IF EXISTS wo_work_order_step_result_id_seq;

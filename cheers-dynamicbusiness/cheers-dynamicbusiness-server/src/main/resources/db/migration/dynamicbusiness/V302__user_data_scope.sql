-- 数据范围单独记在人身上：可以多选组织、场站、管线。
-- 任职仍表示人在某个单元担任某个职务，不再单独决定能看哪些记录。
-- 现有有效任职迁成同一批范围，并补上职务上的人员，避免上线后原来能看的人变成空范围。
SET search_path TO dynamicbusiness;

CREATE TABLE IF NOT EXISTS dynamic_user_data_scope (
    id            bigserial PRIMARY KEY,
    user_id       bigint       NOT NULL,
    registry_code varchar(64)  NOT NULL,
    unit_id       bigint       NOT NULL,
    creator       varchar(64)           DEFAULT '',
    create_time   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater       varchar(64)           DEFAULT '',
    update_time   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted       boolean      NOT NULL DEFAULT false,
    tenant_id     bigint       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_user_data_scope
    ON dynamic_user_data_scope (tenant_id, user_id, registry_code, unit_id)
    WHERE deleted = false;

ALTER TABLE dynamic_post_assignment
    ADD COLUMN IF NOT EXISTS management_unit_registry varchar(64);

UPDATE dynamic_post_assignment
SET management_unit_registry = 'organization'
WHERE management_unit_registry IS NULL
   OR btrim(management_unit_registry) = '';

ALTER TABLE dynamic_post_assignment
    ALTER COLUMN management_unit_registry SET DEFAULT 'organization';

ALTER TABLE dynamic_post_assignment
    ALTER COLUMN management_unit_registry SET NOT NULL;

INSERT INTO dynamic_user_data_scope (
    user_id, registry_code, unit_id, creator, updater, deleted, tenant_id)
SELECT DISTINCT p.user_id, p.management_unit_registry, p.management_unit_id,
       'V302', 'V302', false, p.tenant_id
FROM dynamic_post_assignment p
WHERE p.deleted = false
  AND (p.valid_from IS NULL OR p.valid_from <= CURRENT_DATE)
  AND (p.valid_to IS NULL OR p.valid_to >= CURRENT_DATE)
  AND NOT EXISTS (
      SELECT 1
      FROM dynamic_user_data_scope s
      WHERE s.deleted = false
        AND s.tenant_id = p.tenant_id
        AND s.user_id = p.user_id
        AND s.registry_code = p.management_unit_registry
        AND s.unit_id = p.management_unit_id
  );

INSERT INTO dynamic_duty_post_user (
    duty_post_id, user_id, creator, updater, deleted, tenant_id)
SELECT DISTINCT p.duty_post_id, p.user_id, 'V302', 'V302', false, p.tenant_id
FROM dynamic_post_assignment p
WHERE p.deleted = false
  AND (p.valid_from IS NULL OR p.valid_from <= CURRENT_DATE)
  AND (p.valid_to IS NULL OR p.valid_to >= CURRENT_DATE)
  AND NOT EXISTS (
      SELECT 1
      FROM dynamic_duty_post_user h
      WHERE h.deleted = false
        AND h.tenant_id = p.tenant_id
        AND h.duty_post_id = p.duty_post_id
        AND h.user_id = p.user_id
  );

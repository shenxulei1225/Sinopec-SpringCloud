-- 业务开通可以添加多本数据目录。每本目录在第二步单独一个标签。
-- 不写目录之间的挂接。已有型号登记推出的目录先收成一行，避免当前租户的标签变空。
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamic_management_unit_registry_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_management_unit_registry (
    id              BIGINT       DEFAULT nextval('dynamic_management_unit_registry_id_seq'::regclass) NOT NULL,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    registry_code   VARCHAR(64)  NOT NULL,
    sort_order      INTEGER      NOT NULL DEFAULT 0,
    creator         VARCHAR(64)  DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT dynamic_management_unit_registry_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_management_unit_registry
    ON dynamic_management_unit_registry (tenant_id, registry_code)
    WHERE deleted = FALSE;

COMMENT ON TABLE dynamic_management_unit_registry IS
    '已添加为管理单元的数据目录。第二步按目录分标签配置业务。不表示目录之间谁挂谁。';

INSERT INTO dynamic_management_unit_registry (tenant_id, registry_code, sort_order, creator, updater, deleted)
SELECT t.tenant_id, btrim(m.entity_type_code), MIN(t.id)::INTEGER, 'V296', 'V296', FALSE
FROM dynamic_management_unit_type t
JOIN dynamic_model m ON m.id = t.model_id AND m.deleted = FALSE
WHERE t.deleted = FALSE
  AND m.entity_type_code IS NOT NULL
  AND btrim(m.entity_type_code) <> ''
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_management_unit_registry r
      WHERE r.deleted = FALSE
        AND r.tenant_id = t.tenant_id
        AND r.registry_code = btrim(m.entity_type_code)
  )
GROUP BY t.tenant_id, btrim(m.entity_type_code);

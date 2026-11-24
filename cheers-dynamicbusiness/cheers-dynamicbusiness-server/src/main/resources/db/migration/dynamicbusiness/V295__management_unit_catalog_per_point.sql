-- 可管目录改为按管理点（具体记录）登记，不再按管理单元类型批量挂目录。
-- 管理单元类型单独登记在 dynamic_management_unit_type。
SET search_path TO dynamicbusiness;

CREATE SEQUENCE IF NOT EXISTS dynamic_management_unit_type_id_seq
    START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;

CREATE TABLE IF NOT EXISTS dynamic_management_unit_type (
    id              BIGINT       DEFAULT nextval('dynamic_management_unit_type_id_seq'::regclass) NOT NULL,
    tenant_id       BIGINT       NOT NULL DEFAULT 0,
    model_id        BIGINT       NOT NULL,
    creator         VARCHAR(64)  DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT dynamic_management_unit_type_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_management_unit_type
    ON dynamic_management_unit_type (tenant_id, model_id)
    WHERE deleted = FALSE;

COMMENT ON TABLE dynamic_management_unit_type IS
    '已登记为管理单元类型的型号；推出管理单元目录用，不承载可管目录清单。';

INSERT INTO dynamic_management_unit_type (tenant_id, model_id, creator, updater, deleted)
SELECT DISTINCT c.tenant_id, c.management_unit_model_id, 'V295', 'V295', FALSE
FROM dynamic_management_unit_catalog c
WHERE c.deleted = FALSE
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_management_unit_type t
      WHERE t.deleted = FALSE
        AND t.tenant_id = c.tenant_id
        AND t.model_id = c.management_unit_model_id
  );

CREATE TABLE IF NOT EXISTS dynamic_management_unit_catalog_new (
    id                              BIGINT       DEFAULT nextval('dynamic_management_unit_catalog_id_seq'::regclass) NOT NULL,
    tenant_id                       BIGINT       NOT NULL DEFAULT 0,
    management_unit_registry_code   VARCHAR(128) NOT NULL,
    management_unit_id              BIGINT       NOT NULL,
    registry_code                   VARCHAR(64)  NOT NULL,
    creator                         VARCHAR(64)  DEFAULT '',
    create_time                     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                         VARCHAR(64)  DEFAULT '',
    update_time                     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                         BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT dynamic_management_unit_catalog_new_pkey PRIMARY KEY (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_management_unit_catalog_new
    ON dynamic_management_unit_catalog_new (
        tenant_id, management_unit_registry_code, management_unit_id, registry_code
    )
    WHERE deleted = FALSE;

COMMENT ON TABLE dynamic_management_unit_catalog_new IS
    '某管理点能管的数据目录；任职所在单元按本表与角色功能授权取交集。';

DO $$
DECLARE
    tenant_row RECORD;
    unit_catalog VARCHAR(128);
    entity_table REGCLASS;
    sql_expand TEXT;
BEGIN
    FOR tenant_row IN
        SELECT DISTINCT c.tenant_id
        FROM dynamic_management_unit_catalog c
        WHERE c.deleted = FALSE
    LOOP
        SELECT MIN(m.entity_type_code) INTO unit_catalog
        FROM dynamic_management_unit_catalog c
        JOIN dynamic_model m ON m.id = c.management_unit_model_id AND m.deleted = FALSE
        WHERE c.deleted = FALSE
          AND c.tenant_id = tenant_row.tenant_id;

        IF unit_catalog IS NULL OR unit_catalog = '' THEN
            CONTINUE;
        END IF;

        entity_table := to_regclass(format('ent_%s_t%s', unit_catalog, tenant_row.tenant_id));
        IF entity_table IS NULL THEN
            CONTINUE;
        END IF;

        sql_expand := format(
            $SQL$
            INSERT INTO dynamic_management_unit_catalog_new (
                tenant_id, management_unit_registry_code, management_unit_id, registry_code,
                creator, updater, deleted
            )
            SELECT DISTINCT
                c.tenant_id,
                %L,
                e.id,
                c.registry_code,
                'V295',
                'V295',
                FALSE
            FROM dynamic_management_unit_catalog c
            JOIN dynamic_model m ON m.id = c.management_unit_model_id AND m.deleted = FALSE
            JOIN %s e ON e.deleted = FALSE AND e.model_id = c.management_unit_model_id
            WHERE c.deleted = FALSE
              AND c.tenant_id = %s
              AND NOT EXISTS (
                  SELECT 1 FROM dynamic_management_unit_catalog_new n
                  WHERE n.deleted = FALSE
                    AND n.tenant_id = c.tenant_id
                    AND n.management_unit_registry_code = %L
                    AND n.management_unit_id = e.id
                    AND n.registry_code = c.registry_code
              )
            $SQL$,
            unit_catalog,
            entity_table,
            tenant_row.tenant_id,
            unit_catalog
        );
        EXECUTE sql_expand;
    END LOOP;
END $$;

DROP TABLE IF EXISTS dynamic_management_unit_catalog;

ALTER TABLE dynamic_management_unit_catalog_new RENAME TO dynamic_management_unit_catalog;

ALTER INDEX IF EXISTS uk_dynamic_management_unit_catalog_new RENAME TO uk_dynamic_management_unit_catalog;

COMMENT ON TABLE dynamic_management_unit_catalog IS
    '某管理点能管的数据目录；任职所在单元按本表与角色功能授权取交集。';
COMMENT ON COLUMN dynamic_management_unit_catalog.management_unit_registry_code IS
    '管理单元目录注册编码';
COMMENT ON COLUMN dynamic_management_unit_catalog.management_unit_id IS
    '管理单元目录里的记录编号';
COMMENT ON COLUMN dynamic_management_unit_catalog.registry_code IS
    '能管的数据目录注册编码';
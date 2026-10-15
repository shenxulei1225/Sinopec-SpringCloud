-- 同一条授权要能分清是实体、分类还是型号。
-- 已有行都是实体上的操作，补成 entity。唯一约束带上对象，避免三类勾选互相覆盖。
SET search_path TO dynamicbusiness;

ALTER TABLE dynamic_catalog_role_grant
    ADD COLUMN IF NOT EXISTS subject character varying(16) NOT NULL DEFAULT 'entity';

DROP INDEX IF EXISTS uk_dynamic_catalog_role_grant;

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_catalog_role_grant
    ON dynamic_catalog_role_grant (role_id, registry_code, subject, operation, tenant_id)
    WHERE (deleted = false);

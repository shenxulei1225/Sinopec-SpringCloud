-- 统一分组模型：dynamic_group / dynamic_group_relation

CREATE TABLE IF NOT EXISTS dynamic_group (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    group_type VARCHAR(32) NOT NULL,
    code VARCHAR(128),
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1024),
    parent_id BIGINT,
    path VARCHAR(1024),
    level INT,
    sort INT,
    status INT,
    creator VARCHAR(64),
    create_time TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX IF NOT EXISTS idx_dynamic_group_tenant_type_parent
    ON dynamic_group(tenant_id, group_type, parent_id);
CREATE INDEX IF NOT EXISTS idx_dynamic_group_tenant_type_name
    ON dynamic_group(tenant_id, group_type, name);

-- 兼容已存在表（若之前失败时按 BIT 建过表，这里统一为 BOOLEAN）
ALTER TABLE dynamic_group
    ALTER COLUMN deleted TYPE BOOLEAN USING (CASE WHEN deleted::text IN ('1', 't', 'true') THEN TRUE ELSE FALSE END);
ALTER TABLE dynamic_group
    ALTER COLUMN deleted SET DEFAULT FALSE;

CREATE TABLE IF NOT EXISTS dynamic_group_relation (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    group_type VARCHAR(32) NOT NULL,
    group_id BIGINT NOT NULL,
    target_id BIGINT NOT NULL,
    sort INT,
    creator VARCHAR(64),
    create_time TIMESTAMP,
    updater VARCHAR(64),
    update_time TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_group_relation
    ON dynamic_group_relation(tenant_id, group_type, group_id, target_id);
CREATE INDEX IF NOT EXISTS idx_dynamic_group_relation_target
    ON dynamic_group_relation(tenant_id, group_type, target_id);

-- 兼容已存在表（若之前失败时按 BIT 建过表，这里统一为 BOOLEAN）
ALTER TABLE dynamic_group_relation
    ALTER COLUMN deleted TYPE BOOLEAN USING (CASE WHEN deleted::text IN ('1', 't', 'true') THEN TRUE ELSE FALSE END);
ALTER TABLE dynamic_group_relation
    ALTER COLUMN deleted SET DEFAULT FALSE;

-- 迁移字段分组数据 -> 通用分组表
INSERT INTO dynamic_group (tenant_id, group_type, code, name, description, parent_id, path, level, sort, status,
                          creator, create_time, updater, update_time, deleted)
SELECT tenant_id, 'FIELD', code, name, description, parent_id, path, level, sort, status,
       creator, create_time, updater, update_time,
       COALESCE(deleted, FALSE)
FROM dynamic_field_group fg
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_group g
    WHERE g.group_type = 'FIELD' AND g.tenant_id = fg.tenant_id AND g.code = fg.code
);

-- 迁移单位分组数据 -> 通用分组表
INSERT INTO dynamic_group (tenant_id, group_type, code, name, description, parent_id, path, level, sort, status,
                          creator, create_time, updater, update_time, deleted)
SELECT tenant_id, 'UNIT', code, name, description, parent_id, path, level, sort, status,
       creator, create_time, updater, update_time,
       COALESCE(deleted, FALSE)
FROM dynamic_unit_group ug
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_group g
    WHERE g.group_type = 'UNIT' AND g.tenant_id = ug.tenant_id AND g.code = ug.code
);

-- 迁移字段分组关联（去重后再入库，避免同键重复触发唯一约束）
INSERT INTO dynamic_group_relation (tenant_id, group_type, group_id, target_id, sort,
                                   creator, create_time, updater, update_time, deleted)
SELECT d.tenant_id, 'FIELD', d.field_group_id, d.field_id, d.sort,
       d.creator, d.create_time, d.updater, d.update_time,
       d.deleted
FROM (
    SELECT DISTINCT ON (r.tenant_id, r.field_group_id, r.field_id)
           r.tenant_id,
           r.field_group_id,
           r.field_id,
           r.sort,
           r.creator,
           r.create_time,
           r.updater,
           r.update_time,
           COALESCE(r.deleted, FALSE) AS deleted
    FROM dynamic_field_group_relation r
    ORDER BY r.tenant_id, r.field_group_id, r.field_id,
             COALESCE(r.deleted, FALSE) ASC, r.update_time DESC NULLS LAST, r.create_time DESC NULLS LAST
) d
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.tenant_id = d.tenant_id
      AND gr.group_type = 'FIELD'
      AND gr.group_id = d.field_group_id
      AND gr.target_id = d.field_id
);

-- 迁移单位分组关联（去重后再入库，避免同键重复触发唯一约束）
INSERT INTO dynamic_group_relation (tenant_id, group_type, group_id, target_id, sort,
                                   creator, create_time, updater, update_time, deleted)
SELECT d.tenant_id, 'UNIT', d.unit_group_id, d.unit_id, d.sort,
       d.creator, d.create_time, d.updater, d.update_time,
       d.deleted
FROM (
    SELECT DISTINCT ON (r.tenant_id, r.unit_group_id, r.unit_id)
           r.tenant_id,
           r.unit_group_id,
           r.unit_id,
           r.sort,
           r.creator,
           r.create_time,
           r.updater,
           r.update_time,
           COALESCE(r.deleted, FALSE) AS deleted
    FROM dynamic_unit_group_relation r
    ORDER BY r.tenant_id, r.unit_group_id, r.unit_id,
             COALESCE(r.deleted, FALSE) ASC, r.update_time DESC NULLS LAST, r.create_time DESC NULLS LAST
) d
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_group_relation gr
    WHERE gr.tenant_id = d.tenant_id
      AND gr.group_type = 'UNIT'
      AND gr.group_id = d.unit_group_id
      AND gr.target_id = d.unit_id
);

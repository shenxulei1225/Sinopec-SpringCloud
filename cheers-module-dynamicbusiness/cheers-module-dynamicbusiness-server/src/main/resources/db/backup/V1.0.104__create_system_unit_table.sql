-- 单位主数据表（替代 dict_data 的 unit_type）
CREATE TABLE IF NOT EXISTS dynamic_unit (
    id BIGSERIAL PRIMARY KEY,
    tenant_id BIGINT NOT NULL DEFAULT 0,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(64) NOT NULL,
    unit_type VARCHAR(64),
    sort INT NOT NULL DEFAULT 1,
    status INT NOT NULL DEFAULT 1,
    remark VARCHAR(500),
    creator VARCHAR(64) DEFAULT '',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater VARCHAR(64) DEFAULT '',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_dynamic_unit_tenant_code
    ON dynamic_unit(tenant_id, code)
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_unit_tenant_type
    ON dynamic_unit(tenant_id, unit_type)
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_dynamic_unit_tenant_status
    ON dynamic_unit(tenant_id, status)
    WHERE deleted = FALSE;

-- 从字典迁移单位数据（幂等）
INSERT INTO dynamic_unit (tenant_id, name, code, unit_type, sort, status, remark, creator, create_time, updater, update_time, deleted)
SELECT
    0 AS tenant_id,
    d.label AS name,
    d.value AS code,
    CASE
        WHEN d.remark LIKE '%"unitType"%' THEN regexp_replace(d.remark, '.*"unitType"\s*:\s*"([^"]+)".*', '\1')
        ELSE NULL
    END AS unit_type,
    COALESCE(d.sort, 1),
    COALESCE(d.status, 1),
    d.remark,
    COALESCE(d.creator, ''),
    COALESCE(d.create_time, CURRENT_TIMESTAMP),
    COALESCE(d.updater, ''),
    COALESCE(d.update_time, CURRENT_TIMESTAMP),
    COALESCE(d.deleted, FALSE)
FROM dynamic_dict_data d
WHERE d.dict_type = 'unit_type'
  AND d.deleted = FALSE
  AND NOT EXISTS (
      SELECT 1 FROM dynamic_unit u
      WHERE u.tenant_id = 0
        AND u.code = d.value
        AND u.deleted = FALSE
  );

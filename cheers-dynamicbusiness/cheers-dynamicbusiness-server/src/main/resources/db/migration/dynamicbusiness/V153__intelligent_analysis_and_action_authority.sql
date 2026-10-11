-- 智能分析能力、服务接入和分析任务的单一权威。
-- 动作只引用平台分析能力。厂商算法编号、地址和鉴权引用只保存在服务接入与能力目录。
-- 同时修复 action_domain 已登记为基础字段、但 ent_action* 没有物理列导致动作列表整体失败的问题。

SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS intelligent_analysis_provider (
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL DEFAULT 0,
    code                VARCHAR(64)  NOT NULL,
    name                VARCHAR(128) NOT NULL,
    adapter_type        VARCHAR(64)  NOT NULL,
    base_url            VARCHAR(512),
    credential_ref      VARCHAR(256),
    config_json         JSONB        NOT NULL DEFAULT '{}'::jsonb,
    enabled             BOOLEAN      NOT NULL DEFAULT FALSE,
    creator             VARCHAR(64),
    create_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater             VARCHAR(64),
    update_time         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted             BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_intelligent_analysis_provider_code
    ON intelligent_analysis_provider (tenant_id, code)
    WHERE deleted = FALSE;

COMMENT ON TABLE intelligent_analysis_provider IS
    '智能分析服务接入。只保存服务地址、适配器种类和密钥引用，不保存动作';
COMMENT ON COLUMN intelligent_analysis_provider.credential_ref IS
    '密钥管理系统中的引用。禁止把密码或令牌直接写入本表';

CREATE TABLE IF NOT EXISTS intelligent_analysis_capability (
    id                         BIGSERIAL PRIMARY KEY,
    tenant_id                  BIGINT       NOT NULL DEFAULT 0,
    code                       VARCHAR(64)  NOT NULL,
    name                       VARCHAR(128) NOT NULL,
    description                VARCHAR(512),
    provider_id                BIGINT       NOT NULL,
    provider_capability_code   VARCHAR(128) NOT NULL,
    capability_version         VARCHAR(64)  NOT NULL,
    supported_media            JSONB        NOT NULL DEFAULT '[]'::jsonb,
    parameter_schema_json      JSONB        NOT NULL DEFAULT '{}'::jsonb,
    result_schema_json         JSONB        NOT NULL DEFAULT '{}'::jsonb,
    enabled                    BOOLEAN      NOT NULL DEFAULT TRUE,
    creator                    VARCHAR(64),
    create_time                TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                    VARCHAR(64),
    update_time                TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                    BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_analysis_capability_provider
        FOREIGN KEY (provider_id) REFERENCES intelligent_analysis_provider (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_intelligent_analysis_capability_code
    ON intelligent_analysis_capability (tenant_id, code)
    WHERE deleted = FALSE;

COMMENT ON TABLE intelligent_analysis_capability IS
    '平台智能分析能力目录。code 是动作引用的稳定标识，provider_capability_code 只供适配器使用';

CREATE TABLE IF NOT EXISTS intelligent_analysis_job (
    id                          BIGSERIAL PRIMARY KEY,
    tenant_id                   BIGINT       NOT NULL DEFAULT 0,
    job_no                      VARCHAR(64)  NOT NULL,
    capability_id               BIGINT       NOT NULL,
    capability_code_snapshot    VARCHAR(64)  NOT NULL,
    capability_version_snapshot VARCHAR(64)  NOT NULL,
    provider_id_snapshot        BIGINT       NOT NULL,
    input_type                  VARCHAR(32)  NOT NULL,
    input_ref                   VARCHAR(2048) NOT NULL,
    parameter_snapshot          JSONB        NOT NULL DEFAULT '{}'::jsonb,
    business_ref_type           VARCHAR(64),
    business_ref_id             VARCHAR(256),
    status                      VARCHAR(32)  NOT NULL,
    provider_job_id             VARCHAR(256),
    normalized_result_json      JSONB,
    raw_result_json             JSONB,
    failure_message             VARCHAR(1024),
    accepted_time               TIMESTAMP,
    completed_time              TIMESTAMP,
    creator                     VARCHAR(64),
    create_time                 TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                     VARCHAR(64),
    update_time                 TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                     BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_analysis_job_capability
        FOREIGN KEY (capability_id) REFERENCES intelligent_analysis_capability (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_intelligent_analysis_job_no
    ON intelligent_analysis_job (tenant_id, job_no)
    WHERE deleted = FALSE;
CREATE INDEX IF NOT EXISTS idx_intelligent_analysis_job_status
    ON intelligent_analysis_job (tenant_id, status, create_time DESC)
    WHERE deleted = FALSE;

COMMENT ON TABLE intelligent_analysis_job IS
    '一次图片或录像分析任务。创建时冻结能力编码、版本和参数，回调只更新这条任务';

CREATE TABLE IF NOT EXISTS intelligent_analysis_session (
    id                          BIGSERIAL PRIMARY KEY,
    tenant_id                   BIGINT       NOT NULL DEFAULT 0,
    session_no                  VARCHAR(64)  NOT NULL,
    capability_id               BIGINT       NOT NULL,
    capability_code_snapshot    VARCHAR(64)  NOT NULL,
    capability_version_snapshot VARCHAR(64)  NOT NULL,
    stream_ref                  VARCHAR(2048) NOT NULL,
    parameter_snapshot          JSONB        NOT NULL DEFAULT '{}'::jsonb,
    status                      VARCHAR(32)  NOT NULL,
    provider_session_id         VARCHAR(256),
    failure_message             VARCHAR(1024),
    creator                     VARCHAR(64),
    create_time                 TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater                     VARCHAR(64),
    update_time                 TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted                     BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_analysis_session_capability
        FOREIGN KEY (capability_id) REFERENCES intelligent_analysis_capability (id)
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_intelligent_analysis_session_no
    ON intelligent_analysis_session (tenant_id, session_no)
    WHERE deleted = FALSE;

-- 已核对的本地 detection 服务。地址必须由管理员确认后填写并启用。
INSERT INTO intelligent_analysis_provider (
    tenant_id, code, name, adapter_type, config_json, enabled, creator
) VALUES (
    1,
    'LOCAL_DETECTION',
    '本地视频分析服务',
    'LEGACY_DETECTION_MULTIPART',
    '{"markId":"4","allowedMediaHosts":[]}'::jsonb,
    FALSE,
    'flyway-v153'
)
ON CONFLICT (tenant_id, code) WHERE deleted = FALSE
DO UPDATE SET
    name = EXCLUDED.name,
    adapter_type = EXCLUDED.adapter_type,
    updater = 'flyway-v153',
    update_time = CURRENT_TIMESTAMP;

-- 只登记 detection/接口手册与 models/name_dict.json 能够核对的算法。
INSERT INTO intelligent_analysis_capability (
    tenant_id, code, name, description, provider_id, provider_capability_code,
    capability_version, supported_media, parameter_schema_json, result_schema_json,
    enabled, creator
)
SELECT
    1,
    v.code,
    v.name,
    v.description,
    p.id,
    v.provider_code,
    '1',
    v.supported_media::jsonb,
    '{"type":"object","properties":{"roi":{"type":"array","minItems":4,"maxItems":4}}}'::jsonb,
    '{"type":"object","properties":{"verdict":{"enum":["NORMAL","ALARM"]},"confidence":{"type":"number"},"detections":{"type":"array"}}}'::jsonb,
    TRUE,
    'flyway-v153'
FROM intelligent_analysis_provider p
JOIN (
    VALUES
        ('MIXER_ROTATION', '搅拌机运行识别', '识别搅拌机是否正在转动', '0_0_0_3002_0_0', '["IMAGE","VIDEO"]'),
        ('GAS_FIRE', '漏气与烟火识别', '识别动态漏气或烟火', '0_0_0_3004_0_0', '["IMAGE","VIDEO"]'),
        ('LIQUID_LEAKAGE', '跑冒滴漏识别', '识别漏液和跑冒滴漏', '0_0_0_3005_0_0', '["IMAGE"]'),
        ('FLANGE_LEAKAGE', '法兰漏料识别', '识别法兰区域漏料', '0_0_0_3007_0_0', '["IMAGE"]')
) AS v(code, name, description, provider_code, supported_media)
    ON p.tenant_id = 1 AND p.code = 'LOCAL_DETECTION' AND p.deleted = FALSE
ON CONFLICT (tenant_id, code) WHERE deleted = FALSE
DO UPDATE SET
    name = EXCLUDED.name,
    description = EXCLUDED.description,
    provider_id = EXCLUDED.provider_id,
    provider_capability_code = EXCLUDED.provider_capability_code,
    supported_media = EXCLUDED.supported_media,
    updater = 'flyway-v153',
    update_time = CURRENT_TIMESTAMP;

-- 动作权威列必须和基础字段登记一致。模板表与所有现有租户表一次补齐。
DO $$
DECLARE
    tbl text;
BEGIN
    FOR tbl IN
        SELECT table_name
        FROM information_schema.tables
        WHERE table_schema = 'dynamicbusiness'
          AND table_type = 'BASE TABLE'
          AND table_name ~ '^ent_action(_t[0-9]+)?$'
    LOOP
        EXECUTE format(
            'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS action_domain VARCHAR(32)',
            tbl
        );
        EXECUTE format(
            'ALTER TABLE dynamicbusiness.%I ADD COLUMN IF NOT EXISTS analysis_capability_id BIGINT',
            tbl
        );
        EXECUTE format(
            $sql$
            UPDATE dynamicbusiness.%I
            SET action_domain = CASE
                    WHEN COALESCE(custom_fields ->> 'action_domain', '') = 'INTELLIGENT_RECOGNITION'
                        THEN 'INTELLIGENT_ANALYSIS'
                    ELSE COALESCE(NULLIF(action_domain, ''), custom_fields ->> 'action_domain')
                END,
                custom_fields = COALESCE(custom_fields, '{}'::jsonb)
                    - 'action_domain'
                    - 'recognition_capability_code',
                updater = COALESCE(updater, 'flyway-v153'),
                update_time = CURRENT_TIMESTAMP
            WHERE deleted = FALSE
              AND (
                  action_domain IS NULL
                  OR custom_fields ? 'action_domain'
                  OR custom_fields ? 'recognition_capability_code'
              )
            $sql$,
            tbl
        );
        EXECUTE format(
            'COMMENT ON COLUMN dynamicbusiness.%I.action_domain IS %L',
            tbl,
            '动作执行归属：DEVICE、INTELLIGENT_ANALYSIS、MANUAL、PLATFORM'
        );
        EXECUTE format(
            'COMMENT ON COLUMN dynamicbusiness.%I.analysis_capability_id IS %L',
            tbl,
            '智能分析动作引用的平台分析能力 id；非智能分析动作为空'
        );
    END LOOP;
END $$;

-- 字段库只保留新主名。旧识别能力字段停止参与运行时投影。
UPDATE dynamic_field
SET deleted = TRUE, status = 0, updater = 'flyway-v153', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND code = 'recognition_capability_code' AND deleted = FALSE;

UPDATE dynamic_entity_type_base_field
SET deleted = TRUE, status = 0, updater = 'flyway-v153', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND entity_type_code = 'action'
  AND field_code = 'recognition_capability_code'
  AND deleted = FALSE;

UPDATE dynamic_model_field_assignment
SET deleted = TRUE, updater = 'flyway-v153', update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND model_code = 'action'
  AND field_code = 'recognition_capability_code'
  AND deleted = FALSE;

INSERT INTO dynamic_field (
    code, name, type, unit, description, source, status, max_relations,
    index_strategy, options, provider_code, semantic_type, tenant_id, creator
) VALUES (
    'analysis_capability_id',
    '智能分析能力',
    'NUMBER',
    NULL,
    '智能分析动作引用的平台分析能力编号。非智能分析动作为空。',
    'BASE',
    1,
    1,
    'NONE',
    NULL,
    NULL,
    'analysis_capability',
    1,
    'flyway-v153'
)
ON CONFLICT (code, tenant_id) WHERE deleted = FALSE
DO UPDATE SET
    name = EXCLUDED.name,
    type = EXCLUDED.type,
    description = EXCLUDED.description,
    updater = 'flyway-v153',
    update_time = CURRENT_TIMESTAMP;

UPDATE dynamic_field
SET
    deleted = FALSE,
    status = 1,
    type = 'ENUM',
    name = '动作类型',
    description = '动作的唯一执行归属。智能分析动作调用智能分析模块，不进入设备协议。',
    options = '[
      {"label":"设备动作","value":"DEVICE"},
      {"label":"智能分析动作","value":"INTELLIGENT_ANALYSIS"},
      {"label":"人工动作","value":"MANUAL"},
      {"label":"平台动作","value":"PLATFORM"}
    ]',
    updater = 'flyway-v153',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1 AND code = 'action_domain';

UPDATE dynamic_entity_type_base_field
SET
    deleted = FALSE,
    status = 1,
    field_name = '动作类型',
    data_type = 'STRING',
    required = TRUE,
    description = '动作的唯一执行归属。智能分析动作调用智能分析模块，不进入设备协议。',
    type_config = '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb,
    updater = 'flyway-v153',
    update_time = CURRENT_TIMESTAMP
WHERE tenant_id = 1
  AND entity_type_code = 'action'
  AND field_code = 'action_domain';

INSERT INTO dynamic_entity_type_base_field (
    entity_type_code, library_field_id, field_code, field_name, data_type, required,
    default_value, description, type_config, sort_order, status, tenant_id, creator
)
SELECT
    'action',
    f.id,
    'analysis_capability_id',
    '智能分析能力',
    'NUMBER',
    FALSE,
    NULL,
    '智能分析动作引用的平台分析能力编号。非智能分析动作为空。',
    '{"createVisible":false,"editVisible":false,"detailVisible":false}'::jsonb,
    12,
    1,
    1,
    'flyway-v153'
FROM dynamic_field f
WHERE f.tenant_id = 1
  AND f.code = 'analysis_capability_id'
  AND f.deleted = FALSE
ON CONFLICT (entity_type_code, field_code, tenant_id) WHERE deleted = FALSE
DO UPDATE SET
    library_field_id = EXCLUDED.library_field_id,
    field_name = EXCLUDED.field_name,
    data_type = EXCLUDED.data_type,
    required = EXCLUDED.required,
    description = EXCLUDED.description,
    type_config = EXCLUDED.type_config,
    status = 1,
    updater = 'flyway-v153',
    update_time = CURRENT_TIMESTAMP;

INSERT INTO dynamic_model_field_assignment (
    model_id, field_id, required, is_searchable, is_filterable, is_sortable,
    sort, field_source, creator, tenant_id, model_code, field_code
)
SELECT
    m.id,
    f.id,
    FALSE,
    FALSE,
    FALSE,
    FALSE,
    12,
    'BASE',
    'flyway-v153',
    1,
    'action',
    'analysis_capability_id'
FROM dynamic_model m
JOIN dynamic_field f
  ON f.tenant_id = 1 AND f.code = 'analysis_capability_id' AND f.deleted = FALSE
WHERE m.tenant_id = 1
  AND m.entity_type_code = 'action'
  AND m.code = 'action'
  AND m.deleted = FALSE
ON CONFLICT (tenant_id, model_code, field_code) WHERE deleted = FALSE
DO UPDATE SET
    model_id = EXCLUDED.model_id,
    field_id = EXCLUDED.field_id,
    field_source = 'BASE',
    updater = 'flyway-v153',
    update_time = CURRENT_TIMESTAMP;

DELETE FROM model_crud_form_definition
WHERE entity_type_code = 'action';

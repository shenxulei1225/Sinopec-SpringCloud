-- 办事状态机：定义挂在台账类型上；每一跳的脉络行单独存放。
-- 不管作业标准步骤，不管审批流。报修（maintanance）第一版预置。
SET search_path TO dynamicbusiness;

CREATE TABLE IF NOT EXISTS dynamic_ledger_state_machine (
    id                BIGSERIAL PRIMARY KEY,
    entity_type_code  VARCHAR(64)  NOT NULL,
    version_no        INT          NOT NULL,
    published         BOOLEAN      NOT NULL DEFAULT TRUE,
    creator           VARCHAR(64)  DEFAULT '',
    create_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater           VARCHAR(64)  DEFAULT '',
    update_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted           BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id         BIGINT       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_ledger_state_machine
    ON dynamic_ledger_state_machine (tenant_id, entity_type_code, version_no)
    WHERE deleted = FALSE;

CREATE TABLE IF NOT EXISTS dynamic_ledger_state (
    id           BIGSERIAL PRIMARY KEY,
    machine_id   BIGINT       NOT NULL,
    state_code   VARCHAR(64)  NOT NULL,
    state_name   VARCHAR(64)  NOT NULL,
    start_state  BOOLEAN      NOT NULL DEFAULT FALSE,
    terminal_state BOOLEAN    NOT NULL DEFAULT FALSE,
    sort_no      INT          NOT NULL DEFAULT 0,
    creator      VARCHAR(64)  DEFAULT '',
    create_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater      VARCHAR(64)  DEFAULT '',
    update_time  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted      BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id    BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS dynamic_ledger_transition (
    id              BIGSERIAL PRIMARY KEY,
    machine_id      BIGINT       NOT NULL,
    from_state      VARCHAR(64)  NOT NULL,
    to_state        VARCHAR(64)  NOT NULL,
    page_code       VARCHAR(64)  NOT NULL DEFAULT '',
    page_action     VARCHAR(64)  NOT NULL DEFAULT '',
    required_fields VARCHAR(512) NOT NULL DEFAULT '',
    effect_codes    VARCHAR(512) NOT NULL DEFAULT '',
    trace_template  VARCHAR(512) NOT NULL,
    sort_no         INT          NOT NULL DEFAULT 0,
    creator         VARCHAR(64)  DEFAULT '',
    create_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater         VARCHAR(64)  DEFAULT '',
    update_time     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted         BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id       BIGINT       NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS dynamic_ledger_state_trace (
    id               BIGSERIAL PRIMARY KEY,
    entity_type_code VARCHAR(64)  NOT NULL,
    entity_id        BIGINT       NOT NULL,
    occurred_at      TIMESTAMP    NOT NULL,
    sentence         VARCHAR(512) NOT NULL,
    to_state         VARCHAR(64)  NOT NULL,
    definition_version INT        NOT NULL,
    creator          VARCHAR(64)  DEFAULT '',
    create_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updater          VARCHAR(64)  DEFAULT '',
    update_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id        BIGINT       NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_ledger_state_trace_entity
    ON dynamic_ledger_state_trace (tenant_id, entity_type_code, entity_id, occurred_at)
    WHERE deleted = FALSE;

-- 报修第一版。页面动作为空的转移不出现在受理页，只供工单验收回写。
WITH machine AS (
    INSERT INTO dynamic_ledger_state_machine (
        entity_type_code, version_no, published, creator, updater, tenant_id
    )
    SELECT 'maintanance', 1, TRUE, 'flyway-v202', 'flyway-v202', 1
    WHERE NOT EXISTS (
        SELECT 1 FROM dynamic_ledger_state_machine
        WHERE deleted = FALSE AND tenant_id = 1 AND entity_type_code = 'maintanance' AND version_no = 1
    )
    RETURNING id
), mid AS (
    SELECT id FROM machine
    UNION ALL
    SELECT id FROM dynamic_ledger_state_machine
    WHERE deleted = FALSE AND tenant_id = 1 AND entity_type_code = 'maintanance' AND version_no = 1
    LIMIT 1
)
INSERT INTO dynamic_ledger_state (machine_id, state_code, state_name, start_state, terminal_state, sort_no, creator, updater, tenant_id)
SELECT mid.id, s.code, s.name, s.start_state, s.terminal_state, s.sort_no, 'flyway-v202', 'flyway-v202', 1
FROM mid
CROSS JOIN (VALUES
    ('submitted', '已上报', TRUE, FALSE, 1),
    ('accepted', '已受理', FALSE, FALSE, 2),
    ('need_info', '待补充', FALSE, FALSE, 3),
    ('rejected', '已驳回', FALSE, TRUE, 4),
    ('dispatched', '已派工', FALSE, FALSE, 5),
    ('closed', '已关闭', FALSE, TRUE, 6)
) AS s(code, name, start_state, terminal_state, sort_no)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_ledger_state st
    WHERE st.deleted = FALSE AND st.machine_id = mid.id AND st.state_code = s.code
);

WITH mid AS (
    SELECT id FROM dynamic_ledger_state_machine
    WHERE deleted = FALSE AND tenant_id = 1 AND entity_type_code = 'maintanance' AND version_no = 1
    LIMIT 1
)
INSERT INTO dynamic_ledger_transition (
    machine_id, from_state, to_state, page_code, page_action, required_fields, effect_codes, trace_template, sort_no, creator, updater, tenant_id
)
SELECT mid.id, t.from_state, t.to_state, t.page_code, t.page_action, t.required_fields, t.effect_codes, t.trace_template, t.sort_no,
       'flyway-v202', 'flyway-v202', 1
FROM mid
CROSS JOIN (VALUES
    ('submitted', 'accepted', 'repair-accept', 'maintain', '', 'record_accept_time', '已受理', 1),
    ('need_info', 'accepted', 'repair-accept', 'maintain', '', 'record_accept_time', '已受理', 2),
    ('submitted', 'rejected', 'repair-accept', 'maintain', 'remark', 'save_remark', '已驳回：{说明}', 3),
    ('need_info', 'rejected', 'repair-accept', 'maintain', 'remark', 'save_remark', '已驳回：{说明}', 4),
    ('submitted', 'need_info', 'repair-accept', 'maintain', 'remark', 'save_remark', '待补充：{说明}', 5),
    ('need_info', 'need_info', 'repair-accept', 'maintain', 'remark', 'save_remark', '待补充：{说明}', 6),
    ('accepted', 'dispatched', 'repair-accept', 'maintain', 'teamId,plannedStart,standardId,title', 'create_work_order', '已安排给{班组} {执行人}，预计{开始}开始', 7),
    ('dispatched', 'closed', '', '', '', 'set_status', '已关闭', 8)
) AS t(from_state, to_state, page_code, page_action, required_fields, effect_codes, trace_template, sort_no)
WHERE NOT EXISTS (
    SELECT 1 FROM dynamic_ledger_transition tr
    WHERE tr.deleted = FALSE AND tr.machine_id = mid.id
      AND tr.from_state = t.from_state AND tr.to_state = t.to_state AND tr.sort_no = t.sort_no
);

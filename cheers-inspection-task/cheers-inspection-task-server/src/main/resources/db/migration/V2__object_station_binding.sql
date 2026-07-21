-- ============================================================================
-- Object profile, station binding, route plan ledger + task route snapshot columns
-- ============================================================================

CREATE TABLE inspection_object_profile (
    id                     BIGINT       NOT NULL PRIMARY KEY,
    facility_id            BIGINT       NOT NULL,
    object_id              BIGINT       NOT NULL,
    inspection_type        VARCHAR(32)  NOT NULL,
    default_work_minutes   INT          DEFAULT NULL,
    creator                VARCHAR(64)  DEFAULT '',
    create_time            TIMESTAMP    DEFAULT NULL,
    updater                VARCHAR(64)  DEFAULT '',
    update_time            TIMESTAMP    DEFAULT NULL,
    deleted                BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id              BIGINT       NOT NULL DEFAULT 100,
    CONSTRAINT uk_insp_obj_profile UNIQUE (tenant_id, facility_id, object_id)
);

COMMENT ON TABLE inspection_object_profile IS 'Inspection object profile (inspection type ledger)';
COMMENT ON COLUMN inspection_object_profile.inspection_type IS 'HUMAN|GROUND_ROBOT|UAV';

CREATE TABLE inspection_object_station_binding (
    id               BIGINT       NOT NULL PRIMARY KEY,
    facility_id      BIGINT       NOT NULL,
    object_id        BIGINT       NOT NULL,
    station_node_id  VARCHAR(128) NOT NULL,
    work_minutes     INT          DEFAULT NULL,
    sort_no          INT          NOT NULL DEFAULT 0,
    creator          VARCHAR(64)  DEFAULT '',
    create_time      TIMESTAMP    DEFAULT NULL,
    updater          VARCHAR(64)  DEFAULT '',
    update_time      TIMESTAMP    DEFAULT NULL,
    deleted          BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id        BIGINT       NOT NULL DEFAULT 100,
    CONSTRAINT uk_insp_obj_station UNIQUE (tenant_id, facility_id, object_id, station_node_id)
);

COMMENT ON TABLE inspection_object_station_binding IS 'Object to topology station node binding ledger';

CREATE TABLE inspection_route_plan (
    id                         BIGINT       NOT NULL PRIMARY KEY,
    facility_id                BIGINT       NOT NULL,
    name                       VARCHAR(255) NOT NULL,
    network_ref                VARCHAR(128) NOT NULL,
    inspection_type            VARCHAR(32)  NOT NULL,
    stop_ids                   TEXT         NOT NULL,
    planned_route              TEXT         DEFAULT NULL,
    duration_estimate_minutes  INT          DEFAULT NULL,
    task_id                    BIGINT       DEFAULT NULL,
    creator                    VARCHAR(64)  DEFAULT '',
    create_time                TIMESTAMP    DEFAULT NULL,
    updater                    VARCHAR(64)  DEFAULT '',
    update_time                TIMESTAMP    DEFAULT NULL,
    deleted                    BOOLEAN      NOT NULL DEFAULT FALSE,
    tenant_id                  BIGINT       NOT NULL DEFAULT 100
);

COMMENT ON TABLE inspection_route_plan IS 'Patrol route plan ledger';

ALTER TABLE inspection_task
    ADD COLUMN IF NOT EXISTS network_ref VARCHAR(128),
    ADD COLUMN IF NOT EXISTS planned_route TEXT,
    ADD COLUMN IF NOT EXISTS duration_estimate_minutes INT,
    ADD COLUMN IF NOT EXISTS inspection_type VARCHAR(32),
    ADD COLUMN IF NOT EXISTS route_plan_id BIGINT;

COMMENT ON COLUMN inspection_task.network_ref IS 'Confirmed route network reference snapshot';
COMMENT ON COLUMN inspection_task.planned_route IS 'Confirmed planned route JSON snapshot';
COMMENT ON COLUMN inspection_task.duration_estimate_minutes IS 'Confirmed route duration estimate in minutes';
COMMENT ON COLUMN inspection_task.inspection_type IS 'HUMAN|GROUND_ROBOT|UAV';
COMMENT ON COLUMN inspection_task.route_plan_id IS 'Linked inspection_route_plan id';

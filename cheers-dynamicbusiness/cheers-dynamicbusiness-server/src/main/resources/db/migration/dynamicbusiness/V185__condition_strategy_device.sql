-- 策略挂到哪些设备。一条策略可以挂多台。没挂上的设备不套用这条策略。
SET search_path TO dynamicbusiness, public;

CREATE TABLE IF NOT EXISTS dynamic_condition_strategy_device (
    id           BIGSERIAL PRIMARY KEY,
    tenant_id    BIGINT    NOT NULL DEFAULT 0,
    strategy_id  BIGINT    NOT NULL,
    device_id    BIGINT    NOT NULL,
    creator      VARCHAR(64),
    create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updater      VARCHAR(64),
    update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted      BOOLEAN   DEFAULT FALSE
);

COMMENT ON TABLE dynamic_condition_strategy_device IS
    '条件策略挂到哪些设备。数据处理只判断已经挂上的策略。';
COMMENT ON COLUMN dynamic_condition_strategy_device.strategy_id IS
    'dynamic_condition_strategy.id';
COMMENT ON COLUMN dynamic_condition_strategy_device.device_id IS
    '设备实体编号。不是点位逻辑编号。';

CREATE UNIQUE INDEX IF NOT EXISTS uk_condition_strategy_device
    ON dynamic_condition_strategy_device (strategy_id, device_id)
    WHERE deleted = FALSE;

CREATE INDEX IF NOT EXISTS idx_condition_strategy_device_tenant
    ON dynamic_condition_strategy_device (tenant_id, strategy_id)
    WHERE deleted = FALSE;

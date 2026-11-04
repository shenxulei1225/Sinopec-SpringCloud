-- 设备上有哪些点。这是配置，不是采样。
-- 采样值在历史库，不写进这张表，也不写进设备行。

CREATE TABLE IF NOT EXISTS dynamicbusiness.device_collection_point (
    id bigserial PRIMARY KEY,
    tenant_id bigint NOT NULL,
    device_id bigint NOT NULL,
    point_code varchar(64) NOT NULL,
    point_name varchar(64) NOT NULL,
    unit varchar(32),
    CONSTRAINT device_collection_point_uk UNIQUE (tenant_id, device_id, point_code)
);

COMMENT ON TABLE dynamicbusiness.device_collection_point IS '一台设备上要采集的点。设备编号指向设备台账的 id。这里不存采样值。';
COMMENT ON COLUMN dynamicbusiness.device_collection_point.device_id IS '设备编号。';
COMMENT ON COLUMN dynamicbusiness.device_collection_point.point_code IS '点的编码。同一台设备上不能重复。';
COMMENT ON COLUMN dynamicbusiness.device_collection_point.point_name IS '点的名称，例如压力、温度、湿度。';
COMMENT ON COLUMN dynamicbusiness.device_collection_point.unit IS '工程值使用的单位。还没定单位时留空。';

CREATE INDEX IF NOT EXISTS device_collection_point_device_idx
    ON dynamicbusiness.device_collection_point (tenant_id, device_id);

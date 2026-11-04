-- 设备点的历史采样。只在历史库执行，不进业务库。
-- 一条是：租户、设备编号、点、时间、工程值、质量。
-- 按月分表。整月都早于保留期才删。质量只允许「正常」和「异常」。

CREATE SCHEMA IF NOT EXISTS history;

CREATE TABLE IF NOT EXISTS history.device_point_sample (
    tenant_id bigint NOT NULL,
    device_id bigint NOT NULL,
    point_code varchar(64) NOT NULL,
    sampled_at timestamptz NOT NULL,
    engineering_value double precision NOT NULL,
    quality varchar(16) NOT NULL,
    CONSTRAINT device_point_sample_quality_chk CHECK (quality IN ('正常', '异常')),
    PRIMARY KEY (tenant_id, device_id, point_code, sampled_at)
) PARTITION BY RANGE (sampled_at);

COMMENT ON TABLE history.device_point_sample IS '设备点的历史采样。设备编号是业务库设备行的编号，这里不设外键。';
COMMENT ON COLUMN history.device_point_sample.device_id IS '设备编号。';
COMMENT ON COLUMN history.device_point_sample.point_code IS '点。同一台设备上的一项读数，例如压力、温度、湿度。';
COMMENT ON COLUMN history.device_point_sample.sampled_at IS '这条读数发生的时间。';
COMMENT ON COLUMN history.device_point_sample.engineering_value IS '工程值。已经按该点的单位换算后的数。';
COMMENT ON COLUMN history.device_point_sample.quality IS '质量。正常可以当曲线上的点；异常只留档，不当正常点。';

CREATE INDEX IF NOT EXISTS device_point_sample_device_time_idx
    ON history.device_point_sample (tenant_id, device_id, point_code, sampled_at DESC);

-- 按上海时区的自然月建分表。写入前调用，缺月就建，已有则不动。
CREATE OR REPLACE FUNCTION history.ensure_sample_month(p_at timestamptz)
RETURNS void
LANGUAGE plpgsql
AS $$
DECLARE
    month_start timestamptz;
    month_end timestamptz;
    part_name text;
BEGIN
    month_start := date_trunc('month', p_at AT TIME ZONE 'Asia/Shanghai') AT TIME ZONE 'Asia/Shanghai';
    month_end := month_start + interval '1 month';
    part_name := 'device_point_sample_' || to_char(month_start AT TIME ZONE 'Asia/Shanghai', 'YYYY_MM');
    IF NOT EXISTS (
        SELECT 1
        FROM pg_class c
        JOIN pg_namespace n ON n.oid = c.relnamespace
        WHERE n.nspname = 'history'
          AND c.relname = part_name
    ) THEN
        EXECUTE format(
            'CREATE TABLE history.%I PARTITION OF history.device_point_sample FOR VALUES FROM (%L) TO (%L)',
            part_name,
            month_start,
            month_end
        );
    END IF;
END;
$$;

-- 只删除整段都早于截止时间的月表。还压着保留期的那个月留下。
CREATE OR REPLACE FUNCTION history.drop_sample_partitions_ended_before(p_before timestamptz)
RETURNS integer
LANGUAGE plpgsql
AS $$
DECLARE
    dropped integer := 0;
    part record;
    bounds text;
    upper_bound timestamptz;
BEGIN
    FOR part IN
        SELECT c.relname AS part_name, pg_get_expr(c.relpartbound, c.oid) AS bound_expr
        FROM pg_inherits i
        JOIN pg_class c ON c.oid = i.inhrelid
        JOIN pg_class parent ON parent.oid = i.inhparent
        JOIN pg_namespace n ON n.oid = parent.relnamespace
        WHERE n.nspname = 'history'
          AND parent.relname = 'device_point_sample'
    LOOP
        bounds := part.bound_expr;
        IF bounds IS NULL OR bounds !~ 'TO \(' THEN
            CONTINUE;
        END IF;
        upper_bound := substring(bounds FROM 'TO \(''([^'']+)''\)')::timestamptz;
        IF upper_bound <= p_before THEN
            EXECUTE format('DROP TABLE history.%I', part.part_name);
            dropped := dropped + 1;
        END IF;
    END LOOP;
    RETURN dropped;
END;
$$;

SELECT history.ensure_sample_month(now());
SELECT history.ensure_sample_month(now() + interval '1 month');

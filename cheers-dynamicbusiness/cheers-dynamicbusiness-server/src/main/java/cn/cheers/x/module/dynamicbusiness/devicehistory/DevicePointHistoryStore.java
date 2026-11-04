package cn.cheers.x.module.dynamicbusiness.devicehistory;

import cn.cheers.x.framework.common.exception.ServiceException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 历史库的采样读写。连接只指向历史库。
 * 不管点的名单，名单在业务库。
 * 禁止把采样写进业务库，也禁止在读的时候编一条最新值。
 */
@Component
public class DevicePointHistoryStore {

    private static final Logger log = LoggerFactory.getLogger(DevicePointHistoryStore.class);
    private static final int UNAVAILABLE = 503;

    private final DevicePointHistoryProperties properties;
    private HikariDataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    public DevicePointHistoryStore(DevicePointHistoryProperties properties) {
        this.properties = properties;
    }

    /**
     * 连接在第一次读写时打开。没配好就拒绝，不连到业务库顶上。
     */
    private synchronized JdbcTemplate jdbc() {
        if (!properties.isReady()) {
            log.error("历史库未配置，采样不能写入或查询");
            throw new ServiceException(UNAVAILABLE, "历史库未配置");
        }
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(pool());
        }
        return jdbcTemplate;
    }

    private synchronized HikariDataSource pool() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getJdbcUrl());
            config.setUsername(properties.getUsername());
            config.setPassword(properties.getPassword());
            config.setMaximumPoolSize(4);
            config.setPoolName("device-history");
            dataSource = new HikariDataSource(config);
        }
        return dataSource;
    }

    @PreDestroy
    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    /**
     * 追加一条采样。调用前点必须已经在业务库配好。
     * 质量异常也记。没有工程值的不记。
     */
    public void append(long tenantId, long deviceId, String pointCode, Instant sampledAt,
                       double engineeringValue, String quality) {
        DevicePointHistoryRules.requireQuality(quality);
        DevicePointHistoryRules.requireEngineeringValue(engineeringValue);
        if (sampledAt == null) {
            throw new ServiceException(400, "这条采集没有时间");
        }
        JdbcTemplate jdbc = jdbc();
        jdbc.query("SELECT history.ensure_sample_month(?)", rs -> null, Timestamp.from(sampledAt));
        jdbc.update("""
                INSERT INTO history.device_point_sample
                    (tenant_id, device_id, point_code, sampled_at, engineering_value, quality)
                VALUES (?, ?, ?, ?, ?, ?)
                """, tenantId, deviceId, pointCode, Timestamp.from(sampledAt), engineeringValue, quality);
    }

    /**
     * 一批采样一次写入。空列表不写。
     * 同一个月只确认一次分表。调用前这些点已经在点位对照里。
     */
    public void appendBatch(List<DevicePointWrite> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        for (DevicePointWrite row : rows) {
            DevicePointHistoryRules.requireQuality(row.quality());
            DevicePointHistoryRules.requireEngineeringValue(row.engineeringValue());
            if (row.sampledAt() == null) {
                throw new ServiceException(400, "这条采集没有时间");
            }
        }
        JdbcTemplate jdbc = jdbc();
        ZoneId zone = ZoneId.systemDefault();
        Set<YearMonth> months = new LinkedHashSet<>();
        for (DevicePointWrite row : rows) {
            months.add(YearMonth.from(row.sampledAt().atZone(zone)));
        }
        for (YearMonth month : months) {
            Instant at = month.atDay(1).atStartOfDay(zone).toInstant();
            jdbc.query("SELECT history.ensure_sample_month(?)", rs -> null, Timestamp.from(at));
        }
        jdbc.batchUpdate("""
                INSERT INTO history.device_point_sample
                    (tenant_id, device_id, point_code, sampled_at, engineering_value, quality)
                VALUES (?, ?, ?, ?, ?, ?)
                """, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement statement, int index) throws SQLException {
                DevicePointWrite row = rows.get(index);
                statement.setLong(1, row.tenantId());
                statement.setLong(2, row.deviceId());
                statement.setString(3, row.pointCode());
                statement.setTimestamp(4, Timestamp.from(row.sampledAt()));
                statement.setDouble(5, row.engineeringValue());
                statement.setString(6, row.quality());
            }

            @Override
            public int getBatchSize() {
                return rows.size();
            }
        });
    }

    /**
     * 这些设备里，历史库已经有过采样的。没有采样的不在结果里。
     * 历史库没配好时返回 null，调用方不要把设备画成在线或不在线。
     */
    public Set<Long> devicesWithSamples(long tenantId, List<Long> deviceIds) {
        if (!properties.isReady()) {
            return null;
        }
        List<Long> ids = deviceIds == null ? List.of() : deviceIds.stream()
                .filter(id -> id != null && id > 0)
                .distinct()
                .toList();
        if (ids.isEmpty()) {
            return Set.of();
        }
        Set<Long> online = new LinkedHashSet<>();
        JdbcTemplate jdbc = jdbc();
        for (int start = 0; start < ids.size(); start += 400) {
            List<Long> chunk = ids.subList(start, Math.min(start + 400, ids.size()));
            String placeholders = "?,".repeat(chunk.size()).substring(0, chunk.size() * 2 - 1);
            List<Object> args = new java.util.ArrayList<>();
            args.add(tenantId);
            args.addAll(chunk);
            jdbc.query("""
                    SELECT DISTINCT device_id
                    FROM history.device_point_sample
                    WHERE tenant_id = ? AND device_id IN (%s)
                    """.formatted(placeholders),
                    (rs, rowNum) -> {
                        online.add(rs.getLong("device_id"));
                        return null;
                    }, args.toArray());
        }
        return online;
    }

    /**
     * 每个点最新的一条。没有采样就返回空列表。
     */
    public List<DevicePointSample> latest(long tenantId, long deviceId,
                                          List<DeviceCollectionPoint> points) {
        return jdbc().query("""
                SELECT DISTINCT ON (point_code)
                    device_id, point_code, sampled_at, engineering_value, quality
                FROM history.device_point_sample
                WHERE tenant_id = ? AND device_id = ?
                ORDER BY point_code, sampled_at DESC
                """, (rs, rowNum) -> toSample(points, rs.getLong("device_id"), rs.getString("point_code"),
                rs.getTimestamp("sampled_at").toInstant(), rs.getDouble("engineering_value"),
                rs.getString("quality")), tenantId, deviceId);
    }

    /**
     * 一段时间内的采样，按时间从早到晚。超过 5000 条就拒绝，让人缩小时间。
     */
    public List<DevicePointSample> range(long tenantId, long deviceId, String pointCode,
                                         Instant from, Instant to, List<DeviceCollectionPoint> points) {
        DevicePointHistoryRules.requireRange(from, to);
        List<DevicePointSample> rows = jdbc().query("""
                SELECT device_id, point_code, sampled_at, engineering_value, quality
                FROM history.device_point_sample
                WHERE tenant_id = ? AND device_id = ?
                  AND (?::varchar IS NULL OR point_code = ?)
                  AND sampled_at >= ? AND sampled_at < ?
                ORDER BY sampled_at
                LIMIT 5001
                """, (rs, rowNum) -> toSample(points, rs.getLong("device_id"), rs.getString("point_code"),
                rs.getTimestamp("sampled_at").toInstant(), rs.getDouble("engineering_value"),
                rs.getString("quality")), tenantId, deviceId, pointCode, pointCode,
                Timestamp.from(from), Timestamp.from(to));
        if (rows.size() > 5000) {
            throw new ServiceException(400, "这个时间范围里的点太多，请缩小时间");
        }
        return rows;
    }

    private DevicePointSample toSample(List<DeviceCollectionPoint> points, long deviceId, String pointCode,
                                       Instant sampledAt, double engineeringValue, String quality) {
        String pointName = pointCode;
        String unit = null;
        for (DeviceCollectionPoint point : points) {
            if (point.getPointCode().equals(pointCode)) {
                pointName = point.getPointName();
                unit = point.getUnit();
                break;
            }
        }
        return new DevicePointSample(deviceId, pointCode, pointName, unit, sampledAt, engineeringValue, quality);
    }
}

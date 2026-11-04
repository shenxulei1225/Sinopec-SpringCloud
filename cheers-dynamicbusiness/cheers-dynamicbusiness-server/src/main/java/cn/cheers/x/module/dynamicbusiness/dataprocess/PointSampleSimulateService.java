package cn.cheers.x.module.dynamicbusiness.dataprocess;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DeviceCollectionPointQuery;
import cn.cheers.x.module.dynamicbusiness.devicehistory.DevicePointHistoryRules;
import cn.cheers.x.module.dynamicbusiness.devicehistory.ParsedPointSample;
import cn.cheers.x.module.dynamicbusiness.devicehistory.PointSampleBatchReporter;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 把指定设备、点上的读数交给正式判断入口。
 * 位置必须已经写在设备上。点必须是这台设备型号上的采集点。
 * 不负责：判断阈值、写告警、查找联动、下发风机。
 * 禁止：缺位置时猜场站，缺采集点时用别的字段冒充。
 */
@Service
public class PointSampleSimulateService {

    private static final int BAD_REQUEST = 400;
    private static final String EQUIPMENT = "equipment";
    private static final Pattern TABLE_NAME = Pattern.compile("^[a-z][a-z0-9_]*$");

    private final PointSampleBatchReporter reporter;
    private final DeviceCollectionPointQuery points;
    private final EntityTableNameHandler tableNames;
    private final JdbcTemplate jdbcTemplate;

    public PointSampleSimulateService(PointSampleBatchReporter reporter, DeviceCollectionPointQuery points,
                                      EntityTableNameHandler tableNames, JdbcTemplate jdbcTemplate) {
        this.reporter = reporter;
        this.points = points;
        this.tableNames = tableNames;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 按列表顺序交给判断。同一台设备同一个点，后一条把前一条当成上一次读数。
     * 第一条已经越过限值就会开告警。仍越限的后续读数不会再开一次。
     */
    public int report(List<Reading> readings) {
        if (readings == null || readings.isEmpty()) {
            throw new ServiceException(BAD_REQUEST, "没有读数");
        }
        long tenantId = TenantContextHolder.getRequiredTenantId();
        Instant start = Instant.now();
        List<ParsedPointSample> samples = new ArrayList<>(readings.size());
        for (int index = 0; index < readings.size(); index++) {
            samples.add(toSample(tenantId, readings.get(index), start.plusSeconds(index)));
        }
        reporter.accept(samples);
        return samples.size();
    }

    private ParsedPointSample toSample(long tenantId, Reading reading, Instant sampledAt) {
        if (reading == null || reading.deviceId() == null || reading.deviceId() <= 0) {
            throw new ServiceException(BAD_REQUEST, "读数没有设备");
        }
        if (reading.pointCode() == null || reading.pointCode().isBlank()) {
            throw new ServiceException(BAD_REQUEST, "读数没有点");
        }
        DevicePointHistoryRules.requireEngineeringValue(reading.value());
        points.require(tenantId, reading.deviceId(), reading.pointCode().trim());
        long zoneId = requireZoneId(tenantId, reading.deviceId());
        return new ParsedPointSample(
                tenantId,
                reading.deviceId(),
                reading.pointCode().trim(),
                reading.pointCode().trim(),
                zoneId,
                sampledAt,
                reading.value(),
                DevicePointHistoryRules.QUALITY_NORMAL);
    }

    private long requireZoneId(long tenantId, long deviceId) {
        String table = tableNames.resolvePhysicalTableName(EQUIPMENT);
        if (!TABLE_NAME.matcher(table).matches()) {
            throw new ServiceException(BAD_REQUEST, "设备表名不可用");
        }
        try {
            Long zoneId = jdbcTemplate.queryForObject(
                    "SELECT zone_id FROM " + table + " WHERE id = ? AND tenant_id = ? AND deleted = false",
                    Long.class, deviceId, tenantId);
            if (zoneId == null || zoneId <= 0) {
                throw new ServiceException(BAD_REQUEST, "这台设备没有位置，不参加告警判断");
            }
            return zoneId;
        } catch (EmptyResultDataAccessException ex) {
            throw new ServiceException(BAD_REQUEST, "没有这台设备");
        }
    }

    /**
     * 一条要上报的读数。值必须是工程值。
     */
    public record Reading(Long deviceId, String pointCode, Double value) {
    }
}

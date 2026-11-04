package cn.cheers.x.module.dynamicbusiness.devicehistory;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * 设备详情读历史，采集写入历史。两边用同一份点名单。
 * 不管页面推送，也不下发控制。
 */
@Service
public class DevicePointHistoryService {

    private final DeviceCollectionPointQuery points;
    private final DevicePointHistoryStore history;

    public DevicePointHistoryService(DeviceCollectionPointQuery points, DevicePointHistoryStore history) {
        this.points = points;
        this.history = history;
    }

    /**
     * 设备详情打开时，这台设备配了哪些点。
     */
    public List<DeviceCollectionPoint> listPoints(long tenantId, long deviceId) {
        return points.list(tenantId, deviceId);
    }

    /**
     * 这些设备里，历史库已经有过采样的。没有采样的不在结果里。
     * 历史库没配好时返回 null，调用方不要把设备画成在线或不在线。
     */
    public Set<Long> devicesWithSamples(long tenantId, List<Long> deviceIds) {
        return history.devicesWithSamples(tenantId, deviceIds);
    }

    /**
     * 每个点目前最后一条读数。还没有读数时列表为空。
     */
    public List<DevicePointSample> latest(long tenantId, long deviceId) {
        return history.latest(tenantId, deviceId, points.list(tenantId, deviceId));
    }

    /**
     * 设备详情里看一段曲线。点可以不传，不传就看这台设备上的全部点。
     */
    public List<DevicePointSample> range(long tenantId, long deviceId, String pointCode, Instant from, Instant to) {
        String code = pointCode == null || pointCode.isBlank() ? null : pointCode;
        if (code != null) {
            points.require(tenantId, deviceId, code);
        }
        return history.range(tenantId, deviceId, code, from, to, points.list(tenantId, deviceId));
    }

    /**
     * 采集到一条读数后只写入历史库。不改型号字段，也不改设备台账上的值。
 * 当前值是历史库里这个点最新的一行。
     */
    public void append(long tenantId, long deviceId, String pointCode, Instant sampledAt,
                       double engineeringValue, String quality) {
        DeviceCollectionPoint point = points.require(tenantId, deviceId, pointCode);
        history.append(tenantId, point.getDeviceId(), point.getPointCode(), sampledAt, engineeringValue, quality);
    }

    /**
     * 网关交来的一批读数一次写入。点已经在点位对照里，这里不再逐条查名单。
     */
    public void appendBatch(List<DevicePointWrite> rows) {
        history.appendBatch(rows);
    }
}

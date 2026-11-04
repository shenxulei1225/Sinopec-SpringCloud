package cn.cheers.x.module.dynamicbusiness.devicehistory;

import java.time.Instant;

/**
 * 网关解析后的一条读数。
 * 只带对照里已经有的设备、点、分区和时间质量。不管阈值，也不管要不要告警。
 */
public record ParsedPointSample(
        long tenantId,
        long deviceId,
        String pointCode,
        String bindingCode,
        Long zoneId,
        Instant sampledAt,
        double engineeringValue,
        String quality
) {
}

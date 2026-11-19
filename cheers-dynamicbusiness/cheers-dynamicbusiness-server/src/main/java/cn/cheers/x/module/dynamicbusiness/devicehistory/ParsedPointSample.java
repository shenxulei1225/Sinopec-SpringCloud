package cn.cheers.x.module.dynamicbusiness.devicehistory;

import java.time.Instant;

/**
 * 网关解析后的一条读数。
 * 不管阈值，也不管要不要告警。告警归属由告警目录的声明决定，不在这条读数上带。
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

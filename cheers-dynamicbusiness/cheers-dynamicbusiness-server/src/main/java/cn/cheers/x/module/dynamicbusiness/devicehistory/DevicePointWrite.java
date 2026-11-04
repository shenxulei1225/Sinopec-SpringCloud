package cn.cheers.x.module.dynamicbusiness.devicehistory;

import java.time.Instant;

/**
 * 准备写入历史库的一条采样。
 * 点编号是型号字段编号。不管告警，也不管页面。
 */
public record DevicePointWrite(
        long tenantId,
        long deviceId,
        String pointCode,
        Instant sampledAt,
        double engineeringValue,
        String quality
) {
}

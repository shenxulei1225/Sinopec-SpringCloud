package cn.cheers.x.module.dynamicbusiness.devicehistory;

import java.time.Instant;

/**
 * 历史库里的一条采样。设备编号是抄过去的数字，不是关联表。
 */
public class DevicePointSample {

    private final long deviceId;
    private final String pointCode;
    private final String pointName;
    private final String unit;
    private final Instant sampledAt;
    private final double engineeringValue;
    private final String quality;

    public DevicePointSample(long deviceId, String pointCode, String pointName, String unit,
                             Instant sampledAt, double engineeringValue, String quality) {
        this.deviceId = deviceId;
        this.pointCode = pointCode;
        this.pointName = pointName;
        this.unit = unit;
        this.sampledAt = sampledAt;
        this.engineeringValue = engineeringValue;
        this.quality = quality;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public String getPointCode() {
        return pointCode;
    }

    public String getPointName() {
        return pointName;
    }

    public String getUnit() {
        return unit;
    }

    public Instant getSampledAt() {
        return sampledAt;
    }

    public double getEngineeringValue() {
        return engineeringValue;
    }

    public String getQuality() {
        return quality;
    }
}

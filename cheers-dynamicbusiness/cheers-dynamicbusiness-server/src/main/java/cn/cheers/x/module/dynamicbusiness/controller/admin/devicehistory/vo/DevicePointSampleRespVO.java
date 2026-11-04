package cn.cheers.x.module.dynamicbusiness.controller.admin.devicehistory.vo;

import java.time.Instant;

/**
 * 设备详情上的一条点值。没有采样时不使用这个对象充数。
 */
public class DevicePointSampleRespVO {

    private Long deviceId;
    private String pointCode;
    private String pointName;
    private String unit;
    private Instant sampledAt;
    private Double engineeringValue;
    private String quality;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getPointCode() {
        return pointCode;
    }

    public void setPointCode(String pointCode) {
        this.pointCode = pointCode;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Instant getSampledAt() {
        return sampledAt;
    }

    public void setSampledAt(Instant sampledAt) {
        this.sampledAt = sampledAt;
    }

    public Double getEngineeringValue() {
        return engineeringValue;
    }

    public void setEngineeringValue(Double engineeringValue) {
        this.engineeringValue = engineeringValue;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }
}

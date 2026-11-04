package cn.cheers.x.module.dynamicbusiness.controller.admin.devicehistory.vo;

/**
 * 设备详情上列出的一个点。这里没有采样值。
 */
public class DeviceCollectionPointRespVO {

    private Long deviceId;
    private String pointCode;
    private String pointName;
    private String unit;

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
}

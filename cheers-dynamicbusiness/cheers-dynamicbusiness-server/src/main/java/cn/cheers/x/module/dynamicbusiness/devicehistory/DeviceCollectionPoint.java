package cn.cheers.x.module.dynamicbusiness.devicehistory;

/**
 * 型号上的一个采集点。点的编号是字段编号。名称和单位给人看。没有采样值。
 */
public class DeviceCollectionPoint {

    private final long deviceId;
    private final String pointCode;
    private final String pointName;
    private final String unit;

    public DeviceCollectionPoint(long deviceId, String pointCode, String pointName, String unit) {
        this.deviceId = deviceId;
        this.pointCode = pointCode;
        this.pointName = pointName;
        this.unit = unit;
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
}

package cn.cheers.x.module.dynamicbusiness.controller.admin.alarmlinkageverify.vo;

/**
 * 验证页下拉里的一台设备。名称给人看，编号只给后续查询用。
 */
public class AlarmLinkageVerifyDeviceRespVO {

    private Long deviceId;
    private String deviceName;
    private String modelName;

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }
}

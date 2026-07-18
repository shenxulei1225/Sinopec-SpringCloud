package cn.iocoder.yudao.module.sinopec.device.api.admin.dto;

import lombok.Data;

/**
 * 设备详情响应 DTO。
 */
@Data
public class DeviceDetailRespDTO {

    private String deviceId;
    private String deviceName;
    private Integer siteId;
    private Integer deviceType;
    private String stateDesc;
    private Integer stateCode;
    private String ipAddress;
    private String longitude;
    private String latitude;

}

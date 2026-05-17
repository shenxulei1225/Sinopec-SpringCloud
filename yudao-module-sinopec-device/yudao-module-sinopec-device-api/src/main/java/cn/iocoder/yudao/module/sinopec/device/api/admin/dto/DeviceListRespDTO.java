package cn.iocoder.yudao.module.sinopec.device.api.admin.dto;

import lombok.Data;

/**
 * 设备列表响应 DTO。
 */
@Data
public class DeviceListRespDTO {

    private String deviceId;
    private String deviceName;
    private Integer deviceType;
    private String deviceModel;
    private Integer currentStateTypeCode;
    private String currentStateTypeDesc;

}

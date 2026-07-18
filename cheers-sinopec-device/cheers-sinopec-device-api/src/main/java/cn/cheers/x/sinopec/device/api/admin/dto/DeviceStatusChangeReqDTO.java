package cn.cheers.x.sinopec.device.api.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 设备状态变更请求 DTO。
 */
@Data
public class DeviceStatusChangeReqDTO {

    @NotNull(message = "站场编号不能为空")
    private Integer siteId;

    @NotBlank(message = "设备编号不能为空")
    private String deviceId;

    @NotBlank(message = "操作密码不能为空")
    private String password;

    private String reason;

}

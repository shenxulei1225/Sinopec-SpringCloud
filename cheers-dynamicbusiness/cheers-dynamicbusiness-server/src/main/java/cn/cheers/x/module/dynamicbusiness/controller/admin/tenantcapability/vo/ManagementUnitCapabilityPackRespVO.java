package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ManagementUnitCapabilityPackRespVO {

    @Schema(description = "锚点数据目录注册编码")
    private String registryCode;

    @Schema(description = "锚点记录 id")
    private Long managementUnitId;

    @Schema(description = "已启用的包编码")
    private List<String> packCodes;
}
package cn.cheers.x.module.dynamicbusiness.controller.admin.tenantcapability.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ManagementUnitCapabilityPackSaveReqVO {

    @Schema(description = "锚点数据目录注册编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "缺少数据目录编码")
    private String registryCode;

    @Schema(description = "锚点记录 id", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "缺少锚点记录")
    private Long managementUnitId;

    @Schema(description = "启用的业务能力包编码整份替换；空列表表示全部取消")
    private List<String> packCodes;
}
package cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo;

import cn.cheers.x.module.dynamicbusiness.service.capability.contract.BusinessCapabilityFullContract;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 能力全集响应。
 */
@Schema(description = "管理后台 - 业务能力全集响应")
@Data
public class BusinessCapabilityFullRespVO {

    @Schema(description = "业务类型编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    private String businessTypeCode;

    @Schema(description = "完整能力契约结构体", requiredMode = Schema.RequiredMode.REQUIRED)
    private BusinessCapabilityFullContract capabilityFull;

    @Schema(description = "能力版本号", example = "8")
    private Long version;
}

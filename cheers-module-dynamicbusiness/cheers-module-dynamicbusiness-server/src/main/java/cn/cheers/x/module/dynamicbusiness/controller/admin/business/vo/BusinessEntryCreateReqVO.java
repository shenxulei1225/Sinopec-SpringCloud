package cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 业务入口创建 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessEntryCreateReqVO extends BusinessEntryBaseVO {

    @Schema(description = "所属业务编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "业务编号不能为空")
    private Long businessId;
}

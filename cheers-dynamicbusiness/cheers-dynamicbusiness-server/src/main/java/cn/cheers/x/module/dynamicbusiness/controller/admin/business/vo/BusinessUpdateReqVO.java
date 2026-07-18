package cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 门户业务更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class BusinessUpdateReqVO extends BusinessBaseVO {

    @Schema(description = "业务编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "业务编号不能为空")
    private Long id;
}

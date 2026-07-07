package cn.cheers.x.module.dynamicbusiness.controller.admin.businesstype.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 业务类型更新 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BusinessTypeUpdateReqVO extends BusinessTypeBaseVO {

    @Schema(description = "业务类型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "业务类型编号不能为空")
    private Long id;

}
 
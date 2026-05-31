package cn.cheers.x.module.dynamicbusiness.controller.admin.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理后台 - 通用分组更新 Request VO")
public class GroupUpdateReqVO extends GroupBaseVO {

    @Schema(description = "分组ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "分组ID不能为空")
    private Long id;
}

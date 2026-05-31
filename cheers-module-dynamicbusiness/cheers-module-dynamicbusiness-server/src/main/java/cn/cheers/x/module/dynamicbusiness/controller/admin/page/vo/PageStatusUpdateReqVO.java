package cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 页面状态更新 Request VO")
@Data
public class PageStatusUpdateReqVO {

    @Schema(description = "页面ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "页面ID不能为空")
    private Long id;

    @Schema(description = "页面状态（1-发布，0-停用）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "页面状态不能为空")
    private Integer status;
}



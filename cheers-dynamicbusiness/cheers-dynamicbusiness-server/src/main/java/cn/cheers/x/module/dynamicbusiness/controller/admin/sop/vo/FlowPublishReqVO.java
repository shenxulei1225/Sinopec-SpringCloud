package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 按版本发布 SOP 请求。
 */
@Data
public class FlowPublishReqVO {

    @Schema(description = "要发布的版本号（version_no）", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    @NotNull(message = "versionNo 不能为空")
    @Min(value = 1, message = "versionNo 必须大于 0")
    private Integer versionNo;
}


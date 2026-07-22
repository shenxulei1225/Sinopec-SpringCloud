package cn.iocoder.yudao.module.emergency.controller.admin.guarantee.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 保障资源创建 Request VO")
@Data
public class GuaranteeResourceCreateReqVO {

    @Schema(description = "保障ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "保障ID不能为空")
    private Long guaranteeId;

    @Schema(description = "资源ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "资源ID不能为空")
    private Long resourceId;

    @Schema(description = "资源类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "equipment")
    @NotNull(message = "资源类型不能为空")
    private String resourceType;

    @Schema(description = "数量", example = "10")
    private Integer quantity;
}




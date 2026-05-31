package cn.cheers.x.module.dynamicbusiness.controller.admin.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "管理后台 - 通用分组重排 Request VO")
public class GroupReorderGroupReqVO {

    @Schema(description = "分组类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "FIELD")
    @NotBlank(message = "分组类型不能为空")
    private String groupType;

    @Schema(description = "被拖动分组ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "被拖动分组ID不能为空")
    private Long movedGroupId;

    @Schema(description = "前一个分组ID", example = "9")
    private Long prevGroupId;

    @Schema(description = "后一个分组ID", example = "11")
    private Long nextGroupId;
}

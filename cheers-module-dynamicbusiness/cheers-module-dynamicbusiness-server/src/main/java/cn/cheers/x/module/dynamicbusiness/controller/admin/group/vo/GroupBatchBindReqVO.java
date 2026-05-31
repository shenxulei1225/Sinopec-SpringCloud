package cn.cheers.x.module.dynamicbusiness.controller.admin.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "管理后台 - 通用分组批量绑定 Request VO")
public class GroupBatchBindReqVO {

    @Schema(description = "分组类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "UNIT")
    @NotBlank(message = "分组类型不能为空")
    private String groupType;

    @Schema(description = "分组ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "分组ID不能为空")
    private Long groupId;

    @Schema(description = "目标ID列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "目标ID列表不能为空")
    private List<Long> targetIds;
}

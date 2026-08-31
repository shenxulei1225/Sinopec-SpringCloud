package cn.cheers.x.module.dynamicbusiness.controller.admin.action.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "动作库列表查询入参")
@Data
public class ActionListReqVO {

    @Schema(description = "执行手段过滤（可选）；如 MANUAL / UAV / ROBOT / FIXED_CAMERA", example = "UAV")
    private String executionMeans;

    @Schema(description = "启用主体种类（可选；与 ownerId 成对传入）", example = "model")
    private String ownerKind;

    @Schema(description = "启用主体 id（可选；与 ownerKind 成对传入）", example = "12")
    private Long ownerId;
}

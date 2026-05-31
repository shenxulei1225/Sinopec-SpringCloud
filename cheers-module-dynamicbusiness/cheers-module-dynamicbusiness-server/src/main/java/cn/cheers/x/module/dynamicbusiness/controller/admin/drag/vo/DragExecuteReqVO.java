package cn.cheers.x.module.dynamicbusiness.controller.admin.drag.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 拖拽执行请求 VO")
@Data
public class DragExecuteReqVO {

    @Schema(description = "拖拽源节点ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "125")
    @NotNull(message = "sourceId 不能为空")
    private Long sourceId;

    @Schema(description = "拖拽源节点类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "MODEL")
    @NotBlank(message = "sourceNodeType 不能为空")
    private String sourceNodeType;

    @Schema(description = "拖拽目标节点ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "47")
    @NotNull(message = "targetId 不能为空")
    private Long targetId;

    @Schema(description = "拖拽目标节点类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "MODEL")
    @NotBlank(message = "targetNodeType 不能为空")
    private String targetNodeType;

    @Schema(description = "落点位置 BEFORE/AFTER/INNER", requiredMode = Schema.RequiredMode.REQUIRED, example = "BEFORE")
    @NotBlank(message = "position 不能为空")
    private String position;

    @Schema(description = "源容器ID（可选：模型节点当前所属分类）", example = "87")
    private Long sourceContainerId;

    @Schema(description = "目标容器ID（可选：模型排序所属分类）", example = "87")
    private Long targetContainerId;

    @Schema(description = "业务类型编码（ENTITY->CATEGORY 时建议显式传入）", example = "equipment")
    private String businessTypeCode;
}

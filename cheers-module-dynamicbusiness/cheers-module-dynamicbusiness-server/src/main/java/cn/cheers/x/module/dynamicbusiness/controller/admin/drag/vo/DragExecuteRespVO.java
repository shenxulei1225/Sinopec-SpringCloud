package cn.cheers.x.module.dynamicbusiness.controller.admin.drag.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Schema(description = "管理后台 - 拖拽执行响应 VO")
@Data
@Builder
public class DragExecuteRespVO {

    @Schema(description = "是否执行成功", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean success;

    @Schema(description = "动作类型", example = "MODEL_REORDER")
    private String action;

    @Schema(description = "提示信息", example = "操作成功")
    private String message;
}

package cn.iocoder.yudao.module.emergency.controller.admin.task.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 根据事件ID查询任务列表请求")
@Data
public class TaskListByEventReqVO {

    @Schema(description = "事件ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "事件ID不能为空")
    private Long eventId;

    @Schema(description = "阶段过滤（WARNING预警/RESPONSE响应，为空则显示所有阶段）", example = "WARNING")
    private String stage;

    @Schema(description = "是否按阶段分组显示", example = "true")
    private Boolean groupByStage;
}

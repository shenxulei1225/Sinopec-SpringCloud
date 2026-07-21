package cn.cheers.x.bpm.api.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "RPC 服务 - 运行中活动节点 Response DTO")
@Data
public class BpmActivityNodeRespDTO {

    @Schema(description = "任务编号", example = "1024")
    private String taskId;

    @Schema(description = "任务定义 Key", example = "event_confirm")
    private String taskDefinitionKey;

    @Schema(description = "任务名称", example = "确认")
    private String name;

    @Schema(description = "流程实例编号")
    private String processInstanceId;

}

package cn.cheers.x.bpm.api.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "RPC 服务 - 通过流程任务 Request DTO")
@Data
public class BpmTaskApproveReqDTO {

    @Schema(description = "任务编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotEmpty(message = "任务编号不能为空")
    private String id;

    @Schema(description = "审批意见", example = "确认通过")
    private String reason;

    @Schema(description = "变量实例")
    private Map<String, Object> variables;

    @Schema(description = "下一个节点审批人", example = "{nodeId:[1, 2]}")
    private Map<String, List<Long>> nextAssignees;

}

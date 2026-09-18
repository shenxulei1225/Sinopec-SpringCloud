package cn.cheers.x.module.dynamicbusiness.controller.admin.orchestration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Schema(description = "业务编排 upsert 请求")
@Data
public class BusinessOrchestrationUpsertReqVO {

    @Schema(description = "业务编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "business.patrol")
    @NotBlank(message = "业务编码不能为空")
    @Size(max = 128)
    private String businessCode;

    @Schema(description = "业务名称", example = "巡检")
    @Size(max = 128)
    private String businessName;

    @Schema(description = "任务域", example = "巡检")
    @Size(max = 64)
    private String taskDomain;

    @Schema(description = "编排权威 JSON 字符串（BusinessOrchestration）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "编排 JSON 不能为空")
    private String orchestrationJson;

    @Schema(description = "画布 JSON（节点坐标等展示态，可空）")
    private String canvasJson;
}

package cn.cheers.x.module.dynamicbusiness.controller.admin.orchestration.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "业务编排响应")
@Data
public class BusinessOrchestrationRespVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "业务编码")
    private String businessCode;

    @Schema(description = "业务名称")
    private String businessName;

    @Schema(description = "任务域")
    private String taskDomain;

    @Schema(description = "编排权威 JSON")
    private String orchestrationJson;

    @Schema(description = "画布 JSON")
    private String canvasJson;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}

package cn.cheers.x.module.dynamicbusiness.controller.admin.ledgerstate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "执行办事转移")
@Data
public class LedgerStateExecuteReqVO {

    @Schema(description = "台账类型编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "台账类型不能为空")
    private String entityTypeCode;

    @Schema(description = "台账单编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "单据不能为空")
    private Long entityId;

    @Schema(description = "目标状态码")
    private String toState;

    @Schema(description = "当前页面编码")
    private String pageCode;

    @Schema(description = "这一跳要带上的填写内容")
    private Map<String, Object> payload;
}

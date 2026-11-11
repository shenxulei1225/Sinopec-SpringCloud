package cn.cheers.x.module.dynamicbusiness.controller.admin.ledgerstate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "当前页可执行的一条办事转移")
@Data
public class LedgerTransitionRespVO {

    @Schema(description = "目标状态码")
    private String toState;

    @Schema(description = "按钮上的状态名")
    private String toStateName;

    @Schema(description = "这一跳必填的载荷键，逗号分隔")
    private String requiredFields;

    @Schema(description = "成功后追加的脉络句子模板")
    private String traceTemplate;
}

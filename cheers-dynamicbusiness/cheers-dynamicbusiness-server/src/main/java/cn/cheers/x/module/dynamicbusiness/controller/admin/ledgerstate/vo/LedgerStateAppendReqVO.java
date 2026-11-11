package cn.cheers.x.module.dynamicbusiness.controller.admin.ledgerstate.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "工单进度追加到报修脉络")
@Data
public class LedgerStateAppendReqVO {

    @NotNull(message = "台账类型不能为空")
    private String entityTypeCode;

    @NotNull(message = "单据不能为空")
    private Long entityId;

    @NotNull(message = "工单不能为空")
    private Long workOrderId;

    @NotBlank(message = "进度句子不能为空")
    private String sentence;
}

package cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration;

import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;

/**
 * 计划点回写门面请求。
 */
@Data
public class PatrolSlotWritebackReqVO {

    @NotBlank(message = "slotId 不能为空")
    private String slotId;

    @NotNull(message = "slotStatus 不能为空")
    private SlotStatus slotStatus;

    private OffsetDateTime actualStart;

    private OffsetDateTime actualEnd;

    private String reason;
}

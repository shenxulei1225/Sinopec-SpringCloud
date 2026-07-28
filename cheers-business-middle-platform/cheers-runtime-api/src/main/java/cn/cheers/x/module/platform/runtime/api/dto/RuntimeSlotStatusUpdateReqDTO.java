package cn.cheers.x.module.platform.runtime.api.dto;

import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 计划点状态回写请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuntimeSlotStatusUpdateReqDTO {

    @NotBlank
    private String slotId;

    @NotNull
    private SlotStatus slotStatus;

    private OffsetDateTime actualStart;

    private OffsetDateTime actualEnd;

    /** 业务原因文案，写入过程时间线摘要 */
    private String reason;

    private Long facilityId;
}

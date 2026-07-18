package cn.cheers.x.module.platform.contract.dto.schedule;

import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.enums.RuntimeJobStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 排程运行响应。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRunResponse {

    private String contractVersion;
    private String runtimeJobId;
    private RuntimeJobStatus status;
    private List<ScheduleSlotDTO> slots;
    private String decisionTraceId;
    private String plainSummary;
    /** 派工生成的工单 ID 列表；未派工时为 null */
    private List<Long> workOrderIds;
}

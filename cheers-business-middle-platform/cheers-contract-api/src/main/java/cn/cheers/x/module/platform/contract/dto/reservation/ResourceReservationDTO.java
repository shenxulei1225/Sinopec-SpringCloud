package cn.cheers.x.module.platform.contract.dto.reservation;

import cn.cheers.x.module.platform.contract.dto.slot.AssignedResourceDTO;
import cn.cheers.x.module.platform.contract.enums.CandidateType;
import cn.cheers.x.module.platform.contract.enums.SlotLockState;
import cn.cheers.x.module.platform.contract.enums.SlotStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 资源占窗（L4 {@code platform_resource_reservation}）— 编排 SOLVE 输出 / 持久化。
 * <p>候选：{@link #candidateStart} / {@link #candidateEnd} + {@link #candidateType}。
 * <p>计划：{@link #plannedStart} / {@link #plannedEnd}（启用排程后定稿）。
 * <p>实测：{@link #actualStart} / {@link #actualEnd}。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceReservationDTO {

    private String contractVersion;
    private String candidateId;
    private String runtimeJobId;
    private CandidateType candidateType;
    private String workId;
    private String entityTypeCode;

    /** 执行计划开始（启用排程后定稿） */
    private String plannedStart;
    /** 执行计划结束 */
    private String plannedEnd;
    /** 实测开始 */
    private String actualStart;
    /** 实测结束 */
    private String actualEnd;

    /** 排期候选占窗起止（智能编排/冲突检测） */
    private String candidateStart;
    private String candidateEnd;

    private List<AssignedResourceDTO> assignedResources;
    private SlotLockState lockState;
    private SlotStatus candidateStatus;
    private String policySnapshotId;
    private String decisionTraceId;
}

package cn.iocoder.yudao.module.emergency.api.orchestration.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 资源调度编排请求（validate / expand / solve / persist 共用）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyResourceDispatchReqDTO {

    @NotNull
    private Long eventId;

    @NotNull
    private Long resourceId;

    /** 响应编号；预警阶段可空 */
    private Long responseId;

    /** 调度阶段（预警/响应），可空 */
    private String stage;

    /** 可选占窗起点（ISO-8601 字符串）；有窗则 solve 须真实排程，不得假成功 */
    private String windowStart;

    /** 可选占窗终点 */
    private String windowEnd;

    private String orchestrationRef;
}

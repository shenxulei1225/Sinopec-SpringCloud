package cn.iocoder.yudao.module.emergency.api.orchestration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyResourceDispatchExpandRespDTO {

    private Long eventId;
    private Long resourceId;
    private Long responseId;
    private String stage;
    private String orchestrationRef;

    /** 可选占窗；有值则 solve 不得假成功占用 */
    private String windowStart;
    private String windowEnd;

    /** solve 无占窗时为 true */
    private Boolean solveSkipped;

    /** persist 后回填 */
    private Long dispatchId;
}

package cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 组路线预览/确认请求：选对象与设施，触发编排 run。
 */
@Data
public class PatrolRouteRunReqVO {

    @NotNull(message = "facilityId 不能为空")
    private Long facilityId;

    @NotEmpty(message = "objectIds 不能为空")
    private List<Long> objectIds;

    /** 多条已发布路网时显式指定 */
    private String preferredNetworkRef;

    /** 确认写入台账时必填，绑定任务路线快照 */
    private Long taskId;

    /**
     * 固定起点停靠点 id（透传至路径引擎 {@code RouteRequestDTO.startStopId}）。
     * <p>routing 当前不支持独立终点字段；勿在请求中伪造 endStopId。</p>
     */
    private String startStopId;

    /**
     * 是否回到起点。未传且 {@link #startStopId} 有值时，路径侧默认 true。
     */
    private Boolean returnToStart;
}

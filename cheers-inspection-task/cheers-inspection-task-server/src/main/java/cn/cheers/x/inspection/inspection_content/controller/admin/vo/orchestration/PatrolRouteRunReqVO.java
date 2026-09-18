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
     * 任务创建选定的起点（无人机起飞点），透传到路径引擎。
     */
    private String startStopId;

    /**
     * 任务创建选定的终点（无人机降落点）。
     * 与起点相同则回到起点；不同则路线最后一站落到该点。
     */
    private String endStopId;

    /**
     * 是否回到起点。起终点相同时为 true。
     */
    private Boolean returnToStart;

    /**
     * 检查项位置展开后的路网点。算路只认这个，不读对象↔停靠点绑定。
     */
    @NotEmpty(message = "stopIds 不能为空")
    private List<String> stopIds;

    /**
     * 任务已选巡检方式对应的路网类型（HUMAN / UAV / GROUND_ROBOT）。
     */
    private String inspectionType;
}

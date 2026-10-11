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

    /** 本厂有多条路网时，指定用哪一条。 */
    private String preferredNetworkRef;

    /** 修改已有任务时携带，用于识别当前任务；新建任务预览可为空。 */
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
     * 检查项位置展开后的路网点。开始规划必填。
     * 算路只认这个，不读对象↔停靠点绑定。
     */
    private List<String> stopIds;

    /**
     * 任务已选巡检方式对应的路网类型（HUMAN / UAV / GROUND_ROBOT）。
     */
    private String inspectionType;

    /**
     * 站序策略：自动规划 optimize_order，手工 as_given。透传到路径引擎。
     */
    private String strategy;
}

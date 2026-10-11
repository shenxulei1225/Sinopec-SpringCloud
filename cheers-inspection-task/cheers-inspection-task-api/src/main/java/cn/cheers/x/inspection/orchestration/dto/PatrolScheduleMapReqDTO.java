package cn.cheers.x.inspection.orchestration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 展开巡检作业项（WorkItem）请求：路线预览/保存与智能编排 EXPAND 阶段共用。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatrolScheduleMapReqDTO {

    private Long facilityId;

    private List<Long> objectIds;

    private String preferredNetworkRef;

    private Long taskId;

    private Boolean fromSavedRouteSnapshot;

    /** 模板作业项 id（可选，回写 workItems 时使用） */
    private String templateWorkItemId;

    private String startStopId;

    private String endStopId;

    private Boolean returnToStart;

    private List<String> stopIds;

    private String inspectionType;

    /** 路径引擎站序策略（optimize_order / as_given）。 */
    private String strategy;

    /**
     * previewOrchestration 试排时为 true：按排期模板复制多条作业项（每个计划时刻一条）。
     */
    private Boolean expandWorkItemsFromScheduleTemplate;

    /**
     * 建任务向导这一页上的路线、排期、勾选、执行设备。
     * 有这些字段时，展开计划点只认这一页，不读任务上已发布的那一份，也不把这一页写回去。
     * 列表里启用已经生成的任务时不带这些字段，展开仍读已发布字段。
     */
    private Object pagePlannedRoute;

    private Object pageScheduleConfig;

    private Object pageInspectionContent;

    private Object pageExecutionDeviceBinding;

    private String pagePatrolExecutionMode;

    private String pageStartStopId;

    private String pageEndStopId;
}

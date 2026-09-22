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

    /**
     * previewOrchestration 试排时为 true：按排期模板复制多条作业项（每个计划时刻一条）。
     */
    private Boolean expandWorkItemsFromScheduleTemplate;
}

package cn.cheers.x.module.platform.contract.dto.schedule;

import cn.cheers.x.module.platform.contract.dto.route.RoutePreviewDTO;
import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
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
    /** 智能编排要改时间的已确认占窗。预览不落库，生成任务时按原窗口编号更新。 */
    private List<ScheduleSlotDTO> shiftedSlots;
    private String decisionTraceId;
    private String plainSummary;
    /** 派工生成的工单 ID 列表；未派工时为 null */
    private List<Long> workOrderIds;
    /** 路线预览（route 阶段产出；预览/确认组路线时回填） */
    private RoutePreviewDTO routePreview;
    /**
     * 开始编排按当前停靠点重算后的路线。只回到这一页，不写入已保存路线。
     */
    private java.util.Map<String, Object> plannedRoute;
    /** 编排阶段后的工作项（含 expand/route 回写字段） */
    private List<WorkItemDTO> workItems;
    /**
     * 智能编排试排后总任务步骤图（step_tree_json）；仅 inspection previewOrchestration 回填，供核对计划步展示。
     */
    private Object executionStepTree;
}

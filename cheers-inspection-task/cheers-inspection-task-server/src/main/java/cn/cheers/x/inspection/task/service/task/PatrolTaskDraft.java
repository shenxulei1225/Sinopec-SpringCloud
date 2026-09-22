package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.model.task.ResourcePolicy;

/**
 * 总任务上的巡检草稿：名称、勾选对象、巡检方式、执行设备、已生成步骤图、建任务已放到哪一步。
 * <p>权威在底座任务表，不是旧固定表。
 * <p>orchestrationCommitted：任务已生成并排期（非「排期模板填好」）；对应详情/列表 {@code enabled}。
 * <p>orchestrationPreview*：试排占窗快照，未 confirm 前不写 runtimeJobId。
 */
public record PatrolTaskDraft(
        Long id,
        String name,
        String domain,
        Long facilityId,
        String patrolExecutionMode,
        InspectionContent inspectionContent,
        ExecutionDeviceBinding executionDeviceBinding,
        ResourcePolicy resourcePolicy,
        Object stepTree,
        Object plannedRoute,
        String manageStatus,
        Integer createUnlockedStep,
        String startStopId,
        String endStopId,
        String runtimeJobId,
        Boolean orchestrationCommitted,
        /** FLD-TSK-018 排期模板；与前端 ScheduleConfig 对齐 */
        Object scheduleConfig,
        /** 草稿袋 schedulePolicyId */
        Long schedulePolicyId,
        /** 草稿袋 orchestrationPreviewSlots：试排计划点 JSON，未确认前不占 runtime */
        Object orchestrationPreviewSlots,
        /** 草稿袋 orchestrationPreviewPlainSummary：试排摘要 */
        String orchestrationPreviewPlainSummary
) {
}

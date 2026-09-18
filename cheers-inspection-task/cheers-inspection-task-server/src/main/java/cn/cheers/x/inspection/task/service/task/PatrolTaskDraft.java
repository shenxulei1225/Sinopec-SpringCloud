package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.model.task.ExecutionDeviceBinding;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.inspection.task.model.task.ResourcePolicy;

/**
 * 总任务上的巡检草稿：名称、勾选对象、巡检方式、执行设备、已生成步骤图、建任务已放到哪一步。
 * <p>权威在底座任务表，不是旧固定表。
 * <p>createUnlockedStep：建任务向导已放行到哪一步（0 选对象 / 1 路线 / 2 排期与资源）。
 * 缺这个键表示还没走过创建流程，就是第 0 步；禁止读路径按「已有路线」反推。
 * 旧草稿若写成 3（曾经把资源单独当最后一步），读时按最后一步 2 处理。
 * <p>startStopId / endStopId：路线规划步下拉选的起点、终点。保存草稿就要落库；
 * 还没确认路线时也要回显。禁止只在确认路线后才有这两点。
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
        String endStopId
) {
}

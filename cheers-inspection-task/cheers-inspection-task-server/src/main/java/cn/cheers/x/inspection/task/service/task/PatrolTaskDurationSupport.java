package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.model.task.InspectionContent;

/**
 * 排期窗口长度 = 检查项动作耗时 + 路径耗时。
 * <p>不负责：从动作库加总、算路。缺动作耗时就返回空，禁止用路线上的旧动作时长冒充。
 */
public final class PatrolTaskDurationSupport {

    private PatrolTaskDurationSupport() {
    }

    public static Integer totalMinutes(InspectionContent content, Object plannedRoute, String patrolExecutionMode) {
        Integer action = PatrolItemActionDurationSupport.minutesOf(content);
        Integer travel = PatrolPlannedRouteSupport.estimatedTravelDuration(plannedRoute);
        if (travel == null) {
            travel = travelFromDistance(plannedRoute, patrolExecutionMode);
        }
        if (action == null && travel == null) {
            return null;
        }
        return (action == null ? 0 : action) + (travel == null ? 0 : travel);
    }

    private static Integer travelFromDistance(Object plannedRoute, String patrolExecutionMode) {
        return PatrolPlannedRouteSupport.resolveTravelMinutesOnly(plannedRoute, patrolExecutionMode);
    }
}

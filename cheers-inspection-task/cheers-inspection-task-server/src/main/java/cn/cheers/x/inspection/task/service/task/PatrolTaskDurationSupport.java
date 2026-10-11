package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.model.task.InspectionContent;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 排期窗口长度 = 检查项动作耗时 + 路径耗时。这两份都按秒存。
 * <p>占窗按整分钟：秒合计除以 60，有零头向上取整。0 仍是 0。
 * <p>不负责：从动作库加总、算路。缺动作耗时就返回空，禁止用路线上的旧动作时长冒充。
 */
public final class PatrolTaskDurationSupport {

    private PatrolTaskDurationSupport() {
    }

    public static Integer totalMinutes(InspectionContent content, Object plannedRoute, String patrolExecutionMode) {
        BigDecimal action = PatrolItemActionDurationSupport.minutesOf(content);
        Integer travel = PatrolPlannedRouteSupport.estimatedTravelDuration(plannedRoute);
        if (travel == null) {
            travel = travelFromDistance(plannedRoute, patrolExecutionMode);
        }
        if (action == null && travel == null) {
            return null;
        }
        BigDecimal totalSeconds = (action == null ? BigDecimal.ZERO : action)
                .add(BigDecimal.valueOf(travel == null ? 0 : Math.max(0, travel)));
        return occupancyMinutes(totalSeconds.divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP));
    }

    /**
     * 占窗用的整分钟。有零头就进上一分钟；0 或空仍是 0。
     */
    public static int occupancyMinutes(BigDecimal exact) {
        if (exact == null || exact.signum() <= 0) {
            return 0;
        }
        return exact.setScale(0, RoundingMode.CEILING).intValue();
    }

    private static Integer travelFromDistance(Object plannedRoute, String patrolExecutionMode) {
        return PatrolPlannedRouteSupport.resolveTravelMinutesOnly(plannedRoute, patrolExecutionMode);
    }
}

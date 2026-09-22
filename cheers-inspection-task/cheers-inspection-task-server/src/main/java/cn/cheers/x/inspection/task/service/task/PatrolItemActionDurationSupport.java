package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;

/**
 * 第 1 步：检查项动作耗时只写在巡检内容里。
 * <p>负责：读已写下的分钟数；存量从已保存路线迁出。
 * <p>不负责：算路径耗时、冲突检测。动作库现算由 {@link PatrolItemActionDurationCalculator} 做。
 */
public final class PatrolItemActionDurationSupport {

    private PatrolItemActionDurationSupport() {
    }

    public static Integer minutesOf(InspectionContent content) {
        if (content == null) {
            return null;
        }
        Integer minutes = content.getItemActionDurationMinutes();
        if (minutes == null || minutes < 0) {
            return null;
        }
        return minutes;
    }

    /**
     * 旧草稿曾把动作耗时写在已保存路线上。打开现算时迁到巡检内容，之后不再从路线读。
     */
    public static Integer migrateFromPlannedRoute(InspectionContent content, Object plannedRoute) {
        Integer written = minutesOf(content);
        if (written != null) {
            return written;
        }
        Integer leftover = PatrolPlannedRouteSupport.estimatedActionDuration(plannedRoute);
        if (leftover == null || leftover <= 0 || content == null) {
            return leftover != null && leftover > 0 ? leftover : null;
        }
        content.setItemActionDurationMinutes(leftover);
        return leftover;
    }

    public static void stripFromPlannedRoute(java.util.Map<String, Object> planned) {
        if (planned != null) {
            planned.remove(RoutePayloadKeys.ESTIMATED_ACTION_DURATION);
        }
    }
}

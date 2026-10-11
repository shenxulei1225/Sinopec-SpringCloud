package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.inspection.task.model.task.InspectionContent;
import cn.cheers.x.module.platform.orchestration.route.RoutePayloadKeys;

import java.math.BigDecimal;

/**
 * 第 1 步：检查项动作耗时只写在巡检内容里。
 * <p>负责：读已写下的分钟数；存量从已保存路线迁出。
 * <p>不负责：算路径耗时、冲突检测。动作库现算由 {@link PatrolItemActionDurationCalculator} 做。
 */
public final class PatrolItemActionDurationSupport {

    private PatrolItemActionDurationSupport() {
    }

    public static BigDecimal minutesOf(InspectionContent content) {
        if (content == null) {
            return null;
        }
        BigDecimal minutes = content.getItemActionDurationMinutes();
        if (minutes == null || minutes.signum() < 0) {
            return null;
        }
        return minutes;
    }

    /** 保存路线时去掉曾经写在路线上的动作耗时。耗时只在巡检内容里。 */
    public static void stripFromPlannedRoute(java.util.Map<String, Object> planned) {
        if (planned != null) {
            planned.remove(RoutePayloadKeys.ESTIMATED_ACTION_DURATION);
        }
    }
}

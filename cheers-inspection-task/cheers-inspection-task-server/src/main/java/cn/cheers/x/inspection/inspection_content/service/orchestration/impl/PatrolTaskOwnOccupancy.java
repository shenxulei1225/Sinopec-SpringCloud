package cn.cheers.x.inspection.inspection_content.service.orchestration.impl;

import cn.cheers.x.module.platform.contract.dto.slot.ScheduleSlotDTO;
import org.springframework.util.StringUtils;

/**
 * 判断一段占窗是不是「这一条巡检任务自己写下的」。
 * <p>用户在排期页对照的「已有」，只能是别人的占窗。本任务历次试排/生成留下的窗口，
 * 不论作业号还在不在草稿上，都不能当成别人。
 * <p>工作项号由展开排期写出：当前 {@code patrol-task-{任务id}}，以及历史上写过的
 * {@code patrol-reserve-} / {@code patrol-arrange-} / {@code patrol-} 前缀。
 * <p>不管：怎么算重叠、怎么挪已有、怎么落库。禁止用设备号或场站号冒充本任务。
 */
final class PatrolTaskOwnOccupancy {

    private PatrolTaskOwnOccupancy() {
    }

    /**
     * 这段占窗属于指定任务：作业号对得上，或工作项号是这条任务的前缀。
     *
     * @return true 时检测和核窗必须跳过，不能报成与已有任务冲突
     */
    static boolean isOwnSlot(Long taskId, String ownJobId, String previousJobId, ScheduleSlotDTO slot) {
        if (slot == null) {
            return false;
        }
        if (sameJob(ownJobId, slot.getRuntimeJobId()) || sameJob(previousJobId, slot.getRuntimeJobId())) {
            return true;
        }
        return isOwnWorkId(taskId, slot.getWorkId());
    }

    static boolean isOwnSlot(Long taskId, String ownJobId, ScheduleSlotDTO slot) {
        return isOwnSlot(taskId, ownJobId, null, slot);
    }

    /**
     * 工作项号是不是这条任务展开出来的。
     * {@code patrol-task-1-0} 属于任务 1，不属于任务 10。
     */
    static boolean isOwnWorkId(Long taskId, String workId) {
        if (taskId == null || !StringUtils.hasText(workId)) {
            return false;
        }
        String trimmed = workId.trim();
        String id = String.valueOf(taskId);
        return matchesStem(trimmed, "patrol-task-" + id)
                || matchesStem(trimmed, "patrol-reserve-" + id)
                || matchesStem(trimmed, "patrol-arrange-" + id)
                || matchesStem(trimmed, "patrol-" + id);
    }

    private static boolean matchesStem(String workId, String stem) {
        return workId.equals(stem) || workId.startsWith(stem + "-");
    }

    private static boolean sameJob(String expected, String actual) {
        return StringUtils.hasText(expected)
                && StringUtils.hasText(actual)
                && expected.trim().equals(actual.trim());
    }
}

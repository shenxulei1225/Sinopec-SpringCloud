package cn.cheers.x.inspection.task.service.task;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import org.springframework.util.StringUtils;

/**
 * 建任务向导：哪份事实变了，后面从哪一步起重做。
 *
 * <p>管什么：把「变了什么」映射成保留到哪一步、要不要作废已保存路线。
 * 不管什么：不算路、不写试排、不决定页面文案。</p>
 * <p>禁止：按机器人/无人机等具体手段两两写死。巡检方式是检查方法档的选择器，
 * 方式变了就等于检查方法变了，从选对象起重做。</p>
 */
public enum CreateWizardInvalidation {

    /**
     * 巡检方式变了：每个检查项换了一档检查方法，对象/项是否仍适用也要重新确认。
     */
    EXECUTION_MEANS_CHANGED(0, true),

    /** 勾选对象或检查项变了：到达位置和路线作废，从保存路线起重做。 */
    OBJECTS_OR_ITEMS_CHANGED(1, true),

    /** 刚保存路线：路线留下，试排和步骤图作废。 */
    ROUTE_SAVED(1, false),

    /** 到达位置或路径耗时对不上已保存路线：路线作废，从保存路线起重做。 */
    PATH_INPUTS_CHANGED(1, true),

    /** 检查项动作耗时变了：路线还能用，排期要重做。 */
    ITEM_ACTION_DURATION_CHANGED(2, false);

    private final int keepThroughStep;
    private final boolean clearPlannedRoute;

    CreateWizardInvalidation(int keepThroughStep, boolean clearPlannedRoute) {
        this.keepThroughStep = keepThroughStep;
        this.clearPlannedRoute = clearPlannedRoute;
    }

    public int keepThroughStep() {
        return keepThroughStep;
    }

    public boolean clearPlannedRoute() {
        return clearPlannedRoute;
    }

    public static CreateWizardInvalidation require(String reason) {
        if (!StringUtils.hasText(reason)) {
            throw ServiceExceptionUtil.invalidParamException("作废原因不能为空");
        }
        try {
            return CreateWizardInvalidation.valueOf(reason.trim());
        } catch (IllegalArgumentException ex) {
            throw ServiceExceptionUtil.invalidParamException("不认识的向导作废原因：" + reason);
        }
    }
}

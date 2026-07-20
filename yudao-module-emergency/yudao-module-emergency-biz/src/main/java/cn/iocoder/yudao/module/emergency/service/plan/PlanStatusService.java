package cn.iocoder.yudao.module.emergency.service.plan;

/**
 * 预案状态管理 Service 接口
 *
 * @author 芋道源码
 */
public interface PlanStatusService {

    /**
     * 发布预案
     * 状态转换：草稿 → 已发布
     *
     * @param planId 预案ID
     */
    void publishPlan(Long planId);

    /**
     * 停用预案
     * 状态转换：已发布 → 已停用
     *
     * @param planId 预案ID
     */
    void disablePlan(Long planId);

    /**
     * 验证预案是否可用于创建响应
     * 只有已发布的预案才能被用于创建应急响应
     *
     * @param planId 预案ID
     * @return 是否可用
     */
    boolean isPlanAvailable(Long planId);
}







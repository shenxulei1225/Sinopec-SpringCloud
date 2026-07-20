package cn.iocoder.yudao.module.emergency.service.dispatch;

import cn.iocoder.yudao.module.emergency.dal.dataobject.dispatch.ResourceDispatchDO;

public interface ResourceDispatchService {

    /**
     * 创建调度，初始为 pending
     */
    ResourceDispatchDO createDispatch(ResourceDispatchDO dispatch);

    /**
     * 派发：pending -> dispatched
     */
    void dispatch(Long dispatchId);

    /**
     * 使用中：dispatched -> in_use
     */
    void markInUse(Long dispatchId);

    /**
     * 回收：in_use/ dispatched -> recovered
     */
    void recover(Long dispatchId, String reason);

    /**
     * 分配资源（使用分布式锁防止并发冲突）
     * 
     * @param resourceId 资源ID
     * @param eventId 事件ID
     * @param responseId 响应ID（可选，预警阶段时为null）
     * @param stage 调度阶段（预警阶段/响应阶段）
     * @return 资源调度记录
     */
    ResourceDispatchDO dispatchResource(Long resourceId, Long eventId, Long responseId, String stage);

    ResourceDispatchDO get(Long id);
}


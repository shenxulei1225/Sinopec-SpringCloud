package cn.iocoder.yudao.module.emergency.service.dispatch;

import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyResourceDispatchExpandRespDTO;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyResourceDispatchReqDTO;
import cn.iocoder.yudao.module.emergency.controller.admin.dispatch.vo.ResourceDispatchAssignReqVO;
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

    /**
     * HTTP：经编排模板 orch.emergency.resource_dispatch_v1 派发资源，返回调度记录 id。
     */
    Long assignViaOrchestration(ResourceDispatchAssignReqVO reqVO);

    void validateDispatchForOrchestration(EmergencyResourceDispatchReqDTO req);

    EmergencyResourceDispatchExpandRespDTO expandDispatchForOrchestration(EmergencyResourceDispatchReqDTO req);

    EmergencyResourceDispatchExpandRespDTO solveDispatchForOrchestration(EmergencyResourceDispatchExpandRespDTO expand);

    EmergencyResourceDispatchExpandRespDTO persistDispatchForOrchestration(EmergencyResourceDispatchExpandRespDTO expandResult);
}

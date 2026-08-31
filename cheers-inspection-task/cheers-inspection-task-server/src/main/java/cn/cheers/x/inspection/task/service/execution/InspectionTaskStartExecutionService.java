package cn.cheers.x.inspection.task.service.execution;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;

/**
 * 巡检任务「开始执行」：读任务会话意图与执行设备绑定，调协议网关下发。
 * <p>与排程 enable 分开；不查设备台账；不绑瞬时 WebSocket。
 */
public interface InspectionTaskStartExecutionService {

    /**
     * 开始向地面站下发本任务的执行意图。
     *
     * @param taskId 巡检任务 id
     * @return 网关下行结果；success=false 时调用方已抛业务错，正常返回则 success=true
     */
    MissionStartRespDTO startExecution(Long taskId);
}

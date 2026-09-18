package cn.cheers.x.inspection.task.service.execution;

import cn.cheers.x.device.protocolgateway.api.dto.MissionStartRespDTO;

/**
 * 巡检任务「开始执行」：读任务步骤图与执行设备绑定，发出「人点了开始」。
 * <p>新建账、发给设备、标进行中由条件策略做。与排程 enable 分开。
 * <p>步骤图读任务台账；实参读设备检查参数包。
 */
public interface InspectionTaskStartExecutionService {

    /**
     * 按任务步骤图向终端下发本次执行。
     *
     * @param taskId 巡检任务 id
     * @return 网关下行结果。设备离线或答卷失败时 {@code success=false}，不假装成功。
     */
    MissionStartRespDTO startExecution(Long taskId);
}

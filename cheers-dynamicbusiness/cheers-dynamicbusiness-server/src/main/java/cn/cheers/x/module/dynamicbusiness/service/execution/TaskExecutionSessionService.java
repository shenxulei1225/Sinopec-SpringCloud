package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;

/**
 * 任务模块标准执行会话。
 * <p>负责：新建这次执行的账；更新这次/某一步的状态；往账里记一条过程。
 * <p>不负责：外部通道协议、算路、域专用设备列、策略匹配。
 */
public interface TaskExecutionSessionService {

    TaskExecutionStartRespDTO start(TaskExecutionStartReqDTO req);

    void writeback(TaskExecutionWritebackReqDTO req);

    /**
     * 往这次执行的账里记一条过程。
     * 没有账本编号或找不到这本账 → 失败，不按设备号猜。
     */
    TaskExecutionAppendProcessRespDTO appendProcess(TaskExecutionAppendProcessReqDTO req);
}

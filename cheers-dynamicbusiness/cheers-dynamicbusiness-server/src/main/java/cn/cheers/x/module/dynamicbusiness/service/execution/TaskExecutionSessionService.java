package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;

/**
 * 任务模块标准执行会话。
 * <p>负责：创建执行记录 + bootstrap 步骤；按执行记录回写。
 * <p>不负责：外部通道协议、算路、域专用设备列。
 */
public interface TaskExecutionSessionService {

    TaskExecutionStartRespDTO start(TaskExecutionStartReqDTO req);

    void writeback(TaskExecutionWritebackReqDTO req);
}

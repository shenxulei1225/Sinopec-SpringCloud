package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendPoseReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStepSnapshotDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;

import java.util.List;

/**
 * 任务模块标准执行会话。
 * <p>负责：新建这次执行的账；更新这次/某一步的状态；往账里记一条过程。
 * <p>不负责：外部通道协议、算路、域专用设备列、策略匹配。
 */
public interface TaskExecutionSessionService {

    TaskExecutionStartRespDTO start(TaskExecutionStartReqDTO req);

    /**
     * 同一份步骤清单写入多条待执行账。
     * 每条账只差计划点和名称；步骤行按这份清单整批插入。
     */
    TaskExecutionStartBatchRespDTO startBatch(TaskExecutionStartBatchReqDTO req);

    void writeback(TaskExecutionWritebackReqDTO req);

    /**
     * 往这次执行的账里记一条过程。
     * 没有账本编号或找不到这本账 → 失败，不按设备号猜。
     */
    TaskExecutionAppendProcessRespDTO appendProcess(TaskExecutionAppendProcessReqDTO req);

    /**
     * 往这次执行另存一个回放位置点。不写入过程列表。
     */
    void appendPose(TaskExecutionAppendPoseReqDTO req);

    /**
     * 列出这次执行账上的步骤。没有步骤时返回空列表，不按设备号另查。
     */
    List<TaskExecutionStepSnapshotDTO> listSteps(Long executionRecordId);
}

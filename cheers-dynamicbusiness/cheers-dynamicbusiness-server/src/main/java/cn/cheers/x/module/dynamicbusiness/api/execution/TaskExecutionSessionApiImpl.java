package cn.cheers.x.module.dynamicbusiness.api.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendPoseReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionAppendProcessRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionDayAccountPageDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionMarkProcessReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionMarkProcessRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartBatchRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStepSnapshotDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionUnfinishedOptionDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;

import java.util.List;
import cn.cheers.x.module.dynamicbusiness.service.execution.OpenExecutionAccountQuery;
import cn.cheers.x.module.dynamicbusiness.service.execution.PatrolLayerDayAccountQuery;
import cn.cheers.x.module.dynamicbusiness.service.execution.TaskExecutionSessionService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 执行会话对外入口：新建账、改状态、记过程。
 * <p>不负责策略匹配。
 */
@RestController
@Validated
public class TaskExecutionSessionApiImpl implements TaskExecutionSessionApi {

    @Resource
    private TaskExecutionSessionService taskExecutionSessionService;

    @Resource
    private OpenExecutionAccountQuery openExecutionAccountQuery;

    @Resource
    private PatrolLayerDayAccountQuery patrolLayerDayAccountQuery;

    @Override
    public CommonResult<TaskExecutionStartRespDTO> start(TaskExecutionStartReqDTO req) {
        return success(taskExecutionSessionService.start(req));
    }

    @Override
    public CommonResult<TaskExecutionStartBatchRespDTO> startBatch(TaskExecutionStartBatchReqDTO req) {
        return success(taskExecutionSessionService.startBatch(req));
    }

    @Override
    public CommonResult<Boolean> writeback(TaskExecutionWritebackReqDTO req) {
        taskExecutionSessionService.writeback(req);
        return success(true);
    }

    @Override
    public CommonResult<Boolean> deletePending(Long executionRecordId) {
        taskExecutionSessionService.deletePendingExecution(executionRecordId);
        return success(true);
    }

    @Override
    public CommonResult<TaskExecutionAppendProcessRespDTO> appendProcess(
            TaskExecutionAppendProcessReqDTO req) {
        return success(taskExecutionSessionService.appendProcess(req));
    }

    @Override
    public CommonResult<TaskExecutionMarkProcessRespDTO> markProcessEvent(TaskExecutionMarkProcessReqDTO req) {
        return success(taskExecutionSessionService.markProcessEvent(req));
    }

    @Override
    public CommonResult<Boolean> appendPose(TaskExecutionAppendPoseReqDTO req) {
        taskExecutionSessionService.appendPose(req);
        return success(true);
    }

    @Override
    public CommonResult<List<TaskExecutionStepSnapshotDTO>> listSteps(Long executionRecordId) {
        return success(taskExecutionSessionService.listSteps(executionRecordId));
    }

    @Override
    public CommonResult<List<TaskExecutionUnfinishedOptionDTO>> listUnfinishedForDevice(String deviceId) {
        return success(openExecutionAccountQuery.listUnfinishedForDevice(deviceId));
    }

    @Override
    public CommonResult<TaskExecutionDayAccountPageDTO> listPatrolLayerDay(
            Long facilityId, String day, Integer pageNo, Integer pageSize) {
        if (facilityId == null || pageNo == null || pageSize == null) {
            throw new ServiceException(400, "当天巡检账查询条件不完整");
        }
        return success(patrolLayerDayAccountQuery.page(facilityId, day, pageNo, pageSize));
    }
}

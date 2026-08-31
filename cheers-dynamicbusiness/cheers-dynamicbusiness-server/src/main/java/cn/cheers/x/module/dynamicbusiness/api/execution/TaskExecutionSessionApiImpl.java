package cn.cheers.x.module.dynamicbusiness.api.execution;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartReqDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionStartRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.execution.dto.TaskExecutionWritebackReqDTO;
import cn.cheers.x.module.dynamicbusiness.service.execution.TaskExecutionSessionService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 任务执行会话 RPC。
 */
@RestController
@Validated
public class TaskExecutionSessionApiImpl implements TaskExecutionSessionApi {

    @Resource
    private TaskExecutionSessionService taskExecutionSessionService;

    @Override
    public CommonResult<TaskExecutionStartRespDTO> start(TaskExecutionStartReqDTO req) {
        return success(taskExecutionSessionService.start(req));
    }

    @Override
    public CommonResult<Boolean> writeback(TaskExecutionWritebackReqDTO req) {
        taskExecutionSessionService.writeback(req);
        return success(Boolean.TRUE);
    }
}

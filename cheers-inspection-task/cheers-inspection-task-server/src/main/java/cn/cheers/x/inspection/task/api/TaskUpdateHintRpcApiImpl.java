package cn.cheers.x.inspection.task.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.task.service.task.GeneratedTaskUpdateHintService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 动作库或检查项保存后，把「需要更新」写到用到这次改动的已生成任务上。
 */
@RestController
@Validated
public class TaskUpdateHintRpcApiImpl implements TaskUpdateHintRpcApi {

    @Resource
    private GeneratedTaskUpdateHintService generatedTaskUpdateHintService;

    @Override
    public CommonResult<Boolean> mark(TaskUpdateHintMarkReqDTO req) {
        generatedTaskUpdateHintService.mark(req);
        return success(Boolean.TRUE);
    }
}

package cn.cheers.x.module.platform.runtime.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionRespDTO;
import cn.cheers.x.module.platform.runtime.service.ProcessTimelineService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class ProcessTimelineApiImpl implements ProcessTimelineApi {

    @Resource
    private ProcessTimelineService processTimelineService;

    @Override
    public CommonResult<Long> append(ProcessTimelineActionAppendReqDTO request) {
        return success(processTimelineService.append(request));
    }

    @Override
    public CommonResult<PageResult<ProcessTimelineActionRespDTO>> pageByTarget(
            String targetType, String targetId, Integer pageNo, Integer pageSize) {
        return success(processTimelineService.pageByTarget(targetType, targetId, pageNo, pageSize));
    }
}

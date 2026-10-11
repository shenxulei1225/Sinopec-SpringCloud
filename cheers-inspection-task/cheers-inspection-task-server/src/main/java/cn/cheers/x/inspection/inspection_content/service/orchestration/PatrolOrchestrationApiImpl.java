package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.orchestration.PatrolOrchestrationApi;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapRespDTO;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class PatrolOrchestrationApiImpl implements PatrolOrchestrationApi {

    @Resource
    private PatrolScheduleMapService patrolScheduleMapService;

    @Override
    public CommonResult<PatrolScheduleMapRespDTO> expandPatrolWorkItems(PatrolScheduleMapReqDTO request) {
        return success(patrolScheduleMapService.expandPatrolWorkItems(request));
    }

}

package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.orchestration.PatrolOrchestrationApi;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmRespDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandRespDTO;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class PatrolOrchestrationApiImpl implements PatrolOrchestrationApi {

    @Resource
    private PatrolExpandMapService patrolExpandMapService;

    @Resource
    private PatrolConfirmService patrolConfirmService;

    @Override
    public CommonResult<PatrolExpandRespDTO> expand(PatrolExpandReqDTO request) {
        return success(patrolExpandMapService.expand(request));
    }

    @Override
    public CommonResult<PatrolConfirmRespDTO> confirm(PatrolConfirmReqDTO request) {
        return success(patrolConfirmService.confirm(request));
    }
}

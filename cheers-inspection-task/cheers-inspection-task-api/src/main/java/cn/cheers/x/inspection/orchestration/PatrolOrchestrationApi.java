package cn.cheers.x.inspection.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.enums.ApiConstants;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmRespDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME)
public interface PatrolOrchestrationApi {

    String PREFIX = ApiConstants.PREFIX;

    @PostMapping(PREFIX + "/orchestration/patrol/expand")
    @Operation(summary = "巡检编排 EXPAND：选网取点展开停靠点")
    CommonResult<PatrolExpandRespDTO> expand(@RequestBody PatrolExpandReqDTO request);

    @PostMapping(PREFIX + "/orchestration/patrol/confirm")
    @Operation(summary = "巡检编排 CONFIRM：写入路线方案与任务快照")
    CommonResult<PatrolConfirmRespDTO> confirm(@RequestBody PatrolConfirmReqDTO request);
}

package cn.cheers.x.inspection.orchestration;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.enums.ApiConstants;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolSaveRouteRespDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = ApiConstants.NAME)
public interface PatrolOrchestrationApi {

    String PREFIX = ApiConstants.PREFIX;

    @PostMapping(PREFIX + "/orchestration/patrol/map-work-items")
    @Operation(summary = "巡检智能编排：选网取点并映射排期工作项")
    CommonResult<PatrolScheduleMapRespDTO> expandPatrolWorkItems(@RequestBody PatrolScheduleMapReqDTO request);

    @PostMapping(PREFIX + "/orchestration/patrol/save-route")
    @Operation(summary = "保存路线：写入总任务 plannedRoute（不写路线方案台账）")
    CommonResult<PatrolSaveRouteRespDTO> saveRoute(@RequestBody PatrolSaveRouteReqDTO request);
}

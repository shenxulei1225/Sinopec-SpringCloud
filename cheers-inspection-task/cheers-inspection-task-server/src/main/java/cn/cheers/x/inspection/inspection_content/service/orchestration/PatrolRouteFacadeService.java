package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolScheduleEnableReqVO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;

/**
 * 巡检组路线门面：只触发编排 run，不直连路径/排程引擎。
 */
public interface PatrolRouteFacadeService {

    ScheduleRunResponse previewRoute(PatrolRouteRunReqVO reqVO);

    ScheduleRunResponse confirmRoute(PatrolRouteRunReqVO reqVO);

    ScheduleRunResponse enableSchedule(PatrolScheduleEnableReqVO reqVO);
}

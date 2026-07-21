package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolRouteRunReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolScheduleEnableReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolSlotWritebackReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskPauseReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.orchestration.PatrolTaskResumeReqVO;
import cn.cheers.x.module.platform.contract.dto.schedule.ScheduleRunResponse;

/**
 * 巡检组路线门面：只触发编排 / 运行时，不直连路径/排程引擎。
 */
public interface PatrolRouteFacadeService {

    ScheduleRunResponse previewRoute(PatrolRouteRunReqVO reqVO);

    ScheduleRunResponse confirmRoute(PatrolRouteRunReqVO reqVO);

    /**
     * 排期预占：expand + solve + persist，任务保持已排未启用（enabled=false）。
     */
    ScheduleRunResponse reserveSchedule(PatrolScheduleEnableReqVO reqVO);

    /**
     * 启用开跑：启用前验占窗，通过后任务已启用（enabled=true）。
     * <p>
     * 波次 2 同名方法曾混用「占窗+启用」；波次 3 起须先 {@link #reserveSchedule}。
     */
    ScheduleRunResponse enableSchedule(PatrolScheduleEnableReqVO reqVO);

    void holdPause(PatrolTaskPauseReqVO reqVO);

    void yieldPause(PatrolTaskPauseReqVO reqVO);

    void abort(PatrolTaskPauseReqVO reqVO);

    ScheduleRunResponse resume(PatrolTaskResumeReqVO reqVO);

    void writebackSlot(PatrolSlotWritebackReqVO reqVO);
}

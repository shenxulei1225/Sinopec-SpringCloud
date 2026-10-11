package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapRespDTO;
import cn.cheers.x.inspection.task.service.task.PatrolTaskDraft;

/**
 * 巡检智能编排：选网 + 映射排期工作项；不负责冲突求解。
 */
public interface PatrolScheduleMapService {

    PatrolScheduleMapRespDTO expandPatrolWorkItems(PatrolScheduleMapReqDTO request);

    /**
     * 按向导这一页已经叠好的任务内容展开计划点。不读已发布路线，不写任务。
     */
    PatrolScheduleMapRespDTO expandFromPageDraft(PatrolTaskDraft draft);
}

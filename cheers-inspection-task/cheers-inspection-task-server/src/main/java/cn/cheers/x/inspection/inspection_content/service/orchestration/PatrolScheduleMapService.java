package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolScheduleMapRespDTO;

/**
 * 巡检智能编排：选网 + 映射排期工作项；不负责冲突求解。
 */
public interface PatrolScheduleMapService {

    PatrolScheduleMapRespDTO expandPatrolWorkItems(PatrolScheduleMapReqDTO request);
}

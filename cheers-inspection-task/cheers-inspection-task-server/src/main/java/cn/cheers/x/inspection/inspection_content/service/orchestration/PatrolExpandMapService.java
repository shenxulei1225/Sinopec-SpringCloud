package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.inspection.orchestration.dto.PatrolExpandReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolExpandRespDTO;

/**
 * 巡检编排 EXPAND：选网 + 展开停靠点 + 汇总作业时长。
 */
public interface PatrolExpandMapService {

    PatrolExpandRespDTO expand(PatrolExpandReqDTO request);
}

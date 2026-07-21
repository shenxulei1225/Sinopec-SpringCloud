package cn.cheers.x.inspection.inspection_content.service.orchestration;

import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmReqDTO;
import cn.cheers.x.inspection.orchestration.dto.PatrolConfirmRespDTO;

/**
 * 巡检编排 CONFIRM：写入路线方案台账与任务快照。
 */
public interface PatrolConfirmService {

    PatrolConfirmRespDTO confirm(PatrolConfirmReqDTO request);
}

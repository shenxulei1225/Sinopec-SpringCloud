package cn.cheers.x.inspection.task.service.query.scheduleboard;

import cn.cheers.x.inspection.task.controller.admin.vo.scheduleboard.ScheduleBoardExecutionDetailRespVO;
import cn.cheers.x.inspection.task.controller.admin.vo.scheduleboard.ScheduleBoardPlanPointRespVO;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 巡检排期看板查询：L4 计划点 + 执行态增强 + 详情读模型。
 */
public interface InspectionScheduleBoardQueryService {

    List<ScheduleBoardPlanPointRespVO> listPlanPoints(
            OffsetDateTime from,
            OffsetDateTime to,
            Long facilityId);

    ScheduleBoardExecutionDetailRespVO getExecutionDetail(String slotId);
}

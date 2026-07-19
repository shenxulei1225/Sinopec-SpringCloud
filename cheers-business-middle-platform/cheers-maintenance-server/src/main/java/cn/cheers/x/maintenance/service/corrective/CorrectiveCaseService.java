package cn.cheers.x.maintenance.service.corrective;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.maintenance.controller.admin.vo.corrective.*;

public interface CorrectiveCaseService {
    Long create(CorrectiveCaseCreateReqVO reqVO);
    CorrectiveCaseRespVO get(Long id);
    PageResult<CorrectiveCaseRespVO> page(CorrectiveCasePageReqVO reqVO);
    void submitApproval(Long id);
    void approve(Long id, boolean approved);
    void onApprovalResult(Long id, boolean approved);
    Long dispatch(Long id, CorrectiveDispatchReqVO reqVO);
    void completeFromWorkOrder(Long id);
}

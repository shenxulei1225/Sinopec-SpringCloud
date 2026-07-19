package cn.cheers.x.maintenance.service.standard;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.maintenance.controller.admin.vo.standard.*;

public interface FieldWorkStandardService {
    Long createStandard(FieldWorkStandardCreateReqVO createReqVO);
    void updateStandard(Long id, FieldWorkStandardUpdateReqVO updateReqVO);
    FieldWorkStandardRespVO getStandard(Long id);
    PageResult<FieldWorkStandardRespVO> getStandardPage(FieldWorkStandardPageReqVO pageReqVO);
    Long publishStandard(Long id);
}

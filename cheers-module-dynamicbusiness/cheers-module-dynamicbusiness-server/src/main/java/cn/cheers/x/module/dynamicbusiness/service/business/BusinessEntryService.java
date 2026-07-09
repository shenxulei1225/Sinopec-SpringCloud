package cn.cheers.x.module.dynamicbusiness.service.business;

import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessEntryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessEntryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.business.vo.BusinessEntryUpdateReqVO;

import java.util.List;

public interface BusinessEntryService {

    Long create(BusinessEntryCreateReqVO reqVO);

    void update(BusinessEntryUpdateReqVO reqVO);

    void delete(Long id);

    BusinessEntryRespVO get(Long id);

    List<BusinessEntryRespVO> listByBusinessId(Long businessId);
}

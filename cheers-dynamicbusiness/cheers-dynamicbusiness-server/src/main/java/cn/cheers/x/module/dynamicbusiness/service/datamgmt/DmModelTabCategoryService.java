package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmModelTabCategoryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmModelTabCategorySaveReqVO;

public interface DmModelTabCategoryService {

    DmModelTabCategoryRespVO getByEntityTypeCode(String entityTypeCode);

    DmModelTabCategoryRespVO save(DmModelTabCategorySaveReqVO reqVO);

    void clear(String entityTypeCode);
}

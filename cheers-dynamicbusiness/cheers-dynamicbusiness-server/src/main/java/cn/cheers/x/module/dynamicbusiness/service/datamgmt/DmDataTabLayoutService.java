package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutSaveReqVO;

import java.util.List;

public interface DmDataTabLayoutService {

    List<DmDataTabLayoutRespVO> listByEntityTypeCode(String entityTypeCode);

    void saveLayouts(DmDataTabLayoutSaveReqVO reqVO);
}

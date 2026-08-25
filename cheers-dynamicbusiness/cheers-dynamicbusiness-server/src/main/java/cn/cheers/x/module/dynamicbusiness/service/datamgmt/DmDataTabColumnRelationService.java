package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabColumnRelationSaveReqVO;

import java.util.List;

public interface DmDataTabColumnRelationService {

    List<DmDataTabColumnRelationRespVO> listByLayoutId(Long layoutId);

    List<DmDataTabColumnRelationRespVO> listByEntityTypeCode(String entityTypeCode);

    void saveRelations(DmDataTabColumnRelationSaveReqVO reqVO);
}

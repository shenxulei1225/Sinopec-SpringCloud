package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmEntityDimensionRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmEntityDimensionSaveReqVO;

import java.util.List;

public interface DmEntityDimensionService {

    List<DmEntityDimensionRespVO> listByEntityTypeCode(String entityTypeCode);

    void saveDimensions(DmEntityDimensionSaveReqVO reqVO);
}

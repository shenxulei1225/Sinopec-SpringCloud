package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmCatalogOrchestrationBundleRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmCatalogOrchestrationBundleSaveReqVO;

public interface DmCatalogOrchestrationService {

    DmCatalogOrchestrationBundleRespVO getBundle(String registryCode);

    void saveBundle(DmCatalogOrchestrationBundleSaveReqVO reqVO);
}

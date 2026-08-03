package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmFiveWOrchestrationBundleSaveReqVO;

public interface DmFiveWOrchestrationService {

    DmFiveWOrchestrationBundleRespVO getBundle(String registryCode);

    void saveBundle(DmFiveWOrchestrationBundleSaveReqVO reqVO);
}

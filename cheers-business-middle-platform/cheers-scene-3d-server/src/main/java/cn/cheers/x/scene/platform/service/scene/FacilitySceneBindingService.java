package cn.cheers.x.scene.platform.service.scene;

import cn.cheers.x.scene.platform.controller.admin.scene.vo.FacilitySceneBindingRespVO;

public interface FacilitySceneBindingService {

    /**
     * 按设施编号查询场景绑定
     */
    FacilitySceneBindingRespVO getByFacilityId(Long facilityId);
}

package cn.cheers.x.scene.platform.service.scene;

import cn.cheers.x.scene.platform.controller.admin.scene.vo.FacilitySceneBindingRespVO;

import java.util.List;

public interface FacilitySceneBindingService {

    /**
     * 按设施编号查询所属场景（对内入口）
     */
    FacilitySceneBindingRespVO getByFacilityId(Long facilityId);

    /**
     * 按设施编码查询所属场景（导入导出 / 对外入口）
     */
    FacilitySceneBindingRespVO getByFacilityCode(String facilityCode);

    /**
     * 按场景编码列出成员设施（一场景可多设施）
     */
    List<FacilitySceneBindingRespVO> listBySceneCode(String sceneCode);
}

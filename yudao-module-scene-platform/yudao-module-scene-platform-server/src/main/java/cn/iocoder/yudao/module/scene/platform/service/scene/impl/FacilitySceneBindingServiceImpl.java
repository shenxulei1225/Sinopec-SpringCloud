package cn.iocoder.yudao.module.scene.platform.service.scene.impl;

import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.FacilitySceneBindingRespVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.FacilitySceneBindingDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.scene.FacilitySceneBindingMapper;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.scene.SceneMapper;
import cn.iocoder.yudao.module.scene.platform.service.scene.FacilitySceneBindingService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.scene.platform.enums.ErrorCodeConstants.FACILITY_SCENE_BINDING_NOT_EXISTS;

@Service
@Validated
public class FacilitySceneBindingServiceImpl implements FacilitySceneBindingService {

    @Resource
    private FacilitySceneBindingMapper facilitySceneBindingMapper;

    @Resource
    private SceneMapper sceneMapper;

    @Override
    public FacilitySceneBindingRespVO getByFacilityId(Long facilityId) {
        FacilitySceneBindingDO binding = facilitySceneBindingMapper.selectByFacilityId(facilityId);
        if (binding == null) {
            throw exception(FACILITY_SCENE_BINDING_NOT_EXISTS);
        }

        FacilitySceneBindingRespVO respVO = new FacilitySceneBindingRespVO();
        respVO.setFacilityId(binding.getFacilityId());
        respVO.setSceneCode(binding.getSceneCode());

        SceneDO scene = sceneMapper.selectBySceneCode(binding.getSceneCode());
        if (scene != null) {
            respVO.setSceneId(scene.getId());
        }
        return respVO;
    }
}

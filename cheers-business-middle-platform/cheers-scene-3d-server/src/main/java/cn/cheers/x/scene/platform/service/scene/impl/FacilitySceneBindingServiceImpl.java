package cn.cheers.x.scene.platform.service.scene.impl;

import cn.cheers.x.scene.platform.controller.admin.scene.vo.FacilitySceneBindingRespVO;
import cn.cheers.x.scene.platform.dal.dataobject.scene.FacilitySceneBindingDO;
import cn.cheers.x.scene.platform.dal.dataobject.scene.SceneDO;
import cn.cheers.x.scene.platform.dal.mysql.scene.FacilitySceneBindingMapper;
import cn.cheers.x.scene.platform.dal.mysql.scene.SceneMapper;
import cn.cheers.x.scene.platform.service.scene.FacilitySceneBindingService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.scene.platform.enums.ErrorCodeConstants.FACILITY_SCENE_BINDING_NOT_EXISTS;

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
        return toRespVO(binding);
    }

    @Override
    public FacilitySceneBindingRespVO getByFacilityCode(String facilityCode) {
        FacilitySceneBindingDO binding = facilitySceneBindingMapper.selectByFacilityCode(facilityCode);
        if (binding == null) {
            throw exception(FACILITY_SCENE_BINDING_NOT_EXISTS);
        }
        return toRespVO(binding);
    }

    @Override
    public List<FacilitySceneBindingRespVO> listBySceneCode(String sceneCode) {
        List<FacilitySceneBindingDO> bindings = facilitySceneBindingMapper.selectListBySceneCode(sceneCode);
        List<FacilitySceneBindingRespVO> result = new ArrayList<>(bindings.size());
        SceneDO scene = sceneMapper.selectBySceneCode(sceneCode);
        Long sceneId = scene != null ? scene.getId() : null;
        for (FacilitySceneBindingDO binding : bindings) {
            FacilitySceneBindingRespVO respVO = toRespVO(binding, sceneId);
            result.add(respVO);
        }
        return result;
    }

    private FacilitySceneBindingRespVO toRespVO(FacilitySceneBindingDO binding) {
        SceneDO scene = sceneMapper.selectBySceneCode(binding.getSceneCode());
        Long sceneId = scene != null ? scene.getId() : null;
        return toRespVO(binding, sceneId);
    }

    private FacilitySceneBindingRespVO toRespVO(FacilitySceneBindingDO binding, Long sceneId) {
        FacilitySceneBindingRespVO respVO = new FacilitySceneBindingRespVO();
        respVO.setFacilityId(binding.getFacilityId());
        respVO.setFacilityCode(binding.getFacilityCode());
        respVO.setSceneCode(binding.getSceneCode());
        respVO.setSceneId(sceneId);
        return respVO;
    }
}

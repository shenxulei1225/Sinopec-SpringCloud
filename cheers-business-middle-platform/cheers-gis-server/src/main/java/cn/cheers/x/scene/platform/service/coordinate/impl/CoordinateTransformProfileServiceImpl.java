package cn.cheers.x.scene.platform.service.coordinate.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceSaveReqVO;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateTransformProfileRespVO;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateTransformProfileSaveReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.coordinate.CoordinateTransformProfileDO;
import cn.cheers.x.scene.platform.dal.mysql.coordinate.CoordinateTransformProfileMapper;
import cn.cheers.x.scene.platform.service.coordinate.CoordinateTransformProfileService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class CoordinateTransformProfileServiceImpl implements CoordinateTransformProfileService {

    @Resource
    private CoordinateTransformProfileMapper coordinateTransformProfileMapper;

    @Override
    public List<CoordinateTransformProfileRespVO> getProfileList() {
        return BeanUtils.toBean(coordinateTransformProfileMapper.selectList(), CoordinateTransformProfileRespVO.class);
    }

    @Override
    public CoordinateTransformProfileRespVO getProfile(String profileCode) {
        return BeanUtils.toBean(getRequiredProfile(profileCode), CoordinateTransformProfileRespVO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProfile(CoordinateTransformProfileSaveReqVO reqVO) {
        CoordinateTransformProfileDO profile = BeanUtils.toBean(reqVO, CoordinateTransformProfileDO.class);
        profile.setStatus(1);
        coordinateTransformProfileMapper.insert(profile);
        return profile.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProfile(String profileCode, CoordinateTransformProfileSaveReqVO reqVO) {
        CoordinateTransformProfileDO profile = getRequiredProfile(profileCode);
        BeanUtils.copyProperties(reqVO, profile);
        coordinateTransformProfileMapper.updateById(profile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableProfile(String profileCode) {
        CoordinateTransformProfileDO profile = getRequiredProfile(profileCode);
        profile.setStatus(0);
        coordinateTransformProfileMapper.updateById(profile);
    }

    @Override
    public CoordinateReferenceSaveReqVO buildReferenceTemplate(String profileCode) {
        CoordinateTransformProfileDO profile = getRequiredProfile(profileCode);
        CoordinateReferenceSaveReqVO reqVO = new CoordinateReferenceSaveReqVO();
        reqVO.setGeographicCrsCode(profile.getGeographicCrsCode());
        reqVO.setProjectedCrsCode(profile.getProjectedCrsCode());
        reqVO.setDatumCode(profile.getDatumCode());
        reqVO.setEllipsoidCode(profile.getEllipsoidCode());
        reqVO.setPlanetShape(profile.getPlanetShape());
        reqVO.setReferenceFrameType(profile.getReferenceFrameType());
        reqVO.setLocalFrameType(profile.getLocalFrameType());
        reqVO.setEngineFrameType(profile.getEngineFrameType());
        reqVO.setAxisOrder(profile.getAxisOrder());
        reqVO.setHandedness(profile.getHandedness());
        reqVO.setLinearUnit(profile.getLinearUnit());
        reqVO.setAngularUnit(profile.getAngularUnit());
        reqVO.setTransformProfileCode(profile.getProfileCode());
        reqVO.setTransformConfigJson(profile.getTransformPipelineJson());
        return reqVO;
    }

    private CoordinateTransformProfileDO getRequiredProfile(String profileCode) {
        CoordinateTransformProfileDO profile = coordinateTransformProfileMapper.selectByProfileCode(profileCode);
        if (profile == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "坐标转换模板不存在");
        }
        return profile;
    }
}

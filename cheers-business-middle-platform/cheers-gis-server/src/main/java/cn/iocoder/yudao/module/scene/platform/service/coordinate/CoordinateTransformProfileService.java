package cn.iocoder.yudao.module.scene.platform.service.coordinate;

import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateTransformProfileRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateTransformProfileSaveReqVO;

import java.util.List;

public interface CoordinateTransformProfileService {

    List<CoordinateTransformProfileRespVO> getProfileList();

    CoordinateTransformProfileRespVO getProfile(String profileCode);

    Long createProfile(CoordinateTransformProfileSaveReqVO reqVO);

    void updateProfile(String profileCode, CoordinateTransformProfileSaveReqVO reqVO);

    void disableProfile(String profileCode);

    CoordinateReferenceSaveReqVO buildReferenceTemplate(String profileCode);
}

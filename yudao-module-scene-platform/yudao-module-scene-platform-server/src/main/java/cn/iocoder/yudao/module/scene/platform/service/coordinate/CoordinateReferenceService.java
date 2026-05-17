package cn.iocoder.yudao.module.scene.platform.service.coordinate;

import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceUpdateRespVO;

public interface CoordinateReferenceService {

    CoordinateReferenceRespVO getBySceneCode(String sceneCode);

    CoordinateReferenceUpdateRespVO updateBySceneCode(String sceneCode, CoordinateReferenceSaveReqVO reqVO, boolean strict);
}

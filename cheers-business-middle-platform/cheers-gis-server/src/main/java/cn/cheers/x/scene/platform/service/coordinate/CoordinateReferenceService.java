package cn.cheers.x.scene.platform.service.coordinate;

import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceOriginHeightUpdateReqVO;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceRespVO;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceSaveReqVO;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceUpdateRespVO;

public interface CoordinateReferenceService {

    CoordinateReferenceRespVO getBySceneCode(String sceneCode);

    CoordinateReferenceUpdateRespVO updateBySceneCode(String sceneCode, CoordinateReferenceSaveReqVO reqVO, boolean strict);

    /**
     * 仅更新原点高程（及可选经纬度/来源），不校验完整 CRS 契约。
     */
    CoordinateReferenceRespVO updateOriginHeightBySceneCode(String sceneCode, CoordinateReferenceOriginHeightUpdateReqVO reqVO);
}

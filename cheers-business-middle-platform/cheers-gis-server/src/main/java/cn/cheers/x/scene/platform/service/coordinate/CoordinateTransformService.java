package cn.cheers.x.scene.platform.service.coordinate;

import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateConvertReqVO;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateConvertRespVO;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinatePreviewRespVO;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateValidateRespVO;

public interface CoordinateTransformService {

    CoordinateConvertRespVO convert(CoordinateConvertReqVO reqVO);

    CoordinatePreviewRespVO preview(CoordinateConvertReqVO reqVO);

    CoordinateValidateRespVO validate(String sceneCode);
}

package cn.iocoder.yudao.module.scene.platform.service.coordinate;

import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateConvertReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateConvertRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinatePreviewRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateValidateRespVO;

public interface CoordinateTransformService {

    CoordinateConvertRespVO convert(CoordinateConvertReqVO reqVO);

    CoordinatePreviewRespVO preview(CoordinateConvertReqVO reqVO);

    CoordinateValidateRespVO validate(String sceneCode);
}

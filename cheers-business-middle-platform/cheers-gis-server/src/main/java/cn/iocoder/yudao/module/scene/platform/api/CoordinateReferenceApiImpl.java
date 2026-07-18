package cn.iocoder.yudao.module.scene.platform.api;

import cn.cheers.x.gis.api.CoordinateReferenceApi;
import cn.cheers.x.gis.api.dto.CoordinateReferenceRespDTO;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceRespVO;
import cn.iocoder.yudao.module.scene.platform.service.coordinate.CoordinateReferenceService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class CoordinateReferenceApiImpl implements CoordinateReferenceApi {

    @Resource
    private CoordinateReferenceService coordinateReferenceService;

    @Override
    public CommonResult<CoordinateReferenceRespDTO> getBySceneCode(String sceneCode) {
        CoordinateReferenceRespVO vo = coordinateReferenceService.getBySceneCode(sceneCode);
        return success(BeanUtils.toBean(vo, CoordinateReferenceRespDTO.class));
    }
}

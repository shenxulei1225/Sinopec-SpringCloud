package cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceUpdateRespVO;
import cn.iocoder.yudao.module.scene.platform.service.coordinate.CoordinateReferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 坐标参考")
@RestController
@RequestMapping("/scene-platform/scenes/{sceneCode}/coordinate-reference")
@Validated
public class CoordinateReferenceController {

    @Resource
    private CoordinateReferenceService coordinateReferenceService;

    @GetMapping
    @Operation(summary = "获得场景坐标参考详情")
    public CommonResult<CoordinateReferenceRespVO> getCoordinateReference(@PathVariable String sceneCode) {
        return success(coordinateReferenceService.getBySceneCode(sceneCode));
    }

    @PutMapping
    @Operation(summary = "更新场景坐标参考")
    public CommonResult<CoordinateReferenceUpdateRespVO> updateCoordinateReference(@PathVariable String sceneCode,
                                                                                   @Valid @RequestBody CoordinateReferenceSaveReqVO reqVO,
                                                                                   @RequestParam(value = "strict", defaultValue = "true") boolean strict) {
        return success(coordinateReferenceService.updateBySceneCode(sceneCode, reqVO, strict));
    }
}

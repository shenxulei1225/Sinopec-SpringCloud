package cn.cheers.x.scene.platform.controller.admin.coordinate;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateConvertReqVO;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateConvertRespVO;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinatePreviewRespVO;
import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateValidateRespVO;
import cn.cheers.x.scene.platform.service.coordinate.CoordinateTransformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 坐标转换")
@RestController
@RequestMapping("/scene-platform/coordinate")
@Validated
public class CoordinateTransformController {

    @Resource
    private CoordinateTransformService coordinateTransformService;

    @PostMapping("/convert")
    @Operation(summary = "坐标转换")
    public CommonResult<CoordinateConvertRespVO> convert(@Valid @RequestBody CoordinateConvertReqVO reqVO) {
        return success(coordinateTransformService.convert(reqVO));
    }

    @PostMapping("/preview")
    @Operation(summary = "坐标转换预览")
    public CommonResult<CoordinatePreviewRespVO> preview(@Valid @RequestBody CoordinateConvertReqVO reqVO) {
        return success(coordinateTransformService.preview(reqVO));
    }

    @PostMapping("/validate/{sceneCode}")
    @Operation(summary = "场景坐标配置校验")
    public CommonResult<CoordinateValidateRespVO> validate(@org.springframework.web.bind.annotation.PathVariable String sceneCode) {
        return success(coordinateTransformService.validate(sceneCode));
    }
}

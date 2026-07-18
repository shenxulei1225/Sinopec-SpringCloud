package cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateReferenceSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateTransformProfileRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateTransformProfileSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.service.coordinate.CoordinateTransformProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 坐标转换配置")
@RestController
@RequestMapping("/scene-platform/coordinate/transform-profiles")
@Validated
public class CoordinateTransformProfileController {

    @Resource
    private CoordinateTransformProfileService coordinateTransformProfileService;

    @GetMapping
    @Operation(summary = "获得坐标转换配置列表")
    public CommonResult<List<CoordinateTransformProfileRespVO>> getProfileList() {
        return success(coordinateTransformProfileService.getProfileList());
    }

    @GetMapping("/{profileCode}")
    @Operation(summary = "获得坐标转换配置详情")
    public CommonResult<CoordinateTransformProfileRespVO> getProfile(@PathVariable String profileCode) {
        return success(coordinateTransformProfileService.getProfile(profileCode));
    }

    @PostMapping
    @Operation(summary = "创建坐标转换配置")
    public CommonResult<Long> createProfile(@Valid @RequestBody CoordinateTransformProfileSaveReqVO reqVO) {
        return success(coordinateTransformProfileService.createProfile(reqVO));
    }

    @PutMapping("/{profileCode}")
    @Operation(summary = "更新坐标转换配置")
    public CommonResult<Boolean> updateProfile(@PathVariable String profileCode,
                                               @Valid @RequestBody CoordinateTransformProfileSaveReqVO reqVO) {
        coordinateTransformProfileService.updateProfile(profileCode, reqVO);
        return success(Boolean.TRUE);
    }

    @PutMapping("/{profileCode}/disable")
    @Operation(summary = "停用坐标转换配置")
    public CommonResult<Boolean> disableProfile(@PathVariable String profileCode) {
        coordinateTransformProfileService.disableProfile(profileCode);
        return success(Boolean.TRUE);
    }

    @GetMapping("/{profileCode}/reference-template")
    @Operation(summary = "获得基于配置生成的坐标参考默认值")
    public CommonResult<CoordinateReferenceSaveReqVO> getReferenceTemplate(@PathVariable String profileCode) {
        return success(coordinateTransformProfileService.buildReferenceTemplate(profileCode));
    }
}

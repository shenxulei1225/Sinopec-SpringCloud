package cn.cheers.x.scene.platform.controller.admin.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.component.vo.SplineComponentRespVO;
import cn.cheers.x.scene.platform.controller.admin.component.vo.SplineComponentSaveReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.component.SplineComponentDO;
import cn.cheers.x.scene.platform.service.component.SplineComponentService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Spline 组件")
@RestController
@RequestMapping("/scene-platform/spline-components")
@Validated
public class SplineComponentController {

    @Resource
    private SplineComponentService splineComponentService;

    @GetMapping
    @Operation(summary = "获得 Spline 组件列表")
    public CommonResult<List<SplineComponentRespVO>> getSplineComponentList() {
        return success(splineComponentService.getSplineComponentList().stream().map(this::convert).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Spline 组件详情")
    public CommonResult<SplineComponentRespVO> getSplineComponent(@PathVariable Long id) {
        return success(convert(splineComponentService.getSplineComponent(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Spline 组件")
    public CommonResult<Boolean> updateSplineComponent(@PathVariable Long id,
                                                       @Valid @RequestBody SplineComponentSaveReqVO reqVO) {
        splineComponentService.updateSplineComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Spline 组件默认值")
    public CommonResult<Boolean> updateSplineComponentDefaults(@PathVariable Long id,
                                                                @Valid @RequestBody SplineComponentSaveReqVO reqVO) {
        splineComponentService.updateSplineComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Spline 组件属性定义")
    public CommonResult<Boolean> updateSplineComponentSchema(@PathVariable Long id,
                                                             @Valid @RequestBody SplineComponentSaveReqVO reqVO) {
        splineComponentService.updateSplineComponentSchema(id, reqVO.toDO());
        return success(true);
    }

    private SplineComponentRespVO convert(SplineComponentDO item) {
        SplineComponentRespVO vo = new SplineComponentRespVO();
        vo.setId(item.getId());
        vo.setComponentCode(item.getComponentCode());
        vo.setDisplayName(item.getDisplayName());
        vo.setSplinePointsJson(item.getSplinePointsJson());
        vo.setClosedLoop(item.getClosedLoop());
        vo.setMobility(item.getMobility());
        vo.setMetadataJson(item.getMetadataJson());
        return vo;
    }
}

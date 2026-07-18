package cn.cheers.x.scene.platform.controller.admin.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.component.vo.SplineMeshComponentRespVO;
import cn.cheers.x.scene.platform.controller.admin.component.vo.SplineMeshComponentSaveReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.component.SplineMeshComponentDO;
import cn.cheers.x.scene.platform.service.component.SplineMeshComponentService;
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

@Tag(name = "管理后台 - Spline Mesh 组件")
@RestController
@RequestMapping("/scene-platform/spline-mesh-components")
@Validated
public class SplineMeshComponentController {

    @Resource
    private SplineMeshComponentService splineMeshComponentService;

    @GetMapping
    @Operation(summary = "获得 Spline Mesh 组件列表")
    public CommonResult<List<SplineMeshComponentRespVO>> getSplineMeshComponentList() {
        return success(splineMeshComponentService.getSplineMeshComponentList().stream().map(this::convert).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Spline Mesh 组件详情")
    public CommonResult<SplineMeshComponentRespVO> getSplineMeshComponent(@PathVariable Long id) {
        return success(convert(splineMeshComponentService.getSplineMeshComponent(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Spline Mesh 组件")
    public CommonResult<Boolean> updateSplineMeshComponent(@PathVariable Long id,
                                                           @Valid @RequestBody SplineMeshComponentSaveReqVO reqVO) {
        splineMeshComponentService.updateSplineMeshComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Spline Mesh 组件默认值")
    public CommonResult<Boolean> updateSplineMeshComponentDefaults(@PathVariable Long id,
                                                                   @Valid @RequestBody SplineMeshComponentSaveReqVO reqVO) {
        splineMeshComponentService.updateSplineMeshComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Spline Mesh 组件属性定义")
    public CommonResult<Boolean> updateSplineMeshComponentSchema(@PathVariable Long id,
                                                                  @Valid @RequestBody SplineMeshComponentSaveReqVO reqVO) {
        splineMeshComponentService.updateSplineMeshComponentSchema(id, reqVO.toDO());
        return success(true);
    }

    private SplineMeshComponentRespVO convert(SplineMeshComponentDO item) {
        SplineMeshComponentRespVO vo = new SplineMeshComponentRespVO();
        vo.setId(item.getId());
        vo.setComponentCode(item.getComponentCode());
        vo.setDisplayName(item.getDisplayName());
        vo.setSourceSplineCode(item.getSourceSplineCode());
        vo.setMeshCode(item.getMeshCode());
        vo.setCastShadow(item.getCastShadow());
        vo.setReceiveShadow(item.getReceiveShadow());
        vo.setMetadataJson(item.getMetadataJson());
        return vo;
    }
}

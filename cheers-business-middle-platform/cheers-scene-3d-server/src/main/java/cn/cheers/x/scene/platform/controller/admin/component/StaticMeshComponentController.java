package cn.cheers.x.scene.platform.controller.admin.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.component.vo.StaticMeshComponentRespVO;
import cn.cheers.x.scene.platform.controller.admin.component.vo.StaticMeshComponentSaveReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.component.StaticMeshComponentDO;
import cn.cheers.x.scene.platform.service.component.StaticMeshComponentService;
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

@Tag(name = "管理后台 - Static Mesh 组件")
@RestController
@RequestMapping("/scene-3d/static-mesh-components")
@Validated
public class StaticMeshComponentController {

    @Resource
    private StaticMeshComponentService staticMeshComponentService;

    @GetMapping
    @Operation(summary = "获得 Static Mesh 组件列表")
    public CommonResult<List<StaticMeshComponentRespVO>> getStaticMeshComponentList() {
        return success(staticMeshComponentService.getStaticMeshComponentList().stream().map(this::convert).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Static Mesh 组件详情")
    public CommonResult<StaticMeshComponentRespVO> getStaticMeshComponent(@PathVariable Long id) {
        return success(convert(staticMeshComponentService.getStaticMeshComponent(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Static Mesh 组件")
    public CommonResult<Boolean> updateStaticMeshComponent(@PathVariable Long id,
                                                           @Valid @RequestBody StaticMeshComponentSaveReqVO reqVO) {
        staticMeshComponentService.updateStaticMeshComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Static Mesh 组件默认值")
    public CommonResult<Boolean> updateStaticMeshComponentDefaults(@PathVariable Long id,
                                                                   @Valid @RequestBody StaticMeshComponentSaveReqVO reqVO) {
        staticMeshComponentService.updateStaticMeshComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Static Mesh 组件属性定义")
    public CommonResult<Boolean> updateStaticMeshComponentSchema(@PathVariable Long id,
                                                                 @Valid @RequestBody StaticMeshComponentSaveReqVO reqVO) {
        staticMeshComponentService.updateStaticMeshComponentSchema(id, reqVO.toDO());
        return success(true);
    }

    private StaticMeshComponentRespVO convert(StaticMeshComponentDO item) {
        StaticMeshComponentRespVO vo = new StaticMeshComponentRespVO();
        vo.setId(item.getId());
        vo.setComponentCode(item.getComponentCode());
        vo.setDisplayName(item.getDisplayName());
        vo.setMeshCode(item.getMeshCode());
        vo.setMaterialsJson(item.getMaterialsJson());
        vo.setCastShadow(item.getCastShadow());
        vo.setReceiveShadow(item.getReceiveShadow());
        vo.setGenerateOverlapEvents(item.getGenerateOverlapEvents());
        vo.setMobility(item.getMobility());
        vo.setMetadataJson(item.getMetadataJson());
        return vo;
    }
}

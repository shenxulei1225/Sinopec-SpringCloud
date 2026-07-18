package cn.iocoder.yudao.module.scene.platform.controller.admin.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.PrimitiveComponentRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.PrimitiveComponentSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.PrimitiveComponentDO;
import cn.iocoder.yudao.module.scene.platform.service.component.PrimitiveComponentService;
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

@Tag(name = "管理后台 - Primitive 组件")
@RestController
@RequestMapping("/scene-platform/primitive-components")
@Validated
public class PrimitiveComponentController {

    @Resource
    private PrimitiveComponentService primitiveComponentService;

    @GetMapping
    @Operation(summary = "获得 Primitive 组件列表")
    public CommonResult<List<PrimitiveComponentRespVO>> getPrimitiveComponentList() {
        return success(primitiveComponentService.getPrimitiveComponentList().stream().map(this::convert).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Primitive 组件详情")
    public CommonResult<PrimitiveComponentRespVO> getPrimitiveComponent(@PathVariable Long id) {
        return success(convert(primitiveComponentService.getPrimitiveComponent(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Primitive 组件")
    public CommonResult<Boolean> updatePrimitiveComponent(@PathVariable Long id,
                                                          @Valid @RequestBody PrimitiveComponentSaveReqVO reqVO) {
        primitiveComponentService.updatePrimitiveComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Primitive 组件默认值")
    public CommonResult<Boolean> updatePrimitiveComponentDefaults(@PathVariable Long id,
                                                                   @Valid @RequestBody PrimitiveComponentSaveReqVO reqVO) {
        primitiveComponentService.updatePrimitiveComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Primitive 组件属性定义")
    public CommonResult<Boolean> updatePrimitiveComponentSchema(@PathVariable Long id,
                                                                @Valid @RequestBody PrimitiveComponentSaveReqVO reqVO) {
        primitiveComponentService.updatePrimitiveComponentSchema(id, reqVO.toDO());
        return success(true);
    }

    private PrimitiveComponentRespVO convert(PrimitiveComponentDO item) {
        PrimitiveComponentRespVO vo = new PrimitiveComponentRespVO();
        vo.setId(item.getId());
        vo.setComponentCode(item.getComponentCode());
        vo.setDisplayName(item.getDisplayName());
        vo.setCollisionEnabled(item.getCollisionEnabled());
        vo.setObjectType(item.getObjectType());
        vo.setGenerateOverlapEvents(item.getGenerateOverlapEvents());
        vo.setSimulationGeneratesHitEvents(item.getSimulationGeneratesHitEvents());
        vo.setCanCharacterStepUpOn(item.getCanCharacterStepUpOn());
        vo.setUseDefaultCollision(item.getUseDefaultCollision());
        vo.setPhysicsMaterialJson(item.getPhysicsMaterialJson());
        vo.setBoundsJson(item.getBoundsJson());
        vo.setCollisionResponseJson(item.getCollisionResponseJson());
        vo.setMetadataJson(item.getMetadataJson());
        return vo;
    }
}

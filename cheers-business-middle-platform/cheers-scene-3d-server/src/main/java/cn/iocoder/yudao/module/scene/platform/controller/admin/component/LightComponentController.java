package cn.iocoder.yudao.module.scene.platform.controller.admin.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.LightComponentRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.LightComponentSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.LightComponentDO;
import cn.iocoder.yudao.module.scene.platform.service.component.LightComponentService;
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

@Tag(name = "管理后台 - Light 组件")
@RestController
@RequestMapping("/scene-platform/light-components")
@Validated
public class LightComponentController {

    @Resource
    private LightComponentService lightComponentService;

    @GetMapping
    @Operation(summary = "获得 Light 组件列表")
    public CommonResult<List<LightComponentRespVO>> getLightComponentList() {
        return success(lightComponentService.getLightComponentList().stream().map(this::convert).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Light 组件详情")
    public CommonResult<LightComponentRespVO> getLightComponent(@PathVariable Long id) {
        return success(convert(lightComponentService.getLightComponent(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Light 组件")
    public CommonResult<Boolean> updateLightComponent(@PathVariable Long id,
                                                      @Valid @RequestBody LightComponentSaveReqVO reqVO) {
        lightComponentService.updateLightComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Light 组件默认值")
    public CommonResult<Boolean> updateLightComponentDefaults(@PathVariable Long id,
                                                              @Valid @RequestBody LightComponentSaveReqVO reqVO) {
        lightComponentService.updateLightComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Light 组件属性定义")
    public CommonResult<Boolean> updateLightComponentSchema(@PathVariable Long id,
                                                             @Valid @RequestBody LightComponentSaveReqVO reqVO) {
        lightComponentService.updateLightComponentSchema(id, reqVO.toDO());
        return success(true);
    }

    private LightComponentRespVO convert(LightComponentDO item) {
        LightComponentRespVO vo = new LightComponentRespVO();
        vo.setId(item.getId());
        vo.setComponentCode(item.getComponentCode());
        vo.setDisplayName(item.getDisplayName());
        vo.setIntensity(item.getIntensity());
        vo.setLightColor(item.getLightColor());
        vo.setCastShadows(item.getCastShadows());
        vo.setMobility(item.getMobility());
        vo.setMetadataJson(item.getMetadataJson());
        return vo;
    }
}

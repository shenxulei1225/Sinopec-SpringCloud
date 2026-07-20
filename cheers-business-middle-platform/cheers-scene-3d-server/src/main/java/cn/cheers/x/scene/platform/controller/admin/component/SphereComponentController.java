package cn.cheers.x.scene.platform.controller.admin.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.component.vo.SphereComponentSaveReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.component.SphereComponentDO;
import cn.cheers.x.scene.platform.service.component.SphereComponentService;
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

@Tag(name = "管理后台 - Sphere 组件")
@RestController
@RequestMapping("/scene-3d/sphere-components")
@Validated
public class SphereComponentController {

    @Resource
    private SphereComponentService sphereComponentService;

    @GetMapping
    @Operation(summary = "获得 Sphere 组件列表")
    public CommonResult<List<SphereComponentDO>> getSphereComponentList() {
        return success(sphereComponentService.getSphereComponentList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Sphere 组件详情")
    public CommonResult<SphereComponentDO> getSphereComponent(@PathVariable Long id) {
        return success(sphereComponentService.getSphereComponent(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Sphere 组件")
    public CommonResult<Boolean> updateSphereComponent(@PathVariable Long id,
                                                       @Valid @RequestBody SphereComponentSaveReqVO reqVO) {
        sphereComponentService.updateSphereComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Sphere 组件默认值")
    public CommonResult<Boolean> updateSphereComponentDefaults(@PathVariable Long id,
                                                                @Valid @RequestBody SphereComponentSaveReqVO reqVO) {
        sphereComponentService.updateSphereComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Sphere 组件属性定义")
    public CommonResult<Boolean> updateSphereComponentSchema(@PathVariable Long id,
                                                             @Valid @RequestBody SphereComponentSaveReqVO reqVO) {
        sphereComponentService.updateSphereComponentSchema(id, reqVO.toDO());
        return success(true);
    }
}

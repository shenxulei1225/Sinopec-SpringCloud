package cn.cheers.x.scene.platform.controller.admin.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.component.vo.CapsuleComponentSaveReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.component.CapsuleComponentDO;
import cn.cheers.x.scene.platform.service.component.CapsuleComponentService;
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

@Tag(name = "管理后台 - Capsule 组件")
@RestController
@RequestMapping("/scene-3d/capsule-components")
@Validated
public class CapsuleComponentController {

    @Resource
    private CapsuleComponentService capsuleComponentService;

    @GetMapping
    @Operation(summary = "获得 Capsule 组件列表")
    public CommonResult<List<CapsuleComponentDO>> getCapsuleComponentList() {
        return success(capsuleComponentService.getCapsuleComponentList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Capsule 组件详情")
    public CommonResult<CapsuleComponentDO> getCapsuleComponent(@PathVariable Long id) {
        return success(capsuleComponentService.getCapsuleComponent(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Capsule 组件")
    public CommonResult<Boolean> updateCapsuleComponent(@PathVariable Long id,
                                                       @Valid @RequestBody CapsuleComponentSaveReqVO reqVO) {
        capsuleComponentService.updateCapsuleComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Capsule 组件默认值")
    public CommonResult<Boolean> updateCapsuleComponentDefaults(@PathVariable Long id,
                                                                @Valid @RequestBody CapsuleComponentSaveReqVO reqVO) {
        capsuleComponentService.updateCapsuleComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Capsule 组件属性定义")
    public CommonResult<Boolean> updateCapsuleComponentSchema(@PathVariable Long id,
                                                             @Valid @RequestBody CapsuleComponentSaveReqVO reqVO) {
        capsuleComponentService.updateCapsuleComponentSchema(id, reqVO.toDO());
        return success(true);
    }
}

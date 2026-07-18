package cn.iocoder.yudao.module.scene.platform.controller.admin.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.MeshComponentSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.MeshComponentDO;
import cn.iocoder.yudao.module.scene.platform.service.component.MeshComponentService;
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

@Tag(name = "管理后台 - Mesh 组件")
@RestController
@RequestMapping("/scene-platform/mesh-components")
@Validated
public class MeshComponentController {

    @Resource
    private MeshComponentService meshComponentService;

    @GetMapping
    @Operation(summary = "获得 Mesh 组件列表")
    public CommonResult<List<MeshComponentDO>> getMeshComponentList() {
        return success(meshComponentService.getMeshComponentList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Mesh 组件详情")
    public CommonResult<MeshComponentDO> getMeshComponent(@PathVariable Long id) {
        return success(meshComponentService.getMeshComponent(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Mesh 组件")
    public CommonResult<Boolean> updateMeshComponent(@PathVariable Long id,
                                                     @Valid @RequestBody MeshComponentSaveReqVO reqVO) {
        meshComponentService.updateMeshComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Mesh 组件默认值")
    public CommonResult<Boolean> updateMeshComponentDefaults(@PathVariable Long id,
                                                              @Valid @RequestBody MeshComponentSaveReqVO reqVO) {
        meshComponentService.updateMeshComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Mesh 组件属性定义")
    public CommonResult<Boolean> updateMeshComponentSchema(@PathVariable Long id,
                                                            @Valid @RequestBody MeshComponentSaveReqVO reqVO) {
        meshComponentService.updateMeshComponentSchema(id, reqVO.toDO());
        return success(true);
    }
}

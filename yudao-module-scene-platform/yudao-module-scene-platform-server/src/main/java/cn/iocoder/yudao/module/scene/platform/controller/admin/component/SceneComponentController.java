package cn.iocoder.yudao.module.scene.platform.controller.admin.component;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.SceneComponentPageReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.SceneComponentRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.SceneComponentSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.SceneComponentDO;
import cn.iocoder.yudao.module.scene.platform.service.component.SceneComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 场景级全局组件")
@RestController
@RequestMapping("/scene-platform/scene-component")
@Validated
public class SceneComponentController {

    @Resource
    private SceneComponentService sceneComponentService;

    @GetMapping("/list-by-scene")
    @Operation(summary = "获取场景的所有全局组件")
    @Parameter(name = "sceneId", description = "场景 ID", required = true)
    @PreAuthorize("@ss.hasPermission('scene-platform:scene-component:query')")
    public CommonResult<List<SceneComponentRespVO>> getSceneComponents(@RequestParam("sceneId") Long sceneId) {
        List<SceneComponentDO> list = sceneComponentService.getSceneComponents(sceneId);
        return success(SceneComponentRespVO.convert(list));
    }

    @GetMapping("/get-by-type")
    @Operation(summary = "获取场景的指定类型全局组件")
    @Parameter(name = "sceneId", description = "场景 ID", required = true)
    @Parameter(name = "componentType", description = "组件类型", required = true)
    @PreAuthorize("@ss.hasPermission('scene-platform:scene-component:query')")
    public CommonResult<SceneComponentRespVO> getSceneComponentByType(
            @RequestParam("sceneId") Long sceneId,
            @RequestParam("componentType") String componentType) {
        SceneComponentDO sceneComponentDO = sceneComponentService.getSceneComponentByType(sceneId, componentType);
        return success(SceneComponentRespVO.convert(sceneComponentDO));
    }

    @GetMapping("/get")
    @Operation(summary = "获取场景组件详情")
    @Parameter(name = "id", description = "组件 ID", required = true)
    @PreAuthorize("@ss.hasPermission('scene-platform:scene-component:query')")
    public CommonResult<SceneComponentRespVO> getSceneComponent(@RequestParam("id") Long id) {
        SceneComponentDO sceneComponentDO = sceneComponentService.getSceneComponent(id);
        return success(SceneComponentRespVO.convert(sceneComponentDO));
    }

    @PostMapping("/create")
    @Operation(summary = "创建场景全局组件")
    @PreAuthorize("@ss.hasPermission('scene-platform:scene-component:create')")
    public CommonResult<Long> createSceneComponent(@Valid @RequestBody SceneComponentSaveReqVO createReqVO) {
        Long id = sceneComponentService.createSceneComponent(SceneComponentSaveReqVO.convert(createReqVO));
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新场景全局组件")
    @PreAuthorize("@ss.hasPermission('scene-platform:scene-component:update')")
    public CommonResult<Boolean> updateSceneComponent(@Valid @RequestBody SceneComponentSaveReqVO updateReqVO) {
        sceneComponentService.updateSceneComponent(updateReqVO.getId(), SceneComponentSaveReqVO.convert(updateReqVO));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除场景全局组件")
    @Parameter(name = "id", description = "组件 ID", required = true)
    @PreAuthorize("@ss.hasPermission('scene-platform:scene-component:delete')")
    public CommonResult<Boolean> deleteSceneComponent(@RequestParam("id") Long id) {
        sceneComponentService.deleteSceneComponent(id);
        return success(true);
    }

    @PutMapping("/enable")
    @Operation(summary = "启用/禁用场景全局组件")
    @PreAuthorize("@ss.hasPermission('scene-platform:scene-component:update')")
    public CommonResult<Boolean> setSceneComponentEnabled(
            @RequestParam("id") Long id,
            @RequestParam("enabled") Boolean enabled) {
        sceneComponentService.setSceneComponentEnabled(id, enabled);
        return success(true);
    }

    @GetMapping("/types")
    @Operation(summary = "获取场景组件类型枚举")
    public CommonResult<List<String>> getComponentTypes() {
        return success(sceneComponentService.getComponentTypes());
    }
}
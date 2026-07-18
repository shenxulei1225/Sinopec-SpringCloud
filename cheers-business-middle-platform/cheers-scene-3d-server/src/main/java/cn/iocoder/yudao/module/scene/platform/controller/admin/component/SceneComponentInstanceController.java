package cn.iocoder.yudao.module.scene.platform.controller.admin.component;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.SceneComponentInstanceRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.SceneComponentInstanceSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.service.component.SceneComponentInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 场景组件挂载")
@RestController
@RequestMapping("/scene-platform/scene-component-instances")
@Validated
public class SceneComponentInstanceController {

    @Resource
    private SceneComponentInstanceService sceneComponentInstanceService;

    @GetMapping
    @Operation(summary = "获得场景组件挂载列表")
    public CommonResult<List<SceneComponentInstanceRespVO>> getSceneComponentInstanceList() {
        return success(sceneComponentInstanceService.getSceneComponentInstanceList().stream().map(item -> {
            SceneComponentInstanceRespVO vo = new SceneComponentInstanceRespVO();
            vo.setId(item.getId());
            vo.setSceneId(item.getSceneId());
            vo.setSceneCode(item.getSceneCode());
            vo.setInstanceCode(item.getInstanceCode());
            vo.setComponentCode(item.getComponentCode());
            vo.setEnabledFlag(item.getEnabledFlag());
            vo.setConfigJson(item.getConfigJson());
            vo.setMetadataJson(item.getMetadataJson());
            vo.setSortNo(item.getSortNo());
            return vo;
        }).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得场景组件挂载详情")
    public CommonResult<SceneComponentInstanceRespVO> getSceneComponentInstance(@PathVariable Long id) {
        var item = sceneComponentInstanceService.getSceneComponentInstance(id);
        SceneComponentInstanceRespVO vo = new SceneComponentInstanceRespVO();
        vo.setId(item.getId());
        vo.setSceneId(item.getSceneId());
        vo.setSceneCode(item.getSceneCode());
        vo.setInstanceCode(item.getInstanceCode());
        vo.setComponentCode(item.getComponentCode());
        vo.setEnabledFlag(item.getEnabledFlag());
        vo.setConfigJson(item.getConfigJson());
        vo.setMetadataJson(item.getMetadataJson());
        vo.setSortNo(item.getSortNo());
        return success(vo);
    }

    @PostMapping
    @Operation(summary = "创建场景组件挂载")
    public CommonResult<Long> createSceneComponentInstance(@Valid @RequestBody SceneComponentInstanceSaveReqVO reqVO) {
        return success(sceneComponentInstanceService.createSceneComponentInstance(reqVO.toDO()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新场景组件挂载")
    public CommonResult<Boolean> updateSceneComponentInstance(@PathVariable Long id,
                                                               @Valid @RequestBody SceneComponentInstanceSaveReqVO reqVO) {
        sceneComponentInstanceService.updateSceneComponentInstance(id, reqVO.toDO());
        return success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "停用场景组件挂载")
    public CommonResult<Boolean> disableSceneComponentInstance(@PathVariable Long id) {
        sceneComponentInstanceService.disableSceneComponentInstance(id);
        return success(true);
    }
}

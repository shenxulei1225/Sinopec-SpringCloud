package cn.iocoder.yudao.module.scene.platform.controller.admin.scene;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.ActorRuntimeReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.ActorRuntimeRespVO;
import cn.iocoder.yudao.module.scene.platform.service.scene.SceneRuntimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 场景运行时管理")
@RestController
@RequestMapping("/scene-platform/runtime")
@Validated
public class SceneRuntimeController {

    @Resource
    private SceneRuntimeService sceneRuntimeService;

    @PutMapping("/actor/update")
    @Operation(summary = "更新 Actor 运行时状态")
    public CommonResult<Boolean> updateActorRuntime(@Valid @RequestBody ActorRuntimeReqVO reqVO) {
        sceneRuntimeService.updateActorRuntime(
                reqVO.getSceneCode(),
                reqVO.getInstanceId(),
                reqVO.getInstanceCode(),
                reqVO.getTransform(),
                reqVO.getStatus(),
                reqVO.getActorCategory()
        );
        return success(true);
    }

    @GetMapping("/actor")
    @Operation(summary = "获取 Actor 运行时状态")
    @Parameter(name = "sceneCode", description = "场景编码", required = true)
    @Parameter(name = "instanceCode", description = "Actor 实例编码", required = true)
    public CommonResult<ActorRuntimeRespVO> getActorRuntime(
            @RequestParam("sceneCode") String sceneCode,
            @RequestParam("instanceCode") String instanceCode) {
        ActorRuntimeRespVO vo = sceneRuntimeService.getActorRuntime(sceneCode, instanceCode);
        return success(vo);
    }

    @GetMapping("/actors")
    @Operation(summary = "获取场景所有 Actor 运行时状态")
    @Parameter(name = "sceneCode", description = "场景编码", required = true)
    public CommonResult<List<ActorRuntimeRespVO>> getAllActorsRuntime(@RequestParam("sceneCode") String sceneCode) {
        List<ActorRuntimeRespVO> list = sceneRuntimeService.getAllActorsRuntime(sceneCode);
        return success(list);
    }

    @DeleteMapping("/actor")
    @Operation(summary = "删除 Actor 运行时数据")
    @Parameter(name = "sceneCode", description = "场景编码", required = true)
    @Parameter(name = "instanceCode", description = "Actor 实例编码", required = true)
    public CommonResult<Boolean> removeActorRuntime(
            @RequestParam("sceneCode") String sceneCode,
            @RequestParam("instanceCode") String instanceCode) {
        sceneRuntimeService.removeActorRuntime(sceneCode, instanceCode);
        return success(true);
    }

    @PutMapping("/online-count")
    @Operation(summary = "更新场景在线用户数")
    @Parameter(name = "sceneCode", description = "场景编码", required = true)
    @Parameter(name = "delta", description = "增减数量（正数=增加，负数=减少）", required = true)
    public CommonResult<Boolean> updateSceneOnlineCount(
            @RequestParam("sceneCode") String sceneCode,
            @RequestParam("delta") int delta) {
        sceneRuntimeService.updateSceneOnlineCount(sceneCode, delta);
        return success(true);
    }

    @GetMapping("/online-count")
    @Operation(summary = "获取场景在线用户数")
    @Parameter(name = "sceneCode", description = "场景编码", required = true)
    public CommonResult<Integer> getSceneOnlineCount(@RequestParam("sceneCode") String sceneCode) {
        int count = sceneRuntimeService.getSceneOnlineCount(sceneCode);
        return success(count);
    }
}

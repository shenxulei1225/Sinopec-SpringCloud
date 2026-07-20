package cn.cheers.x.scene.platform.controller.admin.actor;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.scene.vo.ActorRuntimeReqVO;
import cn.cheers.x.scene.platform.model.Transform;
import cn.cheers.x.scene.platform.model.Vector3;
import cn.cheers.x.scene.platform.service.scene.SceneRuntimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Actor 运行时管理")
@RestController
@RequestMapping("/scene-3d/actor-runtime")
@Validated
public class ActorRuntimeController {

    @Resource
    private SceneRuntimeService sceneRuntimeService;

    @PutMapping("/update-runtime")
    @Operation(summary = "更新 Actor 运行时状态（GPS/传感器上报）")
    public CommonResult<Boolean> updateActorRuntime(@Valid @RequestBody ActorRuntimeReqVO reqVO) {
        Transform transform = reqVO.getTransform();
        if (transform == null) {
            transform = new Transform();
            transform.setLocation(new Vector3());
        }
        sceneRuntimeService.updateActorRuntime(
                reqVO.getSceneCode(),
                reqVO.getInstanceId(),
                reqVO.getInstanceCode(),
                transform,
                reqVO.getStatus(),
                null
        );
        return success(true);
    }

    @GetMapping("/online-count")
    @Operation(summary = "获取场景在线用户数")
    @Parameter(name = "sceneCode", description = "场景编码", required = true)
    public CommonResult<Integer> getOnlineCount(@RequestParam("sceneCode") String sceneCode) {
        int count = sceneRuntimeService.getSceneOnlineCount(sceneCode);
        return success(count);
    }
}

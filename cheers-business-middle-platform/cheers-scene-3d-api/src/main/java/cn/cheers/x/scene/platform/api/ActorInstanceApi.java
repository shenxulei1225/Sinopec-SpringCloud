package cn.cheers.x.scene.platform.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.api.dto.ActorInstanceRuntimeRespDTO;
import cn.cheers.x.scene.platform.api.dto.ActorInstanceSimpleRespDTO;
import cn.cheers.x.scene.platform.api.dto.ActorInstanceTransformUpdateReqDTO;
import cn.cheers.x.scene.platform.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - Actor 实例")
public interface ActorInstanceApi {

    String PREFIX = ApiConstants.PREFIX + "/actor-instance";

    @GetMapping(PREFIX + "/{id}")
    @Operation(summary = "获取 ActorInstance 精简信息")
    CommonResult<ActorInstanceSimpleRespDTO> getActorInstance(@PathVariable("id") Long id);

    @GetMapping(PREFIX + "/list-by-ids")
    @Operation(summary = "批量获取 ActorInstance 精简信息")
    CommonResult<List<ActorInstanceSimpleRespDTO>> getActorInstances(@RequestParam("ids") List<Long> ids);

    @GetMapping(PREFIX + "/list-by-scene")
    @Operation(summary = "按场景获取 ActorInstance 精简列表")
    CommonResult<List<ActorInstanceSimpleRespDTO>> getActorInstancesByScene(@RequestParam("sceneId") Long sceneId,
                                                                             @RequestParam(value = "keyword", required = false) String keyword);

    @GetMapping(PREFIX + "/{id}/runtime")
    @Operation(summary = "获取 ActorInstance 运行时展示信息")
    CommonResult<ActorInstanceRuntimeRespDTO> getActorInstanceRuntime(@PathVariable("id") Long id);

    @PutMapping(PREFIX + "/{id}/transform")
    @Operation(summary = "更新 ActorInstance 变换信息")
    CommonResult<Boolean> updateActorInstanceTransform(@PathVariable("id") Long id,
                                                       @RequestBody ActorInstanceTransformUpdateReqDTO reqDTO);
}

package cn.cheers.x.twin.controller.admin;

import cn.cheers.x.twin.controller.admin.vo.TwinMappingBindReqVO;
import cn.cheers.x.twin.controller.admin.vo.TwinMappingRespVO;
import cn.cheers.x.twin.controller.admin.vo.TwinMappingUnbindReqVO;
import cn.cheers.x.twin.controller.admin.vo.TwinSceneMappingOverviewRespVO;
import cn.cheers.x.twin.service.TwinMappingService;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Twin 映射")
@RestController
@RequestMapping("/twin/mapping")
@Validated
public class TwinMappingController {

    @Resource
    private TwinMappingService twinMappingService;

    @PostMapping("/bind")
    @Operation(summary = "建立或换绑 Facility ↔ ActorInstance")
    public CommonResult<TwinMappingRespVO> bind(@Valid @RequestBody TwinMappingBindReqVO reqVO) {
        return success(twinMappingService.bind(reqVO));
    }

    @PostMapping("/unbind")
    @Operation(summary = "解除映射")
    public CommonResult<Boolean> unbind(@Valid @RequestBody TwinMappingUnbindReqVO reqVO) {
        twinMappingService.unbind(reqVO);
        return success(true);
    }

    @GetMapping("/facility/{facilityId}")
    @Operation(summary = "按 Facility 查询当前映射")
    public CommonResult<TwinMappingRespVO> getByFacility(@PathVariable Long facilityId) {
        return success(twinMappingService.getByFacilityId(facilityId));
    }

    @GetMapping("/actor-instance/{actorInstanceId}")
    @Operation(summary = "按 ActorInstance 查询当前映射")
    public CommonResult<TwinMappingRespVO> getByActorInstance(@PathVariable Long actorInstanceId) {
        return success(twinMappingService.getByActorInstanceId(actorInstanceId));
    }

    @GetMapping("/scene/overview")
    @Operation(summary = "按场景查询映射概览")
    public CommonResult<TwinSceneMappingOverviewRespVO> getSceneOverview(@RequestParam(value = "sceneId", required = false) Long sceneId,
                                                                         @RequestParam(value = "sceneCode", required = false) String sceneCode) {
        return success(twinMappingService.getSceneOverview(sceneId, sceneCode));
    }
}

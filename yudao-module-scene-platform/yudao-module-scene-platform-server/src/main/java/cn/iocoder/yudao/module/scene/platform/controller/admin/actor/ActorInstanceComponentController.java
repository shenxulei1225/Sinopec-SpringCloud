package cn.iocoder.yudao.module.scene.platform.controller.admin.actor;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorInstanceComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Actor 实例组件")
@RestController
@RequestMapping("/scene-platform/actor-instance-components")
@Validated
public class ActorInstanceComponentController {

    @Resource
    private ActorInstanceComponentService actorInstanceComponentService;

    @GetMapping
    @Operation(summary = "获得 Actor 实例组件列表")
    public CommonResult<List<ActorInstanceComponentRespVO>> getActorInstanceComponentList(@PathVariable Long actorInstanceId) {
        return success(actorInstanceComponentService.getActorInstanceComponentList(actorInstanceId).stream().map(this::convert).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得 Actor 实例组件详情")
    public CommonResult<ActorInstanceComponentRespVO> getActorInstanceComponent(@PathVariable Long id) {
        return success(convert(actorInstanceComponentService.getActorInstanceComponent(id)));
    }

    @PostMapping
    @Operation(summary = "创建 Actor 实例组件")
    public CommonResult<Long> createActorInstanceComponent(@Valid @RequestBody ActorInstanceComponentSaveReqVO reqVO) {
        return success(actorInstanceComponentService.createActorInstanceComponent(reqVO.toDO()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 Actor 实例组件")
    public CommonResult<Boolean> updateActorInstanceComponent(@PathVariable Long id,
                                                              @Valid @RequestBody ActorInstanceComponentSaveReqVO reqVO) {
        actorInstanceComponentService.updateActorInstanceComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新 Actor 实例组件默认值")
    public CommonResult<Boolean> updateActorInstanceComponentDefaults(@PathVariable Long id,
                                                                       @Valid @RequestBody ActorInstanceComponentSaveReqVO reqVO) {
        actorInstanceComponentService.updateActorInstanceComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新 Actor 实例组件属性定义")
    public CommonResult<Boolean> updateActorInstanceComponentSchema(@PathVariable Long id,
                                                                     @Valid @RequestBody ActorInstanceComponentSaveReqVO reqVO) {
        actorInstanceComponentService.updateActorInstanceComponentSchema(id, reqVO.toDO());
        return success(true);
    }

    private ActorInstanceComponentRespVO convert(ActorInstanceComponentDO item) {
        ActorInstanceComponentRespVO vo = new ActorInstanceComponentRespVO();
        vo.setId(item.getId());
        vo.setActorInstanceId(item.getActorInstanceId());
        vo.setActorCode(item.getActorCode());
        vo.setInstanceCode(item.getInstanceCode());
        vo.setComponentCode(item.getComponentCode());
        vo.setEnabledFlag(item.getEnabledFlag());
        vo.setSortNo(item.getSortNo());
        vo.setOverrideJson(item.getOverrideJson());
        vo.setMetadataJson(item.getMetadataJson());
        return vo;
    }
}

package cn.iocoder.yudao.module.scene.platform.api;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.api.dto.ActorInstanceRuntimeRespDTO;
import cn.iocoder.yudao.module.scene.platform.api.dto.ActorInstanceSimpleRespDTO;
import cn.iocoder.yudao.module.scene.platform.api.dto.ActorInstanceTransformUpdateReqDTO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.actor.ActorInstanceComponentMapper;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.scene.SceneMapper;
import cn.iocoder.yudao.module.scene.platform.model.Rotator;
import cn.iocoder.yudao.module.scene.platform.model.Transform;
import cn.iocoder.yudao.module.scene.platform.model.Vector3;
import cn.iocoder.yudao.module.scene.platform.model.render.ActorRenderConfig;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorInstanceService;
import cn.iocoder.yudao.module.scene.platform.service.actor.ActorService;
import cn.iocoder.yudao.module.scene.platform.service.actor.support.ActorRenderConfigValidator;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class ActorInstanceApiImpl implements ActorInstanceApi {

    @Resource
    private ActorInstanceService actorInstanceService;
    @Resource
    private SceneMapper sceneMapper;
    @Resource
    private ActorService actorService;
    @Resource
    private ActorInstanceComponentMapper actorInstanceComponentMapper;
    @Resource
    private ActorRenderConfigValidator actorRenderConfigValidator;

    @Override
    public CommonResult<ActorInstanceSimpleRespDTO> getActorInstance(Long id) {
        return success(convert(actorInstanceService.getActorInstance(id)));
    }

    @Override
    public CommonResult<List<ActorInstanceSimpleRespDTO>> getActorInstances(List<Long> ids) {
        return success(actorInstanceService.getActorInstances(ids).stream().map(this::convert).toList());
    }

    @Override
    public CommonResult<List<ActorInstanceSimpleRespDTO>> getActorInstancesByScene(Long sceneId, String keyword) {
        return success(actorInstanceService.getActorInstanceSimpleList(sceneId, keyword).stream().map(this::convert).toList());
    }

    @Override
    public CommonResult<ActorInstanceRuntimeRespDTO> getActorInstanceRuntime(Long id) {
        ActorInstanceDO instance = actorInstanceService.getActorInstance(id);
        return success(convertRuntime(instance));
    }

    @Override
    public CommonResult<Boolean> updateActorInstanceTransform(Long id, ActorInstanceTransformUpdateReqDTO reqDTO) {
        actorInstanceService.updateActorInstanceTransform(id, buildTransform(reqDTO));
        return success(true);
    }

    private ActorInstanceSimpleRespDTO convert(ActorInstanceDO instance) {
        return BeanUtils.toBean(instance, ActorInstanceSimpleRespDTO.class);
    }

    private ActorInstanceRuntimeRespDTO convertRuntime(ActorInstanceDO instance) {
        ActorInstanceRuntimeRespDTO dto = new ActorInstanceRuntimeRespDTO();
        dto.setId(instance.getId());
        dto.setSceneId(instance.getSceneId());
        dto.setActorCode(instance.getActorCode());
        dto.setInstanceCode(instance.getInstanceCode());
        dto.setInstanceName(instance.getInstanceName());
        dto.setInstanceStatus(instance.getInstanceStatus());
        dto.setVisibleFlag(instance.getVisibleFlag());
        dto.setLayerKeys(instance.getLayerKeys());
        dto.setMetadataJson(instance.getMetadataJson());

        SceneDO scene = sceneMapper.selectById(instance.getSceneId());
        dto.setSceneCode(scene == null ? null : scene.getSceneCode());

        fillTransform(dto, instance.getTransform());
        fillRenderConfig(dto, instance);
        return dto;
    }

    private void fillTransform(ActorInstanceRuntimeRespDTO dto, Transform transform) {
        Vector3 location = transform != null && transform.getLocation() != null ? transform.getLocation() : new Vector3(0D, 0D, 0D);
        Rotator rotation = transform != null && transform.getRotation() != null ? transform.getRotation() : new Rotator();
        Vector3 scale = transform != null && transform.getScale() != null ? transform.getScale() : new Vector3(1D, 1D, 1D);

        dto.setPositionX(defaultNumber(location.getX(), 0D));
        dto.setPositionY(defaultNumber(location.getY(), 0D));
        dto.setPositionZ(defaultNumber(location.getZ(), 0D));
        dto.setRotationX(defaultNumber(rotation.getPitch(), 0D));
        dto.setRotationY(defaultNumber(rotation.getYaw(), 0D));
        dto.setRotationZ(defaultNumber(rotation.getRoll(), 0D));
        dto.setScaleX(defaultNumber(scale.getX(), 1D));
        dto.setScaleY(defaultNumber(scale.getY(), 1D));
        dto.setScaleZ(defaultNumber(scale.getZ(), 1D));
    }

    private void fillRenderConfig(ActorInstanceRuntimeRespDTO dto, ActorInstanceDO instance) {
        List<ActorInstanceComponentDO> components = actorInstanceComponentMapper.selectListByActorInstanceId(instance.getId());
        ActorRenderConfig config = components.stream()
                .sorted(Comparator.comparing(ActorInstanceComponentDO::getSortNo, Comparator.nullsLast(Integer::compareTo)))
                .map(actorRenderConfigValidator::resolve)
                .filter(item -> item != null && !item.isEmpty())
                .findFirst()
                .orElseGet(() -> resolveFromActorMetadata(instance.getActorCode()));

        if (config == null || config.isEmpty()) {
            return;
        }
        dto.setRenderType(config.getRenderType());
        dto.setModelUrl(config.isSplat() ? config.getSplatUrl() : config.getModelUrl());
    }

    private ActorRenderConfig resolveFromActorMetadata(String actorCode) {
        ActorDO actor = actorService.getActorByCode(actorCode);
        if (actor == null || actor.getMetadataJson() == null || actor.getMetadataJson().isBlank()) {
            return null;
        }
        ActorInstanceComponentDO fakeComponent = new ActorInstanceComponentDO();
        fakeComponent.setComponentCode(actorCode + "-metadata");
        fakeComponent.setComponentTypeName("StaticMeshComponent");
        fakeComponent.setMetadataJson(actor.getMetadataJson());
        return actorRenderConfigValidator.resolve(fakeComponent);
    }

    private Transform buildTransform(ActorInstanceTransformUpdateReqDTO reqDTO) {
        Transform transform = new Transform();
        transform.setLocation(new Vector3(defaultNumber(reqDTO.getPositionX(), 0D), defaultNumber(reqDTO.getPositionY(), 0D), defaultNumber(reqDTO.getPositionZ(), 0D)));
        Rotator rotator = new Rotator();
        rotator.setPitch(defaultNumber(reqDTO.getRotationX(), 0D));
        rotator.setYaw(defaultNumber(reqDTO.getRotationY(), 0D));
        rotator.setRoll(defaultNumber(reqDTO.getRotationZ(), 0D));
        transform.setRotation(rotator);
        transform.setScale(new Vector3(defaultNumber(reqDTO.getScaleX(), 1D), defaultNumber(reqDTO.getScaleY(), 1D), defaultNumber(reqDTO.getScaleZ(), 1D)));
        return transform;
    }

    private Double defaultNumber(Double value, Double defaultValue) {
        return value == null ? defaultValue : value;
    }
}

package cn.iocoder.yudao.module.twin.service;

import cn.iocoder.yudao.module.twin.api.dto.TwinMappingDO;
import cn.iocoder.yudao.module.twin.controller.admin.vo.TwinMappingBindReqVO;
import cn.iocoder.yudao.module.twin.controller.admin.vo.TwinMappingRespVO;
import cn.iocoder.yudao.module.twin.controller.admin.vo.TwinMappingUnbindReqVO;
import cn.iocoder.yudao.module.twin.controller.admin.vo.TwinSceneMappingOverviewRespVO;
import cn.iocoder.yudao.module.twin.dal.dataobject.TwinMappingHistoryDO;
import cn.iocoder.yudao.module.twin.dal.mysql.TwinMappingHistoryMapper;
import cn.iocoder.yudao.module.twin.dal.mysql.TwinMappingMapper;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.iocoder.yudao.module.scene.platform.api.ActorInstanceApi;
import cn.iocoder.yudao.module.scene.platform.api.dto.ActorInstanceSimpleRespDTO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class TwinMappingServiceImpl implements TwinMappingService {

    private static final Integer ACTIVE = 1;
    private static final Integer INACTIVE = 0;
    private static final Integer FACILITY_ACTOR_MAPPING_TYPE = 1;
    private static final Integer ONE_TO_ONE_PRIMARY = 1;

    private static final String ENTITY_TYPE_FACILITY = "facility";

    @Resource
    private TwinMappingMapper twinMappingMapper;
    @Resource
    private TwinMappingHistoryMapper twinMappingHistoryMapper;
    @Resource
    private EntityRpcApi entityRpcApi;
    @Resource
    private ActorInstanceApi actorInstanceApi;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TwinMappingRespVO bind(TwinMappingBindReqVO reqVO) {
        EntityRespDTO facility = requireFacility(reqVO.getFacilityId());
        ActorInstanceSimpleRespDTO actorInstance = requireActorInstance(reqVO.getActorInstanceId());

        TwinMappingDO facilityOccupied = twinMappingMapper.selectActiveByFacilityId(reqVO.getFacilityId());
        TwinMappingDO actorOccupied = twinMappingMapper.selectActiveByActorInstanceId(reqVO.getActorInstanceId());
        boolean forceRebind = Boolean.TRUE.equals(reqVO.getForceRebind());

        if (facilityOccupied != null && !Objects.equals(facilityOccupied.getActorInstanceId(), reqVO.getActorInstanceId())) {
            if (!forceRebind) {
                throw new ServiceException(400, "该设施已绑定其他 ActorInstance，请确认是否换绑");
            }
            deactivate(facilityOccupied, "REBIND_SOURCE_FACILITY", reqVO.getRemark());
        }
        if (actorOccupied != null && !Objects.equals(actorOccupied.getFacilityId(), reqVO.getFacilityId())) {
            if (!forceRebind) {
                throw new ServiceException(400, "该 ActorInstance 已绑定其他设施，请确认是否换绑");
            }
            deactivate(actorOccupied, "REBIND_SOURCE_ACTOR", reqVO.getRemark());
        }

        TwinMappingDO current = twinMappingMapper.selectActiveByFacilityId(reqVO.getFacilityId());
        if (current != null && Objects.equals(current.getActorInstanceId(), reqVO.getActorInstanceId())) {
            return buildResp(current, facility, actorInstance);
        }

        TwinMappingDO mapping = TwinMappingDO.builder()
                .mappingCode(generateMappingCode())
                .mappingType(FACILITY_ACTOR_MAPPING_TYPE)
                .relationMode(ONE_TO_ONE_PRIMARY)
                .facilityId(reqVO.getFacilityId())
                .actorInstanceId(reqVO.getActorInstanceId())
                .sceneId(reqVO.getSceneId() != null ? reqVO.getSceneId() : actorInstance.getSceneId())
                .sceneCode(reqVO.getSceneCode())
                .bindSource(reqVO.getBindSource())
                .status(ACTIVE)
                .version(0)
                .remark(reqVO.getRemark())
                .extJson(new HashMap<>())
                .deleted(Boolean.FALSE)
                .build();
        twinMappingMapper.insert(mapping);
        recordHistory(mapping, current == null && actorOccupied == null ? "BIND" : "REBIND", reqVO.getRemark());
        log.info("Twin bind success, facilityId={}, actorInstanceId={}, mappingId={}", mapping.getFacilityId(), mapping.getActorInstanceId(), mapping.getId());
        return buildResp(mapping, facility, actorInstance);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbind(TwinMappingUnbindReqVO reqVO) {
        TwinMappingDO mapping = resolveActiveMapping(reqVO);
        if (mapping == null) {
            return;
        }
        deactivate(mapping, "UNBIND", reqVO.getRemark());
        log.info("Twin unbind success, mappingId={}", mapping.getId());
    }

    @Override
    public TwinMappingRespVO getByFacilityId(Long facilityId) {
        TwinMappingDO mapping = twinMappingMapper.selectActiveByFacilityId(facilityId);
        return mapping == null ? null : buildResp(mapping, requireFacility(mapping.getFacilityId()), requireActorInstance(mapping.getActorInstanceId()));
    }

    @Override
    public TwinMappingRespVO getByActorInstanceId(Long actorInstanceId) {
        TwinMappingDO mapping = twinMappingMapper.selectActiveByActorInstanceId(actorInstanceId);
        return mapping == null ? null : buildResp(mapping, requireFacility(mapping.getFacilityId()), requireActorInstance(mapping.getActorInstanceId()));
    }

    @Override
    public TwinSceneMappingOverviewRespVO getSceneOverview(Long sceneId, String sceneCode) {
        List<TwinMappingDO> mappings = sceneId != null
                ? twinMappingMapper.selectActiveBySceneId(sceneId)
                : twinMappingMapper.selectActiveBySceneCode(sceneCode);
        Map<Long, EntityRespDTO> facilityMap = listFacilities(mappings.stream().map(TwinMappingDO::getFacilityId).toList());
        Map<Long, ActorInstanceSimpleRespDTO> actorMap = listActorInstances(mappings.stream().map(TwinMappingDO::getActorInstanceId).toList());

        TwinSceneMappingOverviewRespVO overview = new TwinSceneMappingOverviewRespVO();
        overview.setSceneId(sceneId != null ? sceneId : mappings.stream().map(TwinMappingDO::getSceneId).filter(Objects::nonNull).findFirst().orElse(null));
        overview.setSceneCode(sceneCode != null ? sceneCode : mappings.stream().map(TwinMappingDO::getSceneCode).filter(Objects::nonNull).findFirst().orElse(null));
        overview.setTotalMappings(mappings.size());
        overview.setMappings(mappings.stream()
                .map(item -> buildResp(item, facilityMap.get(item.getFacilityId()), actorMap.get(item.getActorInstanceId())))
                .toList());
        return overview;
    }

    private TwinMappingDO resolveActiveMapping(TwinMappingUnbindReqVO reqVO) {
        if (reqVO.getMappingId() != null) {
            return twinMappingMapper.selectActiveById(reqVO.getMappingId());
        }
        if (reqVO.getFacilityId() != null) {
            return twinMappingMapper.selectActiveByFacilityId(reqVO.getFacilityId());
        }
        if (reqVO.getActorInstanceId() != null) {
            return twinMappingMapper.selectActiveByActorInstanceId(reqVO.getActorInstanceId());
        }
        throw new ServiceException(400, "解绑请求至少需要 mappingId/facilityId/actorInstanceId 之一");
    }

    private void deactivate(TwinMappingDO mapping, String operationType, String remark) {
        mapping.setStatus(INACTIVE);
        mapping.setRemark(remark);
        twinMappingMapper.updateById(mapping);
        recordHistory(mapping, operationType, remark);
    }

    private void recordHistory(TwinMappingDO mapping, String operationType, String remark) {
        TwinMappingHistoryDO history = new TwinMappingHistoryDO();
        history.setMappingId(mapping.getId());
        history.setOperationType(operationType);
        history.setMappingType(mapping.getMappingType());
        history.setRelationMode(mapping.getRelationMode());
        history.setFacilityId(mapping.getFacilityId());
        history.setActorInstanceId(mapping.getActorInstanceId());
        history.setSceneId(mapping.getSceneId());
        history.setSceneCode(mapping.getSceneCode());
        history.setBindSource(mapping.getBindSource());
        history.setOperationRemark(remark);
        history.setOccurredAt(LocalDateTime.now());
        history.setSnapshotJson(Map.of(
                "mappingCode", mapping.getMappingCode(),
                "status", mapping.getStatus(),
                "remark", remark == null ? "" : remark
        ));
        history.setDeleted(Boolean.FALSE);
        twinMappingHistoryMapper.insert(history);
    }

    private TwinMappingRespVO buildResp(TwinMappingDO mapping, EntityRespDTO facility, ActorInstanceSimpleRespDTO actorInstance) {
        TwinMappingRespVO vo = new TwinMappingRespVO();
        vo.setId(mapping.getId());
        vo.setMappingCode(mapping.getMappingCode());
        vo.setMappingType(mapping.getMappingType());
        vo.setRelationMode(mapping.getRelationMode());
        vo.setFacilityId(mapping.getFacilityId());
        vo.setActorInstanceId(mapping.getActorInstanceId());
        vo.setSceneId(mapping.getSceneId());
        vo.setSceneCode(mapping.getSceneCode());
        vo.setBindSource(mapping.getBindSource());
        vo.setStatus(mapping.getStatus());
        vo.setVersion(mapping.getVersion());
        vo.setRemark(mapping.getRemark());
        vo.setExtJson(mapping.getExtJson());
        vo.setCreateTime(mapping.getCreateTime());
        vo.setUpdateTime(mapping.getUpdateTime());
        vo.setFacility(facility);
        vo.setActorInstance(actorInstance);
        return vo;
    }

    private EntityRespDTO requireFacility(Long facilityId) {
        CommonResult<EntityRespDTO> result = entityRpcApi.getEntity(facilityId, ENTITY_TYPE_FACILITY);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw new ServiceException(400, "设施不存在: " + facilityId);
        }
        return result.getData();
    }

    private ActorInstanceSimpleRespDTO requireActorInstance(Long actorInstanceId) {
        CommonResult<ActorInstanceSimpleRespDTO> result = actorInstanceApi.getActorInstance(actorInstanceId);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw new ServiceException(400, "ActorInstance 不存在: " + actorInstanceId);
        }
        return result.getData();
    }

    private Map<Long, EntityRespDTO> listFacilities(List<Long> ids) {
        CommonResult<List<EntityRespDTO>> result = entityRpcApi.listEntitiesByIds(ids, ENTITY_TYPE_FACILITY);
        List<EntityRespDTO> data = result == null || !result.isSuccess() || result.getData() == null ? List.of() : result.getData();
        return data.stream().collect(Collectors.toMap(EntityRespDTO::getId, Function.identity(), (a, b) -> a));
    }

    private Map<Long, ActorInstanceSimpleRespDTO> listActorInstances(List<Long> ids) {
        CommonResult<List<ActorInstanceSimpleRespDTO>> result = actorInstanceApi.getActorInstances(ids);
        List<ActorInstanceSimpleRespDTO> data = result == null || !result.isSuccess() || result.getData() == null ? List.of() : result.getData();
        return data.stream().collect(Collectors.toMap(ActorInstanceSimpleRespDTO::getId, Function.identity(), (a, b) -> a));
    }

    private String generateMappingCode() {
        return "TM-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}

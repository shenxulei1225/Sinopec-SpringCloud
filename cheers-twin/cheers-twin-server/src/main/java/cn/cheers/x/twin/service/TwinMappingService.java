package cn.cheers.x.twin.service;

import cn.cheers.x.twin.controller.admin.vo.TwinEquipmentBindReqVO;
import cn.cheers.x.twin.controller.admin.vo.TwinEquipmentUnbindReqVO;
import cn.cheers.x.twin.controller.admin.vo.TwinMappingBindReqVO;
import cn.cheers.x.twin.controller.admin.vo.TwinMappingRespVO;
import cn.cheers.x.twin.controller.admin.vo.TwinMappingUnbindReqVO;
import cn.cheers.x.twin.controller.admin.vo.TwinSceneMappingOverviewRespVO;

public interface TwinMappingService {

    TwinMappingRespVO bind(TwinMappingBindReqVO reqVO);

    void unbind(TwinMappingUnbindReqVO reqVO);

    TwinMappingRespVO getByFacilityId(Long facilityId);

    TwinMappingRespVO getByActorInstanceId(Long actorInstanceId);

    TwinSceneMappingOverviewRespVO getSceneOverview(Long sceneId, String sceneCode);

    TwinMappingRespVO bindEquipment(TwinEquipmentBindReqVO reqVO);

    void unbindEquipment(TwinEquipmentUnbindReqVO reqVO);

    TwinMappingRespVO getEquipmentByActorInstanceId(Long actorInstanceId);

    TwinMappingRespVO getEquipmentByEntity(Long facilityId, Long entityId, String entityTypeCode);
}

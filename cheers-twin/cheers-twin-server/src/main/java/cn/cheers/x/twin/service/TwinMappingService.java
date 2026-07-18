package cn.cheers.x.twin.service;

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
}

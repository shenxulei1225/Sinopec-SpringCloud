package cn.iocoder.yudao.module.twin.service;

import cn.iocoder.yudao.module.twin.controller.admin.vo.TwinMappingBindReqVO;
import cn.iocoder.yudao.module.twin.controller.admin.vo.TwinMappingRespVO;
import cn.iocoder.yudao.module.twin.controller.admin.vo.TwinMappingUnbindReqVO;
import cn.iocoder.yudao.module.twin.controller.admin.vo.TwinSceneMappingOverviewRespVO;

public interface TwinMappingService {

    TwinMappingRespVO bind(TwinMappingBindReqVO reqVO);

    void unbind(TwinMappingUnbindReqVO reqVO);

    TwinMappingRespVO getByFacilityId(Long facilityId);

    TwinMappingRespVO getByActorInstanceId(Long actorInstanceId);

    TwinSceneMappingOverviewRespVO getSceneOverview(Long sceneId, String sceneCode);
}

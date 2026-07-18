package cn.iocoder.yudao.module.scene.platform.service.scene;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneLayerRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneLayerSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceDO;

import java.util.List;

public interface SceneLayerService {

    List<SceneLayerRespVO> getSceneLayers(String sceneCode);

    SceneLayerRespVO getSceneLayer(String sceneCode, String layerKey);

    Long createSceneLayer(String sceneCode, SceneLayerSaveReqVO reqVO);

    void updateSceneLayer(String sceneCode, String layerKey, SceneLayerSaveReqVO reqVO);

    void deleteSceneLayer(String sceneCode, String layerKey);

    List<ActorInstanceDO> getLayerInstances(String sceneCode, String layerKey);

    PageResult<ActorInstanceDO> getLayerInstancesPaginated(String sceneCode, String layerKey, int pageNo, int pageSize);
}
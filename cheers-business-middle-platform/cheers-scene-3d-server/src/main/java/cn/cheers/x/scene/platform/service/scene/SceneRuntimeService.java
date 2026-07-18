package cn.cheers.x.scene.platform.service.scene;

import cn.cheers.x.scene.platform.controller.admin.scene.vo.ActorRuntimeRespVO;
import cn.cheers.x.scene.platform.model.Transform;

import java.util.List;

public interface SceneRuntimeService {

    void updateActorRuntime(String sceneCode, Long instanceId, String instanceCode, Transform transform, String status, String actorCategory);

    ActorRuntimeRespVO getActorRuntime(String sceneCode, String instanceCode);

    List<ActorRuntimeRespVO> getAllActorsRuntime(String sceneCode);

    void removeActorRuntime(String sceneCode, String instanceCode);

    void clearSceneRuntime(String sceneCode);

    void updateSceneOnlineCount(String sceneCode, int delta);

    int getSceneOnlineCount(String sceneCode);
}

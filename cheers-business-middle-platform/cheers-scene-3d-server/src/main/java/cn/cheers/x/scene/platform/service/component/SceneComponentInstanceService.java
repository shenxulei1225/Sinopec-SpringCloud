package cn.cheers.x.scene.platform.service.component;

import cn.cheers.x.scene.platform.dal.dataobject.component.SceneComponentInstanceDO;

import java.util.List;

public interface SceneComponentInstanceService {

    List<SceneComponentInstanceDO> getSceneComponentInstanceList();

    SceneComponentInstanceDO getSceneComponentInstance(Long id);

    Long createSceneComponentInstance(SceneComponentInstanceDO sceneComponentInstanceDO);

    void updateSceneComponentInstance(Long id, SceneComponentInstanceDO sceneComponentInstanceDO);

    void disableSceneComponentInstance(Long id);
}

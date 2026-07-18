package cn.cheers.x.scene.platform.service.component.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.scene.platform.dal.dataobject.component.SceneComponentInstanceDO;
import cn.cheers.x.scene.platform.dal.mysql.component.SceneComponentInstanceMapper;
import cn.cheers.x.scene.platform.service.component.SceneComponentInstanceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class SceneComponentInstanceServiceImpl implements SceneComponentInstanceService {

    @Resource
    private SceneComponentInstanceMapper sceneComponentInstanceMapper;

    @Override
    public List<SceneComponentInstanceDO> getSceneComponentInstanceList() {
        return sceneComponentInstanceMapper.selectList();
    }

    @Override
    public SceneComponentInstanceDO getSceneComponentInstance(Long id) {
        SceneComponentInstanceDO sceneComponentInstanceDO = sceneComponentInstanceMapper.selectById(id);
        if (sceneComponentInstanceDO == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "场景组件挂载不存在");
        }
        return sceneComponentInstanceDO;
    }

    @Override
    public Long createSceneComponentInstance(SceneComponentInstanceDO sceneComponentInstanceDO) {
        sceneComponentInstanceMapper.insert(sceneComponentInstanceDO);
        return sceneComponentInstanceDO.getId();
    }

    @Override
    public void updateSceneComponentInstance(Long id, SceneComponentInstanceDO sceneComponentInstanceDO) {
        SceneComponentInstanceDO db = getSceneComponentInstance(id);
        sceneComponentInstanceDO.setId(db.getId());
        sceneComponentInstanceMapper.updateById(sceneComponentInstanceDO);
    }

    @Override
    public void disableSceneComponentInstance(Long id) {
        sceneComponentInstanceMapper.deleteById(id);
    }
}

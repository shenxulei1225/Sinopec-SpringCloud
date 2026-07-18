package cn.cheers.x.scene.platform.service.library.scene.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.scene.platform.controller.admin.scene.vo.SceneRespVO;
import cn.cheers.x.scene.platform.controller.admin.scene.vo.SceneSaveReqVO;
import cn.cheers.x.scene.platform.controller.admin.scene.vo.SceneCopyReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.scene.SceneDO;
import cn.cheers.x.scene.platform.dal.mysql.scene.SceneMapper;
import cn.cheers.x.scene.platform.service.library.scene.SceneLibraryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class SceneLibraryServiceImpl implements SceneLibraryService {

    @Resource
    private SceneMapper sceneMapper;

    @Override
    public List<SceneRespVO> getSceneList() {
        return BeanUtils.toBean(sceneMapper.selectList(), SceneRespVO.class);
    }

    @Override
    public SceneRespVO getScene(Long id) {
        return BeanUtils.toBean(getRequiredScene(id), SceneRespVO.class);
    }

    @Override
    public Long createScene(SceneSaveReqVO reqVO) {
        SceneDO scene = BeanUtils.toBean(reqVO, SceneDO.class);
        scene.setStatus(1);
        sceneMapper.insert(scene);
        return scene.getId();
    }

    @Override
    public void updateScene(Long id, SceneSaveReqVO reqVO) {
        SceneDO scene = getRequiredScene(id);
        BeanUtils.copyProperties(reqVO, scene);
        sceneMapper.updateById(scene);
    }

    @Override
    public void disableScene(Long id) {
        SceneDO scene = getRequiredScene(id);
        scene.setStatus(0);
        sceneMapper.updateById(scene);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyScene(Long id, SceneCopyReqVO reqVO) {
        SceneDO source = getRequiredScene(id);
        SceneDO target = BeanUtils.toBean(source, SceneDO.class);
        target.setId(null);
        target.setSceneCode(reqVO.getTargetSceneCode());
        target.setSceneName(reqVO.getTargetSceneName());
        target.setStatus(1);
        sceneMapper.insert(target);
        return target.getId();
    }

    private SceneDO getRequiredScene(Long id) {
        SceneDO scene = sceneMapper.selectById(id);
        if (scene == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "场景不存在");
        }
        return scene;
    }
}

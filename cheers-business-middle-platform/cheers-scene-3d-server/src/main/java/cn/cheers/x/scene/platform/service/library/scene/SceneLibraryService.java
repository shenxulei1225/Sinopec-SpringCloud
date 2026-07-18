package cn.cheers.x.scene.platform.service.library.scene;

import cn.cheers.x.scene.platform.controller.admin.scene.vo.SceneRespVO;
import cn.cheers.x.scene.platform.controller.admin.scene.vo.SceneSaveReqVO;
import cn.cheers.x.scene.platform.controller.admin.scene.vo.SceneCopyReqVO;

import java.util.List;

public interface SceneLibraryService {

    List<SceneRespVO> getSceneList();

    SceneRespVO getScene(Long id);

    Long createScene(SceneSaveReqVO reqVO);

    void updateScene(Long id, SceneSaveReqVO reqVO);

    void disableScene(Long id);

    Long copyScene(Long id, SceneCopyReqVO reqVO);
}

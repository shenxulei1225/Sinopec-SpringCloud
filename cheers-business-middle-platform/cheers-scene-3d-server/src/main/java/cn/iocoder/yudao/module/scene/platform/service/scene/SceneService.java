package cn.iocoder.yudao.module.scene.platform.service.scene;



import cn.cheers.x.framework.common.pojo.PageResult;

import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.ScenePageReqVO;

import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneRespVO;

import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneSaveReqVO;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneDO;



import java.util.List;



public interface SceneService {



    /**

     * 创建场景

     */

    Long createScene(SceneSaveReqVO reqVO);



    /**

     * 更新场景

     */

    void updateScene(SceneSaveReqVO reqVO);



    /**

     * 删除场景

     */

    void deleteScene(Long id);



    /**

     * 按编码删除场景

     */

    void deleteSceneByCode(String sceneCode);



    /**

     * 获取场景详情

     */

    SceneRespVO getScene(Long id);



    /**

     * 按编码获取场景

     */

    SceneRespVO getSceneByCode(String sceneCode);



    /**

     * 获取场景列表

     */

    List<SceneRespVO> listScene(ScenePageReqVO reqVO);



    /**

     * 按状态获取场景列表

     */

    List<SceneRespVO> listSceneByStatus(Integer status);



    /**

     * 按项目编码获取场景列表

     */

    List<SceneRespVO> listSceneByProjectCode(String projectCode);



    /**

     * 按发布状态获取场景列表

     */

    List<SceneRespVO> listSceneByPublishStatus(String publishStatus);



    /**

     * 加载场景（合并 DB 定义 + Redis 运行时数据）

     */

    cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneLoadRespVO loadScene(String sceneCode);



    /**

     * 发布场景

     */

    void publishScene(String sceneCode);



    /**

     * 取消发布场景

     */

    void unpublishScene(String sceneCode);

}


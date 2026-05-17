package cn.iocoder.yudao.module.scene.platform.service.component;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.SceneComponentDO;

import java.util.List;

/**
 * 场景级全局组件服务
 * 
 * 管理场景级别的全局组件，如环境雾效、天气系统、触发器 Volume 等
 * 
 * @author Sinopec
 */
public interface SceneComponentService {

    /**
     * 获取场景的所有全局组件（返回 DO）
     * 
     * @param sceneId 场景 ID
     * @return 场景全局组件列表
     */
    List<SceneComponentDO> getSceneComponents(Long sceneId);

    /**
     * 获取场景的所有全局组件（返回 RespVO，用于前端加载场景）
     * 
     * @param sceneId 场景 ID
     * @return 场景全局组件列表（VO）
     */
    List<cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.SceneComponentRespVO> listComponents(Long sceneId);

    /**
     * 获取场景的指定类型全局组件
     * 
     * @param sceneId 场景 ID
     * @param componentType 组件类型 (POST_PROCESS_VOLUME, WEATHER_SYSTEM, TRIGGER_VOLUME)
     * @return 指定类型的场景全局组件
     */
    /**
     * 根据 ID 获取场景组件详情
     *
     * @param id 组件 ID
     * @return 场景组件详情
     */
    SceneComponentDO getSceneComponent(Long id);

    /**
     * 获取场景的指定类型全局组件
     *
     * @param sceneId       场景 ID
     * @param componentType 组件类型 (POST_PROCESS_VOLUME, WEATHER_SYSTEM, TRIGGER_VOLUME)
     * @return 指定类型的场景全局组件
     */
    SceneComponentDO getSceneComponentByType(Long sceneId, String componentType);

    /**
     * 创建场景全局组件
     * 
     * @param sceneComponentDO 场景全局组件 DO
     * @return 组件 ID
     */
    Long createSceneComponent(SceneComponentDO sceneComponentDO);

    /**
     * 更新场景全局组件
     * 
     * @param id 组件 ID
     * @param sceneComponentDO 场景全局组件 DO
     */
    void updateSceneComponent(Long id, SceneComponentDO sceneComponentDO);

    /**
     * 删除场景全局组件
     * 
     * @param id 组件 ID
     */
    void deleteSceneComponent(Long id);

    /**
     * 启用/禁用场景全局组件
     * 
     * @param id 组件 ID
     * @param enabled 是否启用
     */
    void setSceneComponentEnabled(Long id, Boolean enabled);

    /**
     * 获取场景组件类型枚举
     * 
     * @return 组件类型列表
     */
    List<String> getComponentTypes();
}

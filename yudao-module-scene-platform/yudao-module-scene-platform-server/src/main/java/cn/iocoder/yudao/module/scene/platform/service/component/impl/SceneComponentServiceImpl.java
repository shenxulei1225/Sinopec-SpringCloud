package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo.SceneComponentRespVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.SceneComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.SceneComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.SceneComponentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.Arrays;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

/**
 * 场景级全局组件服务实现
 * 
 * @author Sinopec
 */
@Service
@Validated
public class SceneComponentServiceImpl implements SceneComponentService {

    @Resource
    private SceneComponentMapper sceneComponentMapper;

    private static final List<String> COMPONENT_TYPES = Arrays.asList(
            "POST_PROCESS_VOLUME",
            "WEATHER_SYSTEM",
            "TRIGGER_VOLUME"
    );

    @Override
    public List<SceneComponentDO> getSceneComponents(Long sceneId) {
        return sceneComponentMapper.selectList(
                new LambdaQueryWrapper<SceneComponentDO>()
                        .eq(SceneComponentDO::getSceneId, sceneId)
        );
    }

    @Override
    public SceneComponentDO getSceneComponent(Long id) {
        return sceneComponentMapper.selectById(id);
    }

    @Override
    public SceneComponentDO getSceneComponentByType(Long sceneId, String componentType) {
        return sceneComponentMapper.selectOne(
                new LambdaQueryWrapper<SceneComponentDO>()
                        .eq(SceneComponentDO::getSceneId, sceneId)
                        .eq(SceneComponentDO::getComponentType, componentType)
                        .last("LIMIT 1")
        );
    }

    @Override
    public Long createSceneComponent(SceneComponentDO sceneComponentDO) {
        // 验证组件类型
        if (!COMPONENT_TYPES.contains(sceneComponentDO.getComponentType())) {
            throw exception(NOT_FOUND, "不支持的组件类型: " + sceneComponentDO.getComponentType());
        }
        
        // 设置默认值
        if (sceneComponentDO.getEnabled() == null) {
            sceneComponentDO.setEnabled(true);
        }
        if (sceneComponentDO.getVisible() == null) {
            sceneComponentDO.setVisible(true);
        }
        if (sceneComponentDO.getDisplayName() == null || sceneComponentDO.getDisplayName().isEmpty()) {
            sceneComponentDO.setDisplayName(buildDisplayName(sceneComponentDO.getComponentType()));
        }
        
        sceneComponentMapper.insert(sceneComponentDO);
        return sceneComponentDO.getId();
    }

    @Override
    public void updateSceneComponent(Long id, SceneComponentDO sceneComponentDO) {
        // 检查组件是否存在
        SceneComponentDO existing = sceneComponentMapper.selectById(id);
        if (existing == null) {
            throw exception(NOT_FOUND);
        }
        
        sceneComponentDO.setId(id);
        sceneComponentMapper.updateById(sceneComponentDO);
    }

    @Override
    public void deleteSceneComponent(Long id) {
        // 检查组件是否存在
        SceneComponentDO existing = sceneComponentMapper.selectById(id);
        if (existing == null) {
            throw exception(NOT_FOUND);
        }
        
        sceneComponentMapper.deleteById(id);
    }

    @Override
    public void setSceneComponentEnabled(Long id, Boolean enabled) {
        // 检查组件是否存在
        SceneComponentDO existing = sceneComponentMapper.selectById(id);
        if (existing == null) {
            throw exception(NOT_FOUND);
        }
        
        sceneComponentMapper.update(null,
                new LambdaUpdateWrapper<SceneComponentDO>()
                        .eq(SceneComponentDO::getId, id)
                        .set(SceneComponentDO::getEnabled, enabled)
        );
    }

    @Override
    public List<String> getComponentTypes() {
        return COMPONENT_TYPES;
    }

    @Override
    public List<SceneComponentRespVO> listComponents(Long sceneId) {
        List<SceneComponentDO> dos = getSceneComponents(sceneId);
        return SceneComponentRespVO.convert(dos);
    }

    /**
     * 根据组件类型生成默认显示名称
     */
    private String buildDisplayName(String componentType) {
        switch (componentType) {
            case "POST_PROCESS_VOLUME":
                return "环境雾效/后处理";
            case "WEATHER_SYSTEM":
                return "天气系统";
            case "TRIGGER_VOLUME":
                return "触发器 Volume";
            default:
                return "场景组件";
        }
    }
}

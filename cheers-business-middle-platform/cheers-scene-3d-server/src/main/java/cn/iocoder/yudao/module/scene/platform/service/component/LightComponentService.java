package cn.iocoder.yudao.module.scene.platform.service.component;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.LightComponentDO;

import java.util.List;

public interface LightComponentService {

    List<LightComponentDO> getLightComponentList();

    LightComponentDO getLightComponent(Long id);

    void updateLightComponent(Long id, LightComponentDO lightComponentDO);

    void updateLightComponentDefaults(Long id, LightComponentDO lightComponentDO);

    void updateLightComponentSchema(Long id, LightComponentDO lightComponentDO);
}

package cn.iocoder.yudao.module.scene.platform.service.component;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.WidgetComponentDO;

import java.util.List;

public interface WidgetComponentService {

    List<WidgetComponentDO> getWidgetComponentList();

    WidgetComponentDO getWidgetComponent(Long id);

    void updateWidgetComponent(Long id, WidgetComponentDO widgetComponentDO);

    void updateWidgetComponentDefaults(Long id, WidgetComponentDO widgetComponentDO);

    void updateWidgetComponentSchema(Long id, WidgetComponentDO widgetComponentDO);
}

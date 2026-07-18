package cn.cheers.x.scene.platform.service.component;

import cn.cheers.x.scene.platform.dal.dataobject.component.WidgetComponentDO;

import java.util.List;

public interface WidgetComponentService {

    List<WidgetComponentDO> getWidgetComponentList();

    WidgetComponentDO getWidgetComponent(Long id);

    void updateWidgetComponent(Long id, WidgetComponentDO widgetComponentDO);

    void updateWidgetComponentDefaults(Long id, WidgetComponentDO widgetComponentDO);

    void updateWidgetComponentSchema(Long id, WidgetComponentDO widgetComponentDO);
}

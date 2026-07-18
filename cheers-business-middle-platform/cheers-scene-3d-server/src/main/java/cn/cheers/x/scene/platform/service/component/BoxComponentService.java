package cn.cheers.x.scene.platform.service.component;

import cn.cheers.x.scene.platform.dal.dataobject.component.BoxComponentDO;

import java.util.List;

public interface BoxComponentService {

    List<BoxComponentDO> getBoxComponentList();

    BoxComponentDO getBoxComponent(Long id);

    void updateBoxComponent(Long id, BoxComponentDO boxComponentDO);

    void updateBoxComponentDefaults(Long id, BoxComponentDO boxComponentDO);

    void updateBoxComponentSchema(Long id, BoxComponentDO boxComponentDO);
}

package cn.iocoder.yudao.module.scene.platform.service.component;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.SplineComponentDO;

import java.util.List;

public interface SplineComponentService {

    List<SplineComponentDO> getSplineComponentList();

    SplineComponentDO getSplineComponent(Long id);

    void updateSplineComponent(Long id, SplineComponentDO splineComponentDO);

    void updateSplineComponentDefaults(Long id, SplineComponentDO splineComponentDO);

    void updateSplineComponentSchema(Long id, SplineComponentDO splineComponentDO);
}

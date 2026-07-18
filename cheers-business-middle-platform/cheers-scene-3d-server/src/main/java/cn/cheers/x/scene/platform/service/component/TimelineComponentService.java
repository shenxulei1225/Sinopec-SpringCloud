package cn.cheers.x.scene.platform.service.component;

import cn.cheers.x.scene.platform.dal.dataobject.component.TimelineComponentDO;

import java.util.List;

public interface TimelineComponentService {

    List<TimelineComponentDO> getTimelineComponentList();

    TimelineComponentDO getTimelineComponent(Long id);

    void updateTimelineComponent(Long id, TimelineComponentDO timelineComponentDO);

    void updateTimelineComponentDefaults(Long id, TimelineComponentDO timelineComponentDO);

    void updateTimelineComponentSchema(Long id, TimelineComponentDO timelineComponentDO);
}

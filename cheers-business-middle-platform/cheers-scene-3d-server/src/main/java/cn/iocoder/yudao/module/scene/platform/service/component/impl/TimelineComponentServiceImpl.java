package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.TimelineComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.TimelineComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.TimelineComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class TimelineComponentServiceImpl implements TimelineComponentService {

    @Resource
    private TimelineComponentMapper timelineComponentMapper;

    @Override
    public List<TimelineComponentDO> getTimelineComponentList() {
        return timelineComponentMapper.selectList();
    }

    @Override
    public TimelineComponentDO getTimelineComponent(Long id) {
        TimelineComponentDO item = timelineComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Timeline 组件不存在");
        }
        return item;
    }

    @Override
    public void updateTimelineComponent(Long id, TimelineComponentDO timelineComponentDO) {
        TimelineComponentDO db = getTimelineComponent(id);
        BeanUtils.copyProperties(timelineComponentDO, db);
        timelineComponentMapper.updateById(db);
    }

    @Override
    public void updateTimelineComponentDefaults(Long id, TimelineComponentDO timelineComponentDO) {
        TimelineComponentDO db = getTimelineComponent(id);
        db.setTimelineConfigJson(timelineComponentDO.getTimelineConfigJson());
        db.setAutoPlay(timelineComponentDO.getAutoPlay());
        db.setLoop(timelineComponentDO.getLoop());
        db.setMetadataJson(timelineComponentDO.getMetadataJson());
        timelineComponentMapper.updateById(db);
    }

    @Override
    public void updateTimelineComponentSchema(Long id, TimelineComponentDO timelineComponentDO) {
        TimelineComponentDO db = getTimelineComponent(id);
        db.setMetadataJson(timelineComponentDO.getMetadataJson());
        timelineComponentMapper.updateById(db);
    }
}

package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.WidgetComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.WidgetComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.WidgetComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class WidgetComponentServiceImpl implements WidgetComponentService {

    @Resource
    private WidgetComponentMapper widgetComponentMapper;

    @Override
    public List<WidgetComponentDO> getWidgetComponentList() {
        return widgetComponentMapper.selectList();
    }

    @Override
    public WidgetComponentDO getWidgetComponent(Long id) {
        WidgetComponentDO item = widgetComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Widget 组件不存在");
        }
        return item;
    }

    @Override
    public void updateWidgetComponent(Long id, WidgetComponentDO widgetComponentDO) {
        WidgetComponentDO db = getWidgetComponent(id);
        BeanUtils.copyProperties(widgetComponentDO, db);
        widgetComponentMapper.updateById(db);
    }

    @Override
    public void updateWidgetComponentDefaults(Long id, WidgetComponentDO widgetComponentDO) {
        WidgetComponentDO db = getWidgetComponent(id);
        db.setWidgetCode(widgetComponentDO.getWidgetCode());
        db.setUiConfigJson(widgetComponentDO.getUiConfigJson());
        db.setDrawAtDesiredSize(widgetComponentDO.getDrawAtDesiredSize());
        db.setReceiveHardwareInput(widgetComponentDO.getReceiveHardwareInput());
        db.setMetadataJson(widgetComponentDO.getMetadataJson());
        widgetComponentMapper.updateById(db);
    }

    @Override
    public void updateWidgetComponentSchema(Long id, WidgetComponentDO widgetComponentDO) {
        WidgetComponentDO db = getWidgetComponent(id);
        db.setMetadataJson(widgetComponentDO.getMetadataJson());
        widgetComponentMapper.updateById(db);
    }
}

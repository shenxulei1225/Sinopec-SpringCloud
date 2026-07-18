package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.BoxComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.BoxComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.BoxComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class BoxComponentServiceImpl implements BoxComponentService {

    @Resource
    private BoxComponentMapper boxComponentMapper;

    @Override
    public List<BoxComponentDO> getBoxComponentList() {
        return boxComponentMapper.selectList();
    }

    @Override
    public BoxComponentDO getBoxComponent(Long id) {
        BoxComponentDO item = boxComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Box 组件不存在");
        }
        return item;
    }

    @Override
    public void updateBoxComponent(Long id, BoxComponentDO boxComponentDO) {
        BoxComponentDO db = getBoxComponent(id);
        BeanUtils.copyProperties(boxComponentDO, db);
        boxComponentMapper.updateById(db);
    }

    @Override
    public void updateBoxComponentDefaults(Long id, BoxComponentDO boxComponentDO) {
        BoxComponentDO db = getBoxComponent(id);
        db.setBoxExtentX(boxComponentDO.getBoxExtentX());
        db.setBoxExtentY(boxComponentDO.getBoxExtentY());
        db.setBoxExtentZ(boxComponentDO.getBoxExtentZ());
        db.setCollisionEnabled(boxComponentDO.getCollisionEnabled());
        db.setGenerateOverlapEvents(boxComponentDO.getGenerateOverlapEvents());
        db.setMetadataJson(boxComponentDO.getMetadataJson());
        boxComponentMapper.updateById(db);
    }

    @Override
    public void updateBoxComponentSchema(Long id, BoxComponentDO boxComponentDO) {
        BoxComponentDO db = getBoxComponent(id);
        db.setMetadataJson(boxComponentDO.getMetadataJson());
        boxComponentMapper.updateById(db);
    }
}

package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.ChildActorComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.ChildActorComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.ChildActorComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class ChildActorComponentServiceImpl implements ChildActorComponentService {

    @Resource
    private ChildActorComponentMapper childActorComponentMapper;

    @Override
    public List<ChildActorComponentDO> getChildActorComponentList() {
        return childActorComponentMapper.selectList();
    }

    @Override
    public ChildActorComponentDO getChildActorComponent(Long id) {
        ChildActorComponentDO item = childActorComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Child Actor 组件不存在");
        }
        return item;
    }

    @Override
    public void updateChildActorComponent(Long id, ChildActorComponentDO childActorComponentDO) {
        ChildActorComponentDO db = getChildActorComponent(id);
        BeanUtils.copyProperties(childActorComponentDO, db);
        childActorComponentMapper.updateById(db);
    }

    @Override
    public void updateChildActorComponentDefaults(Long id, ChildActorComponentDO childActorComponentDO) {
        ChildActorComponentDO db = getChildActorComponent(id);
        db.setChildActorCode(childActorComponentDO.getChildActorCode());
        db.setInheritTransform(childActorComponentDO.getInheritTransform());
        db.setChildActorEditable(childActorComponentDO.getChildActorEditable());
        db.setMetadataJson(childActorComponentDO.getMetadataJson());
        childActorComponentMapper.updateById(db);
    }

    @Override
    public void updateChildActorComponentSchema(Long id, ChildActorComponentDO childActorComponentDO) {
        ChildActorComponentDO db = getChildActorComponent(id);
        db.setMetadataJson(childActorComponentDO.getMetadataJson());
        childActorComponentMapper.updateById(db);
    }
}

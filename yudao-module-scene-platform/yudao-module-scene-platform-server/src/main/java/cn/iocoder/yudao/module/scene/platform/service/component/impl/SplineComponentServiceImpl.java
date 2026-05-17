package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.SplineComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.SplineComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.SplineComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class SplineComponentServiceImpl implements SplineComponentService {

    @Resource
    private SplineComponentMapper splineComponentMapper;

    @Override
    public List<SplineComponentDO> getSplineComponentList() {
        return splineComponentMapper.selectList();
    }

    @Override
    public SplineComponentDO getSplineComponent(Long id) {
        SplineComponentDO item = splineComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Spline 组件不存在");
        }
        return item;
    }

    @Override
    public void updateSplineComponent(Long id, SplineComponentDO splineComponentDO) {
        SplineComponentDO db = getSplineComponent(id);
        BeanUtils.copyProperties(splineComponentDO, db);
        splineComponentMapper.updateById(db);
    }

    @Override
    public void updateSplineComponentDefaults(Long id, SplineComponentDO splineComponentDO) {
        SplineComponentDO db = getSplineComponent(id);
        db.setSplinePointsJson(splineComponentDO.getSplinePointsJson());
        db.setClosedLoop(splineComponentDO.getClosedLoop());
        db.setMobility(splineComponentDO.getMobility());
        db.setMetadataJson(splineComponentDO.getMetadataJson());
        splineComponentMapper.updateById(db);
    }

    @Override
    public void updateSplineComponentSchema(Long id, SplineComponentDO splineComponentDO) {
        SplineComponentDO db = getSplineComponent(id);
        db.setMetadataJson(splineComponentDO.getMetadataJson());
        splineComponentMapper.updateById(db);
    }
}

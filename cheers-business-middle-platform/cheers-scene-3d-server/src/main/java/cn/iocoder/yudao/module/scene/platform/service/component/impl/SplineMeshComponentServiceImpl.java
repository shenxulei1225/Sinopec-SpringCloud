package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.SplineMeshComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.SplineMeshComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.SplineMeshComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class SplineMeshComponentServiceImpl implements SplineMeshComponentService {

    @Resource
    private SplineMeshComponentMapper splineMeshComponentMapper;

    @Override
    public List<SplineMeshComponentDO> getSplineMeshComponentList() {
        return splineMeshComponentMapper.selectList();
    }

    @Override
    public SplineMeshComponentDO getSplineMeshComponent(Long id) {
        SplineMeshComponentDO item = splineMeshComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Spline Mesh 组件不存在");
        }
        return item;
    }

    @Override
    public void updateSplineMeshComponent(Long id, SplineMeshComponentDO splineMeshComponentDO) {
        SplineMeshComponentDO db = getSplineMeshComponent(id);
        BeanUtils.copyProperties(splineMeshComponentDO, db);
        splineMeshComponentMapper.updateById(db);
    }

    @Override
    public void updateSplineMeshComponentDefaults(Long id, SplineMeshComponentDO splineMeshComponentDO) {
        SplineMeshComponentDO db = getSplineMeshComponent(id);
        db.setSourceSplineCode(splineMeshComponentDO.getSourceSplineCode());
        db.setMeshCode(splineMeshComponentDO.getMeshCode());
        db.setCastShadow(splineMeshComponentDO.getCastShadow());
        db.setReceiveShadow(splineMeshComponentDO.getReceiveShadow());
        db.setMetadataJson(splineMeshComponentDO.getMetadataJson());
        splineMeshComponentMapper.updateById(db);
    }

    @Override
    public void updateSplineMeshComponentSchema(Long id, SplineMeshComponentDO splineMeshComponentDO) {
        SplineMeshComponentDO db = getSplineMeshComponent(id);
        db.setMetadataJson(splineMeshComponentDO.getMetadataJson());
        splineMeshComponentMapper.updateById(db);
    }
}

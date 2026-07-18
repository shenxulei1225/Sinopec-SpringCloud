package cn.cheers.x.scene.platform.service.component.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.scene.platform.dal.dataobject.component.MeshComponentDO;
import cn.cheers.x.scene.platform.dal.mysql.component.MeshComponentMapper;
import cn.cheers.x.scene.platform.service.component.MeshComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class MeshComponentServiceImpl implements MeshComponentService {

    @Resource
    private MeshComponentMapper meshComponentMapper;

    @Override
    public List<MeshComponentDO> getMeshComponentList() {
        return meshComponentMapper.selectList();
    }

    @Override
    public MeshComponentDO getMeshComponent(Long id) {
        MeshComponentDO item = meshComponentMapper.selectById(id);
        if (item == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "Mesh 组件不存在");
        }
        return item;
    }

    @Override
    public void updateMeshComponent(Long id, MeshComponentDO meshComponentDO) {
        MeshComponentDO db = getMeshComponent(id);
        BeanUtils.copyProperties(meshComponentDO, db);
        meshComponentMapper.updateById(db);
    }

    @Override
    public void updateMeshComponentDefaults(Long id, MeshComponentDO meshComponentDO) {
        MeshComponentDO db = getMeshComponent(id);
        db.setMeshDescription(meshComponentDO.getMeshDescription());
        db.setMaterialsJson(meshComponentDO.getMaterialsJson());
        db.setMetadataJson(meshComponentDO.getMetadataJson());
        meshComponentMapper.updateById(db);
    }

    @Override
    public void updateMeshComponentSchema(Long id, MeshComponentDO meshComponentDO) {
        MeshComponentDO db = getMeshComponent(id);
        db.setMetadataJson(meshComponentDO.getMetadataJson());
        meshComponentMapper.updateById(db);
    }
}

package cn.cheers.x.module.platformresource.service.component;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentCreateReqVO;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentRespVO;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentUpdateReqVO;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentDO;
import cn.cheers.x.module.platformresource.dal.mysql.component.ComponentMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platformresource.enums.ErrorCodeConstants.*;

@Service
public class ComponentServiceImpl implements ComponentService {

    @Resource
    private ComponentMapper componentMapper;

    @Override
    public Map<String, ComponentRespVO> getEnabledComponents() {
        List<ComponentDO> list = componentMapper.selectEnabledList();
        Map<String, ComponentRespVO> result = new LinkedHashMap<>();
        for (ComponentDO row : list) {
            result.put(row.getComponentCode(), convertToRespVO(row));
        }
        return result;
    }

    @Override
    public ComponentRespVO getComponent(String componentCode) {
        ComponentDO row = componentMapper.selectByComponentCode(componentCode);
        return row == null ? null : convertToRespVO(row);
    }

    @Override
    public List<ComponentRespVO> getComponentList() {
        return componentMapper.selectAllList().stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createComponent(ComponentCreateReqVO reqVO) {
        if (componentMapper.existsByComponentCode(reqVO.getComponentCode())) {
            throw exception(COMPONENT_CODE_DUPLICATE);
        }
        ComponentDO row = new ComponentDO();
        row.setComponentCode(reqVO.getComponentCode());
        row.setType(reqVO.getType());
        row.setName(reqVO.getName());
        row.setIcon(reqVO.getIcon());
        row.setStatus(reqVO.getStatus() != null ? reqVO.getStatus() : 1);
        row.setSort(reqVO.getSort() != null ? reqVO.getSort() : 0);
        row.setDescription(reqVO.getDescription());
        componentMapper.insert(row);
        return row.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateComponent(String componentCode, ComponentUpdateReqVO reqVO) {
        ComponentDO row = componentMapper.selectByComponentCode(componentCode);
        if (row == null) {
            throw exception(COMPONENT_NOT_EXISTS);
        }
        if (reqVO.getType() != null) {
            row.setType(reqVO.getType());
        }
        if (reqVO.getName() != null) {
            row.setName(reqVO.getName());
        }
        if (reqVO.getIcon() != null) {
            row.setIcon(reqVO.getIcon());
        }
        if (reqVO.getStatus() != null) {
            row.setStatus(reqVO.getStatus());
        }
        if (reqVO.getSort() != null) {
            row.setSort(reqVO.getSort());
        }
        if (reqVO.getDescription() != null) {
            row.setDescription(reqVO.getDescription());
        }
        componentMapper.updateById(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComponent(String componentCode) {
        ComponentDO row = componentMapper.selectByComponentCode(componentCode);
        if (row == null) {
            throw exception(COMPONENT_NOT_EXISTS);
        }
        componentMapper.deleteById(row.getId());
    }

    private ComponentRespVO convertToRespVO(ComponentDO row) {
        ComponentRespVO vo = BeanUtils.toBean(row, ComponentRespVO.class);
        vo.setId(row.getId());
        vo.setComponentCode(row.getComponentCode());
        vo.setName(row.getName());
        return vo;
    }
}

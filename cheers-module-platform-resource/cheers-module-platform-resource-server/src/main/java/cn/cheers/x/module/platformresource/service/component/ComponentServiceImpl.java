package cn.cheers.x.module.platformresource.service.component;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentCreateReqVO;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentRespVO;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentUpdateReqVO;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentDO;
import cn.cheers.x.module.platformresource.dal.mysql.component.ComponentMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Map<String, ComponentRespVO> getEnabledComponents() {
        List<ComponentDO> list = componentMapper.selectEnabledList();
        Map<String, ComponentRespVO> result = new LinkedHashMap<>();
        for (ComponentDO row : list) {
            result.put(row.getKey(), convertToRespVO(row));
        }
        return result;
    }

    @Override
    public ComponentRespVO getComponent(String key) {
        ComponentDO row = componentMapper.selectByKey(key);
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
        if (componentMapper.existsByKey(reqVO.getKey())) {
            throw exception(COMPONENT_KEY_DUPLICATE);
        }
        ComponentDO row = new ComponentDO();
        row.setKey(reqVO.getKey());
        row.setType(reqVO.getType());
        row.setName(reqVO.getName());
        row.setIcon(reqVO.getIcon());
        row.setProps(toJson(reqVO.getProps()));
        row.setDataConfig(toJson(reqVO.getDataConfig()));
        row.setApiConfig(toJson(reqVO.getApiConfig()));
        row.setUiConfig(toJson(reqVO.getUiConfig()));
        row.setStatus(reqVO.getStatus() != null ? reqVO.getStatus() : 1);
        row.setSort(reqVO.getSort() != null ? reqVO.getSort() : 0);
        row.setDescription(reqVO.getDescription());
        componentMapper.insert(row);
        return row.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateComponent(String key, ComponentUpdateReqVO reqVO) {
        ComponentDO row = componentMapper.selectByKey(key);
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
        if (reqVO.getProps() != null) {
            row.setProps(toJson(reqVO.getProps()));
        }
        if (reqVO.getDataConfig() != null) {
            row.setDataConfig(toJson(reqVO.getDataConfig()));
        }
        if (reqVO.getApiConfig() != null) {
            row.setApiConfig(toJson(reqVO.getApiConfig()));
        }
        if (reqVO.getUiConfig() != null) {
            row.setUiConfig(toJson(reqVO.getUiConfig()));
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
    public void deleteComponent(String key) {
        ComponentDO row = componentMapper.selectByKey(key);
        if (row == null) {
            throw exception(COMPONENT_NOT_EXISTS);
        }
        componentMapper.deleteById(row.getId());
    }

    private ComponentRespVO convertToRespVO(ComponentDO row) {
        ComponentRespVO vo = BeanUtils.toBean(row, ComponentRespVO.class);
        vo.setId(row.getId());
        vo.setComponentCode(row.getKey());
        vo.setKey(row.getKey());
        vo.setName(row.getName());
        vo.setProps(parseJson(row.getProps(), Object.class));
        vo.setDataConfig(parseJson(row.getDataConfig(), ComponentRespVO.EndpointConfig.class));
        vo.setUiConfig(parseJson(row.getUiConfig(), new TypeReference<Map<String, Object>>() {}));
        vo.setApiConfig(parseJson(row.getApiConfig(),
                new TypeReference<Map<String, ComponentRespVO.EndpointConfig>>() {}));
        return vo;
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    private <T> T parseJson(String json, Class<T> clazz) {
        if (StrUtil.isEmpty(json)) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

    private <T> T parseJson(String json, TypeReference<T> typeRef) {
        if (StrUtil.isEmpty(json)) return null;
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (Exception e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }
}
package cn.cheers.x.module.platformresource.service.component;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.*;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentDO;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentPropsDO;
import cn.cheers.x.module.platformresource.dal.mysql.component.ComponentMapper;
import cn.cheers.x.module.platformresource.dal.mysql.component.ComponentPropsMapper;
import cn.cheers.x.module.platformresource.service.component.ComponentDataSource.Normalized;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platformresource.enums.ErrorCodeConstants.*;

@Service
public class ComponentPropsServiceImpl implements ComponentPropsService {

    @Resource
    private ComponentPropsMapper componentPropsMapper;

    @Resource
    private ComponentMapper componentMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public ComponentPropsRespVO getProps(Long propsId) {
        return convertToRespVO(requireProps(propsId));
    }

    @Override
    public List<ComponentPropsRespVO> getPropsBatch(List<Long> propsIds) {
        if (propsIds == null || propsIds.isEmpty()) {
            return List.of();
        }
        List<Long> uniqueIds = propsIds.stream()
                .filter(Objects::nonNull)
                .filter(id -> id > 0)
                .distinct()
                .collect(Collectors.toList());
        if (uniqueIds.isEmpty()) {
            return List.of();
        }
        return componentPropsMapper.selectListByIds(uniqueIds).stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ComponentPropsRespVO> getPropsList(ComponentPropsListReqVO reqVO) {
        Boolean onlyEnabled = reqVO.getOnlyEnabled() != null ? reqVO.getOnlyEnabled() : Boolean.TRUE;
        Normalized filter = reqVO.resolveDataSourceFilter();
        List<ComponentPropsDO> rows = componentPropsMapper.selectList(
                reqVO.getComponentCode(),
                reqVO.getIsTemplate(),
                filter.isPresent() ? filter : null,
                onlyEnabled);
        return rows.stream().map(this::convertToRespVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTemplate(ComponentPropsCreateTemplateReqVO reqVO) {
        ComponentDO component = requireComponentByCode(reqVO.getComponentCode());
        ComponentPropsDO row = new ComponentPropsDO();
        row.setIsTemplate(true);
        row.setComponentId(component.getId());
        row.setComponentCode(component.getComponentCode());
        row.setTemplateId(null);
        row.setSchemaVersion(reqVO.getSchemaVersion());
        row.setPropsOverride(null);
        row.setName(reqVO.getName());
        row.setStatus(reqVO.getStatus() != null ? reqVO.getStatus() : 1);
        row.setSort(reqVO.getSort() != null ? reqVO.getSort() : 0);
        row.setDescription(reqVO.getDescription());
        row.setProps(toJson(stripCapabilityFields(reqVO.getProps())));
        applyDataSourceFromRequest(row, reqVO.getDataSource());
        componentPropsMapper.insert(row);
        return row.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInstance(ComponentPropsCreateInstanceReqVO reqVO) {
        ComponentPropsDO template = requireTemplate(reqVO.getTemplateId());
        ComponentPropsDO row = new ComponentPropsDO();
        row.setIsTemplate(false);
        row.setComponentId(template.getComponentId());
        row.setComponentCode(template.getComponentCode());
        row.setTemplateId(template.getId());
        row.setSchemaVersion(template.getSchemaVersion());
        row.setProps("{}");
        row.setPropsOverride("{}");
        row.setName(StrUtil.isNotBlank(reqVO.getName()) ? reqVO.getName() : template.getName());
        row.setStatus(1);
        row.setSort(0);
        row.setDescription(reqVO.getDescription());
        Normalized binding = ComponentDataSource.fromVo(reqVO.getDataSource());
        if (binding.isPresent()) {
            ComponentDataSource.applyToRow(row, binding);
        } else {
            row.setDataSource(template.getDataSource());
        }
        componentPropsMapper.insert(row);
        return row.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProps(Long propsId, ComponentPropsSaveReqVO reqVO) {
        ComponentPropsDO row = requireProps(propsId);
        applyMetadata(row, reqVO);
        if (Boolean.TRUE.equals(row.getIsTemplate())) {
            if (reqVO.getProps() != null) {
                row.setProps(toJson(stripCapabilityFields(reqVO.getProps())));
            } else if (!hasMetadataPatch(reqVO) && !hasDataSourcePatch(reqVO)) {
                throw exception(COMPONENT_PROPS_SAVE_PROPS_REQUIRED);
            }
            if (hasDataSourcePatch(reqVO)) {
                applyDataSourceFromRequest(row, reqVO.getDataSource());
            }
            if (StrUtil.isNotBlank(reqVO.getSchemaVersion())) {
                row.setSchemaVersion(reqVO.getSchemaVersion());
            }
            componentPropsMapper.updateById(row);
            return;
        }
        if (row.getTemplateId() == null) {
            throw exception(COMPONENT_PROPS_INSTANCE_MISSING_TEMPLATE);
        }
        if (hasDataSourcePatch(reqVO)) {
            applyDataSourceFromRequest(row, reqVO.getDataSource());
        }
        if (reqVO.getPropsOverride() != null) {
            row.setPropsOverride(toJson(stripCapabilityFields(reqVO.getPropsOverride())));
        } else if (!hasMetadataPatch(reqVO) && !hasDataSourcePatch(reqVO)) {
            throw exception(COMPONENT_PROPS_SAVE_OVERRIDE_REQUIRED);
        }
        componentPropsMapper.updateById(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProps(Long propsId) {
        ComponentPropsDO row = requireProps(propsId);
        if (Boolean.TRUE.equals(row.getIsTemplate())
                && componentPropsMapper.selectCountByTemplateId(propsId) > 0) {
            throw exception(COMPONENT_PROPS_TEMPLATE_HAS_INSTANCES);
        }
        componentPropsMapper.deleteById(propsId);
    }

    private boolean hasDataSourcePatch(ComponentPropsSaveReqVO reqVO) {
        return reqVO.getDataSource() != null;
    }

    private void applyDataSourceFromRequest(ComponentPropsDO row, ComponentDataSourceVO vo) {
        Normalized binding = ComponentDataSource.fromVo(vo);
        if (binding.isPresent()) {
            ComponentDataSource.applyToRow(row, binding);
        }
    }

    private boolean hasMetadataPatch(ComponentPropsSaveReqVO reqVO) {
        return reqVO.getName() != null
                || reqVO.getStatus() != null
                || reqVO.getSort() != null
                || reqVO.getDescription() != null
                || StrUtil.isNotBlank(reqVO.getSchemaVersion())
                || hasDataSourcePatch(reqVO);
    }

    private void applyMetadata(ComponentPropsDO row, ComponentPropsSaveReqVO reqVO) {
        if (reqVO.getName() != null) {
            row.setName(reqVO.getName());
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
    }

    private ComponentPropsDO requireProps(Long propsId) {
        ComponentPropsDO row = componentPropsMapper.selectByIdNotDeleted(propsId);
        if (row == null) {
            throw exception(COMPONENT_PROPS_NOT_EXISTS);
        }
        return row;
    }

    private ComponentPropsDO requireTemplate(Long templateId) {
        ComponentPropsDO row = requireProps(templateId);
        if (!Boolean.TRUE.equals(row.getIsTemplate())) {
            throw exception(COMPONENT_PROPS_NOT_TEMPLATE);
        }
        return row;
    }

    private ComponentDO requireComponentByCode(String componentCode) {
        ComponentDO component = componentMapper.selectByComponentCode(componentCode);
        if (component == null) {
            throw exception(COMPONENT_NOT_EXISTS);
        }
        return component;
    }

    private ComponentPropsRespVO convertToRespVO(ComponentPropsDO row) {
        ComponentPropsRespVO vo = new ComponentPropsRespVO();
        vo.setPropsId(row.getId());
        vo.setIsTemplate(row.getIsTemplate());
        vo.setComponentId(row.getComponentId());
        vo.setComponentCode(row.getComponentCode());
        vo.setDataSource(ComponentDataSource.toVo(ComponentDataSource.fromRow(row)));
        vo.setTemplateId(row.getTemplateId());
        vo.setSchemaVersion(row.getSchemaVersion());
        vo.setProps(parseJsonMap(row.getProps()));
        vo.setPropsOverride(parseJsonMap(row.getPropsOverride()));
        vo.setName(row.getName());
        vo.setStatus(row.getStatus());
        vo.setSort(row.getSort());
        vo.setDescription(row.getDescription());
        return vo;
    }

    private Map<String, Object> stripCapabilityFields(Map<String, Object> props) {
        if (props == null || props.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, Object> out = new LinkedHashMap<>(props);
        out.remove("dataSource");
        Object filter = out.get("filter");
        if (filter instanceof Map<?, ?> filterMap) {
            Map<String, Object> nextFilter = new LinkedHashMap<>();
            filterMap.forEach((k, v) -> {
                if (!"resolvedFilters".equals(String.valueOf(k))) {
                    nextFilter.put(String.valueOf(k), v);
                }
            });
            out.put("filter", nextFilter);
        }
        return out;
    }

    private Map<String, Object> parseJsonMap(String json) {
        if (StrUtil.isBlank(json)) {
            return new LinkedHashMap<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
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
}

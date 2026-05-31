package cn.cheers.x.module.platformresource.service.component;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.*;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentDO;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentPropsDO;
import cn.cheers.x.module.platformresource.dal.mysql.component.ComponentMapper;
import cn.cheers.x.module.platformresource.dal.mysql.component.ComponentPropsMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
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
        ComponentPropsDO row = requireProps(propsId);
        return convertToRespVO(row);
    }

    @Override
    public List<ComponentPropsRespVO> getPropsList(ComponentPropsListReqVO reqVO) {
        Boolean onlyEnabled = reqVO.getOnlyEnabled() != null ? reqVO.getOnlyEnabled() : Boolean.TRUE;
        List<ComponentPropsDO> rows = componentPropsMapper.selectList(
                reqVO.getComponentCode(),
                reqVO.getIsTemplate(),
                reqVO.resolveDataSourceKeyFilter(),
                onlyEnabled);
        return rows.stream().map(this::convertToRespVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTemplate(ComponentPropsCreateTemplateReqVO reqVO) {
        String dataSourceKey = requireDataSourceKey(reqVO.resolveDataSourceKey(), reqVO.getPropsJson());
        ComponentDO component = requireComponentByCode(reqVO.getComponentCode());
        ComponentPropsDO row = new ComponentPropsDO();
        row.setIsTemplate(true);
        row.setComponentId(component.getId());
        row.setComponentCode(component.getKey());
        row.setTemplateId(null);
        row.setSchemaVersion(reqVO.getSchemaVersion());
        row.setPropsOverride(null);
        row.setName(reqVO.getName());
        row.setStatus(reqVO.getStatus() != null ? reqVO.getStatus() : 1);
        row.setSort(reqVO.getSort() != null ? reqVO.getSort() : 0);
        row.setDescription(reqVO.getDescription());
        ComponentPropsJsonSync.applyToRow(row, dataSourceKey, reqVO.getPropsJson());
        componentPropsMapper.insert(row);
        return row.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInstance(ComponentPropsCreateInstanceReqVO reqVO) {
        ComponentPropsDO template = requireTemplate(reqVO.getTemplateId());
        String dataSourceKey = StrUtil.isNotBlank(reqVO.resolveDataSourceKey())
                ? reqVO.resolveDataSourceKey()
                : template.getDataSourceKey();
        ComponentPropsDO row = new ComponentPropsDO();
        row.setIsTemplate(false);
        row.setComponentId(template.getComponentId());
        row.setComponentCode(template.getComponentCode());
        row.setDataSourceKey(dataSourceKey);
        row.setTemplateId(template.getId());
        row.setSchemaVersion(template.getSchemaVersion());
        row.setPropsJson("{}");
        row.setPropsOverride("{}");
        row.setName(StrUtil.isNotBlank(reqVO.getName()) ? reqVO.getName() : template.getName());
        row.setStatus(1);
        row.setSort(0);
        row.setDescription(reqVO.getDescription());
        componentPropsMapper.insert(row);
        return row.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveProps(Long propsId, ComponentPropsSaveReqVO reqVO) {
        ComponentPropsDO row = requireProps(propsId);
        applyMetadata(row, reqVO);
        if (Boolean.TRUE.equals(row.getIsTemplate())) {
            Map<String, Object> propsPatch = reqVO.getPropsJson();
            String dataSourceKey = ComponentPropsJsonSync.resolveDataSourceKey(
                    reqVO.resolveDataSourceKey(), propsPatch);
            if (propsPatch != null) {
                Map<String, Object> merged = ComponentPropsJsonSync.mergeJsonWithKey(
                        row.getPropsJson(), propsPatch, dataSourceKey);
                dataSourceKey = requireDataSourceKey(dataSourceKey, merged);
                row.setPropsJson(toJson(merged));
                row.setDataSourceKey(dataSourceKey);
            } else if (StrUtil.isNotBlank(dataSourceKey)) {
                Map<String, Object> merged = ComponentPropsJsonSync.mergeJsonWithKey(
                        row.getPropsJson(), null, dataSourceKey);
                row.setPropsJson(toJson(merged));
                row.setDataSourceKey(dataSourceKey);
            } else if (!hasMetadataPatch(reqVO)) {
                throw exception(COMPONENT_PROPS_SAVE_JSON_REQUIRED);
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
        if (StrUtil.isNotBlank(reqVO.resolveDataSourceKey())) {
            row.setDataSourceKey(reqVO.resolveDataSourceKey());
        }
        if (reqVO.getPropsOverride() != null) {
            row.setPropsOverride(toJson(reqVO.getPropsOverride()));
        } else if (!hasMetadataPatch(reqVO) && StrUtil.isBlank(reqVO.resolveDataSourceKey())) {
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

    private String requireDataSourceKey(String explicitKey, Map<String, Object> propsJson) {
        String key = ComponentPropsJsonSync.resolveDataSourceKey(explicitKey, propsJson);
        if (StrUtil.isBlank(key)) {
            throw exception(COMPONENT_PROPS_DATA_SOURCE_KEY_REQUIRED);
        }
        return key;
    }

    private boolean hasMetadataPatch(ComponentPropsSaveReqVO reqVO) {
        return reqVO.getName() != null
                || reqVO.getStatus() != null
                || reqVO.getSort() != null
                || reqVO.getDescription() != null
                || StrUtil.isNotBlank(reqVO.getSchemaVersion())
                || StrUtil.isNotBlank(reqVO.resolveDataSourceKey());
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
        ComponentDO component = componentMapper.selectByKey(componentCode);
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
        vo.setDataSourceKey(row.getDataSourceKey());
        vo.setTemplateId(row.getTemplateId());
        vo.setSchemaVersion(row.getSchemaVersion());
        vo.setPropsJson(parseJsonMap(row.getPropsJson()));
        vo.setPropsOverride(parseJsonMap(row.getPropsOverride()));
        vo.setName(row.getName());
        vo.setStatus(row.getStatus());
        vo.setSort(row.getSort());
        vo.setDescription(row.getDescription());
        ComponentPropsJsonSync.enrichRespVO(vo);
        return vo;
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

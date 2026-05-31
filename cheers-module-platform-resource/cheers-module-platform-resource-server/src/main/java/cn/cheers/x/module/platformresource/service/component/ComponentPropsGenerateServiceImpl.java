package cn.cheers.x.module.platformresource.service.component;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.capability.CapabilityContractApi;
import cn.cheers.x.module.dynamicbusiness.api.capability.dto.CapabilityInstanceSummaryDTO;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentPropsGenerateFromContractReqVO;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentDO;
import cn.cheers.x.module.platformresource.dal.dataobject.component.ComponentPropsDO;
import cn.cheers.x.module.platformresource.dal.mysql.component.ComponentMapper;
import cn.cheers.x.module.platformresource.dal.mysql.component.ComponentPropsMapper;
import cn.cheers.x.module.platformresource.service.component.contract.QueryContractPropsGenerator;
import cn.cheers.x.module.platformresource.service.component.contract.QueryContractPropsGenerator.ListGenerateOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.platformresource.enums.ErrorCodeConstants.*;

@Service
@Slf4j
public class ComponentPropsGenerateServiceImpl implements ComponentPropsGenerateService {

    @Resource
    private CapabilityContractApi capabilityContractApi;
    @Resource
    private ComponentMapper componentMapper;
    @Resource
    private ComponentPropsMapper componentPropsMapper;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public Map<String, Object> previewPropsFromContract(ComponentPropsGenerateFromContractReqVO reqVO) {
        Map<String, Object> contract = fetchContract(reqVO);
        return buildPropsJson(contract, reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long generateTemplateFromContract(ComponentPropsGenerateFromContractReqVO reqVO) {
        if (Boolean.TRUE.equals(reqVO.getPreviewOnly())) {
            throw exception(COMPONENT_PROPS_GENERATE_PREVIEW_ONLY);
        }
        Map<String, Object> contract = fetchContract(reqVO);
        Map<String, Object> propsJson = buildPropsJson(contract, reqVO);
        return upsertTemplate(reqVO, propsJson);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int seedAllSystemPropsTemplates() {
        rebuildCapabilitiesBeforeSeed();
        int count = 0;
        for (SystemComponentPropsSeedCatalog.SeedEntry entry : SystemComponentPropsSeedCatalog.all()) {
            count += seedOne(entry.dataSourceKey(), entry.componentCode(), entry.schemaVersion(),
                    entry.propsId(), entry.nameSuffix(), "full");
        }
        log.info("[seedAllSystemPropsTemplates] upserted {} templates", count);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int seedAllDynamicPropsTemplates() {
        capabilityContractApi.rebuildAllDynamicCapabilities();
        CommonResult<List<CapabilityInstanceSummaryDTO>> result =
                capabilityContractApi.listInstances(null, null);
        if (result == null || result.getData() == null) {
            return 0;
        }
        int count = 0;
        for (CapabilityInstanceSummaryDTO row : result.getData()) {
            String key = row.getInstanceKey();
            if (key == null || (!key.startsWith("dynamic-model:") && !key.startsWith("dynamic-entity:"))) {
                continue;
            }
            String label = StrUtil.blankToDefault(row.getLabel(), key);
            count += seedOne(key, "list", "list@1", null, label + " · 列表", "full");
            Map<String, Object> contract = fetchContract(key, true);
            if (QueryContractPropsGenerator.contractSupportsTreeView(contract)) {
                count += seedOne(key, "tree", "tree@1", null, label + " · 树", "full");
            }
        }
        log.info("[seedAllDynamicPropsTemplates] upserted {} templates", count);
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int seedAllCapabilityPropsTemplates() {
        int system = seedAllSystemPropsTemplates();
        int dynamic = seedAllDynamicPropsTemplates();
        return system + dynamic;
    }

    private int seedOne(String dataSourceKey, String componentCode, String schemaVersion,
                        Long propsId, String name, String variant) {
        ComponentPropsGenerateFromContractReqVO req = new ComponentPropsGenerateFromContractReqVO();
        req.setDataSourceKey(dataSourceKey);
        req.setComponentCode(componentCode);
        req.setSchemaVersion(schemaVersion);
        req.setPropsId(propsId);
        req.setName(name);
        req.setVariant(variant);
        req.setRebuildIfMissing(true);
        req.setPreviewOnly(false);
        generateTemplateFromContract(req);
        return 1;
    }

    private Map<String, Object> fetchContract(ComponentPropsGenerateFromContractReqVO reqVO) {
        return fetchContract(reqVO.resolveDataSourceKey(), reqVO.getRebuildIfMissing() == null
                || Boolean.TRUE.equals(reqVO.getRebuildIfMissing()));
    }

    private void rebuildCapabilitiesBeforeSeed() {
        capabilityContractApi.rebuildAllSystemCapabilities();
        log.info("[seed] rebuilt all system capability contracts");
    }

    private Map<String, Object> fetchContract(String instanceKey, boolean rebuildIfMissing) {
        CommonResult<Map<String, Object>> result =
                capabilityContractApi.getContract(instanceKey, rebuildIfMissing);
        if (result == null || result.getData() == null) {
            throw exception(COMPONENT_PROPS_CONTRACT_NOT_FOUND);
        }
        return result.getData();
    }

    private Map<String, Object> buildPropsJson(Map<String, Object> contract, ComponentPropsGenerateFromContractReqVO reqVO) {
        String dataSourceKey = reqVO.resolveDataSourceKey();
        String componentCode = reqVO.getComponentCode();
        if ("tree".equals(componentCode)) {
            return QueryContractPropsGenerator.generateTreeProps(contract, dataSourceKey);
        }
        if ("list".equals(componentCode)) {
            ListGenerateOptions options = "minimal".equalsIgnoreCase(reqVO.getVariant())
                    ? ListGenerateOptions.minimal()
                    : ListGenerateOptions.defaults();
            return QueryContractPropsGenerator.generateListProps(contract, dataSourceKey, options);
        }
        throw exception(COMPONENT_PROPS_UNSUPPORTED_COMPONENT);
    }

    private Long upsertTemplate(ComponentPropsGenerateFromContractReqVO reqVO, Map<String, Object> propsJson) {
        String dataSourceKey = reqVO.resolveDataSourceKey();
        ComponentDO component = requireComponentByCode(reqVO.getComponentCode());
        String name = StrUtil.blankToDefault(reqVO.getName(), dataSourceKey + " 模板");
        String schemaVersion = StrUtil.blankToDefault(reqVO.getSchemaVersion(),
                "tree".equals(reqVO.getComponentCode()) ? "tree@1" : "list@1");

        ComponentPropsDO existing = null;
        if (reqVO.getPropsId() != null) {
            existing = componentPropsMapper.selectByIdNotDeleted(reqVO.getPropsId());
        }
        if (existing == null) {
            existing = componentPropsMapper.selectTemplateByDataSourceKey(dataSourceKey, reqVO.getComponentCode());
        }

        if (existing == null) {
            ComponentPropsDO row = new ComponentPropsDO();
            if (reqVO.getPropsId() != null) {
                row.setId(reqVO.getPropsId());
            }
            row.setIsTemplate(true);
            row.setComponentId(component.getId());
            row.setComponentCode(component.getKey());
            row.setTemplateId(null);
            row.setSchemaVersion(schemaVersion);
            row.setPropsOverride(null);
            row.setName(name);
            row.setStatus(1);
            row.setSort(0);
            row.setDescription("由数据能力契约自动生成");
            ComponentPropsJsonSync.applyToRow(row, dataSourceKey, propsJson);
            componentPropsMapper.insert(row);
            return row.getId();
        }

        existing.setComponentId(component.getId());
        existing.setComponentCode(component.getKey());
        existing.setSchemaVersion(schemaVersion);
        existing.setPropsOverride(null);
        existing.setName(name);
        existing.setDescription("由数据能力契约自动生成");
        ComponentPropsJsonSync.applyToRow(existing, dataSourceKey, propsJson);
        componentPropsMapper.updateById(existing);
        return existing.getId();
    }

    private ComponentDO requireComponentByCode(String componentCode) {
        ComponentDO component = componentMapper.selectByKey(componentCode);
        if (component == null) {
            throw exception(COMPONENT_NOT_EXISTS);
        }
        return component;
    }
}

package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability.InstanceCapabilityRegistryDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype.BusinessTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.capability.InstanceCapabilityRegistryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelFieldAssignmentService;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CapabilityRegistryRebuildServiceImpl implements CapabilityRegistryRebuildService {

    @Resource
    private InstanceCapabilityRegistryMapper registryMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private BusinessTypeMapper businessTypeMapper;
    @Resource
    @Lazy
    private ModelFieldAssignmentService modelFieldAssignmentService;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildModelListCapability(String businessTypeCode) {
        if (businessTypeCode == null || businessTypeCode.isBlank()) {
            return;
        }
        BusinessTypeDO businessType = businessTypeMapper.selectByCode(businessTypeCode);
        ModelDO anchor = new ModelDO();
        anchor.setBusinessTypeCode(businessTypeCode);
        anchor.setName(businessType != null ? businessType.getName() : businessTypeCode);

        Map<String, Object> contract = CapabilityContractBuilder.buildModelListContract(anchor, businessType);
        upsertContract(contract, "dynamic-model", businessTypeCode, null, null,
                String.valueOf(contract.get("label")));
        log.info("[rebuildModelListCapability] businessTypeCode={}", businessTypeCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildEntityCapability(Long modelId) {
        if (modelId == null) {
            return;
        }
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            return;
        }
        BusinessTypeDO businessType = businessTypeMapper.selectByCode(model.getBusinessTypeCode());
        List<ModelFieldAssignmentRespVO> fields = modelFieldAssignmentService.getModelFields(modelId);
        Map<String, Object> contract = CapabilityContractBuilder.buildEntityContract(model, businessType, fields);
        upsertContract(contract, "dynamic-entity", model.getBusinessTypeCode(), modelId, null,
                String.valueOf(contract.get("label")));
        log.info("[rebuildEntityCapability] modelId={}", modelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildAfterModelFieldChange(Long modelId) {
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            return;
        }
        rebuildEntityCapability(modelId);
        rebuildModelListCapability(model.getBusinessTypeCode());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildAllForBusinessType(String businessTypeCode) {
        rebuildModelListCapability(businessTypeCode);
        List<ModelDO> models = modelMapper.selectByBusinessTypeCode(businessTypeCode);
        for (ModelDO model : models) {
            rebuildEntityCapability(model.getId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildSystemCapability(String resourceCode) {
        String code = resourceCode != null ? resourceCode.trim() : "";
        if (code.isBlank()) {
            throw new ServiceException(400, "System 资源编码不能为空");
        }
        SystemCapabilityResourceDef def = SystemCapabilityCatalog.find(code)
                .orElseThrow(() -> new ServiceException(400, "未注册的 System 资源: " + code
                        + "（instanceKey 应为 system:" + code + "）"));
        Map<String, Object> contract = SystemCapabilityContractBuilder.build(def);
        upsertContract(contract, "system", null, null, def.resourceCode(), String.valueOf(contract.get("label")));
        log.info("[rebuildSystemCapability] resourceCode={}", code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildAllSystemCapabilities() {
        for (SystemCapabilityResourceDef def : SystemCapabilityCatalog.all()) {
            rebuildSystemCapability(def.resourceCode());
        }
        log.info("[rebuildAllSystemCapabilities] count={}", SystemCapabilityCatalog.all().size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rebuildAllDynamicCapabilities() {
        List<BusinessTypeDO> types = businessTypeMapper.selectAllList();
        int count = 0;
        for (BusinessTypeDO type : types) {
            if (type == null || type.getCode() == null || type.getCode().isBlank()) {
                continue;
            }
            rebuildAllForBusinessType(type.getCode());
            count++;
        }
        log.info("[rebuildAllDynamicCapabilities] businessTypes={}", count);
    }

    @SuppressWarnings("unchecked")
    private void upsertContract(Map<String, Object> contract, String domain, String businessTypeCode,
            Long modelId, String resourceCode, String label) {
        String instanceKey = String.valueOf(contract.get("instanceKey"));
        InstanceCapabilityRegistryDO existing = registryMapper.selectByInstanceKey(instanceKey);
        int nextVersion = existing != null && existing.getVersion() != null ? existing.getVersion() + 1 : 1;
        contract.put("version", nextVersion);

        String contractJson;
        try {
            contractJson = objectMapper.writeValueAsString(contract);
        } catch (Exception e) {
            throw new IllegalStateException("序列化能力契约失败: " + instanceKey, e);
        }

        if (existing == null) {
            InstanceCapabilityRegistryDO row = InstanceCapabilityRegistryDO.builder()
                    .instanceKey(instanceKey)
                    .domain(domain)
                    .businessTypeCode(businessTypeCode)
                    .modelId(modelId)
                    .resourceCode(resourceCode)
                    .label(label)
                    .contractJson(contractJson)
                    .version(nextVersion)
                    .status(1)
                    .build();
            registryMapper.insert(row);
            return;
        }

        existing.setLabel(label);
        existing.setContractJson(contractJson);
        existing.setVersion(nextVersion);
        existing.setDomain(domain);
        existing.setBusinessTypeCode(businessTypeCode);
        existing.setModelId(modelId);
        existing.setResourceCode(resourceCode);
        registryMapper.updateById(existing);
    }

    Map<String, Object> readContractJson(String contractJson) {
        try {
            return objectMapper.readValue(contractJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("解析能力契约失败", e);
        }
    }
}

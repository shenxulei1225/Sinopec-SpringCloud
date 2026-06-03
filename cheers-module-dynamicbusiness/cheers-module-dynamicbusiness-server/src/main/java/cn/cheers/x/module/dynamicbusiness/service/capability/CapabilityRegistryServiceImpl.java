package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.ComponentCapabilityViewRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.InstanceCapabilitySummaryRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.capability.InstanceCapabilityRegistryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.capability.InstanceCapabilityRegistryMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CapabilityRegistryServiceImpl implements CapabilityRegistryService {

    @Resource
    private InstanceCapabilityRegistryMapper registryMapper;
    @Resource
    private CapabilityRegistryRebuildService rebuildService;
    @Resource
    private ObjectMapper objectMapper;

    @Override
    public List<InstanceCapabilitySummaryRespVO> listInstances(String businessTypeCode, String domain) {
        List<InstanceCapabilityRegistryDO> rows = registryMapper.selectSummaryList(businessTypeCode, domain);
        List<InstanceCapabilitySummaryRespVO> result = new ArrayList<>(rows.size());
        for (InstanceCapabilityRegistryDO row : rows) {
            result.add(toSummary(row));
        }
        return result;
    }

    @Override
    public Map<String, Object> getContract(String instanceKey) {
        String normalizedKey = normalizeInstanceKey(instanceKey);
        InstanceCapabilityRegistryDO row = registryMapper.selectByInstanceKey(normalizedKey);
        if (row == null) {
            throw new ServiceException(404, "实例能力不存在: " + normalizedKey);
        }
        return parseContract(row.getContractJson());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getFilters(String instanceKey) {
        Map<String, Object> contract = getContract(normalizeInstanceKey(instanceKey));
        Object filters = contract.get("filters");
        if (filters instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return List.of();
    }

    @Override
    public Map<String, Object> getContractOrRebuild(String instanceKey) {
        String normalizedKey = normalizeInstanceKey(instanceKey);
        InstanceCapabilityRegistryDO row = registryMapper.selectByInstanceKey(normalizedKey);
        if (row != null) {
            return parseContract(row.getContractJson());
        }
        rebuildFromInstanceKey(normalizedKey);
        row = registryMapper.selectByInstanceKey(normalizedKey);
        if (row == null) {
            throw new ServiceException(404, "实例能力不存在且无法重建: " + normalizedKey);
        }
        return parseContract(row.getContractJson());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getAsyncChecks(String instanceKey) {
        Map<String, Object> contract = getContract(normalizeInstanceKey(instanceKey));
        Object checks = contract.get("asyncChecks");
        if (checks instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return List.of();
    }

    @Override
    public ComponentCapabilityViewRespVO getComponentView(
            String instanceKey, String componentCode, boolean rebuildIfMissing) {
        String normalizedKey = normalizeInstanceKey(instanceKey);
        String normalizedComponent = CapabilityComponentViewBuilder.normalizeComponentCode(componentCode);
        Map<String, Object> contract = rebuildIfMissing
                ? getContractOrRebuild(normalizedKey)
                : getContract(normalizedKey);
        return CapabilityComponentViewBuilder.build(contract, normalizedKey, normalizedComponent);
    }

    private static String normalizeInstanceKey(String instanceKey) {
        if (instanceKey == null) {
            return null;
        }
        return instanceKey.trim().replace('：', ':');
    }

    private void rebuildFromInstanceKey(String instanceKey) {
        if (instanceKey == null || instanceKey.isBlank()) {
            throw new ServiceException(400, "instanceKey 不能为空");
        }
        if (instanceKey.startsWith("dynamic-model:")) {
            String businessTypeCode = instanceKey.substring("dynamic-model:".length()).trim();
            rebuildService.rebuildModelListCapability(businessTypeCode);
            return;
        }
        if (instanceKey.startsWith("dynamic-entity:")) {
            String[] parts = instanceKey.split(":");
            if (parts.length >= 3) {
                rebuildService.rebuildEntityCapability(Long.valueOf(parts[2].trim()));
                return;
            }
        }
        if (instanceKey.startsWith("system:")) {
            String resourceCode = instanceKey.substring("system:".length()).trim();
            rebuildService.rebuildSystemCapability(resourceCode);
            return;
        }
        throw new ServiceException(400,
                "无法识别的 instanceKey: " + instanceKey + "（支持前缀 dynamic-model: / dynamic-entity: / system:）");
    }

    private InstanceCapabilitySummaryRespVO toSummary(InstanceCapabilityRegistryDO row) {
        InstanceCapabilitySummaryRespVO vo = new InstanceCapabilitySummaryRespVO();
        vo.setInstanceKey(row.getInstanceKey());
        vo.setLabel(row.getLabel());
        vo.setDomain(row.getDomain());
        vo.setBusinessTypeCode(row.getBusinessTypeCode());
        vo.setModelId(row.getModelId());
        vo.setResourceCode(row.getResourceCode());
        vo.setVersion(row.getVersion());
        return vo;
    }

    private Map<String, Object> parseContract(String contractJson) {
        try {
            return objectMapper.readValue(contractJson, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new ServiceException(500, "解析能力契约失败");
        }
    }
}

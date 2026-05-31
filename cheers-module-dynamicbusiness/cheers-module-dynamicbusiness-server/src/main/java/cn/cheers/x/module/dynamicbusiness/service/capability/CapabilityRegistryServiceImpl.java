package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
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
        InstanceCapabilityRegistryDO row = registryMapper.selectByInstanceKey(instanceKey);
        if (row == null) {
            throw new ServiceException(404, "实例能力不存在: " + instanceKey);
        }
        return parseContract(row.getContractJson());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getFilters(String instanceKey) {
        Map<String, Object> contract = getContract(instanceKey);
        Object filters = contract.get("filters");
        if (filters instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return List.of();
    }

    @Override
    public Map<String, Object> getContractOrRebuild(String instanceKey) {
        InstanceCapabilityRegistryDO row = registryMapper.selectByInstanceKey(instanceKey);
        if (row != null) {
            return parseContract(row.getContractJson());
        }
        rebuildFromInstanceKey(instanceKey);
        row = registryMapper.selectByInstanceKey(instanceKey);
        if (row == null) {
            throw new ServiceException(404, "实例能力不存在且无法重建: " + instanceKey);
        }
        return parseContract(row.getContractJson());
    }

    private void rebuildFromInstanceKey(String instanceKey) {
        if (instanceKey == null || instanceKey.isBlank()) {
            throw new ServiceException(400, "instanceKey 不能为空");
        }
        if (instanceKey.startsWith("dynamic-model:")) {
            String businessTypeCode = instanceKey.substring("dynamic-model:".length());
            rebuildService.rebuildModelListCapability(businessTypeCode);
            return;
        }
        if (instanceKey.startsWith("dynamic-entity:")) {
            String[] parts = instanceKey.split(":");
            if (parts.length >= 3) {
                rebuildService.rebuildEntityCapability(Long.valueOf(parts[2]));
                return;
            }
        }
        if (instanceKey.startsWith("system:")) {
            String resourceCode = instanceKey.substring("system:".length());
            rebuildService.rebuildSystemCapability(resourceCode);
            return;
        }
        throw new ServiceException(400, "无法识别的 instanceKey: " + instanceKey);
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

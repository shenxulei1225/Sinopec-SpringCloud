package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.CapabilityFieldCheckRespVO;
import cn.cheers.x.module.dynamicbusiness.service.businesstype.BusinessTypeService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 字段异步校验：优先走契约 list 端点探测；业务类型 code 走专用 exists 接口语义。
 */
@Service
@Slf4j
public class CapabilityFieldCheckServiceImpl implements CapabilityFieldCheckService {

    @Resource
    private CapabilityRegistryService capabilityRegistryService;
    @Resource
    private CapabilityListProbeService capabilityListProbeService;
    @Resource
    private BusinessTypeService businessTypeService;

    @Override
    public CapabilityFieldCheckRespVO checkField(
            String instanceKey, String fieldKey, String value, Long excludeId) {
        if (StrUtil.isBlank(instanceKey) || StrUtil.isBlank(fieldKey)) {
            throw new ServiceException(400, "instanceKey 与 fieldKey 不能为空");
        }
        String trimmedValue = StrUtil.trim(value);
        if (StrUtil.isBlank(trimmedValue)) {
            return CapabilityFieldCheckRespVO.ok();
        }

        String normalizedKey = instanceKey.trim().replace('：', ':');
        if ("code".equals(fieldKey) && normalizedKey.startsWith("dynamic-model:")) {
            return checkBusinessTypeCode(trimmedValue, excludeId);
        }

        Map<String, Object> contract = capabilityRegistryService.getContractOrRebuild(normalizedKey);
        List<Map<String, Object>> asyncChecks = asyncChecksFromContract(contract);
        String message = resolveMessage(asyncChecks, fieldKey, fieldKey + "已存在");

        boolean available = capabilityListProbeService.isFieldValueAvailable(
                contract, fieldKey, trimmedValue, excludeId);
        return available ? CapabilityFieldCheckRespVO.ok() : CapabilityFieldCheckRespVO.fail(message);
    }

    private CapabilityFieldCheckRespVO checkBusinessTypeCode(String code, Long excludeId) {
        boolean exists = Boolean.TRUE.equals(businessTypeService.checkBusinessTypeExists(code));
        if (!exists) {
            return CapabilityFieldCheckRespVO.ok();
        }
        if (excludeId != null) {
            try {
                var current = businessTypeService.getByCode(code);
                if (current != null && excludeId.equals(current.getId())) {
                    return CapabilityFieldCheckRespVO.ok();
                }
            } catch (Exception ex) {
                log.debug("[checkField] ignore business type self compare: {}", ex.getMessage());
            }
        }
        return CapabilityFieldCheckRespVO.fail("业务类型编码已存在");
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> asyncChecksFromContract(Map<String, Object> contract) {
        Object checks = contract.get("asyncChecks");
        if (checks instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return List.of();
    }

    private static String resolveMessage(List<Map<String, Object>> asyncChecks, String fieldKey, String fallback) {
        for (Map<String, Object> check : asyncChecks) {
            if (fieldKey.equals(String.valueOf(check.get("fieldKey")))) {
                String message = String.valueOf(check.getOrDefault("message", fallback));
                return StrUtil.blankToDefault(message, fallback);
            }
        }
        return fallback;
    }
}

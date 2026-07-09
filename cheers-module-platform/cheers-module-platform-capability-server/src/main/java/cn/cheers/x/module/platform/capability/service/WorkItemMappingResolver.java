package cn.cheers.x.module.platform.capability.service;

import cn.cheers.x.module.platform.capability.api.dto.ResolveWorkItemsReqDTO;
import cn.cheers.x.module.platform.capability.dal.dataobject.MappingProfileDO;
import cn.cheers.x.module.platform.contract.ContractVersions;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import com.alibaba.fastjson2.JSON;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Component
public class WorkItemMappingResolver {

    WorkItemDTO resolve(MappingProfileDO profile, String entityTypeCode,
                        ResolveWorkItemsReqDTO.SourceInstanceInputDTO instance) {
        Map<String, String> fieldMappings = parseStringMap(profile.getFieldMappings());
        Map<String, Object> customFields = instance.getCustomFields() != null
                ? instance.getCustomFields() : Map.of();

        int duration = readInt(customFields, fieldMappings.get("durationEstimateMinutes"),
                profile.getDefaultDurationMinutes());
        int priority = readInt(customFields, fieldMappings.get("priority"),
                profile.getDefaultPriority());

        Map<String, Object> payload = new HashMap<>(customFields);
        String sourceInstanceId = instance.getSourceInstanceId();

        return WorkItemDTO.builder()
                .contractVersion(ContractVersions.MVP)
                .workId(sourceInstanceId)
                .entityTypeCode(entityTypeCode)
                .sourceModelCode(profile.getSourceModelCode())
                .sourceInstanceId(sourceInstanceId)
                .durationEstimateMinutes(Math.max(duration, 1))
                .priority(priority)
                .payload(payload)
                .build();
    }

    private static int readInt(Map<String, Object> customFields, String fieldCode, Integer fallback) {
        if (!StringUtils.hasText(fieldCode)) {
            return fallback != null ? fallback : 1;
        }
        Object value = customFields.get(fieldCode);
        if (value == null) {
            return fallback != null ? fallback : 1;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return fallback != null ? fallback : 1;
        }
    }

    private static Map<String, String> parseStringMap(String json) {
        if (!StringUtils.hasText(json)) {
            return Map.of();
        }
        return JSON.parseObject(json, new com.alibaba.fastjson2.TypeReference<Map<String, String>>() {
        });
    }
}

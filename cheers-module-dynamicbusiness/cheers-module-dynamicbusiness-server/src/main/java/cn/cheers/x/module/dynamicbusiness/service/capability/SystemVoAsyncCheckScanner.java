package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.hutool.core.util.StrUtil;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 从 SaveReqVO / 模型字段 assignment 扫描需要异步校验（唯一性等）的字段，生成契约 asyncChecks。
 */
final class SystemVoAsyncCheckScanner {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final Set<String> UNIQUE_FIELD_KEYS = Set.of(
            "name", "code", "username", "mobile", "email", "type", "mail", "title",
            "label", "signature", "clientid", "openid", "account", "templatecode");

    private SystemVoAsyncCheckScanner() {
    }

    static List<Map<String, Object>> scanSystemAsyncChecks(SystemCapabilityResourceDef def) {
        if (!SystemVoSchemaRegistry.supportsWrite(def.resourceCode())) {
            return List.of();
        }
        List<Map<String, Object>> writeFields = SystemVoSchemaScanner.scanWriteFields(def, "create");
        List<Map<String, Object>> checks = new ArrayList<>();
        for (Map<String, Object> field : writeFields) {
            String fieldKey = stringVal(field.get("fieldKey"));
            if (!isUniqueCandidate(fieldKey)) {
                continue;
            }
            checks.add(buildCheck(def.instanceKey(), fieldKey, stringVal(field.get("label"))));
        }
        return checks;
    }

    static List<Map<String, Object>> scanEntityAsyncChecks(
            String instanceKey, List<ModelFieldAssignmentRespVO> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            return List.of();
        }
        List<Map<String, Object>> checks = new ArrayList<>();
        for (ModelFieldAssignmentRespVO item : assignments) {
            if (item == null || item.getField() == null) {
                continue;
            }
            String fieldKey = item.getField().getCode();
            if (StrUtil.isBlank(fieldKey) || "id".equals(fieldKey)) {
                continue;
            }
            if (!isUniqueInValidationRules(item.getValidationRules()) && !isUniqueCandidate(fieldKey)) {
                continue;
            }
            checks.add(buildCheck(instanceKey, fieldKey, item.getField().getName()));
        }
        return checks;
    }

    static void attachAsyncCheckIds(List<Map<String, Object>> fields, List<Map<String, Object>> asyncChecks) {
        if (fields == null || asyncChecks == null || asyncChecks.isEmpty()) {
            return;
        }
        for (Map<String, Object> field : fields) {
            String fieldKey = stringVal(field.get("fieldKey"));
            for (Map<String, Object> check : asyncChecks) {
                if (fieldKey.equals(stringVal(check.get("fieldKey")))) {
                    field.put("asyncCheckId", check.get("id"));
                    break;
                }
            }
        }
    }

    private static Map<String, Object> buildCheck(String instanceKey, String fieldKey, String label) {
        String safeLabel = StrUtil.blankToDefault(label, fieldKey);
        Map<String, Object> check = new LinkedHashMap<>();
        String prefix = instanceKey != null ? instanceKey.replace(':', '-') : "entity";
        check.put("id", prefix + "-" + fieldKey + "-unique");
        check.put("fieldKey", fieldKey);
        check.put("purpose", "unique");
        check.put("trigger", "blur");
        check.put("appliesTo", List.of("create", "update"));
        check.put("message", safeLabel + "已存在");
        Map<String, Object> endpoint = new LinkedHashMap<>();
        endpoint.put("url", "/dynamicbusiness/capability/instances/check-field");
        endpoint.put("method", "GET");
        endpoint.put("paramStyle", "query-param");
        check.put("endpoint", endpoint);
        return check;
    }

    private static boolean isUniqueCandidate(String fieldKey) {
        if (StrUtil.isBlank(fieldKey)) {
            return false;
        }
        String normalized = fieldKey.toLowerCase(Locale.ROOT);
        if (UNIQUE_FIELD_KEYS.contains(normalized)) {
            return true;
        }
        return normalized.endsWith("code") || normalized.endsWith("name");
    }

    private static boolean isUniqueInValidationRules(String validationRulesJson) {
        if (StrUtil.isBlank(validationRulesJson)) {
            return false;
        }
        try {
            Map<String, Object> rules = OBJECT_MAPPER.readValue(validationRulesJson, new TypeReference<>() {});
            Object unique = rules.get("unique");
            return Boolean.TRUE.equals(unique);
        } catch (Exception ignored) {
            return validationRulesJson.contains("\"unique\":true") || validationRulesJson.contains("\"unique\": true");
        }
    }

    private static String stringVal(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}

package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

/**
 * 通过契约 list/page 读端点探测字段值是否已存在（供 asyncChecks 唯一性校验）。
 */
@Service
@Slf4j
public class CapabilityListProbeService {

    @Value("${yudao.capability.probe-base-url:}")
    private String probeBaseUrl;

    public boolean isFieldValueAvailable(
            Map<String, Object> contract, String fieldKey, String value, Long excludeId) {
        try {
            Map<String, Object> endpoint = CapabilityComponentViewBuilder.resolveListEndpoint(contract);
            String listUrl = String.valueOf(endpoint.get("url"));
            if (StrUtil.isBlank(listUrl)) {
                return true;
            }
            String method = String.valueOf(endpoint.getOrDefault("method", "GET")).toUpperCase(Locale.ROOT);
            if (!"GET".equals(method)) {
                return true;
            }
            HttpRequest request = HttpRequest.get(buildProbeUrl(listUrl, fieldKey, value, endpoint))
                    .timeout(5000);
            try (HttpResponse response = request.execute()) {
                if (!response.isOk()) {
                    log.warn("[CapabilityListProbe] probe failed status={} url={}", response.getStatus(), listUrl);
                    return true;
                }
                return !hasDuplicateValue(response.body(), endpoint, fieldKey, value, excludeId);
            }
        } catch (Exception ex) {
            log.warn("[CapabilityListProbe] probe error fieldKey={}: {}", fieldKey, ex.getMessage());
            return true;
        }
    }

    private String buildProbeUrl(
            String listUrl, String fieldKey, String value, Map<String, Object> endpoint) {
        String base = resolveProbeBaseUrl();
        String path = listUrl.startsWith("/") ? listUrl : "/" + listUrl;
        StringBuilder url = new StringBuilder(base).append(path);
        url.append(path.contains("?") ? "&" : "?");
        url.append(fieldKey).append("=").append(encode(value));
        if ("page-req".equals(endpoint.get("paramStyle"))) {
            url.append("&pageNo=1&pageSize=20");
        }
        return url.toString();
    }

    private String resolveProbeBaseUrl() {
        if (StrUtil.isNotBlank(probeBaseUrl)) {
            return probeBaseUrl.replaceAll("/+$", "");
        }
        return "http://127.0.0.1:58080/admin-api";
    }

    @SuppressWarnings("unchecked")
    private boolean hasDuplicateValue(
            String body, Map<String, Object> endpoint, String fieldKey, String value, Long excludeId) {
        if (StrUtil.isBlank(body) || !JSONUtil.isTypeJSON(body)) {
            return false;
        }
        JSONObject root = JSONUtil.parseObj(body);
        Object data = root.get("data");
        if (data == null) {
            return false;
        }
        Map<String, Object> mapping = endpoint.get("responseMapping") instanceof Map<?, ?> map
                ? (Map<String, Object>) map
                : Map.of();
        String listPath = String.valueOf(mapping.getOrDefault("listPath", "list"));
        String idField = String.valueOf(mapping.getOrDefault("idField", "id"));
        JSONArray rows = extractRows(data, listPath);
        if (rows == null || rows.isEmpty()) {
            return false;
        }
        String normalizedValue = value.trim();
        for (Object rowObj : rows) {
            if (!(rowObj instanceof JSONObject row)) {
                continue;
            }
            Object rawField = row.get(fieldKey);
            if (rawField == null || !normalizedValue.equals(String.valueOf(rawField).trim())) {
                continue;
            }
            if (excludeId != null) {
                Object rawId = row.get(idField);
                if (rawId != null && excludeId.toString().equals(String.valueOf(rawId))) {
                    continue;
                }
            }
            return true;
        }
        return false;
    }

    private static JSONArray extractRows(Object data, String listPath) {
        if (data instanceof JSONArray array) {
            return array;
        }
        if (!(data instanceof JSONObject obj)) {
            return null;
        }
        if (StrUtil.isBlank(listPath) || "list".equals(listPath)) {
            Object list = obj.get("list");
            if (list instanceof JSONArray array) {
                return array;
            }
            Object records = obj.get("records");
            if (records instanceof JSONArray array) {
                return array;
            }
            return JSONUtil.parseArray(JSONUtil.toJsonStr(obj));
        }
        Object nested = obj.getByPath(listPath.replace('.', '/'));
        if (nested instanceof JSONArray array) {
            return array;
        }
        return null;
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}

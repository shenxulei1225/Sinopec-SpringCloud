package cn.cheers.x.module.dynamicbusiness.service.reference.provider.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.cheers.x.framework.common.util.http.HttpUtils;
import cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo.ReferenceCandidateRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.reference.ReferenceProviderDO;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceBatchGetReq;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceProviderExecutor;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceValidateReq;
import cn.cheers.x.module.dynamicbusiness.service.reference.provider.ReferenceValidationResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class HttpReferenceProviderExecutor implements ReferenceProviderExecutor {

    @Override
    public boolean supports(ReferenceProviderDO provider) {
        return provider != null && "HTTP".equalsIgnoreCase(provider.getProviderType());
    }

    @Override
    public List<ReferenceCandidateRespVO> queryCandidates(ReferenceProviderDO provider, String keyword, Integer pageNo, Integer pageSize) {
        Map<String, Object> config = parseConfig(provider);
        String searchUrl = stringConfig(config, "searchUrl");
        if (StringUtils.isBlank(searchUrl)) {
            return Collections.emptyList();
        }
        Map<String, String> headers = buildHeaders(config);
        String url = appendQuery(searchUrl, keyword, pageNo, pageSize);
        String response = HttpUtils.get(url, headers);
        return parseCandidates(response, config);
    }

    @Override
    public List<ReferenceCandidateRespVO> batchGet(ReferenceProviderDO provider, ReferenceBatchGetReq req) {
        Map<String, Object> config = parseConfig(provider);
        String batchUrl = stringConfig(config, "batchGetUrl");
        if (StringUtils.isBlank(batchUrl) || req == null || req.getIds() == null || req.getIds().isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, String> headers = buildHeaders(config);
        String body = JSONUtil.toJsonStr(Map.of("ids", req.getIds()));
        String response = HttpUtils.post(batchUrl, headers, body);
        return parseCandidates(response, config);
    }

    @Override
    public ReferenceValidationResult validate(ReferenceProviderDO provider, ReferenceValidateReq req) {
        Map<String, Object> config = parseConfig(provider);
        String validateUrl = stringConfig(config, "validateUrl");
        if (StringUtils.isBlank(validateUrl)) {
            return ReferenceValidationResult.builder().valid(true).build();
        }
        Map<String, String> headers = buildHeaders(config);
        Map<String, Object> payload = new HashMap<>();
        payload.put("id", req == null ? null : req.getId());
        payload.put("context", req == null ? null : req.getContext());
        String body = JSONUtil.toJsonStr(payload);
        try {
            String response = HttpUtils.post(validateUrl, headers, body);
            if (StringUtils.isBlank(response)) {
                return ReferenceValidationResult.builder().valid(false).reason("空响应").build();
            }
            JSONObject resp = JSONUtil.parseObj(response);
            if (resp.containsKey("valid")) {
                Object valid = resp.get("valid");
                return ReferenceValidationResult.builder()
                        .valid(Boolean.TRUE.equals(valid))
                        .reason(String.valueOf(resp.getOrDefault("reason", "")))
                        .build();
            }
            // 兼容 code/msg/data 风格
            Object code = resp.get("code");
            boolean successCode = code == null || "0".equals(String.valueOf(code)) || "200".equals(String.valueOf(code));
            Object data = resp.get("data");
            boolean valid = successCode;
            if (data instanceof Boolean dataBoolean) {
                valid = dataBoolean;
            } else if (data instanceof JSONObject dataObj && dataObj.containsKey("valid")) {
                valid = Boolean.TRUE.equals(dataObj.get("valid"));
            }
            return ReferenceValidationResult.builder()
                    .valid(valid)
                    .reason(String.valueOf(resp.getOrDefault("msg", "")))
                    .build();
        } catch (Exception ex) {
            return ReferenceValidationResult.builder().valid(false).reason(ex.getMessage()).build();
        }
    }

    private Map<String, Object> parseConfig(ReferenceProviderDO provider) {
        if (provider == null || StringUtils.isBlank(provider.getConfigJson())) {
            return Collections.emptyMap();
        }
        try {
            JSONObject config = JSONUtil.parseObj(provider.getConfigJson());
            Map<String, Object> result = new HashMap<>();
            config.forEach((k, v) -> result.put(String.valueOf(k), v));
            return result;
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private Map<String, String> buildHeaders(Map<String, Object> config) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        Object token = config.get("token");
        if (token != null && StringUtils.isNotBlank(String.valueOf(token))) {
            headers.put("Authorization", "Bearer " + token);
        }
        return headers;
    }

    private String appendQuery(String baseUrl, String keyword, Integer pageNo, Integer pageSize) {
        String url = baseUrl;
        if (StringUtils.isNotBlank(keyword)) {
            url = HttpUtils.replaceUrlQuery(url, "keyword", keyword);
        }
        if (pageNo != null) {
            url = HttpUtils.replaceUrlQuery(url, "pageNo", String.valueOf(pageNo));
        }
        if (pageSize != null) {
            url = HttpUtils.replaceUrlQuery(url, "pageSize", String.valueOf(pageSize));
        }
        return url;
    }

    private List<ReferenceCandidateRespVO> parseCandidates(String response, Map<String, Object> config) {
        if (StringUtils.isBlank(response)) {
            return Collections.emptyList();
        }
        try {
            JSONArray rows = extractRows(response, config);
            if (rows == null || rows.isEmpty()) {
                return Collections.emptyList();
            }
            String idField = stringConfig(config, "idField", "id");
            String labelField = stringConfig(config, "labelField", "label");
            return rows.stream().map(item -> {
                JSONObject jsonObject = (JSONObject) item;
                Map<String, Object> meta = new HashMap<>();
                jsonObject.forEach((k, v) -> meta.put(String.valueOf(k), v));
                return ReferenceCandidateRespVO.builder()
                        .id(String.valueOf(jsonObject.get(idField)))
                        .label(String.valueOf(jsonObject.get(labelField)))
                        .extra(JSONUtil.toJsonStr(meta))
                        .build();
            }).collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private JSONArray extractRows(String response, Map<String, Object> config) {
        try {
            return JSONUtil.parseArray(response);
        } catch (Exception ignore) {
            // ignore
        }

        JSONObject root = JSONUtil.parseObj(response);
        String listPath = stringConfig(config, "listPath", "data.list");
        Object value = readByPath(root, listPath);
        if (value instanceof JSONArray arr) {
            return arr;
        }
        if (value instanceof List<?> list) {
            return JSONUtil.parseArray(list);
        }

        Object data = root.get("data");
        if (data instanceof JSONArray arr) {
            return arr;
        }
        if (data instanceof JSONObject dataObj) {
            Object list = dataObj.get("list");
            if (list instanceof JSONArray arr) {
                return arr;
            }
            if (list instanceof List<?> rows) {
                return JSONUtil.parseArray(rows);
            }
        }
        return new JSONArray(new ArrayList<>());
    }

    private Object readByPath(JSONObject obj, String path) {
        if (obj == null || StringUtils.isBlank(path)) {
            return null;
        }
        String[] parts = path.split("\\.");
        Object current = obj;
        for (String part : parts) {
            if (!(current instanceof JSONObject json)) {
                return null;
            }
            current = json.get(part);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    private String stringConfig(Map<String, Object> config, String key) {
        return stringConfig(config, key, null);
    }

    private String stringConfig(Map<String, Object> config, String key, String defaultValue) {
        Object value = config.get(key);
        if (value == null) {
            return defaultValue;
        }
        String str = String.valueOf(value);
        return StringUtils.isBlank(str) ? defaultValue : str;
    }
}

package cn.cheers.x.module.platformresource.service.component;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.Map;

final class ComponentPropsHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ComponentPropsHelper() {
    }

    static Map<String, Object> parseJsonMap(String json) {
        if (StrUtil.isBlank(json)) {
            return Collections.emptyMap();
        }
        try {
            return MAPPER.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

    static String toJson(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        try {
            return MAPPER.writeValueAsString(map);
        } catch (Exception e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    static String toJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }
}

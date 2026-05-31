package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * CRUD 表单字段 optionsSource：下拉/关联选择的数据读接口与字段映射（写入 requestBody.fields）。
 * <p>
 * 生成 props 时随 requestFields 一并带给前端，避免每个业务手写选人/选设备接口。
 */
public final class FieldOptionsSourceBuilder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /** 芋道常用：按字典类型拉取字典项 */
    private static final String DICT_LOAD_URL = "/system/dict-data/type";

    private FieldOptionsSourceBuilder() {
    }

    /**
     * 为 requestBody 字段附加 optionsSource（dict / ref-picker / 枚举静态项）。
     */
    public static void attachOptionsSource(
            Map<String, Object> field,
            String control,
            String dictType,
            String refTargetKey,
            String enumOptionsJson) {
        if ("dict".equals(control)) {
            if (StrUtil.isNotBlank(dictType) && !looksLikeEnumJson(dictType)) {
                field.put("optionsSource", dictOptionsSource(dictType));
                return;
            }
            if (StrUtil.isNotBlank(enumOptionsJson)) {
                Map<String, Object> staticSrc = staticEnumOptionsSource(enumOptionsJson);
                if (staticSrc != null) {
                    field.put("optionsSource", staticSrc);
                }
            }
            return;
        }
        if (("ref-picker".equals(control) || "ref-picker-multi".equals(control))
                && StrUtil.isNotBlank(refTargetKey)) {
            Map<String, Object> refSrc = refOptionsSource(refTargetKey);
            if (refSrc != null) {
                field.put("optionsSource", refSrc);
            }
            return;
        }
        if ("category-tree".equals(control)) {
            field.put("optionsSource", categoryTreeOptionsSource());
        }
    }

    /** 分类树：默认走 system category tree（业务可在契约中覆盖 optionsSource） */
    public static Map<String, Object> categoryTreeOptionsSource() {
        Map<String, Object> load = new LinkedHashMap<>();
        load.put("url", "/system/category/tree");
        load.put("method", "GET");

        Map<String, Object> src = new LinkedHashMap<>();
        src.put("kind", "tree");
        src.put("loadEndpoint", load);
        src.put("valueField", "id");
        src.put("labelField", "name");
        src.put("childrenField", "children");
        src.put("listPath", "");
        return src;
    }

    private static boolean looksLikeEnumJson(String dictType) {
        String t = dictType.trim();
        return t.startsWith("[") || t.startsWith("{");
    }

    public static Map<String, Object> dictOptionsSource(String dictType) {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("type", dictType);

        Map<String, Object> load = new LinkedHashMap<>();
        load.put("url", DICT_LOAD_URL);
        load.put("method", "GET");
        load.put("params", params);

        Map<String, Object> src = new LinkedHashMap<>();
        src.put("kind", "dict");
        src.put("dictType", dictType);
        src.put("loadEndpoint", load);
        src.put("valueField", "value");
        src.put("labelField", "label");
        src.put("listPath", "");
        return src;
    }

    /**
     * system:{code} 从 {@link SystemCapabilityCatalog} 解析列表读接口；dynamic-* 仅带 queryContractKey 由前端按契约补全。
     */
    public static Map<String, Object> refOptionsSource(String instanceKey) {
        if (StrUtil.isBlank(instanceKey)) {
            return null;
        }
        if (instanceKey.startsWith("system:")) {
            String code = instanceKey.substring("system:".length());
            Optional<SystemCapabilityResourceDef> def = SystemCapabilityCatalog.find(code);
            if (def.isEmpty()) {
                return refUnresolved(instanceKey);
            }
            return refFromSystemDef(def.get(), instanceKey);
        }
        return refUnresolved(instanceKey);
    }

    private static Map<String, Object> refFromSystemDef(SystemCapabilityResourceDef def, String instanceKey) {
        String url = def.listUrl();
        Map<String, Object> load = new LinkedHashMap<>();
        load.put("url", url);
        load.put("method", "GET");
        if (def.paginated()) {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("pageNo", 1);
            params.put("pageSize", 200);
            load.put("params", params);
        }

        Map<String, Object> src = new LinkedHashMap<>();
        src.put("kind", "ref");
        src.put("queryContractKey", instanceKey);
        src.put("loadEndpoint", load);
        src.put("valueField", "id");
        src.put("labelField", resolveLabelField(def.resourceCode()));
        src.put("listPath", def.paginated() ? "list" : "");
        return src;
    }

    private static String resolveLabelField(String resourceCode) {
        return switch (resourceCode) {
            case "dictData" -> "label";
            case "user" -> "nickname";
            default -> "name";
        };
    }

    private static Map<String, Object> refUnresolved(String instanceKey) {
        Map<String, Object> src = new LinkedHashMap<>();
        src.put("kind", "ref");
        src.put("queryContractKey", instanceKey);
        src.put("valueField", "id");
        src.put("labelField", "name");
        src.put("listPath", "list");
        return src;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> staticEnumOptionsSource(String optionsJson) {
        if (StrUtil.isBlank(optionsJson)) {
            return null;
        }
        try {
            List<Map<String, Object>> raw = OBJECT_MAPPER.readValue(optionsJson, new TypeReference<>() {});
            List<Map<String, Object>> options = new ArrayList<>();
            for (Map<String, Object> item : raw) {
                Object value = item.get("value");
                if (value == null) {
                    value = item.get("code");
                }
                Object label = item.get("label");
                if (label == null) {
                    label = item.get("name");
                }
                if (value == null) {
                    continue;
                }
                Map<String, Object> opt = new LinkedHashMap<>();
                opt.put("value", value);
                opt.put("label", label != null ? String.valueOf(label) : String.valueOf(value));
                options.add(opt);
            }
            if (options.isEmpty()) {
                return null;
            }
            Map<String, Object> src = new LinkedHashMap<>();
            src.put("kind", "static");
            src.put("options", options);
            src.put("valueField", "value");
            src.put("labelField", "label");
            return src;
        } catch (Exception ignored) {
            return null;
        }
    }
}

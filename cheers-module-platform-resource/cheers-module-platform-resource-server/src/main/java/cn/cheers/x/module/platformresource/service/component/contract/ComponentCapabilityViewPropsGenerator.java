package cn.cheers.x.module.platformresource.service.component.contract;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将 for-component 投影视图（ComponentCapabilityView JSON）转换为组件 Props 模板。
 */
public final class ComponentCapabilityViewPropsGenerator {

    private ComponentCapabilityViewPropsGenerator() {
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> generateListProps(
            Map<String, Object> view,
            String dataSourceKey,
            QueryContractPropsGenerator.ListGenerateOptions options) {
        QueryContractPropsGenerator.ListGenerateOptions opts =
                options != null ? options : QueryContractPropsGenerator.ListGenerateOptions.defaults();
        String label = StrUtil.blankToDefault(stringVal(view.get("label")), "列表");
        String title = label.replaceFirst("^系统\\s*-\\s*", "");

        Map<String, Object> read = requireMap(view.get("read"), "read");
        Map<String, Object> endpoint = requireMap(read.get("endpoint"), "read.endpoint");
        String listUrl = stringVal(endpoint.get("url"));
        String method = stringVal(endpoint.getOrDefault("method", "GET"));
        boolean paginated = "page-req".equals(endpoint.get("paramStyle"));

        List<String> displayContent = extractDisplayFieldKeys(read);
        if (displayContent.isEmpty()) {
            displayContent = List.of("name", "id", "status");
        }

        String crudBase = QueryContractPropsGenerator.resolveCrudBasePath(listUrl);

        Map<String, Object> props = new LinkedHashMap<>();
        if (StrUtil.isNotBlank(dataSourceKey)) {
            props.put("dataSourceKey", dataSourceKey.trim());
        }
        props.put("title", Map.of("title", title, "showTitle", true));

        Map<String, Object> dataSource = new LinkedHashMap<>();
        dataSource.put("dataSourceEndpoint", Map.of("url", listUrl, "method", method));
        mergeWriteEndpoints(dataSource, view, crudBase);
        props.put("dataSource", dataSource);
        props.put("selectMode", "single");

        String searchPlaceholder = buildSearchPlaceholder(read);
        props.put("search", Map.of(
                "searchPlaceholder", searchPlaceholder,
                "showSearch", opts.showSearch(),
                "searchScopeSelectedOptions", List.of()));
        props.put("toolbar", opts.toolbar());
        props.put("filter", Map.of("externalFilterEnabled", false, "filterSelectedOptions", List.of()));
        props.put("emptyText", "暂无数据");
        props.put("displayContent", displayContent);
        props.put("selectedPanel", opts.selectedPanel());
        props.put("pagination", paginated || opts.pagination());
        return props;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> generateTreeProps(Map<String, Object> view, String dataSourceKey) {
        Map<String, Object> read = requireMap(view.get("read"), "read");
        Map<String, Object> endpoint = requireMap(read.get("endpoint"), "read.endpoint");
        String listUrl = stringVal(endpoint.get("url"));
        String method = stringVal(endpoint.getOrDefault("method", "GET"));

        Map<String, Object> props = new LinkedHashMap<>();
        if (StrUtil.isNotBlank(dataSourceKey)) {
            props.put("dataSourceKey", dataSourceKey.trim());
        }
        props.put("selectMode", "single");
        props.put("defaultExpandAll", true);
        props.put("showSearch", true);
        props.put("showToolbar", false);
        props.put("draggable", false);
        props.put("emptyText", "暂无数据");
        props.put("searchPlaceholder", buildSearchPlaceholder(read));
        props.put("autoLoad", true);
        props.put("dataSource", Map.of("dataSourceEndpoint", Map.of("url", listUrl, "method", method)));
        return props;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> generateTableProps(Map<String, Object> view, String dataSourceKey) {
        Map<String, Object> read = requireMap(view.get("read"), "read");
        Map<String, Object> endpoint = requireMap(read.get("endpoint"), "read.endpoint");
        String listUrl = stringVal(endpoint.get("url"));
        String method = stringVal(endpoint.getOrDefault("method", "GET"));
        boolean paginated = "page-req".equals(endpoint.get("paramStyle"));

        List<String> displayContent = extractDisplayFieldKeys(read);
        if (displayContent.isEmpty()) {
            displayContent = List.of("name", "id", "status");
        }

        String label = StrUtil.blankToDefault(stringVal(view.get("label")), "表格");
        String title = label.replaceFirst("^系统\\s*-\\s*", "");
        String crudBase = QueryContractPropsGenerator.resolveCrudBasePath(listUrl);

        Map<String, Object> props = new LinkedHashMap<>();
        if (StrUtil.isNotBlank(dataSourceKey)) {
            props.put("dataSourceKey", dataSourceKey.trim());
        }
        props.put("title", Map.of("title", title, "showTitle", true));

        Map<String, Object> dataSource = new LinkedHashMap<>();
        dataSource.put("dataSourceEndpoint", Map.of("url", listUrl, "method", method));
        mergeWriteEndpoints(dataSource, view, crudBase);
        props.put("dataSource", dataSource);
        props.put("emptyText", "暂无数据");
        props.put("displayContent", displayContent);
        props.put("pagination", paginated);
        return props;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> generateCardProps(Map<String, Object> view, String dataSourceKey) {
        Map<String, Object> tableProps = generateTableProps(view, dataSourceKey);
        tableProps.remove("pagination");
        return tableProps;
    }

    public static boolean viewSupportsTree(Map<String, Object> treeView) {
        if (treeView == null) {
            return false;
        }
        Object read = treeView.get("read");
        if (!(read instanceof Map<?, ?> readMap)) {
            return false;
        }
        Object endpoint = readMap.get("endpoint");
        if (!(endpoint instanceof Map<?, ?> endpointMap)) {
            return false;
        }
        return StrUtil.isNotBlank(stringVal(endpointMap.get("url")));
    }

    @SuppressWarnings("unchecked")
    private static List<String> extractDisplayFieldKeys(Map<String, Object> read) {
        Object fields = read.get("displayFields");
        if (!(fields instanceof List<?> list)) {
            return List.of();
        }
        List<String> keys = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map<?, ?> map) {
                Object key = map.get("fieldKey");
                if (key != null) {
                    keys.add(String.valueOf(key));
                }
            }
        }
        return keys;
    }

    @SuppressWarnings("unchecked")
    private static String buildSearchPlaceholder(Map<String, Object> read) {
        Object filters = read.get("filters");
        if (!(filters instanceof List<?> list)) {
            return "搜索…";
        }
        for (Object item : list) {
            if (item instanceof Map<?, ?> filter) {
                if (Boolean.TRUE.equals(filter.get("searchable"))) {
                    Object label = filter.get("label");
                    return "搜索" + (label != null ? label : "") + "…";
                }
            }
        }
        return "搜索…";
    }

    @SuppressWarnings("unchecked")
    private static void mergeWriteEndpoints(
            Map<String, Object> dataSource, Map<String, Object> view, String crudBaseFallback) {
        Object writeObj = view.get("write");
        if (!(writeObj instanceof Map<?, ?> write)) {
            applyCrudFallback(dataSource, crudBaseFallback);
            return;
        }
        Map<String, Object> create = asMap(write.get("create"));
        Map<String, Object> update = asMap(write.get("update"));
        Map<String, Object> delete = asMap(write.get("delete"));
        if (create != null) {
            dataSource.put("createEndpoint", writeEndpointToApiConfig(create));
        } else if (StrUtil.isNotBlank(crudBaseFallback)) {
            dataSource.put("createEndpoint", Map.of("url", crudBaseFallback + "/create", "method", "POST"));
        }
        if (update != null) {
            dataSource.put("updateEndpoint", writeEndpointToApiConfig(update));
        } else if (StrUtil.isNotBlank(crudBaseFallback)) {
            dataSource.put("updateEndpoint", Map.of("url", crudBaseFallback + "/update", "method", "PUT"));
        }
        if (delete != null) {
            dataSource.put("deleteEndpoint", writeEndpointToApiConfig(delete));
        } else if (StrUtil.isNotBlank(crudBaseFallback)) {
            dataSource.put("deleteEndpoint", Map.of("url", crudBaseFallback + "/delete", "method", "DELETE"));
        }
    }

    private static void applyCrudFallback(Map<String, Object> dataSource, String crudBaseFallback) {
        if (StrUtil.isBlank(crudBaseFallback)) {
            return;
        }
        dataSource.put("createEndpoint", Map.of("url", crudBaseFallback + "/create", "method", "POST"));
        dataSource.put("updateEndpoint", Map.of("url", crudBaseFallback + "/update", "method", "PUT"));
        dataSource.put("deleteEndpoint", Map.of("url", crudBaseFallback + "/delete", "method", "DELETE"));
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> writeEndpointToApiConfig(Map<String, Object> endpoint) {
        Map<String, Object> cfg = new LinkedHashMap<>();
        cfg.put("url", endpoint.get("url"));
        cfg.put("method", endpoint.getOrDefault("method", "POST"));
        Object fields = endpoint.get("fields");
        if (fields instanceof List<?> list && !list.isEmpty()) {
            cfg.put("requestFields", fields);
        }
        Object paramStyle = endpoint.get("paramStyle");
        if (paramStyle != null) {
            cfg.put("paramStyle", paramStyle);
        }
        return cfg;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> requireMap(Object value, String name) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        throw new IllegalArgumentException("组件能力视图缺少 " + name);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }

    private static String stringVal(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}

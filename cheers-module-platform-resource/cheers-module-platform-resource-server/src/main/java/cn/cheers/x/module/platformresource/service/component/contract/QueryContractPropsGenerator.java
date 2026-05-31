package cn.cheers.x.module.platformresource.service.component.contract;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 将数据能力契约（QueryContract JSON）转换为组件 Props 模板 JSON（list / tree）。
 */
public final class QueryContractPropsGenerator {

    private QueryContractPropsGenerator() {
    }

    public record ListGenerateOptions(
            boolean showSearch,
            boolean toolbar,
            boolean selectedPanel,
            boolean pagination) {
        public static ListGenerateOptions defaults() {
            return new ListGenerateOptions(true, true, true, false);
        }

        public static ListGenerateOptions minimal() {
            return new ListGenerateOptions(false, false, false, false);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> generateListProps(
            Map<String, Object> contract, String dataSourceKey, ListGenerateOptions options) {
        ListGenerateOptions opts = options != null ? options : ListGenerateOptions.defaults();
        String label = StrUtil.blankToDefault((String) contract.get("label"), "列表");
        String title = label.replaceFirst("^系统\\s*-\\s*", "");

        Map<String, Object> endpoint = resolveListEndpoint(contract);
        String listUrl = String.valueOf(endpoint.get("url"));
        String method = String.valueOf(endpoint.getOrDefault("method", "GET"));
        boolean paginated = "page-req".equals(endpoint.get("paramStyle"));

        List<String> displayContent = extractDisplayFieldKeys(contract);
        if (displayContent.isEmpty()) {
            displayContent = List.of("name", "id", "status");
        }

        String crudBase = resolveCrudBasePath(listUrl);

        Map<String, Object> props = new LinkedHashMap<>();
        if (StrUtil.isNotBlank(dataSourceKey)) {
            props.put("dataSourceKey", dataSourceKey.trim());
        }
        props.put("title", Map.of("title", title, "showTitle", true));

        Map<String, Object> dataSource = new LinkedHashMap<>();
        dataSource.put("dataSourceEndpoint", Map.of("url", listUrl, "method", method));
        mergeWriteEndpoints(dataSource, contract, crudBase);
        props.put("dataSource", dataSource);
        props.put("selectMode", "single");

        String searchPlaceholder = buildSearchPlaceholder(contract);
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
    public static Map<String, Object> generateTreeProps(Map<String, Object> contract, String dataSourceKey) {
        Map<String, Object> endpoint = resolveTreeEndpoint(contract);
        String listUrl = String.valueOf(endpoint.get("url"));
        String method = String.valueOf(endpoint.getOrDefault("method", "GET"));

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
        props.put("searchPlaceholder", buildSearchPlaceholder(contract));
        props.put("autoLoad", true);
        props.put("dataSource", Map.of("dataSourceEndpoint", Map.of("url", listUrl, "method", method)));
        return props;
    }

    /**
     * List 组件：选用 purpose=list/search 的端点，优先 default / page，绝不误用 tree 端点。
     */
    @SuppressWarnings("unchecked")
    static Map<String, Object> resolveListEndpoint(Map<String, Object> contract) {
        Map<String, Object> dataSource = firstDataSource(contract);
        List<Map<String, Object>> endpoints = (List<Map<String, Object>>) dataSource.get("endpoints");
        if (endpoints == null || endpoints.isEmpty()) {
            throw new IllegalArgumentException("契约缺少 dataSources.endpoints");
        }
        String defaultId = String.valueOf(dataSource.getOrDefault("defaultEndpointId", ""));
        for (Map<String, Object> ep : endpoints) {
            if (defaultId.equals(ep.get("id")) && isListPurpose(ep)) {
                return ep;
            }
        }
        for (Map<String, Object> ep : endpoints) {
            if (isListPurpose(ep) && "default".equals(ep.get("activateWhen"))) {
                return ep;
            }
        }
        for (Map<String, Object> ep : endpoints) {
            if (isListPurpose(ep)) {
                return ep;
            }
        }
        throw new IllegalArgumentException("契约缺少 list/page 类 endpoint");
    }

    /**
     * Tree 组件：选用 purpose=tree 或 activateWhen=treeMode 的端点（可与 list 同 path 但 param 不同）。
     */
    @SuppressWarnings("unchecked")
    static Map<String, Object> resolveTreeEndpoint(Map<String, Object> contract) {
        Map<String, Object> dataSource = firstDataSource(contract);
        List<Map<String, Object>> endpoints = (List<Map<String, Object>>) dataSource.get("endpoints");
        if (endpoints == null || endpoints.isEmpty()) {
            throw new IllegalArgumentException("契约缺少 dataSources.endpoints");
        }
        for (Map<String, Object> ep : endpoints) {
            if ("tree".equals(ep.get("purpose"))) {
                return ep;
            }
        }
        for (Map<String, Object> ep : endpoints) {
            if ("treeMode".equals(ep.get("activateWhen"))) {
                return ep;
            }
        }
        throw new IllegalArgumentException("契约缺少 tree 类 endpoint");
    }

    private static boolean isListPurpose(Map<String, Object> ep) {
        String purpose = String.valueOf(ep.getOrDefault("purpose", "list"));
        return "list".equals(purpose) || "search".equals(purpose);
    }

    @SuppressWarnings("unchecked")
    public static boolean contractSupportsTreeView(Map<String, Object> contract) {
        List<Map<String, Object>> sources = (List<Map<String, Object>>) contract.get("dataSources");
        if (sources == null) {
            return false;
        }
        for (Map<String, Object> ds : sources) {
            Object viewTypes = ds.get("viewTypes");
            if (viewTypes instanceof List<?> list && list.contains("tree")) {
                return true;
            }
            List<Map<String, Object>> endpoints = (List<Map<String, Object>>) ds.get("endpoints");
            if (endpoints != null) {
                for (Map<String, Object> ep : endpoints) {
                    if ("tree".equals(ep.get("purpose")) || "treeMode".equals(ep.get("activateWhen"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> firstDataSource(Map<String, Object> contract) {
        List<Map<String, Object>> sources = (List<Map<String, Object>>) contract.get("dataSources");
        if (sources == null || sources.isEmpty()) {
            throw new IllegalArgumentException("契约缺少 dataSources");
        }
        return sources.get(0);
    }

    @SuppressWarnings("unchecked")
    private static List<String> extractDisplayFieldKeys(Map<String, Object> contract) {
        List<Map<String, Object>> fields = (List<Map<String, Object>>) contract.get("displayFields");
        if (fields == null) {
            return List.of();
        }
        List<String> keys = new ArrayList<>();
        for (Map<String, Object> field : fields) {
            Object key = field.get("fieldKey");
            if (key != null) {
                keys.add(String.valueOf(key));
            }
        }
        return keys;
    }

    @SuppressWarnings("unchecked")
    private static String buildSearchPlaceholder(Map<String, Object> contract) {
        List<Map<String, Object>> filters = (List<Map<String, Object>>) contract.get("filters");
        if (filters == null) {
            return "搜索…";
        }
        for (Map<String, Object> filter : filters) {
            if (Boolean.TRUE.equals(filter.get("searchable"))) {
                return "搜索" + filter.getOrDefault("label", "") + "…";
            }
        }
        return "搜索…";
    }

    @SuppressWarnings("unchecked")
    private static void mergeWriteEndpoints(
            Map<String, Object> dataSource, Map<String, Object> contract, String crudBaseFallback) {
        Map<String, Object> createEp = resolveWriteEndpoint(contract, "create");
        Map<String, Object> updateEp = resolveWriteEndpoint(contract, "update");
        Map<String, Object> deleteEp = resolveWriteEndpoint(contract, "delete");
        if (createEp != null) {
            dataSource.put("createEndpoint", endpointToApiConfig(createEp));
        } else if (StrUtil.isNotBlank(crudBaseFallback)) {
            dataSource.put("createEndpoint", Map.of("url", crudBaseFallback + "/create", "method", "POST"));
        }
        if (updateEp != null) {
            dataSource.put("updateEndpoint", endpointToApiConfig(updateEp));
        } else if (StrUtil.isNotBlank(crudBaseFallback)) {
            dataSource.put("updateEndpoint", Map.of("url", crudBaseFallback + "/update", "method", "PUT"));
        }
        if (deleteEp != null) {
            dataSource.put("deleteEndpoint", endpointToApiConfig(deleteEp));
        } else if (StrUtil.isNotBlank(crudBaseFallback)) {
            dataSource.put("deleteEndpoint", Map.of("url", crudBaseFallback + "/delete", "method", "DELETE"));
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> resolveWriteEndpoint(Map<String, Object> contract, String purpose) {
        Map<String, Object> dataSource = firstDataSource(contract);
        List<Map<String, Object>> endpoints = (List<Map<String, Object>>) dataSource.get("endpoints");
        if (endpoints == null) {
            return null;
        }
        for (Map<String, Object> ep : endpoints) {
            if (purpose.equals(ep.get("purpose"))) {
                return ep;
            }
        }
        List<Map<String, Object>> actions = (List<Map<String, Object>>) contract.get("actions");
        if (actions == null) {
            return null;
        }
        String endpointId = null;
        for (Map<String, Object> action : actions) {
            if (purpose.equals(action.get("purpose"))) {
                endpointId = String.valueOf(action.get("endpointId"));
                break;
            }
        }
        if (StrUtil.isBlank(endpointId)) {
            return null;
        }
        for (Map<String, Object> ep : endpoints) {
            if (endpointId.equals(ep.get("id"))) {
                return ep;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> endpointToApiConfig(Map<String, Object> endpoint) {
        Map<String, Object> cfg = new LinkedHashMap<>();
        cfg.put("url", endpoint.get("url"));
        cfg.put("method", endpoint.getOrDefault("method", "POST"));
        Object requestBody = endpoint.get("requestBody");
        if (requestBody instanceof Map<?, ?> body) {
            Object fields = body.get("fields");
            if (fields instanceof List<?> list && !list.isEmpty()) {
                cfg.put("requestFields", fields);
            }
        }
        Object paramStyle = endpoint.get("paramStyle");
        if (paramStyle != null) {
            cfg.put("paramStyle", paramStyle);
        }
        return cfg;
    }

    /**
     * 从列表 URL 推导 CRUD 根路径，如 /system/dept/list → /system/dept。
     */
    static String resolveCrudBasePath(String listUrl) {
        if (StrUtil.isBlank(listUrl) || !listUrl.startsWith("/system/")) {
            return null;
        }
        if (listUrl.endsWith("/list")) {
            return listUrl.substring(0, listUrl.length() - "/list".length());
        }
        if (listUrl.endsWith("/page")) {
            return listUrl.substring(0, listUrl.length() - "/page".length());
        }
        if (listUrl.endsWith("/my-page")) {
            return listUrl.substring(0, listUrl.length() - "/my-page".length());
        }
        return null;
    }
}

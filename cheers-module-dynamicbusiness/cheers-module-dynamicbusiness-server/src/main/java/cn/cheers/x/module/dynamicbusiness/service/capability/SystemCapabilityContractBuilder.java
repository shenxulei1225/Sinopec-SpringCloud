package cn.cheers.x.module.dynamicbusiness.service.capability;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 构建 System 模块固定资源的数据能力契约（与前端 QueryContract / systemListAdapters 对齐）。
 */
final class SystemCapabilityContractBuilder {

    private SystemCapabilityContractBuilder() {
    }

    static Map<String, Object> build(SystemCapabilityResourceDef def) {
        String instanceKey = def.instanceKey();
        Map<String, Object> keyParts = new LinkedHashMap<>();
        keyParts.put("domain", "system");
        keyParts.put("resourceCode", def.resourceCode());

        Map<String, Object> contract = new LinkedHashMap<>();
        contract.put("instanceKey", instanceKey);
        contract.put("queryContractKey", instanceKey);
        contract.put("label", "系统 - " + def.label());
        contract.put("domain", "system");
        contract.put("keyParts", keyParts);
        contract.put("updatedAt", Instant.now().toString());
        contract.put("dataSources", List.of(buildDataSource(def)));
        contract.put("filters", buildFilters(def));
        contract.put("displayFields", buildDisplayFields(def));
        contract.put("actions", buildActions(def));
        return contract;
    }

    private static Map<String, Object> buildDataSource(SystemCapabilityResourceDef def) {
        List<Map<String, Object>> endpoints = new ArrayList<>();
        endpoints.add(listEndpoint(def, "list-default", "default"));
        if (def.supportsTree()) {
            endpoints.add(listEndpoint(def, "list-tree", "treeMode"));
        }
        addSystemWriteEndpoints(endpoints, def);

        List<String> viewTypes = new ArrayList<>(List.of("list", "table"));
        if (def.supportsTree()) {
            viewTypes.add("tree");
        }

        Map<String, Object> ds = new LinkedHashMap<>();
        ds.put("id", "system-list");
        ds.put("label", def.label() + "列表");
        ds.put("viewTypes", viewTypes);
        ds.put("defaultEndpointId", "list-default");
        ds.put("endpoints", endpoints);
        return ds;
    }

    private static Map<String, Object> listEndpoint(SystemCapabilityResourceDef def, String id, String activateWhen) {
        Map<String, Object> ep = new LinkedHashMap<>();
        ep.put("id", id);
        ep.put("role", "primary");
        ep.put("purpose", "tree".equals(activateWhen) ? "tree" : "list");
        ep.put("url", def.listUrl());
        ep.put("method", "GET");
        ep.put("paramStyle", def.paginated() ? "page-req" : "plain");
        if (def.paginated()) {
            Map<String, Object> defaults = new LinkedHashMap<>();
            defaults.put("pageNo", 1);
            defaults.put("pageSize", 10);
            ep.put("defaultParams", defaults);
        }
        ep.put("activateWhen", activateWhen);
        Map<String, Object> mapping = new LinkedHashMap<>();
        if (def.paginated()) {
            mapping.put("listPath", "list");
            mapping.put("totalPath", "total");
        } else {
            mapping.put("listPath", "");
        }
        mapping.put("idField", "id");
        mapping.put("labelField", resolveLabelField(def));
        ep.put("responseMapping", mapping);
        return ep;
    }

    private static String resolveLabelField(SystemCapabilityResourceDef def) {
        return def.displayFields().stream()
                .map(SystemCapabilityResourceDef.DisplaySpec::fieldKey)
                .filter(key -> "name".equals(key) || "title".equals(key) || "username".equals(key) || "label".equals(key))
                .findFirst()
                .orElse("id");
    }

    private static List<Map<String, Object>> buildFilters(SystemCapabilityResourceDef def) {
        List<Map<String, Object>> filters = new ArrayList<>();
        int order = 0;
        for (SystemCapabilityResourceDef.FilterSpec spec : def.filters()) {
            Map<String, Object> f = new LinkedHashMap<>();
            f.put("id", spec.fieldKey());
            f.put("fieldKey", spec.fieldKey());
            f.put("label", spec.label());
            f.put("control", spec.control());
            f.put("sortOrder", order++);
            f.put("bindTo", "query-param");
            f.put("searchable", spec.searchable());
            f.put("sortable", false);
            f.put("defaultVisible", true);
            if (spec.dictType() != null) {
                f.put("dictType", spec.dictType());
            }
            if (spec.refTargetKey() != null) {
                Map<String, Object> refTarget = new LinkedHashMap<>();
                refTarget.put("instanceKey", spec.refTargetKey());
                refTarget.put("queryContractKey", spec.refTargetKey());
                refTarget.put("valueField", "id");
                refTarget.put("labelField", "name");
                f.put("refTarget", refTarget);
                f.put("operators", List.of("EQ", "IN"));
            } else {
                f.put("operators", List.of("EQ", "CONTAINS", "LIKE"));
            }
            filters.add(f);
        }
        return filters;
    }

    private static List<Map<String, Object>> buildDisplayFields(SystemCapabilityResourceDef def) {
        List<Map<String, Object>> displayFields = new ArrayList<>();
        for (SystemCapabilityResourceDef.DisplaySpec spec : def.displayFields()) {
            Map<String, Object> df = new LinkedHashMap<>();
            df.put("id", spec.fieldKey());
            df.put("fieldKey", spec.fieldKey());
            df.put("label", spec.label());
            df.put("applicableViews", List.of("list", "table", "tree", "card"));
            df.put("renderAs", spec.renderAs());
            df.put("sortOrder", spec.sortOrder());
            df.put("defaultVisible", spec.defaultVisible());
            if ("dict".equals(spec.renderAs())) {
                df.put("dictType", "common_status");
            }
            displayFields.add(df);
        }
        return displayFields;
    }

    private static void addSystemWriteEndpoints(List<Map<String, Object>> endpoints, SystemCapabilityResourceDef def) {
        String base = resolveCrudBasePath(def.listUrl());
        if (base == null) {
            return;
        }
        endpoints.add(writeEndpoint(
                "write-create",
                "create",
                base + "/create",
                "POST",
                "json-body",
                InteractionSchemaBuilder.requestBody(
                        InteractionSchemaBuilder.buildSystemWriteFields(def, "create"))));
        endpoints.add(writeEndpoint(
                "write-update",
                "update",
                base + "/update",
                "PUT",
                "json-body",
                InteractionSchemaBuilder.requestBody(
                        InteractionSchemaBuilder.buildSystemWriteFields(def, "update"))));
        endpoints.add(writeEndpoint(
                "write-delete",
                "delete",
                base + "/delete",
                "DELETE",
                "query-param",
                InteractionSchemaBuilder.requestBody(
                        InteractionSchemaBuilder.buildSystemWriteFields(def, "delete"))));
    }

    private static Map<String, Object> writeEndpoint(
            String id,
            String purpose,
            String url,
            String method,
            String paramStyle,
            Map<String, Object> requestBody) {
        Map<String, Object> ep = new LinkedHashMap<>();
        ep.put("id", id);
        ep.put("role", "auxiliary");
        ep.put("purpose", purpose);
        ep.put("url", url);
        ep.put("method", method);
        ep.put("paramStyle", paramStyle);
        ep.put("requestBody", requestBody);
        return ep;
    }

    /** 从 list/page URL 推导 CRUD 根路径 */
    private static String resolveCrudBasePath(String listUrl) {
        if (listUrl == null || listUrl.isBlank() || !listUrl.startsWith("/system/")) {
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

    private static List<Map<String, Object>> buildActions(SystemCapabilityResourceDef def) {
        String code = def.resourceCode();
        String permPrefix = "system:" + code;
        List<Map<String, Object>> actions = new ArrayList<>();
        actions.add(action("create", "新建", "create", "write-create", permPrefix + ":create"));
        actions.add(action("update", "编辑", "update", "write-update", permPrefix + ":update"));
        actions.add(action("delete", "删除", "delete", "write-delete", permPrefix + ":delete"));
        actions.add(action("query", "查询", "detail", null, permPrefix + ":query"));
        return actions;
    }

    private static Map<String, Object> action(
            String id, String label, String purpose, String endpointId, String permission) {
        Map<String, Object> action = new LinkedHashMap<>();
        action.put("id", id);
        action.put("label", label);
        action.put("purpose", purpose);
        if (endpointId != null) {
            action.put("endpointId", endpointId);
        }
        action.put("permission", permission);
        return action;
    }
}

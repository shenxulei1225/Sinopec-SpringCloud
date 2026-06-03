package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.cheers.x.module.dynamicbusiness.controller.admin.field.vo.FieldRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;

import java.time.Instant;
import java.util.*;

/**
 * 构建与前端 InstanceCapabilityContract 对齐的 JSON 契约。
 */
final class CapabilityContractBuilder {

    private CapabilityContractBuilder() {
    }

    static Map<String, Object> buildModelListContract(ModelDO model, BusinessTypeDO businessType) {
        String businessTypeCode = model.getBusinessTypeCode();
        String instanceKey = "dynamic-model:" + businessTypeCode;
        String label = (businessType != null ? businessType.getName() : businessTypeCode) + " - 模型列表";

        Map<String, Object> contract = baseContract(instanceKey, label, "dynamic-model", businessTypeCode, null);
        contract.put("dataSources", List.of(modelListDataSource(businessTypeCode)));
        contract.put("filters", modelListFilters());
        contract.put("displayFields", modelListDisplayFields());
        return contract;
    }

    static Map<String, Object> buildEntityContract(ModelDO model, BusinessTypeDO businessType,
            List<ModelFieldAssignmentRespVO> fields) {
        String businessTypeCode = model.getBusinessTypeCode();
        Long modelId = model.getId();
        String instanceKey = "dynamic-entity:" + businessTypeCode + ":" + modelId;
        String label = model.getName() + " - 实体";

        Map<String, Object> contract = baseContract(instanceKey, label, "dynamic-entity", businessTypeCode, modelId);
        List<Map<String, Object>> entityFilters = buildEntityFilters(fields);
        contract.put("dataSources", List.of(entityListDataSource(businessTypeCode, modelId, fields)));
        contract.put("filters", entityFilters);
        contract.put("displayFields", buildEntityDisplayFields(fields));
        contract.put("actions", entityActions());
        contract.put("asyncChecks", SystemVoAsyncCheckScanner.scanEntityAsyncChecks(instanceKey, fields));
        return contract;
    }

    private static Map<String, Object> baseContract(String instanceKey, String label, String domain,
            String businessTypeCode, Long modelId) {
        Map<String, Object> keyParts = new LinkedHashMap<>();
        keyParts.put("domain", domain);
        keyParts.put("businessTypeCode", businessTypeCode);
        if (modelId != null) {
            keyParts.put("modelId", modelId);
        }

        Map<String, Object> contract = new LinkedHashMap<>();
        contract.put("instanceKey", instanceKey);
        contract.put("label", label);
        contract.put("domain", domain);
        contract.put("keyParts", keyParts);
        contract.put("updatedAt", Instant.now().toString());
        return contract;
    }

    private static Map<String, Object> modelListDataSource(String businessTypeCode) {
        Map<String, Object> ds = new LinkedHashMap<>();
        ds.put("id", "model-list");
        ds.put("label", "模型列表");
        ds.put("viewTypes", List.of("list", "table"));
        ds.put("defaultEndpointId", "list-by-type");
        ds.put("endpoints", List.of(
                endpoint("list-by-type", "primary", "list", "/dynamicbusiness/business/models/list-by-business-type",
                        "plain", "default", businessTypeCode, "list", null, "id", "name"),
                endpoint("list-by-category", "primary", "list",
                        "/dynamicbusiness/business/models/find-by-category-in-business", "plain", "hasCategory",
                        businessTypeCode, "list", null, "id", "name"),
                endpoint("page-models", "primary", "search", "/dynamicbusiness/business/models/page-models",
                        "page-req", "hasKeyword", businessTypeCode, "list", "total", "id", "name")
        ));
        return ds;
    }

    private static Map<String, Object> entityListDataSource(
            String businessTypeCode, Long modelId, List<ModelFieldAssignmentRespVO> fieldAssignments) {
        Map<String, Object> defaultParams = new LinkedHashMap<>();
        defaultParams.put("businessTypeCode", businessTypeCode);
        defaultParams.put("modelIds", List.of(modelId));
        defaultParams.put("scene", "PATTERN_B_ENTITIES_BY_MODEL");
        defaultParams.put("resultShape", "PAGE");
        defaultParams.put("resultDetail", "FULL");
        defaultParams.put("pageNo", 1);
        defaultParams.put("pageSize", 10);

        Map<String, Object> treeParams = new LinkedHashMap<>();
        treeParams.put("businessTypeCode", businessTypeCode);
        treeParams.put("modelIds", List.of(modelId));
        treeParams.put("scene", "PATTERN_B_ENTITIES_BY_MODEL");
        treeParams.put("resultShape", "TREE");
        treeParams.put("resultDetail", "BASIC");

        Map<String, Object> pageParams = new LinkedHashMap<>();
        pageParams.put("businessTypeCode", businessTypeCode);
        pageParams.put("modelId", modelId);
        pageParams.put("pageNo", 1);
        pageParams.put("pageSize", 10);

        Map<String, Object> ds = new LinkedHashMap<>();
        ds.put("id", "entity-list");
        ds.put("label", "实体列表");
        ds.put("viewTypes", List.of("list", "tree", "table", "card"));
        ds.put("defaultEndpointId", "query-by-scene");
        List<Map<String, Object>> endpoints = new ArrayList<>(List.of(
                entityEndpoint("query-by-scene", "list", "/dynamicbusiness/business/entities/query-by-scene",
                        "field-filter-body", "default", defaultParams, "page.list", "page.total"),
                entityEndpoint("page-by-filters", "list", "/dynamicbusiness/business/entities/page-by-filters",
                        "field-filter-body", "pageMode", pageParams, "list", "total"),
                entityEndpoint("entity-tree", "tree", "/dynamicbusiness/business/entities/query-by-scene",
                        "field-filter-body", "treeMode", treeParams, "tree", null)));
        addEntityWriteEndpoints(endpoints, fieldAssignments, "dynamic-entity:" + businessTypeCode + ":" + modelId);
        ds.put("endpoints", endpoints);
        return ds;
    }

    private static void addEntityWriteEndpoints(
            List<Map<String, Object>> endpoints,
            List<ModelFieldAssignmentRespVO> fieldAssignments,
            String instanceKey) {
        String base = "/dynamicbusiness/business/entities";
        endpoints.add(entityWriteEndpoint("write-create", "create", base + "/save", "POST", "json-body",
                entityRequestBody(fieldAssignments, "create", instanceKey)));
        endpoints.add(entityWriteEndpoint("write-update", "update", base + "/save", "PUT", "json-body",
                entityRequestBody(fieldAssignments, "update", instanceKey)));
        endpoints.add(entityWriteEndpoint("write-delete", "delete", base + "/delete", "DELETE", "query-param",
                InteractionSchemaBuilder.requestBody(List.of(
                        InteractionSchemaBuilder.field("id", "编号", "input", true, false, null, null)))));
    }

    private static Map<String, Object> entityRequestBody(
            List<ModelFieldAssignmentRespVO> fieldAssignments, String purpose, String instanceKey) {
        List<Map<String, Object>> fields = InteractionSchemaBuilder.buildEntityWriteFieldsFromAssignments(
                fieldAssignments, purpose, CapabilityContractBuilder::resolveRefInstanceKey);
        List<Map<String, Object>> asyncChecks =
                SystemVoAsyncCheckScanner.scanEntityAsyncChecks(instanceKey, fieldAssignments);
        SystemVoAsyncCheckScanner.attachAsyncCheckIds(fields, asyncChecks);
        return InteractionSchemaBuilder.requestBody(fields);
    }

    private static Map<String, Object> entityWriteEndpoint(
            String id, String purpose, String url, String method, String paramStyle, Map<String, Object> requestBody) {
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

    private static Map<String, Object> endpoint(String id, String role, String purpose, String url,
            String paramStyle, String activateWhen, String businessTypeCode, String listPath, String totalPath,
            String idField, String labelField) {
        Map<String, Object> ep = new LinkedHashMap<>();
        ep.put("id", id);
        ep.put("role", role);
        ep.put("purpose", purpose);
        ep.put("url", url);
        ep.put("method", "GET");
        ep.put("paramStyle", paramStyle);
        if ("page-req".equals(paramStyle)) {
            Map<String, Object> defaults = new LinkedHashMap<>();
            defaults.put("pageNo", 1);
            defaults.put("pageSize", 10);
            defaults.put("businessTypeCode", businessTypeCode);
            ep.put("defaultParams", defaults);
        } else if ("plain".equals(paramStyle)) {
            ep.put("defaultParams", Map.of("businessTypeCode", businessTypeCode));
        }
        ep.put("activateWhen", activateWhen);
        Map<String, Object> mapping = new LinkedHashMap<>();
        mapping.put("listPath", listPath);
        if (totalPath != null) {
            mapping.put("totalPath", totalPath);
        }
        mapping.put("idField", idField);
        mapping.put("labelField", labelField);
        ep.put("responseMapping", mapping);
        return ep;
    }

    private static Map<String, Object> entityEndpoint(String id, String purpose, String url, String paramStyle,
            String activateWhen, Map<String, Object> defaultParams, String listPath, String totalPath) {
        Map<String, Object> ep = new LinkedHashMap<>();
        ep.put("id", id);
        ep.put("role", "primary");
        ep.put("purpose", purpose);
        ep.put("url", url);
        ep.put("method", "GET");
        ep.put("paramStyle", paramStyle);
        ep.put("defaultParams", defaultParams);
        ep.put("activateWhen", activateWhen);
        Map<String, Object> mapping = new LinkedHashMap<>();
        mapping.put("listPath", listPath);
        if (totalPath != null) {
            mapping.put("totalPath", totalPath);
        }
        mapping.put("idField", "id");
        mapping.put("labelField", "name");
        ep.put("responseMapping", mapping);
        return ep;
    }

    private static List<Map<String, Object>> modelListFilters() {
        List<Map<String, Object>> filters = new ArrayList<>();
        filters.add(filter("keyword", "keyword", "搜索", "input", 0, "query-param", true, false, null, null));
        filters.add(filter("categoryId", "categoryId", "分类", "category-tree", 1, "query-param", false, false,
                List.of("businessTypeCode"), null));
        filters.add(filter("status", "status", "状态", "dict", 2, "query-param", false, false, null, "common_status"));
        return filters;
    }

    private static List<Map<String, Object>> modelListDisplayFields() {
        return List.of(
                displayField("name", "name", "模型名称", List.of("list", "table"), "text", 0, true),
                displayField("code", "code", "编码", List.of("list", "table"), "text", 1, true),
                displayField("status", "status", "状态", List.of("list", "table"), "dict", 2, true, "common_status")
        );
    }

    static List<Map<String, Object>> buildEntityFilters(List<ModelFieldAssignmentRespVO> fields) {
        List<Map<String, Object>> filters = new ArrayList<>();
        int order = 0;
        for (ModelFieldAssignmentRespVO item : fields) {
            if (item == null || item.getField() == null) {
                continue;
            }
            FieldRespVO field = item.getField();
            boolean searchable = Boolean.TRUE.equals(item.getIsSearchable());
            boolean filterable = Boolean.TRUE.equals(item.getIsFilterable());
            boolean sortable = Boolean.TRUE.equals(item.getIsSortable());
            if (!searchable && !filterable && !sortable && !FieldTypeEnum.isEntityRef(field.getType())) {
                continue;
            }

            String fieldCode = field.getCode();
            String control = resolveControl(field.getType());
            String bindTo = FieldTypeEnum.isEntityRef(field.getType()) || isCustomField(field)
                    ? "field-filter" : "query-param";
            if (isTextLike(field.getType()) && searchable) {
                bindTo = "field-filter";
            }

            Map<String, Object> f = filter(fieldCode, fieldCode, field.getName(), control, order++, bindTo,
                    searchable, sortable, List.of("businessTypeCode", "modelId"), field.getOptions());
            if (FieldTypeEnum.isEntityRef(field.getType())) {
                Map<String, Object> refTarget = new LinkedHashMap<>();
                refTarget.put("instanceKey", resolveRefInstanceKey(item));
                refTarget.put("valueField", "id");
                refTarget.put("labelField", "name");
                f.put("refTarget", refTarget);
                f.put("operators", List.of("EQ", "IN"));
            } else {
                f.put("operators", resolveOperators(field.getType()));
            }
            f.put("defaultVisible", filterable || searchable);
            filters.add(f);
        }
        return filters;
    }

    static List<Map<String, Object>> buildEntityDisplayFields(List<ModelFieldAssignmentRespVO> fields) {
        List<Map<String, Object>> displayFields = new ArrayList<>();
        int order = 0;
        for (ModelFieldAssignmentRespVO item : fields) {
            if (item == null || item.getField() == null) {
                continue;
            }
            FieldRespVO field = item.getField();
            String renderAs = resolveDisplayRender(field.getType());
            displayFields.add(displayField(field.getCode(), field.getCode(), field.getName(),
                    List.of("list", "tree", "table", "card"), renderAs, order++,
                    order <= 3, field.getOptions()));
        }
        return displayFields;
    }

    private static List<Map<String, Object>> entityActions() {
        return List.of(
                action("create", "新建", "create", "write-create", "dynamicbusiness:entity:create"),
                action("update", "编辑", "update", "write-update", "dynamicbusiness:entity:update"),
                action("delete", "删除", "delete", "write-delete", "dynamicbusiness:entity:delete"),
                action("export", "导出", "export", null, "dynamicbusiness:entity:export")
        );
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

    private static Map<String, Object> filter(String id, String fieldKey, String label, String control, int sortOrder,
            String bindTo, boolean searchable, boolean sortable, List<String> requiredContext, String dictType) {
        Map<String, Object> filter = new LinkedHashMap<>();
        filter.put("id", id);
        filter.put("fieldKey", fieldKey);
        filter.put("label", label);
        filter.put("control", control);
        filter.put("sortOrder", sortOrder);
        filter.put("bindTo", bindTo);
        filter.put("searchable", searchable);
        filter.put("sortable", sortable);
        if (requiredContext != null) {
            filter.put("requiredContext", requiredContext);
        }
        if (dictType != null) {
            filter.put("dictType", dictType);
        }
        return filter;
    }

    private static Map<String, Object> displayField(String id, String fieldKey, String label,
            List<String> applicableViews, String renderAs, int sortOrder, boolean defaultVisible) {
        return displayField(id, fieldKey, label, applicableViews, renderAs, sortOrder, defaultVisible, null);
    }

    private static Map<String, Object> displayField(String id, String fieldKey, String label,
            List<String> applicableViews, String renderAs, int sortOrder, boolean defaultVisible, String options) {
        Map<String, Object> df = new LinkedHashMap<>();
        df.put("id", id);
        df.put("fieldKey", fieldKey);
        df.put("label", label);
        df.put("applicableViews", applicableViews);
        df.put("renderAs", renderAs);
        df.put("sortOrder", sortOrder);
        df.put("defaultVisible", defaultVisible);
        if (options != null && ("dict".equals(renderAs) || "ENUM".equalsIgnoreCase(renderAs))) {
            df.put("dictType", fieldKey);
        }
        return df;
    }

    private static String resolveControl(String rawType) {
        String t = rawType == null ? "" : rawType.trim().toUpperCase();
        return switch (t) {
            case "BOOLEAN", "BOOL" -> "boolean";
            case "DATE", "DATETIME", "TIMESTAMP" -> "date-range";
            case "ENUM", "OPTION", "SELECT", "MULTI_SELECT" -> "dict";
            case "ENTITY_REF", "ENTITY_REF_MULTI", "REFERENCE" -> "ref-picker";
            case "NUMBER", "INTEGER", "DECIMAL", "DOUBLE", "FLOAT", "LONG" -> "number-range";
            default -> "input";
        };
    }

    private static String resolveDisplayRender(String rawType) {
        String t = rawType == null ? "" : rawType.trim().toUpperCase();
        return switch (t) {
            case "BOOLEAN", "BOOL" -> "boolean";
            case "ENUM", "OPTION", "SELECT", "MULTI_SELECT" -> "dict";
            case "ENTITY_REF", "ENTITY_REF_MULTI", "REFERENCE" -> "ref";
            case "DATE", "DATETIME", "TIMESTAMP" -> "datetime";
            default -> "text";
        };
    }

    private static List<String> resolveOperators(String rawType) {
        String t = rawType == null ? "" : rawType.trim().toUpperCase();
        return switch (t) {
            case "NUMBER", "INTEGER", "DECIMAL", "DOUBLE", "FLOAT", "LONG" ->
                    List.of("EQ", "NE", "GTE", "LTE", "GT", "LT", "BETWEEN");
            case "DATE", "DATETIME", "TIMESTAMP" -> List.of("EQ", "GTE", "LTE", "BETWEEN");
            case "BOOLEAN", "BOOL" -> List.of("EQ");
            case "ENUM", "OPTION", "SELECT" -> List.of("EQ", "IN");
            case "MULTI_SELECT" -> List.of("IN", "CONTAINS");
            default -> List.of("EQ", "CONTAINS", "LIKE");
        };
    }

    private static boolean isTextLike(String rawType) {
        String t = rawType == null ? "" : rawType.trim().toUpperCase();
        return "TEXT".equals(t) || "STRING".equals(t) || "LONG_TEXT".equals(t);
    }

    private static boolean isCustomField(FieldRespVO field) {
        return field.getCode() != null && !field.getCode().startsWith("_");
    }

    private static String resolveRefInstanceKey(ModelFieldAssignmentRespVO item) {
        if (item.getTargetBusinessType() != null) {
            return "dynamic-entity:" + item.getTargetBusinessType() + ":*";
        }
        return "dynamic-entity:unknown:*";
    }
}

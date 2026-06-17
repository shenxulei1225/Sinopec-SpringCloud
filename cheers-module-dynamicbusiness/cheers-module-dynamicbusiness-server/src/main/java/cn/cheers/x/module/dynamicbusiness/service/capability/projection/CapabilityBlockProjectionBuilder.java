package cn.cheers.x.module.dynamicbusiness.service.capability.projection;

import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCategoryConstants;
import cn.cheers.x.module.dynamicbusiness.service.capability.system.SystemCapabilityDefinition;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 按能力块（capability block）生成 component_interface JSON。
 *
 * <p>不再使用 read/write 二分结构；各 componentCode 按定稿组合 getList/getTree/getCard 等块。</p>
 */
public final class CapabilityBlockProjectionBuilder {

    private static final Set<String> LIST_LIKE = Set.of("list", "table");
    /** 实体列表读端点：统一场景查询（定稿主路径，非 page-by-filters POC） */
    private static final String ENTITY_SCENE_QUERY_URL = "/dynamicbusiness/business/entities/query-by-scene";
    private static final String MODEL_PAGE_URL = "/dynamicbusiness/business/models/page-models";
    private static final String ENTITY_SCENE_DEFAULT = "PATTERN_ABC_ALL_ENTITIES_BY_BUSINESS_TYPE";
    private static final String ENTITY_DETAIL_URL = "/dynamicbusiness/business/entities/get-by-id";
    private static final String ENTITY_CREATE_URL = "/dynamicbusiness/business/entities/create";
    private static final String ENTITY_UPDATE_URL = "/dynamicbusiness/business/entities/update";
    private static final String ENTITY_DELETE_URL = "/dynamicbusiness/business/entities/delete";
    private static final String CATEGORY_TREE_URL = "/dynamicbusiness/category/tree";

    private CapabilityBlockProjectionBuilder() {
    }

    public static Map<String, Object> buildDynamicEntity(
            String businessTypeCode,
            String componentCode,
            long version,
            List<Map<String, Object>> displayFields,
            List<Map<String, Object>> filterFields,
            List<String> searchableFieldKeys,
            List<String> sortableFieldKeys) {
        Map<String, Object> projection = baseHeader(
                businessTypeCode,
                BusinessCategoryConstants.DYNAMIC,
                BusinessCategoryConstants.KIND_ENTITY,
                componentCode,
                version);

        switch (componentCode) {
            case "list", "table" -> appendEntityListLikeBlocks(
                    projection, businessTypeCode, displayFields, filterFields,
                    searchableFieldKeys, sortableFieldKeys, true);
            case "card" -> appendEntityListLikeBlocks(
                    projection, businessTypeCode, displayFields, filterFields,
                    searchableFieldKeys, sortableFieldKeys, false);
            case "tree" -> appendEntityTreeBlocks(
                    projection, businessTypeCode, displayFields, filterFields, searchableFieldKeys);
            default -> throw new IllegalArgumentException("unsupported componentCode: " + componentCode);
        }
        return projection;
    }

    /** @deprecated 使用 {@link #buildDynamicEntity} */
    @Deprecated
    public static Map<String, Object> buildDynamic(
            String businessTypeCode,
            String componentCode,
            long version,
            List<Map<String, Object>> displayFields,
            List<Map<String, Object>> filterFields,
            List<String> searchableFieldKeys,
            List<String> sortableFieldKeys) {
        return buildDynamicEntity(
                businessTypeCode, componentCode, version,
                displayFields, filterFields, searchableFieldKeys, sortableFieldKeys);
    }

    public static Map<String, Object> buildDynamicModel(
            String businessTypeCode,
            String componentCode,
            long version,
            List<Map<String, Object>> displayFields,
            List<Map<String, Object>> filterFields,
            List<String> searchableFieldKeys,
            List<String> sortableFieldKeys) {
        Map<String, Object> projection = baseHeader(
                businessTypeCode,
                BusinessCategoryConstants.DYNAMIC,
                BusinessCategoryConstants.KIND_MODEL,
                componentCode,
                version);

        switch (componentCode) {
            case "list", "table" -> appendModelListLikeBlocks(
                    projection, businessTypeCode, displayFields, filterFields,
                    searchableFieldKeys, sortableFieldKeys, true);
            case "card" -> appendModelListLikeBlocks(
                    projection, businessTypeCode, displayFields, filterFields,
                    searchableFieldKeys, sortableFieldKeys, false);
            case "tree" -> appendModelTreeBlocks(
                    projection, businessTypeCode, displayFields, filterFields, searchableFieldKeys);
            default -> throw new IllegalArgumentException("unsupported componentCode: " + componentCode);
        }
        return projection;
    }

    public static Map<String, Object> buildSystem(
            SystemCapabilityDefinition definition,
            String componentCode,
            long version,
            List<Map<String, Object>> displayFields,
            List<Map<String, Object>> filterFields,
            List<String> searchableFieldKeys) {
        Map<String, Object> projection = baseHeader(
                definition.getBusinessTypeCode(), BusinessCategoryConstants.SYSTEM,
                BusinessCategoryConstants.KIND_ENTITY, componentCode, version);

        Map<String, Object> endpoint = new LinkedHashMap<>();
        endpoint.put("url", definition.getReadUrl());
        endpoint.put("method", definition.getReadMethod());
        if (definition.isPaginated()) {
            endpoint.put("defaultParams", Map.of("pageNo", 1, "pageSize", 10));
        }
        endpoint.put("responseMapping", listResponseMapping());

        switch (componentCode) {
            case "list", "table" -> {
                projection.put("getList", readBlock(endpoint, displayFields));
                appendSearch(projection, searchableFieldKeys);
                appendFilter(projection, filterFields);
                appendSort(projection, List.of());
                appendPagination(projection);
            }
            case "card" -> {
                projection.put("getCard", readBlock(endpoint, displayFields));
                appendSearch(projection, searchableFieldKeys);
                appendFilter(projection, filterFields);
                appendPagination(projection);
            }
            case "tree" -> {
                Map<String, Object> treeEndpoint = new LinkedHashMap<>(endpoint);
                treeEndpoint.put("responseMapping", treeResponseMapping());
                projection.put("getTree", readBlock(treeEndpoint, displayFields));
                appendSearch(projection, searchableFieldKeys);
                projection.put("externalInputs", List.of(
                        externalInput("rootId", "parentId", "readQuery", true)));
            }
            default -> throw new IllegalArgumentException("unsupported componentCode: " + componentCode);
        }
        return projection;
    }

    private static Map<String, Object> baseHeader(
            String businessTypeCode,
            String businessCategory,
            String dataKind,
            String componentCode,
            long version) {
        Map<String, Object> projection = new LinkedHashMap<>();
        projection.put("businessTypeCode", businessTypeCode);
        projection.put("businessCategory", businessCategory);
        projection.put("dataKind", dataKind);
        projection.put("componentCode", componentCode);
        projection.put("version", version);
        return projection;
    }

    private static void appendEntityListLikeBlocks(
            Map<String, Object> projection,
            String businessTypeCode,
            List<Map<String, Object>> displayFields,
            List<Map<String, Object>> filterFields,
            List<String> searchableFieldKeys,
            List<String> sortableFieldKeys,
            boolean includeSort) {
        Map<String, Object> endpoint = entityListEndpoint(businessTypeCode);
        String readBlockKey = projection.get("componentCode").equals("card") ? "getCard" : "getList";
        projection.put(readBlockKey, readBlock(endpoint, displayFields));
        projection.put("getDetail", detailBlock(businessTypeCode));
        appendSearch(projection, searchableFieldKeys);
        appendFilter(projection, filterFields);
        if (includeSort) {
            appendSort(projection, sortableFieldKeys);
        }
        appendPagination(projection);
        appendCrudBlocks(projection);
        projection.put("asyncChecks", List.of());
        projection.put("externalInputs", List.of(
                externalInput("category", "categoryId", "readBody", true),
                externalInput("modelIds", "modelIds", "readBody", true)));
    }

    private static void appendEntityTreeBlocks(
            Map<String, Object> projection,
            String businessTypeCode,
            List<Map<String, Object>> displayFields,
            List<Map<String, Object>> filterFields,
            List<String> searchableFieldKeys) {
        Map<String, Object> endpoint = new LinkedHashMap<>();
        endpoint.put("url", CATEGORY_TREE_URL);
        endpoint.put("method", "GET");
        endpoint.put("defaultParams", Map.of("businessTypeCode", businessTypeCode));
        endpoint.put("responseMapping", treeResponseMapping());
        projection.put("getTree", readBlock(endpoint, displayFields));
        projection.put("getDetail", detailBlock(businessTypeCode));
        appendSearch(projection, searchableFieldKeys);
        appendFilter(projection, filterFields);
        appendCrudBlocks(projection);
        projection.put("asyncChecks", List.of());
        projection.put("externalInputs", List.of(
                externalInput("rootId", "parentId", "readQuery", true)));
    }

    private static void appendModelListLikeBlocks(
            Map<String, Object> projection,
            String businessTypeCode,
            List<Map<String, Object>> displayFields,
            List<Map<String, Object>> filterFields,
            List<String> searchableFieldKeys,
            List<String> sortableFieldKeys,
            boolean includeSort) {
        Map<String, Object> endpoint = modelListEndpoint(businessTypeCode);
        String readBlockKey = "card".equals(projection.get("componentCode")) ? "getCard" : "getList";
        projection.put(readBlockKey, readBlock(endpoint, displayFields));
        appendSearch(projection, searchableFieldKeys);
        appendFilter(projection, filterFields);
        if (includeSort) {
            appendSort(projection, sortableFieldKeys);
        }
        appendPagination(projection);
        projection.put("asyncChecks", List.of());
        projection.put("externalInputs", List.of());
    }

    private static void appendModelTreeBlocks(
            Map<String, Object> projection,
            String businessTypeCode,
            List<Map<String, Object>> displayFields,
            List<Map<String, Object>> filterFields,
            List<String> searchableFieldKeys) {
        Map<String, Object> endpoint = new LinkedHashMap<>();
        endpoint.put("url", CATEGORY_TREE_URL);
        endpoint.put("method", "GET");
        endpoint.put("defaultParams", Map.of("businessTypeCode", businessTypeCode));
        endpoint.put("responseMapping", treeResponseMapping());
        projection.put("getTree", readBlock(endpoint, displayFields));
        appendSearch(projection, searchableFieldKeys);
        appendFilter(projection, filterFields);
        projection.put("asyncChecks", List.of());
        projection.put("externalInputs", List.of(
                externalInput("rootId", "parentId", "readQuery", true)));
    }

    private static Map<String, Object> modelListEndpoint(String businessTypeCode) {
        Map<String, Object> endpoint = new LinkedHashMap<>();
        endpoint.put("url", MODEL_PAGE_URL);
        endpoint.put("method", "GET");
        Map<String, Object> defaultParams = new LinkedHashMap<>();
        defaultParams.put("pageNo", 1);
        defaultParams.put("pageSize", 10);
        defaultParams.put("businessTypeCode", businessTypeCode);
        endpoint.put("defaultParams", defaultParams);
        endpoint.put("responseMapping", listResponseMapping());
        return endpoint;
    }

    private static Map<String, Object> entityListEndpoint(String businessTypeCode) {
        Map<String, Object> endpoint = new LinkedHashMap<>();
        endpoint.put("url", ENTITY_SCENE_QUERY_URL);
        endpoint.put("method", "GET");
        endpoint.put("paramStyle", "entity-scene");
        Map<String, Object> defaultParams = new LinkedHashMap<>();
        defaultParams.put("pageNo", 1);
        defaultParams.put("pageSize", 10);
        defaultParams.put("businessTypeCode", businessTypeCode);
        defaultParams.put("scene", ENTITY_SCENE_DEFAULT);
        defaultParams.put("resultShape", "PAGE");
        defaultParams.put("resultDetail", "FULL");
        endpoint.put("defaultParams", defaultParams);
        endpoint.put("responseMapping", scenePageResponseMapping());
        return endpoint;
    }

    private static Map<String, Object> detailBlock(String businessTypeCode) {
        Map<String, Object> endpoint = new LinkedHashMap<>();
        endpoint.put("url", ENTITY_DETAIL_URL);
        endpoint.put("method", "GET");
        endpoint.put("defaultParams", Map.of("businessTypeCode", businessTypeCode));
        return Map.of("endpoint", endpoint);
    }

    private static Map<String, Object> readBlock(
            Map<String, Object> endpoint, List<Map<String, Object>> displayFields) {
        Map<String, Object> block = new LinkedHashMap<>();
        block.put("endpoint", endpoint);
        block.put("fields", toProjectionFieldDefs(displayFields));
        return block;
    }

    private static List<Map<String, Object>> toProjectionFieldDefs(List<Map<String, Object>> displayFields) {
        List<Map<String, Object>> fields = new ArrayList<>(displayFields.size());
        for (Map<String, Object> source : displayFields) {
            String fieldCode = String.valueOf(source.getOrDefault("fieldKey", source.get("id")));
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("fieldCode", fieldCode);
            item.put("fieldKey", fieldCode);
            item.put("label", source.get("label"));
            item.put("fieldType", mapFieldType(String.valueOf(source.getOrDefault("renderAs", "text"))));
            item.put("renderAs", source.getOrDefault("renderAs", "text"));
            item.put("sortOrder", source.getOrDefault("sortOrder", 0));
            item.put("defaultVisible", source.getOrDefault("defaultVisible", false));
            if (source.get("applicableViews") != null) {
                item.put("applicableViews", source.get("applicableViews"));
            }
            fields.add(item);
        }
        return fields;
    }

    private static String mapFieldType(String renderAs) {
        if (!StringUtils.hasText(renderAs)) {
            return "text";
        }
        return switch (renderAs.trim().toLowerCase()) {
            case "boolean" -> "boolean";
            case "dict" -> "enum";
            case "datetime" -> "datetime";
            default -> "text";
        };
    }

    private static void appendSearch(Map<String, Object> projection, List<String> searchableFieldKeys) {
        if (searchableFieldKeys.isEmpty()) {
            return;
        }
        projection.put("search", Map.of(
                "paramKey", "keyword",
                "searchableFields", searchableFieldKeys));
    }

    private static void appendFilter(Map<String, Object> projection, List<Map<String, Object>> filterFields) {
        if (filterFields.isEmpty()) {
            return;
        }
        List<Map<String, Object>> fields = new ArrayList<>(filterFields.size());
        for (Map<String, Object> source : filterFields) {
            String fieldCode = String.valueOf(source.getOrDefault("fieldKey", source.get("id")));
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("fieldCode", fieldCode);
            item.put("fieldKey", fieldCode);
            item.put("id", source.getOrDefault("id", fieldCode));
            item.put("label", source.get("label"));
            item.put("control", source.getOrDefault("control", "input"));
            item.put("fieldType", source.getOrDefault("control", "input"));
            item.put("sortOrder", source.getOrDefault("sortOrder", 0));
            item.put("bindTo", source.getOrDefault("bindTo", "field-filter"));
            item.put("operators", List.of("eq"));
            item.put("searchable", source.getOrDefault("searchable", false));
            item.put("sortable", source.getOrDefault("sortable", false));
            item.put("defaultVisible", source.getOrDefault("defaultVisible", false));
            fields.add(item);
        }
        projection.put("filter", Map.of("fields", fields));
    }

    private static void appendSort(Map<String, Object> projection, List<String> sortableFieldKeys) {
        List<Map<String, Object>> fields = new ArrayList<>();
        for (String fieldCode : sortableFieldKeys) {
            fields.add(Map.of("fieldCode", fieldCode, "label", fieldCode));
        }
        projection.put("sort", Map.of(
                "sortByParam", "orderByColumn",
                "sortDirParam", "isAsc",
                "fields", fields));
    }

    private static void appendPagination(Map<String, Object> projection) {
        projection.put("pagination", Map.of(
                "defaultPageSize", 10,
                "pageSizeOptions", List.of(10, 20, 50, 100),
                "paramKeys", Map.of("pageNo", "pageNo", "pageSize", "pageSize")));
    }

    private static void appendCrudBlocks(Map<String, Object> projection) {
        projection.put("create", writeBlock(ENTITY_CREATE_URL, "POST"));
        projection.put("update", writeBlock(ENTITY_UPDATE_URL, "PUT"));
        projection.put("delete", writeBlock(ENTITY_DELETE_URL, "DELETE"));
    }

    private static Map<String, Object> writeBlock(String url, String method) {
        return Map.of("endpoint", Map.of("url", url, "method", method));
    }

    private static Map<String, Object> externalInput(
            String key, String paramKey, String paramScope, boolean optional) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("key", key);
        item.put("paramKey", paramKey);
        item.put("paramScope", paramScope);
        item.put("optional", optional);
        return item;
    }

    private static Map<String, Object> listResponseMapping() {
        Map<String, Object> mapping = new LinkedHashMap<>();
        mapping.put("listPath", "list");
        mapping.put("totalPath", "total");
        mapping.put("idField", "id");
        mapping.put("labelField", "name");
        return mapping;
    }

    /** query-by-scene + resultShape=PAGE 的响应映射 */
    private static Map<String, Object> scenePageResponseMapping() {
        Map<String, Object> mapping = new LinkedHashMap<>();
        mapping.put("listPath", "page.list");
        mapping.put("totalPath", "page.total");
        mapping.put("idField", "id");
        mapping.put("labelField", "name");
        return mapping;
    }

    private static Map<String, Object> treeResponseMapping() {
        Map<String, Object> mapping = new LinkedHashMap<>();
        mapping.put("idField", "id");
        mapping.put("labelField", "name");
        mapping.put("childrenField", "children");
        return mapping;
    }
}

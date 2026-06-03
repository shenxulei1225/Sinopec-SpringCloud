package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 将 registry 完整契约投影为组件所需视图（list 只含 list/page + CRUD；tree 只含 tree 读端点）。
 */
public final class CapabilityComponentViewBuilder {

    private static final Set<String> LIST_LIKE = Set.of("list", "table", "card");

    private CapabilityComponentViewBuilder() {
    }

    public static ComponentCapabilityViewRespVO build(
            Map<String, Object> contract, String instanceKey, String componentCode) {
        String code = normalizeComponentCode(componentCode);
        ComponentCapabilityViewRespVO view = new ComponentCapabilityViewRespVO();
        view.setInstanceKey(instanceKey);
        view.setQueryContractKey(instanceKey);
        view.setComponentCode(code);
        view.setLabel(stringVal(contract.get("label")));
        view.setDomain(stringVal(contract.get("domain")));
        Object version = contract.get("version");
        if (version instanceof Number number) {
            view.setVersion(number.intValue());
        }

        ComponentCapabilityReadRespVO read = new ComponentCapabilityReadRespVO();
        if ("tree".equals(code)) {
            read.setEndpoint(toEndpointVo(resolveTreeEndpoint(contract)));
            read.setFilters(filterFieldsForView(contract, "tree"));
            read.setDisplayFields(displayFieldsForView(contract, "tree"));
            view.setRead(read);
            view.setWrite(null);
            return view;
        }

        read.setEndpoint(toEndpointVo(resolveListEndpoint(contract)));
        read.setFilters(filterFieldsForView(contract, code));
        read.setDisplayFields(displayFieldsForView(contract, code));
        view.setRead(read);
        view.setWrite(buildWriteSection(contract));
        return view;
    }

    @SuppressWarnings("unchecked")
    static List<Map<String, Object>> asyncChecksFromContract(Map<String, Object> contract) {
        Object checks = contract.get("asyncChecks");
        if (checks instanceof List<?> list) {
            return (List<Map<String, Object>>) list;
        }
        return List.of();
    }

    public static String normalizeComponentCode(String componentCode) {
        if (componentCode == null || componentCode.isBlank()) {
            throw new ServiceException(400, "componentCode 不能为空");
        }
        String code = componentCode.trim().toLowerCase(Locale.ROOT);
        if (!LIST_LIKE.contains(code) && !"tree".equals(code)) {
            throw new ServiceException(400, "不支持的 componentCode: " + componentCode + "（支持 list / tree / table / card）");
        }
        return code;
    }

    private static ComponentCapabilityWriteRespVO buildWriteSection(Map<String, Object> contract) {
        ComponentCapabilityWriteRespVO write = new ComponentCapabilityWriteRespVO();
        write.setCreate(toWriteEndpoint(resolveWriteEndpoint(contract, "create")));
        write.setUpdate(toWriteEndpoint(resolveWriteEndpoint(contract, "update")));
        write.setDelete(toWriteEndpoint(resolveWriteEndpoint(contract, "delete")));
        write.setAsyncChecks(asyncChecksFromContract(contract));
        if (write.getCreate() == null && write.getUpdate() == null && write.getDelete() == null) {
            return null;
        }
        return write;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> filterFieldsForView(Map<String, Object> contract, String viewCode) {
        Object filters = contract.get("filters");
        if (!(filters instanceof List<?> list)) {
            return List.of();
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object item : list) {
            if (item instanceof Map<?, ?> map) {
                result.add(new LinkedHashMap<>((Map<String, Object>) map));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> displayFieldsForView(Map<String, Object> contract, String viewCode) {
        Object fields = contract.get("displayFields");
        if (!(fields instanceof List<?> list) || list.isEmpty()) {
            return List.of();
        }
        String viewType = "tree".equals(viewCode) ? "tree" : "list";
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object item : list) {
            if (!(item instanceof Map<?, ?> raw)) {
                continue;
            }
            Map<String, Object> field = (Map<String, Object>) raw;
            if (!appliesToView(field, viewType)) {
                continue;
            }
            result.add(new LinkedHashMap<>(field));
        }
        if (result.isEmpty() && !"tree".equals(viewType)) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> raw) {
                    result.add(new LinkedHashMap<>((Map<String, Object>) raw));
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static boolean appliesToView(Map<String, Object> field, String viewType) {
        Object applicable = field.get("applicableViews");
        if (!(applicable instanceof List<?> views) || views.isEmpty()) {
            return true;
        }
        return views.contains(viewType) || views.contains("table") && "list".equals(viewType);
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> resolveListEndpoint(Map<String, Object> contract) {
        Map<String, Object> dataSource = firstDataSource(contract);
        List<Map<String, Object>> endpoints = (List<Map<String, Object>>) dataSource.get("endpoints");
        if (endpoints == null || endpoints.isEmpty()) {
            throw new ServiceException(500, "契约缺少 dataSources.endpoints");
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
        throw new ServiceException(500, "契约缺少 list/page 类 endpoint");
    }

    @SuppressWarnings("unchecked")
    static Map<String, Object> resolveTreeEndpoint(Map<String, Object> contract) {
        Map<String, Object> dataSource = firstDataSource(contract);
        List<Map<String, Object>> endpoints = (List<Map<String, Object>>) dataSource.get("endpoints");
        if (endpoints == null || endpoints.isEmpty()) {
            throw new ServiceException(500, "契约缺少 dataSources.endpoints");
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
        throw new ServiceException(500, "契约缺少 tree 类 endpoint");
    }

    private static boolean isListPurpose(Map<String, Object> ep) {
        String purpose = String.valueOf(ep.getOrDefault("purpose", "list"));
        return "list".equals(purpose) || "search".equals(purpose);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> firstDataSource(Map<String, Object> contract) {
        List<Map<String, Object>> sources = (List<Map<String, Object>>) contract.get("dataSources");
        if (sources == null || sources.isEmpty()) {
            throw new ServiceException(500, "契约缺少 dataSources");
        }
        return sources.get(0);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> resolveWriteEndpoint(Map<String, Object> contract, String purpose) {
        Map<String, Object> dataSource = firstDataSource(contract);
        List<Map<String, Object>> endpoints = (List<Map<String, Object>>) dataSource.get("endpoints");
        if (endpoints != null) {
            for (Map<String, Object> ep : endpoints) {
                if (purpose.equals(ep.get("purpose"))) {
                    return ep;
                }
            }
        }
        List<Map<String, Object>> actions = (List<Map<String, Object>>) contract.get("actions");
        if (actions == null || endpoints == null) {
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
    private static ComponentCapabilityEndpointRespVO toEndpointVo(Map<String, Object> ep) {
        ComponentCapabilityEndpointRespVO vo = new ComponentCapabilityEndpointRespVO();
        vo.setId(stringVal(ep.get("id")));
        vo.setPurpose(stringVal(ep.get("purpose")));
        vo.setUrl(stringVal(ep.get("url")));
        vo.setMethod(stringVal(ep.getOrDefault("method", "GET")));
        vo.setParamStyle(stringVal(ep.get("paramStyle")));
        vo.setActivateWhen(stringVal(ep.get("activateWhen")));
        Object defaultParams = ep.get("defaultParams");
        if (defaultParams instanceof Map<?, ?> map) {
            vo.setDefaultParams(new LinkedHashMap<>((Map<String, Object>) map));
        }
        Object mapping = ep.get("responseMapping");
        if (mapping instanceof Map<?, ?> map) {
            vo.setResponseMapping(new LinkedHashMap<>((Map<String, Object>) map));
        }
        return vo;
    }

    @SuppressWarnings("unchecked")
    private static ComponentCapabilityWriteEndpointRespVO toWriteEndpoint(Map<String, Object> ep) {
        if (ep == null) {
            return null;
        }
        ComponentCapabilityWriteEndpointRespVO vo = new ComponentCapabilityWriteEndpointRespVO();
        vo.setUrl(stringVal(ep.get("url")));
        vo.setMethod(stringVal(ep.getOrDefault("method", "POST")));
        vo.setParamStyle(stringVal(ep.get("paramStyle")));
        Object requestBody = ep.get("requestBody");
        if (requestBody instanceof Map<?, ?> body) {
            Object fields = body.get("fields");
            if (fields instanceof List<?> list && !list.isEmpty()) {
                List<Map<String, Object>> copied = new ArrayList<>();
                for (Object item : list) {
                    if (item instanceof Map<?, ?> map) {
                        copied.add(new LinkedHashMap<>((Map<String, Object>) map));
                    }
                }
                vo.setFields(copied);
            }
        }
        return vo;
    }

    private static String stringVal(Object value) {
        return value == null ? null : String.valueOf(value);
    }
}

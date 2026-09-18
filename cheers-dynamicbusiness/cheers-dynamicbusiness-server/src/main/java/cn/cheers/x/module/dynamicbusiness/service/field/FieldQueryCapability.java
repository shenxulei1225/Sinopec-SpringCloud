package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.cheers.x.framework.common.exception.ServiceException;

/**
 * 字段值类型决定「搜索 / 筛选 / 排序」能不能开。
 *
 * <p>搜索进关键词；筛选进列表筛选区；排序进列表头。三者分开，禁止互相冒充。</p>
 *
 * <p>不负责：列表组件要不要画出筛选条（那是列表 props，不是型号开关）。</p>
 */
public final class FieldQueryCapability {

    private FieldQueryCapability() {
    }

    public static boolean canSearch(String fieldType) {
        return isTextLike(fieldType);
    }

    public static boolean canFilter(String fieldType) {
        return isNumberOrDate(fieldType) || isOptionOrRef(fieldType);
    }

    public static boolean canSort(String fieldType) {
        return isNumberOrDate(fieldType);
    }

    public static void assertCanEnable(String fieldType, Boolean searchable, Boolean filterable, Boolean sortable) {
        if (Boolean.TRUE.equals(searchable) && !canSearch(fieldType)) {
            throw new ServiceException(400, denyReason(fieldType, "isSearchable"));
        }
        if (Boolean.TRUE.equals(filterable) && !canFilter(fieldType)) {
            throw new ServiceException(400, denyReason(fieldType, "isFilterable"));
        }
        if (Boolean.TRUE.equals(sortable) && !canSort(fieldType)) {
            throw new ServiceException(400, denyReason(fieldType, "isSortable"));
        }
    }

    public static String denyReason(String fieldType, String ruleKey) {
        if ("isSearchable".equals(ruleKey)) {
            return "该字段类型不能搜索，请用筛选区或列表头排序";
        }
        if ("isFilterable".equals(ruleKey)) {
            return "该字段类型不能进筛选区，文本请用搜索";
        }
        if ("isSortable".equals(ruleKey)) {
            return "该字段类型不能在列表头排序";
        }
        return "该字段类型不支持此查询能力";
    }

    private static String normalize(String fieldType) {
        return fieldType == null ? "" : fieldType.trim().toUpperCase();
    }

    private static boolean isTextLike(String fieldType) {
        String type = normalize(fieldType);
        return "TEXT".equals(type) || "STRING".equals(type)
                || "TEXT_SHORT".equals(type) || "LONG_TEXT".equals(type);
    }

    private static boolean isNumberOrDate(String fieldType) {
        String type = normalize(fieldType);
        return "NUMBER".equals(type) || "INTEGER".equals(type) || "FLOAT".equals(type)
                || "DECIMAL".equals(type) || "LONG".equals(type) || "DOUBLE".equals(type)
                || "DATE".equals(type) || "DATETIME".equals(type) || "TIMESTAMP".equals(type);
    }

    private static boolean isOptionOrRef(String fieldType) {
        String type = normalize(fieldType);
        return "BOOLEAN".equals(type) || "BOOL".equals(type)
                || "ENUM".equals(type) || "SELECT".equals(type) || "OPTION".equals(type)
                || "MULTI_SELECT".equals(type)
                || "ENTITY_REF".equals(type) || "ENTITY_REF_MULTI".equals(type)
                || "BATCH_ENTITY_REF".equals(type) || "ENTITY_SELF_REF".equals(type)
                || "REFERENCE".equals(type);
    }
}

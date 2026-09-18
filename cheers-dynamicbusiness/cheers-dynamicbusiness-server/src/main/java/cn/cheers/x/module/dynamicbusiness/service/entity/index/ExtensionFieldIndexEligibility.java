package cn.cheers.x.module.dynamicbusiness.service.entity.index;

import cn.cheers.x.module.dynamicbusiness.service.field.FieldQueryCapability;

/**
 * 扩展字段要不要写入索引表。
 *
 * <p>型号上打开了该类型允许的搜索 / 筛选 / 排序任一能力时，值必须进索引表；
 * 时间段筛选等不能扫实体扩展 JSON。</p>
 *
 * <p>类型不允许的开关即使库里是 true 也不进索引，避免脏开关冒充能力。</p>
 *
 * <p>不负责：关键词 / 筛选 / 排序各自的查询门禁；基础字段物理列。</p>
 */
public final class ExtensionFieldIndexEligibility {

    private ExtensionFieldIndexEligibility() {
    }

    /**
     * 可搜索：类型允许，且分配上明确打开。空值不当作打开。
     */
    public static boolean resolveSearchable(Boolean configured, String fieldType) {
        return FieldQueryCapability.canSearch(fieldType) && Boolean.TRUE.equals(configured);
    }

    /**
     * 可筛选：类型允许，且分配上明确打开。空值不当作打开。
     */
    public static boolean resolveFilterable(Boolean configured, String fieldType) {
        return FieldQueryCapability.canFilter(fieldType) && Boolean.TRUE.equals(configured);
    }

    /**
     * 可排序：类型允许，且分配上明确打开。空值不当作打开。
     */
    public static boolean resolveSortable(Boolean configured, String fieldType) {
        return FieldQueryCapability.canSort(fieldType) && Boolean.TRUE.equals(configured);
    }

    /**
     * 三个能力任一为真，该扩展字段就要在索引表里。
     */
    public static boolean shouldWriteIndex(Boolean searchable, Boolean filterable, Boolean sortable,
                                           String fieldType) {
        return resolveSearchable(searchable, fieldType)
                || resolveFilterable(filterable, fieldType)
                || resolveSortable(sortable, fieldType);
    }
}

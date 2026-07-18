package cn.cheers.x.module.dynamicbusiness.util;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;

import java.util.List;
import java.util.Objects;

/**
 * 数据管理顶栏「未分类」快捷按钮对应的保留分类桶（如 code 含 UNCATEGORIZED）。
 * 查询「全部分类 / 未分类」时应排除，避免与树节点 bucket 重复计数。
 */
public final class DataMgmtCategoryReservedNodes {

    private DataMgmtCategoryReservedNodes() {
    }

    public static boolean isReservedUncategorizedBucket(String categoryCode) {
        if (categoryCode == null || categoryCode.isBlank()) {
            return false;
        }
        return categoryCode.toUpperCase().contains("UNCATEGORIZED");
    }

    public static boolean isReservedUncategorizedBucket(CategoryDO category) {
        return category != null && isReservedUncategorizedBucket(category.getCode());
    }

    public static List<CategoryDO> filterQueryableCategories(List<CategoryDO> categories) {
        if (categories == null || categories.isEmpty()) {
            return List.of();
        }
        return categories.stream()
                .filter(category -> !isReservedUncategorizedBucket(category))
                .toList();
    }

    public static List<Long> filterQueryableCategoryIds(List<CategoryDO> categories) {
        return filterQueryableCategories(categories).stream()
                .map(CategoryDO::getId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }
}

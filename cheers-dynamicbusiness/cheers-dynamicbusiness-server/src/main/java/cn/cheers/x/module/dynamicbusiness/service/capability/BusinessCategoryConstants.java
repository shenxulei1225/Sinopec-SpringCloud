package cn.cheers.x.module.dynamicbusiness.service.capability;

/**
 * 业务分类常量（与组件配置 dataSource.businessCategory 对齐）。
 */
public final class BusinessCategoryConstants {

    public static final String DYNAMIC = "dynamic";
    public static final String SYSTEM = "system";
    public static final String KIND_MODEL = "model";
    public static final String KIND_ENTITY = "entity";

    private BusinessCategoryConstants() {
    }

    public static boolean isSystem(String businessCategory) {
        return SYSTEM.equals(businessCategory);
    }

    public static boolean isDynamic(String businessCategory) {
        return DYNAMIC.equals(businessCategory);
    }

    public static void requireKnownCategory(String businessCategory) {
        if (!isDynamic(businessCategory) && !isSystem(businessCategory)) {
            throw new IllegalArgumentException("businessCategory 须为 dynamic 或 system");
        }
    }
}

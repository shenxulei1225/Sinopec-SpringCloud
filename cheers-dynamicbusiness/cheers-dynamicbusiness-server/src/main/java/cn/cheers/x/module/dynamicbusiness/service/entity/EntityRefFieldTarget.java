package cn.cheers.x.module.dynamicbusiness.service.entity;

/**
 * 引用字段声明的目标业务类型。
 *
 * <p>权威在字段本身：字段库 {@code provider_code=dynamic-entity:动作类型编码}，
 * 或型号分配上的目标类型。保存关联只认这里，不查旧的业务类型关联许可表。</p>
 *
 * <p>不负责：写实体关联边；猜未声明的目标类型。</p>
 */
public final class EntityRefFieldTarget {

    private static final String DYNAMIC_ENTITY_PREFIX = "dynamic-entity:";

    private EntityRefFieldTarget() {
    }

    /**
     * 从字段库来源编码读出指向哪类业务。不是 dynamic-entity 前缀则空，不猜。
     */
    public static String fromProviderCode(String providerCode) {
        if (providerCode == null) {
            return "";
        }
        String raw = providerCode.trim();
        if (raw.length() <= DYNAMIC_ENTITY_PREFIX.length()) {
            return "";
        }
        if (!raw.regionMatches(true, 0, DYNAMIC_ENTITY_PREFIX, 0, DYNAMIC_ENTITY_PREFIX.length())) {
            return "";
        }
        return raw.substring(DYNAMIC_ENTITY_PREFIX.length()).trim();
    }

    public static String firstDeclared(String... candidates) {
        if (candidates == null) {
            return "";
        }
        for (String item : candidates) {
            if (item != null && !item.isBlank()) {
                return item.trim();
            }
        }
        return "";
    }
}

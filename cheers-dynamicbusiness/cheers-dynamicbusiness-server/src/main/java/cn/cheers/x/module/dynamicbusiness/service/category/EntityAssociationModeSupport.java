package cn.cheers.x.module.dynamicbusiness.service.category;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;

import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.CATEGORY_TYPE_MODE_INVALID;

/**
 * 实体与分类挂靠方式（单归属 / 多归属）。
 * <p>
 * 权威配置在分类列组件配置（props.categoryAssociation）；分类种类上的字段仅作参考/兼容回退。
 * 与 {@link CategoryModeSupport}（简单/高级分类）正交：只约束分类–实体关联条数语义，不声明 REF 字段。
 */
public final class EntityAssociationModeSupport {

    public static final String SINGLE = "SINGLE";
    public static final String MULTI = "MULTI";

    private EntityAssociationModeSupport() {
    }

    public static String normalizeForWrite(String raw) {
        if (raw == null || raw.isBlank()) {
            return MULTI;
        }
        String mode = raw.trim().toUpperCase();
        if (SINGLE.equals(mode) || MULTI.equals(mode)) {
            return mode;
        }
        throw ServiceExceptionUtil.exception(CATEGORY_TYPE_MODE_INVALID);
    }

    public static String resolveFromType(CategoryTypeDO type) {
        if (type == null) {
            return MULTI;
        }
        return normalizeForWrite(type.getEntityAssociationMode());
    }

    public static boolean isSingle(CategoryTypeDO type) {
        return SINGLE.equals(resolveFromType(type));
    }

    public static boolean isSingle(String mode) {
        return SINGLE.equals(normalizeForWrite(mode));
    }
}

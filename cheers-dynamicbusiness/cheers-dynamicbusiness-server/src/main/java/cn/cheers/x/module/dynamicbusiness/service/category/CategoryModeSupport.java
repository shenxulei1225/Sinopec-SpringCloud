package cn.cheers.x.module.dynamicbusiness.service.category;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;

import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.CATEGORY_TYPE_MODE_INVALID;

/**
 * 分类建立方式（categoryMode）标准解析。
 * <p>
 * SIMPLE / ADVANCED 只挂在种类（CategoryType）上；节点 CRUD 必须以种类 mode 分支，
 * 不得用「是否存在 category-entity link」反推种类语义。
 */
public final class CategoryModeSupport {

    public static final String SIMPLE = "SIMPLE";
    public static final String ADVANCED = "ADVANCED";

    private CategoryModeSupport() {
    }

    /**
     * 写入种类时归一化；非法值拒绝。空 → SIMPLE。兼容 FILTER / ORG_RECORD / PATTERN_C。
     */
    public static String normalizeForWrite(String raw) {
        if (raw == null || raw.isBlank()) {
            return SIMPLE;
        }
        String mode = mapAlias(raw.trim().toUpperCase());
        if (SIMPLE.equals(mode) || ADVANCED.equals(mode)) {
            return mode;
        }
        throw ServiceExceptionUtil.exception(CATEGORY_TYPE_MODE_INVALID);
    }

    /**
     * 读库/运行时解析；空或缺种类按 SIMPLE。非法别名已映射，未知值拒绝。
     */
    public static String resolveFromType(CategoryTypeDO type) {
        if (type == null) {
            throw new ServiceException(404, "分类种类不存在");
        }
        return normalizeForWrite(type.getCategoryMode());
    }

    public static boolean isAdvanced(String mode) {
        return ADVANCED.equals(mode);
    }

    public static boolean isAdvanced(CategoryTypeDO type) {
        return isAdvanced(resolveFromType(type));
    }

    private static String mapAlias(String mode) {
        if ("FILTER".equals(mode)) {
            return SIMPLE;
        }
        if ("ORG_RECORD".equals(mode) || "PATTERN_C".equals(mode)) {
            return ADVANCED;
        }
        return mode;
    }
}

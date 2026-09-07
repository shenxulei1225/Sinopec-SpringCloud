package cn.cheers.x.module.dynamicbusiness.framework.entitytype;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.framework.hierarchy.OrgTreeParentFieldCodes;

/**
 * 平台系统字段与「用户可配基础字段」的边界。
 *
 * <p>部分系统字段（如站场级 {@code facility_id}、高级分类 {@code parentId}）由 ensure 落库，
 * 界面只经系统区 / 模型字段展示，不得出现在用户基础字段拖入区。</p>
 */
public final class EntityTypePlatformFieldSupport {

    private EntityTypePlatformFieldSupport() {
    }

    /**
     * 落库在 base_field 表、但只经系统区对外展示的基础字段编码。
     */
    public static boolean isPlatformOwnedPersistedFieldCode(String fieldCode) {
        if (fieldCode == null) {
            return false;
        }
        String code = fieldCode.trim();
        return FacilityOwningFieldCodes.FIELD_CODE.equals(code)
                || OrgTreeParentFieldCodes.FIELD_CODE.equalsIgnoreCase(code);
    }

    /**
     * {@code /list} 等面向用户配置基础字段的接口应排除的编码。
     */
    public static boolean excludeFromUserBaseFieldList(String fieldCode) {
        return isPlatformOwnedPersistedFieldCode(fieldCode);
    }

    /**
     * 模型字段行 {@code field_source}：站场级所属场站为 SYSTEM，其余基础字段为 BASE。
     */
    public static String resolveModelAssignmentFieldSource(String fieldCode) {
        return isPlatformOwnedPersistedFieldCode(fieldCode)
                ? ModelFieldAssignmentRespVO.FIELD_SOURCE_SYSTEM
                : ModelFieldAssignmentRespVO.FIELD_SOURCE_BASE;
    }
}

package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import java.util.List;

/**
 * 平台预置的一个业务能力包：编码、名称、业务页、目录模板。
 * 开通时按本定义建目录、写页面查看动作。不负责角色页上的手工勾选。
 *
 * @param unitEnablementRegistryCodes 非空时：租户开通后还须在本表所列目录的某条记录上登记启用，业务接口才放行。
 *                                    空列表表示只认租户级开通，不按单元拆。
 */
public record BusinessCapabilityPackDefinition(
        String code,
        String name,
        String description,
        List<String> pageCodes,
        List<String> catalogCodes,
        List<String> unitEnablementRegistryCodes
) {
    /** 兼容旧构造：不按管理单元/场站拆包。 */
    public BusinessCapabilityPackDefinition(
            String code,
            String name,
            String description,
            List<String> pageCodes,
            List<String> catalogCodes) {
        this(code, name, description, pageCodes, catalogCodes, List.of());
    }

    public boolean requiresUnitEnablement() {
        return unitEnablementRegistryCodes != null && !unitEnablementRegistryCodes.isEmpty();
    }
}

package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import java.util.List;

/**
 * 某业务租户已开通能力包推导出的可见范围。
 * 只表达「租户具备哪些业务」；角色是否有查看类动作另算。
 */
public record TenantCapabilityOpenedScope(
        List<String> packCodes,
        List<String> pageCodes,
        List<String> catalogCodes
) {
    public static TenantCapabilityOpenedScope empty() {
        return new TenantCapabilityOpenedScope(List.of(), List.of(), List.of());
    }
}

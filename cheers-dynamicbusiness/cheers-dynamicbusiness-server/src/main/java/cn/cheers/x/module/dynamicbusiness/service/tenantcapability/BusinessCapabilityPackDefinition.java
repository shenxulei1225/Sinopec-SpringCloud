package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

import java.util.List;

/**
 * 平台预置的一个业务能力包：编码、名称、业务页、目录模板。
 * 开通时按本定义建目录、写页面查看动作。不负责角色页上的手工勾选。
 */
public record BusinessCapabilityPackDefinition(
        String code,
        String name,
        String description,
        List<String> pageCodes,
        List<String> catalogCodes
) {
}

package cn.cheers.x.module.dynamicbusiness.service.tenantcapability;

/**
 * 配置页展示：某一锚点目录下可勾选的业务包（须租户已开通）。
 */
public record UnitEnablementPackOption(String code, String name, String description) {
}
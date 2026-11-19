package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import cn.cheers.x.framework.security.core.service.SecurityFrameworkService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 主数据治理能力。
 *
 * <p>建公司级标准型号（{@link #canCreateCompanyStandard}）按登录人定：超级管理员、租户管理员可以建，
 * 其他人建右上角选中站的本地型号。任职的范围只决定看哪些记录，不决定能不能建公司级型号。
 * 权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md。</p>
 *
 * <p>本地包提升、公司规格停用仍按配置项（{@code cheers.security.mock-enable} 时全部放开），待接入正式权限。</p>
 *
 * <p>不负责：本地型号所有权校验；不读工作模式或右上角选中的站。</p>
 */
@Component
public class DefaultMasterDataCapabilityChecker implements MasterDataCapabilityChecker {

    private static final String SUPER_ADMIN = "super_admin";
    private static final String TENANT_ADMIN = "tenant_admin";

    private final boolean mockSecurityEnabled;
    private final boolean promotePackageEnabled;
    private final boolean deactivateCompanyStandardEnabled;
    private final SecurityFrameworkService securityFrameworkService;

    public DefaultMasterDataCapabilityChecker(
            @Value("${cheers.security.mock-enable:false}") boolean mockSecurityEnabled,
            @Value("${cheers.dynamicbusiness.master-data-capability.can-promote-package:false}")
            boolean promotePackageEnabled,
            @Value("${cheers.dynamicbusiness.master-data-capability.can-deactivate-company-standard:false}")
            boolean deactivateCompanyStandardEnabled,
            SecurityFrameworkService securityFrameworkService) {
        this.mockSecurityEnabled = mockSecurityEnabled;
        this.promotePackageEnabled = promotePackageEnabled;
        this.deactivateCompanyStandardEnabled = deactivateCompanyStandardEnabled;
        this.securityFrameworkService = securityFrameworkService;
    }

    @Override
    public boolean canPromotePackage() {
        return mockSecurityEnabled || promotePackageEnabled;
    }

    /** 管公司的人：超级管理员、租户管理员。 */
    @Override
    public boolean canCreateCompanyStandard() {
        return securityFrameworkService.hasAnyRoles(SUPER_ADMIN, TENANT_ADMIN);
    }

    @Override
    public boolean canDeactivateCompanyStandard() {
        return mockSecurityEnabled || deactivateCompanyStandardEnabled;
    }
}

package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 主数据治理能力的临时配置实现。
 *
 * <p>配置项是 Wave E 验收期间唯一的授权来源，默认全部关闭。本实现不读取角色名、
 * 用户编号、工作模式或设施编号，也不负责本地型号所有权校验。</p>
 */
@Component
public class DefaultMasterDataCapabilityChecker implements MasterDataCapabilityChecker {

    private final boolean createCompanyStandardEnabled;
    private final boolean promotePackageEnabled;
    private final boolean deactivateCompanyStandardEnabled;

    public DefaultMasterDataCapabilityChecker(
            @Value("${cheers.dynamicbusiness.master-data-capability.can-create-company-standard:false}")
            boolean createCompanyStandardEnabled,
            @Value("${cheers.dynamicbusiness.master-data-capability.can-promote-package:false}")
            boolean promotePackageEnabled,
            @Value("${cheers.dynamicbusiness.master-data-capability.can-deactivate-company-standard:false}")
            boolean deactivateCompanyStandardEnabled) {
        this.createCompanyStandardEnabled = createCompanyStandardEnabled;
        this.promotePackageEnabled = promotePackageEnabled;
        this.deactivateCompanyStandardEnabled = deactivateCompanyStandardEnabled;
    }

    @Override
    public boolean canPromotePackage() {
        return promotePackageEnabled;
    }

    @Override
    public boolean canCreateCompanyStandard() {
        return createCompanyStandardEnabled;
    }

    @Override
    public boolean canDeactivateCompanyStandard() {
        return deactivateCompanyStandardEnabled;
    }
}

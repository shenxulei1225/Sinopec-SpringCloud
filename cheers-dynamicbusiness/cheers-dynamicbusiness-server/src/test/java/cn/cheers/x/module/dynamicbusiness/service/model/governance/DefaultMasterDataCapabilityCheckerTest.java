package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultMasterDataCapabilityCheckerTest {

    @Test
    void defaultsCanDenyEveryNetworkGovernanceCapability() {
        DefaultMasterDataCapabilityChecker checker =
                new DefaultMasterDataCapabilityChecker(false, false, false);

        assertFalse(checker.canCreateCompanyStandard());
        assertFalse(checker.canPromotePackage());
        assertFalse(checker.canDeactivateCompanyStandard());
    }

    @Test
    void explicitConfigurationCanEnableAcceptanceCapabilities() {
        DefaultMasterDataCapabilityChecker checker =
                new DefaultMasterDataCapabilityChecker(true, true, true);

        assertTrue(checker.canCreateCompanyStandard());
        assertTrue(checker.canPromotePackage());
        assertTrue(checker.canDeactivateCompanyStandard());
    }
}

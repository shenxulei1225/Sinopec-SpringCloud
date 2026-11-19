package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import cn.cheers.x.framework.security.core.service.SecurityFrameworkService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DefaultMasterDataCapabilityCheckerTest {

    private final SecurityFrameworkService security = mock(SecurityFrameworkService.class);

    private DefaultMasterDataCapabilityChecker checker(boolean mock, boolean promote, boolean deactivate) {
        return new DefaultMasterDataCapabilityChecker(mock, promote, deactivate, security);
    }

    @Test
    void nonAdminCannotCreateCompanyStandard() {
        when(security.hasAnyRoles(any(String[].class))).thenReturn(false);

        assertFalse(checker(true, false, false).canCreateCompanyStandard());
    }

    @Test
    void adminCanCreateCompanyStandard() {
        when(security.hasAnyRoles(any(String[].class))).thenReturn(true);

        assertTrue(checker(false, false, false).canCreateCompanyStandard());
    }

    @Test
    void otherCapabilitiesStillFollowConfiguration() {
        assertFalse(checker(false, false, false).canPromotePackage());
        assertFalse(checker(false, false, false).canDeactivateCompanyStandard());
        assertTrue(checker(false, true, true).canPromotePackage());
        assertTrue(checker(true, false, false).canDeactivateCompanyStandard());
    }
}

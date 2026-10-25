package cn.cheers.x.module.dynamicbusiness.service.useraccount;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.useraccount.vo.UserAccountCreateReqVO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserAccountCreateRulesTest {

    @Test
    void rejectsBlankAccountAndPassword() {
        UserAccountCreateReqVO blankAccount = stationRequest();
        blankAccount.setAccount("  ");
        assertTrue(messageOf(blankAccount).contains("账号不能为空"));

        UserAccountCreateReqVO blankPassword = stationRequest();
        blankPassword.setPassword("  ");
        assertTrue(messageOf(blankPassword).contains("口令不能为空"));
    }

    @Test
    void stationPersonNeedsFacilityAndNotVisitTenants() {
        UserAccountCreateReqVO neither = stationRequest();
        neither.setFacilityId(null);
        assertTrue(messageOf(neither).contains("所属场站"));

        UserAccountCreateReqVO both = stationRequest();
        both.setVisitTenantIds(List.of(1L));
        assertTrue(messageOf(both).contains("只填一边"));
    }

    @Test
    void platformPersonNeedsVisitTenants() {
        UserAccountCreateReqVO request = stationRequest();
        request.setFacilityId(null);
        request.setVisitTenantIds(List.of(1L, 1L));
        assertTrue(messageOf(request).contains("不能重复"));

        UserAccountCreateReqVO ok = stationRequest();
        ok.setFacilityId(null);
        ok.setVisitTenantIds(List.of(2L));
        UserAccountCreateRules.check(ok);
        assertEquals(List.of(2L), ok.getVisitTenantIds());
    }

    @Test
    void statusMustBeEnabledOrDisabled() {
        UserAccountCreateReqVO request = stationRequest();
        request.setStatus(null);
        assertTrue(messageOf(request).contains("状态不能为空"));

        request = stationRequest();
        request.setStatus(3);
        assertTrue(messageOf(request).contains("启用或停用"));
    }

    private static String messageOf(UserAccountCreateReqVO request) {
        ServiceException ex = assertThrows(ServiceException.class, () -> UserAccountCreateRules.check(request));
        return ex.getMessage();
    }

    private static UserAccountCreateReqVO stationRequest() {
        UserAccountCreateReqVO request = new UserAccountCreateReqVO();
        request.setAccount("jinqiao");
        request.setNickname("王磊");
        request.setMobile("13800000000");
        request.setStatus(UserAccountCodes.ENABLED);
        request.setPassword("Station123");
        request.setFacilityId(44L);
        return request;
    }
}

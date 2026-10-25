package cn.cheers.x.module.dynamicbusiness.service.department;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DepartmentOwnerRulesTest {

    @Test
    void 只写场站或只写公司可以通过() {
        DepartmentOwnerRules.checkExactlyOne(44L, null);
        DepartmentOwnerRules.checkExactlyOne(null, 100012L);
    }

    @Test
    void 两处都写时拒绝() {
        ServiceException error = assertThrows(ServiceException.class,
                () -> DepartmentOwnerRules.checkExactlyOne(44L, 100012L));
        assertEquals(DepartmentOwnerRules.BOTH, error.getMessage());
    }

    @Test
    void 两处都空时拒绝() {
        ServiceException error = assertThrows(ServiceException.class,
                () -> DepartmentOwnerRules.checkExactlyOne(null, null));
        assertEquals(DepartmentOwnerRules.NEITHER, error.getMessage());
    }
}

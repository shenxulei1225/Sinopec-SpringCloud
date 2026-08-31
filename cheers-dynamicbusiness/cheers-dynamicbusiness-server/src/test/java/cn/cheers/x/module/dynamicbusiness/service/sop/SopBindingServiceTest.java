package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * SopBinding 入参规范化：任意 subjectType 字符串可写入（不校验业务枚举）。
 */
class SopBindingServiceTest {

    @Test
    void requireTypeCode_acceptsArbitrarySubjectType() {
        assertEquals("demo_subject", SopBindingServiceImpl.requireTypeCode(" demo_subject ", "subjectType"));
        assertEquals("inspection_item", SopBindingServiceImpl.requireTypeCode("inspection_item", "subjectType"));
    }

    @Test
    void requireTypeCode_rejectsBlank() {
        assertThrows(ServiceException.class,
                () -> SopBindingServiceImpl.requireTypeCode("  ", "subjectType"));
    }

    @Test
    void normalizeDimensionValue_uppercasesWithoutEnumGate() {
        assertEquals("CUSTOM_MEANS", SopBindingServiceImpl.normalizeDimensionValue("custom_means"));
        assertEquals("MANUAL", SopBindingServiceImpl.normalizeDimensionValue("manual"));
    }
}

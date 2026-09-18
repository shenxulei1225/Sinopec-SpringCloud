package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * RelationBinding 入参规范化：任意 subjectType 字符串可写入（不校验业务枚举）。
 */
class RelationBindingServiceTest {

    @Test
    void requireTypeCode_acceptsArbitrarySubjectType() {
        assertEquals("demo_subject", RelationBindingServiceImpl.requireTypeCode(" demo_subject ", "subjectType"));
        assertEquals("inspection_item", RelationBindingServiceImpl.requireTypeCode("inspection_item", "subjectType"));
    }

    @Test
    void requireTypeCode_rejectsBlank() {
        assertThrows(ServiceException.class,
                () -> RelationBindingServiceImpl.requireTypeCode("  ", "subjectType"));
    }

    @Test
    void normalizeDimensionValue_uppercasesWithoutEnumGate() {
        assertEquals("CUSTOM_MEANS", RelationBindingServiceImpl.normalizeDimensionValue("custom_means"));
        assertEquals("MANUAL", RelationBindingServiceImpl.normalizeDimensionValue("manual"));
    }
}

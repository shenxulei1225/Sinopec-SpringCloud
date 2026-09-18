package cn.cheers.x.module.dynamicbusiness.service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EntityRefFieldTargetTest {

    @Test
    void fromProviderCode_readsDynamicEntityTarget() {
        assertEquals("action", EntityRefFieldTarget.fromProviderCode("dynamic-entity:action"));
        assertEquals("data_protocol", EntityRefFieldTarget.fromProviderCode("DYNAMIC-ENTITY:data_protocol"));
    }

    @Test
    void fromProviderCode_doesNotGuessOtherProviders() {
        assertEquals("", EntityRefFieldTarget.fromProviderCode("user"));
        assertEquals("", EntityRefFieldTarget.fromProviderCode(""));
        assertEquals("", EntityRefFieldTarget.fromProviderCode(null));
    }

    @Test
    void firstDeclared_skipsBlank() {
        assertEquals("action", EntityRefFieldTarget.firstDeclared("", "  ", "action", "data_protocol"));
        assertEquals("", EntityRefFieldTarget.firstDeclared(null, "  "));
    }
}

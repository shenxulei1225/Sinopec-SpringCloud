package cn.cheers.x.module.dynamicbusiness.service.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EntityBusinessHelperCodeTest {

    @Test
    void equipmentCode_isPlainUuid() {
        String code = EntityBusinessHelper.generatedDedicatedCode("equipment");
        assertTrue(code.matches("[0-9a-f]{32}"));
        assertFalse(code.contains("-"));
    }

    @Test
    void otherDedicatedCode_keepsTypePrefix() {
        String code = EntityBusinessHelper.generatedDedicatedCode("region");
        assertTrue(code.startsWith("region-"));
        assertTrue(code.substring("region-".length()).matches("[0-9a-f]{32}"));
    }
}

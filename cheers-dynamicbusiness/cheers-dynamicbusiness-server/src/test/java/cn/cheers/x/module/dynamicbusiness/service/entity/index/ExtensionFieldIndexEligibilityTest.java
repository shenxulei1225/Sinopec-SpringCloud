package cn.cheers.x.module.dynamicbusiness.service.entity.index;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExtensionFieldIndexEligibilityTest {

    @Test
    void shouldWriteIndex_whenOnlyFilterableOnAndTypeAllows() {
        assertTrue(ExtensionFieldIndexEligibility.shouldWriteIndex(
                false, true, false, "DATE"));
    }

    @Test
    void shouldWriteIndex_whenOnlySortableOnAndTypeAllows() {
        assertTrue(ExtensionFieldIndexEligibility.shouldWriteIndex(
                false, false, true, "DATE"));
    }

    @Test
    void shouldWriteIndex_whenOnlySearchableOnAndTypeAllows() {
        assertTrue(ExtensionFieldIndexEligibility.shouldWriteIndex(
                true, false, false, "TEXT"));
    }

    @Test
    void shouldNotWriteIndex_whenAllOff() {
        assertFalse(ExtensionFieldIndexEligibility.shouldWriteIndex(
                false, false, false, "JSON"));
        assertFalse(ExtensionFieldIndexEligibility.shouldWriteIndex(
                null, null, null, "TEXT"));
    }

    @Test
    void dirtyFilterableOnText_doesNotIndex() {
        assertFalse(ExtensionFieldIndexEligibility.shouldWriteIndex(
                false, true, false, "TEXT"));
        assertFalse(ExtensionFieldIndexEligibility.resolveFilterable(true, "TEXT"));
    }

    @Test
    void resolveFilterable_onlyExplicitTrueAndTypeAllows() {
        assertTrue(ExtensionFieldIndexEligibility.resolveFilterable(true, "DATE"));
        assertFalse(ExtensionFieldIndexEligibility.resolveFilterable(false, "DATE"));
        assertFalse(ExtensionFieldIndexEligibility.resolveFilterable(null, "DATE"));
    }
}

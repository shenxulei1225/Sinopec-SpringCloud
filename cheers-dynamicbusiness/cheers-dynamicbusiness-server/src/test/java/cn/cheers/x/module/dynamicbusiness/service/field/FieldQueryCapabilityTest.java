package cn.cheers.x.module.dynamicbusiness.service.field;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldQueryCapabilityTest {

    @Test
    void textCanSearchOnly() {
        assertTrue(FieldQueryCapability.canSearch("TEXT"));
        assertFalse(FieldQueryCapability.canFilter("TEXT"));
        assertFalse(FieldQueryCapability.canSort("LONG_TEXT"));
    }

    @Test
    void dateCanFilterAndSort() {
        assertFalse(FieldQueryCapability.canSearch("DATE"));
        assertTrue(FieldQueryCapability.canFilter("DATETIME"));
        assertTrue(FieldQueryCapability.canSort("DATE"));
    }

    @Test
    void enumCanFilterNotSort() {
        assertFalse(FieldQueryCapability.canSearch("ENUM"));
        assertTrue(FieldQueryCapability.canFilter("ENTITY_REF"));
        assertFalse(FieldQueryCapability.canSort("BOOLEAN"));
    }

    @Test
    void jsonCannotQuery() {
        assertFalse(FieldQueryCapability.canSearch("JSON"));
        assertFalse(FieldQueryCapability.canFilter("COORDINATE"));
        assertFalse(FieldQueryCapability.canSort("FILE"));
    }

    @Test
    void rejectIllegalEnable() {
        assertThrows(ServiceException.class,
                () -> FieldQueryCapability.assertCanEnable("TEXT", false, true, false));
        assertThrows(ServiceException.class,
                () -> FieldQueryCapability.assertCanEnable("DATE", true, false, false));
        FieldQueryCapability.assertCanEnable("DATE", false, true, true);
    }
}

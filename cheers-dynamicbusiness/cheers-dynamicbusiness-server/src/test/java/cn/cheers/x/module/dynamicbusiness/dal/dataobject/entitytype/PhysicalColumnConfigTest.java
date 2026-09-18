package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PhysicalColumnConfigTest {

    @Test
    void jsonb_toDbTypeAndValidate() {
        PhysicalColumnConfig config = PhysicalColumnConfig.jsonb("coordinate_gis");
        assertEquals("JSONB", config.toDbType());
        assertNull(config.validate());
    }
}
